package br.com.vega.armazem.adapter.in.web;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Percorre o fluxo HTTP de autenticação, estoque, regras e concorrência.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FluxoArmazemIT {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Exige autenticação nas rotas de negócio e mantém o contrato OpenAPI público.
     *
     * @throws Exception quando a chamada HTTP de teste falha
     */
    @Test
    @DisplayName("protege a API e publica o Swagger")
    void deveProtegerApiEPublicarSwagger() throws Exception {
        mockMvc.perform(get("/ingredientes")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"admin\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }

    /**
     * Cadastra farinha, consulta volume e espaço e impede mistura, excesso e troca de tipo no mesmo dia.
     *
     * @throws Exception quando a chamada HTTP de teste falha
     */
    @Test
    @DisplayName("aplica as regras de armazenamento no fluxo HTTP")
    void deveAplicarRegrasNoFluxoHttp() throws Exception {
        String token = login();

        MvcResult criado = mockMvc.perform(post("/ingredientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Farinha de Trigo","tipo":"SECOS","quantidade":400,"compartimentoId":1,"responsavel":"pedro"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("SECO"))
                .andExpect(jsonPath("$.compartimentoCodigo").value("C1"))
                .andReturn();
        Number id = JsonPath.read(criado.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/ingredientes/volume").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("LIQUIDO"))
                .andExpect(jsonPath("$[2].type").value("SECO"))
                .andExpect(jsonPath("$[2].totalQuantity").value(closeTo(400.0, 0.001)));

        mockMvc.perform(get("/compartimentos/disponiveis")
                        .header("Authorization", "Bearer " + token)
                        .param("quantidade", "500")
                        .param("tipo", "SECO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.codigo=='C1')]").isEmpty())
                .andExpect(jsonPath("$[?(@.codigo=='C2')].espacoLivre").value(hasSize(1)));

        mockMvc.perform(get("/compartimentos/disponiveis-para-venda")
                        .header("Authorization", "Bearer " + token)
                        .param("tipo", "SECO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("C1"));

        mockMvc.perform(post("/ingredientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Leite","tipo":"LIQUIDO","quantidade":10,"compartimentoId":1,"responsavel":"pedro"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(org.hamcrest.Matchers.containsString("não pode receber")));

        mockMvc.perform(post("/ingredientes/" + id + "/saida")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantidade\":400,\"responsavel\":\"pedro\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade").value(closeTo(0.0, 0.001)));

        mockMvc.perform(post("/ingredientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Leite","tipo":"LIQUIDO","quantidade":10,"compartimentoId":1,"responsavel":"pedro"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(org.hamcrest.Matchers.containsString("amanhã")));

        mockMvc.perform(get("/historico")
                        .header("Authorization", "Bearer " + token)
                        .param("sortBy", "date")
                        .param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].operacao").value("ENTRADA"))
                .andExpect(jsonPath("$[1].operacao").value("SAIDA"));
    }

    /**
     * Duas entradas simultâneas de 400 kg em um compartimento de 600 kg não podem ambas vencer.
     *
     * @throws Exception quando a coordenação das threads falha
     */
    @Test
    @DisplayName("serializa entradas concorrentes no mesmo compartimento")
    void deveSerializarEntradasConcorrentes() throws Exception {
        String token = login();
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch inicio = new CountDownLatch(1);
        List<Integer> status = Collections.synchronizedList(new ArrayList<>());
        try {
            Future<?> primeira = pool.submit(() -> enviarEntradaConcorrente(token, inicio, status));
            Future<?> segunda = pool.submit(() -> enviarEntradaConcorrente(token, inicio, status));
            inicio.countDown();
            primeira.get(30, TimeUnit.SECONDS);
            segunda.get(30, TimeUnit.SECONDS);
        } finally {
            pool.shutdownNow();
        }
        long aceitas = status.stream().filter(codigo -> codigo == 201).count();
        assertEquals(1, aceitas, "status recebidos: " + status);
        mockMvc.perform(get("/compartimentos").header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$[1].codigo").value("C2"))
                .andExpect(jsonPath("$[1].quantidadeAtual").value(closeTo(400.0, 0.001)));
    }

    /**
     * Autentica o administrador de demonstração.
     *
     * @return token Bearer
     * @throws Exception quando o login falha
     */
    private String login() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"admin\",\"senha\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(resultado.getResponse().getContentAsString(), "$.token");
    }

    /**
     * Dispara uma entrada de 400 kg no C2 depois que as duas threads são liberadas juntas.
     *
     * @param token token já autenticado
     * @param inicio barreira de início
     * @param status lista compartilhada dos códigos HTTP
     */
    private void enviarEntradaConcorrente(String token, CountDownLatch inicio, List<Integer> status) {
        try {
            inicio.await();
            int codigo = mockMvc.perform(post("/ingredientes")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nome":"Acucar","tipo":"SECO","quantidade":400,"compartimentoId":2,"responsavel":"admin"}
                                    """))
                    .andReturn()
                    .getResponse()
                    .getStatus();
            status.add(codigo);
        } catch (Exception excecao) {
            status.add(500);
        }
    }
}

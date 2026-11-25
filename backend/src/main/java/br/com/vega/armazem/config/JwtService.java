package br.com.vega.armazem.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Emite e valida o token JWT usado pelo painel e pelos clientes da API.
 */
@Component
public class JwtService {

    private final String segredo;
    private final long expiracaoMinutos;

    /**
     * Recebe o segredo e o prazo de expiração configurados.
     *
     * @param segredo chave HMAC com pelo menos 32 bytes
     * @param expiracaoMinutos validade do token em minutos
     */
    public JwtService(@Value("${app.jwt.segredo}") String segredo,
                      @Value("${app.jwt.expiracao-minutos}") long expiracaoMinutos) {
        this.segredo = segredo;
        this.expiracaoMinutos = expiracaoMinutos;
    }

    /**
     * Gera um token assinado para o usuário autenticado.
     *
     * @param usuario detalhes carregados pelo Spring Security
     * @return token compacto
     */
    public String gerar(UserDetails usuario) {
        Instant agora = Instant.now();
        List<String> papeis = usuario.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("papeis", papeis)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(expiracaoMinutos, ChronoUnit.MINUTES)))
                .signWith(chave())
                .compact();
    }

    /**
     * Lê o usuário do token e valida a assinatura e a expiração.
     *
     * @param token valor recebido no cabeçalho Authorization
     * @return nome de login contido no token
     */
    public String extrairUsuario(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(chave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Retorna a validade configurada para informar o cliente.
     *
     * @return minutos até a expiração
     */
    public long getExpiracaoMinutos() {
        return expiracaoMinutos;
    }

    /**
     * Monta a chave HMAC a partir do segredo configurado.
     *
     * @return chave secreta de assinatura
     */
    private SecretKey chave() {
        return Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
    }
}

package br.com.vega.armazem.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Usuário capaz de autenticar no painel e na API.
 */
@Entity
@Table(name = "usuario")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String usuario;

    @Column(nullable = false, length = 120)
    private String senha;

    @Column(nullable = false, length = 40)
    private String papel;

    /**
     * Cria a entidade vazia exigida pelo JPA.
     */
    public UsuarioEntity() {
    }

    /**
     * Cria um usuário com senha já codificada e papel do Spring Security.
     *
     * @param usuario nome de login
     * @param senha hash BCrypt
     * @param papel autoridade, como ROLE_ADMIN
     */
    public UsuarioEntity(String usuario, String senha, String papel) {
        this.usuario = usuario;
        this.senha = senha;
        this.papel = papel;
    }

    /**
     * Retorna o identificador do usuário.
     *
     * @return id gerado
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador do usuário.
     *
     * @param id id gerado
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome de login.
     *
     * @return usuário único
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Define o nome de login.
     *
     * @param usuario usuário único
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Retorna a senha codificada.
     *
     * @return hash BCrypt
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Define a senha codificada.
     *
     * @param senha hash BCrypt
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * Retorna o papel de autorização.
     *
     * @return autoridade do Spring Security
     */
    public String getPapel() {
        return papel;
    }

    /**
     * Define o papel de autorização.
     *
     * @param papel autoridade do Spring Security
     */
    public void setPapel(String papel) {
        this.papel = papel;
    }
}

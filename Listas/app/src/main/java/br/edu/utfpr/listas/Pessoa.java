package br.edu.utfpr.listas;

public class Pessoa {

    private final String nome;
    private final String email;

    public Pessoa(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}

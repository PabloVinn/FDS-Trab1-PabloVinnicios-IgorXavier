package br.pucrs.trabalho1fds.demo;

public class Candidato{

    int numero;
    String nome;
    String situacao;

    public Candidato (int numero, String nome, String situacao){
        this.numero = numero;
        this.nome = nome;
        this.situacao = situacao;
    }

    public int getNumero() {
        return numero;
    }

    public String getNome() {
        return nome;
    }

    public String getSituacao() {
        return situacao;
    }


}
package br.pucrs.trabalho1fds.demo;

public class Candidato{

    int numero;
    String nome;
    String situacao;
    Partido partido;
    Localidade localidade;

    public Candidato (int numero, String nome, String situacao, Partido partido, Localidade localidade){
        this.numero = numero;
        this.nome = nome;
        this.situacao = situacao;
        this.partido = partido;
        this.localidade = localidade;
    }

    public int getNumero(){ 
        return numero;}
    public void setNumero(int numero){ 
        this.numero = numero;}

    public String getNome(){ 
        return nome;}
    public void setNome(String nome){ 
        this.nome = nome;}

    public String getSituacao(){ 
        return situacao;}
    public void setSituacao(String situacao){ 
        this.situacao = situacao;}

    public Partido getPartido(){ 
        return partido;}
    public void setPartido(Partido partido){ 
        this.partido = partido;}

    public Localidade getLocalidade(){ 
        return localidade;}
    public void setLocalidade(Localidade localidade){ 
        this.localidade = localidade;}


}
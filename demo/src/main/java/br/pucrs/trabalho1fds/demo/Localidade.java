package br.pucrs.trabalho1fds.demo;

public class Localidade{

    String cep;
    String nome;
    int qtdEleitores;

    public Localidade (String cep, String nome, int qtdEleitores){
        this.cep = cep;
        this.nome = nome;
        this.qtdEleitores = qtdEleitores;
    }

    public String getCep(){
        return cep;}
    public void setCep(String cep){
        this.cep = cep;}
        
    public String getNome(){ 
        return nome;}
    public void setNome(String nome){ 
        this.nome = nome;}
    
    public int getQtdEleitores(){ 
        return qtdEleitores;}
    public void setQtdEleitores(int qtdEleitores){
        this.qtdEleitores = qtdEleitores;}

}
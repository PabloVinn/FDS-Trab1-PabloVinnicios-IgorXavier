package br.pucrs.trabalho1fds.demo;


public class Partido{

    private int codigo;
    private String nome;

    public Partido (int codigo, String nome){
        this.codigo = codigo;
        this.nome = nome;
    }

    public int getCodigo(){ 
        return codigo;}
    public void setCodigo(int codigo){
        this.codigo = codigo;}

    public String getNome(){ 
        return nome;}
    public void setNome(String nome){ 
        this.nome = nome;}

}
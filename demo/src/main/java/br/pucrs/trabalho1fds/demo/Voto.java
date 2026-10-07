package br.pucrs.trabalho1fds.demo;


public class Voto{

    private int id;
    private int hora;
    private Candidato candidato;
    private Localidade localidade;

    public Voto (int id, int hora){
        this.id = id;
        this.hora = hora;
        this.candidato = candidato;
        this.localidade = localidade;
    }

    public int getId(){ 
        return id;}
    public void setId(int id){ 
        this.id = id;}

    public int getHora(){ 
        return hora;}
    public void setHora(int hora){ 
        this.hora = hora;}

    public Candidato getCandidato(){ 
        return candidato;}
    public void setCandidato(Candidato candidato){ 
        this.candidato = candidato;}

    public Localidade getLocalidade(){ 
        return localidade;}
    public void setLocalidade(Localidade localidade){ 
        this.localidade = localidade;}

}


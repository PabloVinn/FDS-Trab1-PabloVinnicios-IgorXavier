package br.pucrs.trabalho1fds.demo;


public class Voto{

    private int id;
    private int hora;
    private Candidato candidato;
    private Localidade localidade;
    private int numeroCandidato;
    private boolean valido;

    public Voto (int id, int hora){
        this(id, hora, 0, null, null);
    }

    public Voto(int id, int hora, int numeroCandidato, Candidato candidato, Localidade localidade) {
        this.id = id;
        this.hora = hora;
        this.numeroCandidato = numeroCandidato;
        this.candidato = candidato;
        this.localidade = localidade;
    }

    public int getNumeroCandidato() {
        return numeroCandidato;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
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


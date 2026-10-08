package br.pucrs.trabalho1fds.demo;


public class Voto{

    private int id;
    private double hora;
    private Candidato candidato;
    private Localidade localidade;
    // Guarda o número informado mesmo se não encontrar o candidato -
    private int numeroCandidato;
    // Guarda se o voto era válido no momento do cadastro -
    private boolean valido;

    public Voto (int id, double hora, Candidato candidato, Localidade localidade){
        // Usa o construtor completo, pegando o número se o candidato existir -
        //NOTE: Se não tiver candidato, esse construtor usa zero como número -
        this(id, hora, candidato == null ? 0 : candidato.getNumero(), candidato, localidade);
    }

    public Voto(int id, double hora, int numeroCandidato, Candidato candidato, Localidade localidade){
        // Cria o voto com os dados recebidos e mantém o número passado no cadastro -
        this.id = id;
        this.hora = hora;
        this.candidato = candidato;
        this.localidade = localidade;
        this.numeroCandidato = numeroCandidato;
    }

    public int getNumeroCandidato(){
        // Retorna o número para contar os votos mesmo sem um objeto Candidato -
        return numeroCandidato;
    }

    public boolean isValido(){
        // Retorna a classificação salva, sem fazer uma nova validação -
        return valido;
    }

    public void setValido(boolean valido){
        // Salva o resultado da validação feita no Acervo -
        this.valido = valido;
    }

    public int getId(){ 
        return id;}
    public void setId(int id){ 
        this.id = id;}

    public double getHora(){ 
        return hora;}
    public void setHora(double hora){ 
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


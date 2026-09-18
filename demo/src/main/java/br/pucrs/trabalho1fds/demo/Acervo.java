package br.pucrs.trabalho1fds.demo;

import java.util.*;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

@Component
public class Acervo {
    private List<Candidato> candidatos;

    public Acervo() {
        candidatos = new ArrayList<>();

        candidatos.add(new Candidato());
        candidatos.add(new Candidato());
        candidatos.add(new Candidato());
        candidatos.add(new Candidato());
        candidatos.add(new Candidato()); 
    }
}
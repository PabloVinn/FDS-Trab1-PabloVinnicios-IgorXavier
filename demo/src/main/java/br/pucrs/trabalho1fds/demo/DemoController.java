package br.pucrs.trabalho1fds.demo;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/acmepolling")
public class DemoController {

    private List<Candidato> candidatos;

    @GetMapping("/")
    public String getMensagemInicial() {
        return "Aplicacao Spring-Boot funcionando!";
    }

    @GetMapping ("/cadastro/listacandidatos")
    public List<Candidato> getCandidatos() {
        return candidatos;
    }

}
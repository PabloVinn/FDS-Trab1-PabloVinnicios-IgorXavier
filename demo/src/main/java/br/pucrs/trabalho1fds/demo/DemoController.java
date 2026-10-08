package br.pucrs.trabalho1fds.demo;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/acmepolling")
public class DemoController {

    private final List<Candidato> candidatos;

    public DemoController(Acervo acervo) {
        // O construtor já puxando o candidatos do acervo -
        this.candidatos = acervo.getCandidatos();
    }

    @GetMapping("/")
    public String getMensagemInicial() {
        return "Aplicacao Spring-Boot funcionando!";
    }

    @GetMapping ("/cadastro/listacandidatos")
    public List<Candidato> getCandidatos() {
        return candidatos;
    }

    @GetMapping ("/cadastro/listacandidatoslocalidade/{cep}/{situacao}")
    public List<Candidato> getCandidatosLocalidade(
        @RequestParam(value = "cep") String cep,
        @RequestParam(value = "situacao") String situacao) 
    {
        return candidatos.stream()
            .filter(candidato -> candidato.getLocalidade().getCep().equals(cep))
            .filter(candidato -> candidato.getSituacao().equals(situacao))
            .toList();
    }

}
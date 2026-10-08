package br.pucrs.trabalho1fds.demo;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/acmepolling")
public class DemoController {

    private final List<Candidato> candidatos;
    private final Acervo acervo;

    public DemoController(Acervo acervo) {
        // Construtor básico com as instancias das dependencias -
        this.acervo     = acervo;
        this.candidatos = acervo.getCandidatos();
    }

    @GetMapping("/")
    public String getMensagemInicial() {
        return "Aplicacao Spring-Boot funcionando!";
    }

    @GetMapping("/cadastro/listacandidatos")
    public List<Candidato> getCandidatos() {
        return candidatos;
    }

    @GetMapping("/cadastro/listacandidatoslocalidade/{cep}/{situacao}")
    public List<Candidato> getCandidatosLocalidade(
        @RequestParam(value = "cep") String cep,
        @RequestParam(value = "situacao") String situacao) 
    {
        return candidatos.stream()
            .filter(candidato -> candidato.getLocalidade().getCep().equals(cep))
            .filter(candidato -> candidato.getSituacao().equals(situacao))
            .toList();
    }

    // Classe para o body do POST, já que o professor quis q tivesse esses args.
    public record CadastroCandidato(Integer numero, String nome, Integer codigo, String cep) {}

    @PostMapping("/cadastro/cadcandidato")
    public boolean cadastrarCandidato(@RequestBody CadastroCandidato dados) {
        // Verifica se os campos estao validos -
        if (dados == null || dados.numero() == null || dados.codigo() == null
                || dados.nome() == null || dados.nome().isBlank()
                || dados.cep() == null || dados.cep().isBlank()) {
            return false;
        }

        boolean numeroJaExiste = false;

        // Verifica se o número do candidato já existe
        for (Candidato candidato : candidatos) {
            if (candidato.getNumero() == dados.numero()) {
                numeroJaExiste = true;
                break;
            }
        }
        if (numeroJaExiste) {
            return false;
        }

        // Busca o partido do candidato -
        Partido partido = null;
        for (Partido p: acervo.getPartidos()) {
            if (p.getCodigo() == dados.codigo()) {
                partido = p;
                break;
            }
        }
        
        // Busca a localidade do candidato -
        Localidade localidade = null;
        for (Localidade l: acervo.getLocalidades()) {
            if (l.getCep().equals(dados.cep())) {
                localidade = l;
                break;
            }
        }

        // Valida se foi encontrado o partido e a localidade -
        if (partido == null || localidade == null) {
            return false;
        }

        // Cria o novo candidato -
        //NOTE: Como eh a sua criação então a sua situacao é PRECANDIDATO -
        Candidato candidato = new Candidato(
                dados.numero(), dados.nome(), "PRECANDIDATO", partido, localidade);
        return candidatos.add(candidato);
    }

}

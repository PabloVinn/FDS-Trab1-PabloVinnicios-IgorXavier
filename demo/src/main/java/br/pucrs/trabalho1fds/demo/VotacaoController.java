package br.pucrs.trabalho1fds.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("acmepolling/votacao")
public class VotacaoController {

    private final Acervo acervo;

    public VotacaoController(Acervo acervo) {
        this.acervo = acervo;
    }

    // Mesma coisa que em Cadastro -
    // Classe para o body do POST -
    public record CadastroVoto(Integer id, Integer hora, Integer numero, String cep) {}

    @PostMapping("/cadvoto")
    public boolean cadastrarVoto(@RequestBody CadastroVoto dados) {

        // Verifica se os campos estao validos -
        if (dados == null || dados.id() == null || dados.hora() == null
                || dados.numero() == null || dados.cep() == null || dados.cep().isBlank()) {
            return false;
        }

        // Cada voto precisa ter um ID diferente.
        for (Voto voto : acervo.getVotos()) {
            if (voto.getId() == dados.id()) {
                return false;
            }
        }

        Localidade localidade = null;
        for (Localidade l : acervo.getLocalidades()) {
            if (l.getCep().equals(dados.cep())) {
                localidade = l;
                break;
            }
        }
        if (localidade == null) {
            return false;
        }

        Candidato candidato = null;
        for (Candidato c : acervo.getCandidatos()) {
            if (c.getNumero() == dados.numero()) {
                candidato = c;
                break;
            }
        }

        // Preserva o número informado mesmo se o candidato não existir -
        Voto voto = new Voto(dados.id(), dados.hora(), dados.numero(), candidato, localidade);
        voto.setValido(acervo.ehVotoValido(voto));

        // Votos inválidos também ficam registrados para a apuração.
        return acervo.getVotos().add(voto);
    }
}

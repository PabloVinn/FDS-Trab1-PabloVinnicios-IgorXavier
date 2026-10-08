package br.pucrs.trabalho1fds.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/acmepolling/apuracao")
public class ApuracaoController {

    private final Acervo acervo;

    public ApuracaoController(Acervo acervo) {
        this.acervo = acervo;
    }

    // Respostas JSON de cada consulta -
    public record VotosCandidato(long quantidade_votos_validos, long quantidade_votos_invalidos) {}

    public record VotacaoCandidato(
        int numero,
        String nome,
        long quantidade_votos_validos,
        long quantidade_votos_invalidos
    ) {}

    public record CandidatoEleito(
        int numero,
        String nome,
        String nome_partido,
        long quantidade_total_votos) {}

    public record MaisVotadoPartido(
        String nome_partido,
        int numero,
        String nome,
        long quantidade_votos_validos) {}

    @GetMapping("/consultacandidato")
    public VotosCandidato consultarCandidato(@RequestParam("numero") int numero) {
        // Valida os votos do candidato -
        int validos = 0;
        int invalidos = 0;

        for (Voto voto : acervo.getVotos()) {
            if (voto.getNumeroCandidato() == numero) {
                if (voto.isValido()) {
                    validos++;
                } else {
                    invalidos++;
                }
            }
        }
        return new VotosCandidato(validos, invalidos);
    }

    @GetMapping("/consultalocalidade")
    public List<VotacaoCandidato> consultarLocalidade(@RequestParam("cep") String cep) {
        List<VotacaoCandidato> resultado = new ArrayList<>();

        for (Candidato candidato : acervo.getCandidatos()) {
            if (candidato.getLocalidade().getCep().equals(cep)) {
                int validos = 0;
                int invalidos = 0;

                for (Voto voto : acervo.getVotos()) {
                    if (voto.getNumeroCandidato() == candidato.getNumero()) {
                        if (voto.isValido()) {
                            validos++;
                        } else {
                            invalidos++;
                        }
                    }
                }
                resultado.add(new VotacaoCandidato(candidato.getNumero(), candidato.getNome(),
                        validos, invalidos));
            }
        }
        return resultado;
    }

    @GetMapping("/consultaeleito")
    public CandidatoEleito consultarEleito(@RequestParam("cep") String cep) {
        Candidato eleito = null;
        int maiorQuantidade = 0;
        int ultimoVotoEleito = 0;

        for (Candidato candidato : acervo.getCandidatos()) {
            if (!candidato.getLocalidade().getCep().equals(cep)) {
                continue;
            }
            // Os estados finais permitem consultar novamente uma apuração já feita.
            String situacao = candidato.getSituacao();
            if (!situacao.equals("ELEGIVEL") && !situacao.equals("ELEITO")
                    && !situacao.equals("NAOELEITO")) {
                continue;
            }

            int quantidade = 0;
            int ultimoVoto = 0;
            for (Voto voto : acervo.getVotos()) {
                if (voto.getNumeroCandidato() == candidato.getNumero() && voto.isValido()) {
                    quantidade++;
                    if (voto.getHora() > ultimoVoto) {
                        ultimoVoto = voto.getHora();
                    }
                }
            }
            if (quantidade == 0) {
                continue;
            }

            if (eleito == null || quantidade > maiorQuantidade
                    || (quantidade == maiorQuantidade && ultimoVoto < ultimoVotoEleito)) {
                eleito = candidato;
                maiorQuantidade = quantidade;
                ultimoVotoEleito = ultimoVoto;
            }
        }

        if (eleito == null) {
            return null;
        }

        // Finaliza a situação dos candidatos que participaram da apuração.
        for (Candidato candidato : acervo.getCandidatos()) {
            String situacao = candidato.getSituacao();
            if (candidato.getLocalidade().getCep().equals(cep)
                    && (situacao.equals("ELEGIVEL") || situacao.equals("ELEITO")
                            || situacao.equals("NAOELEITO"))) {
                if (candidato == eleito) {
                    candidato.setSituacao("ELEITO");
                } else {
                    candidato.setSituacao("NAOELEITO");
                }
            }
        }

        return new CandidatoEleito(eleito.getNumero(), eleito.getNome(),
                eleito.getPartido().getNome(), maiorQuantidade);
    }

    @GetMapping("/listamaisvotadospartido")
    public List<MaisVotadoPartido> listarMaisVotadosPartido() {
        List<MaisVotadoPartido> resultado = new ArrayList<>();

        for (Partido partido : acervo.getPartidos()) {
            Candidato maisVotado = null;
            int maiorQuantidade = 0;

            for (Candidato candidato : acervo.getCandidatos()) {
                if (candidato.getPartido().getCodigo() != partido.getCodigo()) {
                    continue;
                }

                int quantidade = 0;
                for (Voto voto : acervo.getVotos()) {
                    if (voto.getNumeroCandidato() == candidato.getNumero() && voto.isValido()) {
                        quantidade++;
                    }
                }

                if (maisVotado == null || quantidade > maiorQuantidade) {
                    maisVotado = candidato;
                    maiorQuantidade = quantidade;
                }
            }

            // Um partido sem candidatos não tem representante nesta lista.
            if (maisVotado != null) {
                resultado.add(new MaisVotadoPartido(partido.getNome(), maisVotado.getNumero(),
                        maisVotado.getNome(), maiorQuantidade));
            }
        }
        return resultado;
    }

}

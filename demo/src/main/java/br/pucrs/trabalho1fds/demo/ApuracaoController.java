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
        // Construtor básico com as instancias das dependencias -
        this.acervo = acervo;
    }

    // Respostas JSON de cada consulta -
    public record VotosCandidato(
        long quantidade_votos_validos, 
        long quantidade_votos_invalidos
    ) {}

    public record VotacaoCandidato(
        int     numero,
        String  nome,
        long    quantidade_votos_validos,
        long    quantidade_votos_invalidos
    ) {}

    public record CandidatoEleito(
        int     numero,
        String  nome,
        String  nome_partido,
        long    quantidade_total_votos
    ) {}

    public record MaisVotadoPartido(
        String  nome_partido,
        int     numero,
        String  nome,
        long    quantidade_votos_validos
    ) {}

    @GetMapping("/consultacandidato")
    public VotosCandidato consultarCandidato(@RequestParam("numero") int numero) {
        // Valida os votos do candidato -
        int validos     = 0;
        int invalidos   = 0;

        // Percorre pelos votos -
        for (Voto voto : acervo.getVotos()) {
            // Valida se o voto é do candidato informado -
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
        // Cria uma lista do resultado vazio -
        List<VotacaoCandidato> resultado = new ArrayList<>();

        // Percorre pelos candidatos -
        for (Candidato candidato : acervo.getCandidatos()) {
            // Valida se o candidato pertence a localidade informada -
            if (candidato.getLocalidade().getCep().equals(cep)) {
                int validos     = 0;
                int invalidos   = 0;

                // Percorre pelos votos -
                for (Voto voto : acervo.getVotos()) {
                    if (voto.getNumeroCandidato() == candidato.getNumero()) {
                        if (voto.isValido()) {
                            validos++;
                        } else {
                            invalidos++;
                        }
                    }
                }

                // Adciona na lista de resultado -
                resultado.add(new VotacaoCandidato(candidato.getNumero(), candidato.getNome(),
                        validos, invalidos));
            }
        }
        // retorno da lista -
        return resultado;
    }

    @GetMapping("/consultaeleito")
    public CandidatoEleito consultarEleito(@RequestParam("cep") String cep) {
        Candidato eleito        = null;
        int maiorQuantidade     = 0;
        int ultimoVotoEleito    = 0;

        // Percorre pelos candidatos -
        for (Candidato candidato : acervo.getCandidatos()) {
            // Caso não for do CEP continua -
            if (!candidato.getLocalidade().getCep().equals(cep)) {
                continue;
            }
            // Os estados finais permitem consultar novamente uma apuração já feita.
            String situacao = candidato.getSituacao();

            // Se a situação não for uma das três válidas, continua -
            if (!situacao.equals("ELEGIVEL") && !situacao.equals("ELEITO")
                    && !situacao.equals("NAOELEITO")) {
                continue;
            }
            int quantidade = 0;
            int ultimoVoto = 0;

            // Percorre pelos votos -
            for (Voto voto : acervo.getVotos()) {

                // Filtra pela candidato e voto valido -
                if (voto.getNumeroCandidato() == candidato.getNumero() && voto.isValido()) {
                    quantidade++;
                    // Aqui já aproveita e valida se o voto é o último, para desempate -
                    if (voto.getHora() > ultimoVoto) {
                        ultimoVoto = voto.getHora();
                    }
                }
            }

            // Se o candidato não tiver votos válidos, continua -
            if (quantidade == 0) {
                continue;
            }


            // Se não tiver eleito ainda,
            // ou se tiver mais votos que o eleito atual,
            // ou se tiver a mesma quantidade de votos, mas o último voto for mais recente
            // o coloco como eleito -
            if (
                eleito == null || quantidade > maiorQuantidade
                || (quantidade == maiorQuantidade && ultimoVoto < ultimoVotoEleito)
            ) {
                eleito              = candidato;
                maiorQuantidade     = quantidade;
                ultimoVotoEleito    = ultimoVoto;
            }
        }

        // Se não tiver eleito, retorna null -
        if (eleito == null) {
            return null;
        }

        // Percorre novamente pelos candidatos -
        for (Candidato candidato : acervo.getCandidatos()) {
            String situacao = candidato.getSituacao();

            // Valida se o candidato é da localidade e se a situação é uma das três válidas -
            if (
                candidato.getLocalidade().getCep().equals(cep)
                && (situacao.equals("ELEGIVEL") || situacao.equals("ELEITO")
                || situacao.equals("NAOELEITO"))
            ) {

                // Se o candidato for o eleito, seto como ELEITO -
                if (candidato == eleito) {
                    candidato.setSituacao("ELEITO");
                } else {
                    candidato.setSituacao("NAOELEITO");
                }
            }
        }

        // retorna o candidato eleito -
        return new CandidatoEleito(eleito.getNumero(), eleito.getNome(),
                eleito.getPartido().getNome(), maiorQuantidade);
    }

    @GetMapping("/listamaisvotadospartido")
    public List<MaisVotadoPartido> listarMaisVotadosPartido() {

        // Lista vazia -
        List<MaisVotadoPartido> resultado = new ArrayList<>();

        // Percorre pelos partidos -
        for (Partido partido : acervo.getPartidos()) {

            // Ja cria os var de controle -
            Candidato maisVotado = null;
            int maiorQuantidade  = 0;
            
            // Percorre pelos candidatos -
            for (Candidato candidato : acervo.getCandidatos()) {
                if (candidato.getPartido().getCodigo() != partido.getCodigo()) {
                    continue;
                }

                // Soma os votos validos -
                int quantidade = 0;
                for (Voto voto : acervo.getVotos()) {
                    if (voto.getNumeroCandidato() == candidato.getNumero() && voto.isValido()) {
                        quantidade++;
                    }
                }

                // Faz a selecao do mais votado do partido -
                if (maisVotado == null || quantidade > maiorQuantidade) {
                    maisVotado      = candidato;
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

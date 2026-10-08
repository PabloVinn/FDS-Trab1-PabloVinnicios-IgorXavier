package br.pucrs.trabalho1fds.demo;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/acmepolling/cadastro")
public class CandidatoController {

    private final List<Candidato> candidatos;
    private final Acervo acervo;

    public CandidatoController(Acervo acervo) {
        // Construtor básico com as instancias das dependencias -
        this.acervo     = acervo;
        this.candidatos = acervo.getCandidatos();
    }

    // As Classes modelo de retorno dos response.
    // O professor especificou o retorno lá no PDF
    public record CadastroCompleto(int numero, String nome, String situacao,
            String nome_partido, String nome_localidade) {}

    public record CandidatoResumo(int numero, String nome) {}

    @GetMapping("/listacandidatos")
    public List<CadastroCompleto> getCandidatos() {
        // Cria uma lista do resultado vazio -
        List<CadastroCompleto> resultado = new ArrayList<>();
        
        // Para cada candidato coloca na lista -
        for (Candidato candidato : candidatos) {
            resultado.add(new CadastroCompleto(
                    candidato.getNumero(), candidato.getNome(), candidato.getSituacao(),
                    candidato.getPartido().getNome(), candidato.getLocalidade().getNome()));
        }

        // retorna a lista de candidatos -
        return resultado;
    }

    @GetMapping("/listacandidatoslocalidade/{cep}/situação/{situacao}")
    public List<CandidatoResumo> getCandidatosLocalidade(
        @PathVariable("cep") String cep,
        @PathVariable("situacao") String situacao)
    {
        // Cria uma lista do resultado vazio -
        List<CandidatoResumo> resultado = new ArrayList<>();

        // Percorre pelos candidatos -
        for (Candidato candidato : candidatos) {
            if (
                    // Se o CEP eh igual ao pedido -
                    candidato.getLocalidade().getCep().equals(cep)
                    // E se a situacoa eh igual ao pedido -
                    && candidato.getSituacao().equals(situacao)) 
                {
                // Adiciona na lista de resultado o candidato resumido -
                resultado.add(new CandidatoResumo(candidato.getNumero(), candidato.getNome()));
            }
        }
        // retorna a lista de candidatos filtrados -
        return resultado;
    }

    // Classe para o body do POST, já que o professor quis q tivesse esses args.
    public record CadastroCandidato(Integer numero, String nome, Integer codigo, String cep) {}

    @PostMapping("/cadcandidato")
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

    @PutMapping("/atualizacandidato/{numero}/situacao/{status}")
    public CadastroCompleto atualizarCandidato(
            @PathVariable("numero") int numero,
            @PathVariable("status") String status) {
        
        // percorre pelos candidatos -
        for (Candidato candidato : candidatos) {

            // Valida se o numero do candidato é igual ao numero passado no path -
            if (candidato.getNumero() == numero) {
                candidato.setSituacao(status);

                return new CadastroCompleto(
                        candidato.getNumero(), candidato.getNome(), candidato.getSituacao(),
                        candidato.getPartido().getNome(), candidato.getLocalidade().getNome());
            }
        }
        return null;
    }

    @DeleteMapping("/removecandidato")
    public boolean removerCandidato(@RequestBody int numero) {
        // percorre pelos candidatos -
        for (Candidato candidato : candidatos) {
            if (candidato.getNumero() == numero) {
                // Remoção lógica: mantém o candidato na lista.
                candidato.setSituacao("REMOVIDO");
                return true;
            }
        }
        return false;
    }

}

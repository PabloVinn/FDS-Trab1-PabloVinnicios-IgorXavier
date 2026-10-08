package br.pucrs.trabalho1fds.demo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ApuracaoControllerTests {

    @Test
    public void consultarVotosCandidatoTest() {
        Acervo acervo = new Acervo();
        VotacaoController votacao = new VotacaoController(acervo);
        ApuracaoController controller = new ApuracaoController(acervo);
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(1, 10, 101, "90000-000"));
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(2, 7, 101, "90000-000"));

        ApuracaoController.VotosCandidato resultado = controller.consultarCandidato(101);

        assertEquals(1, resultado.quantidade_votos_validos());
        assertEquals(1, resultado.quantidade_votos_invalidos());
    }

    @Test
    public void consultarVotosLocalidadeTest() {
        Acervo acervo = new Acervo();
        VotacaoController votacao = new VotacaoController(acervo);
        ApuracaoController controller = new ApuracaoController(acervo);
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(1, 10, 101, "90000-000"));
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(2, 7, 202, "90000-000"));

        List<ApuracaoController.VotacaoCandidato> resultado =
                controller.consultarLocalidade("90000-000");

        assertEquals(2, resultado.size());
        assertEquals(101, resultado.get(0).numero());
        assertEquals(1, resultado.get(0).quantidade_votos_validos());
        assertEquals(202, resultado.get(1).numero());
        assertEquals(1, resultado.get(1).quantidade_votos_invalidos());
    }

    @Test
    public void consultarEleitoTest() {
        Acervo acervo = new Acervo();
        VotacaoController votacao = new VotacaoController(acervo);
        ApuracaoController controller = new ApuracaoController(acervo);
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(1, 9, 101, "90000-000"));
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(2, 10, 101, "90000-000"));
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(3, 11, 202, "90000-000"));

        ApuracaoController.CandidatoEleito resultado = controller.consultarEleito("90000-000");

        assertEquals(101, resultado.numero());
        assertEquals(2, resultado.quantidade_total_votos());
        assertEquals("ELEITO", acervo.getCandidatos().get(0).getSituacao());
        assertEquals("NAOELEITO", acervo.getCandidatos().get(1).getSituacao());
    }

    @Test
    public void desempateEleitoTest() {
        Acervo acervo = new Acervo();
        VotacaoController votacao = new VotacaoController(acervo);
        ApuracaoController controller = new ApuracaoController(acervo);
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(1, 9, 101, "90000-000"));
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(2, 10, 101, "90000-000"));
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(3, 8, 202, "90000-000"));
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(4, 11, 202, "90000-000"));

        // Os dois têm dois votos; o último voto de 101 foi mais cedo.
        ApuracaoController.CandidatoEleito resultado = controller.consultarEleito("90000-000");

        assertEquals(101, resultado.numero());
    }

    @Test
    public void consultarEleitoSemVotosTest() {
        Acervo acervo = new Acervo();
        ApuracaoController controller = new ApuracaoController(acervo);

        assertNull(controller.consultarEleito("90000-000"));
    }

    @Test
    public void listarMaisVotadosPartidoTest() {
        Acervo acervo = new Acervo();
        VotacaoController votacao = new VotacaoController(acervo);
        ApuracaoController controller = new ApuracaoController(acervo);
        acervo.getCandidatos().add(new Candidato(404, "Carolina", "ELEGIVEL",
                acervo.getPartidos().get(0), acervo.getLocalidades().get(2)));
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(1, 10, 101, "90000-000"));
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(2, 10, 404, "92000-000"));
        votacao.cadastrarVoto(new VotacaoController.CadastroVoto(3, 11, 404, "92000-000"));

        List<ApuracaoController.MaisVotadoPartido> resultado =
                controller.listarMaisVotadosPartido();

        assertEquals(3, resultado.size());
        assertEquals("Partido da Computação", resultado.get(0).nome_partido());
        assertEquals(404, resultado.get(0).numero());
        assertEquals(2, resultado.get(0).quantidade_votos_validos());
    }
}

package br.pucrs.trabalho1fds.demo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CandidatoControllerTests {

    @Test
    public void listarCandidatosTest() {
        Acervo acervo = new Acervo();
        CandidatoController controller = new CandidatoController(acervo);

        List<CandidatoController.CadastroCompleto> resultado = controller.getCandidatos();

        assertEquals(3, resultado.size());
        assertEquals("Agostine", resultado.get(0).nome());
        assertEquals("Partido da Computação", resultado.get(0).nome_partido());
        assertEquals("Porto Alegre", resultado.get(0).nome_localidade());
    }

    @Test
    public void listarCandidatosLocalidadeTest() {
        Acervo acervo = new Acervo();
        CandidatoController controller = new CandidatoController(acervo);

        List<CandidatoController.CandidatoResumo> resultado =
                controller.getCandidatosLocalidade("90000-000", "ELEGIVEL");

        assertEquals(2, resultado.size());
        assertEquals(101, resultado.get(0).numero());
        assertEquals(202, resultado.get(1).numero());
    }

    @Test
    public void listarLocalidadeSemCandidatosTest() {
        Acervo acervo = new Acervo();
        CandidatoController controller = new CandidatoController(acervo);

        assertEquals(0, controller.getCandidatosLocalidade("92000-000", "ELEGIVEL").size());
    }

    @Test
    public void cadastrarCandidatoTest() {
        Acervo acervo = new Acervo();
        CandidatoController controller = new CandidatoController(acervo);
        CandidatoController.CadastroCandidato dados =
                new CandidatoController.CadastroCandidato(404, "Carolina", 10, "92000-000");

        boolean resultado = controller.cadastrarCandidato(dados);

        assertTrue(resultado);
        assertEquals(4, acervo.getCandidatos().size());
        assertEquals("PRECANDIDATO", acervo.getCandidatos().get(3).getSituacao());
    }

    @Test
    public void cadastrarNumeroRepetidoTest() {
        Acervo acervo = new Acervo();
        CandidatoController controller = new CandidatoController(acervo);
        CandidatoController.CadastroCandidato dados =
                new CandidatoController.CadastroCandidato(101, "Carolina", 10, "92000-000");

        assertFalse(controller.cadastrarCandidato(dados));
        assertEquals(3, acervo.getCandidatos().size());
    }

    @Test
    public void atualizarSituacaoTest() {
        Acervo acervo = new Acervo();
        CandidatoController controller = new CandidatoController(acervo);

        CandidatoController.CadastroCompleto resultado =
                controller.atualizarCandidato(101, "INELEGIVEL");

        assertEquals("INELEGIVEL", resultado.situacao());
        assertEquals("INELEGIVEL", acervo.getCandidatos().get(0).getSituacao());
    }

    @Test
    public void bloquearSituacaoInvalidaTest() {
        Acervo acervo = new Acervo();
        CandidatoController controller = new CandidatoController(acervo);

        assertNull(controller.atualizarCandidato(101, "INVALIDO"));
        assertEquals("ELEGIVEL", acervo.getCandidatos().get(0).getSituacao());
    }

    @Test
    public void naoReativarCandidatoRemovidoTest() {
        Acervo acervo = new Acervo();
        CandidatoController controller = new CandidatoController(acervo);
        acervo.getCandidatos().get(0).setSituacao("REMOVIDO");

        assertNull(controller.atualizarCandidato(101, "ELEGIVEL"));
        assertEquals("REMOVIDO", acervo.getCandidatos().get(0).getSituacao());
    }

    @Test
    public void removerCandidatoTest() {
        Acervo acervo = new Acervo();
        CandidatoController controller = new CandidatoController(acervo);
        acervo.getCandidatos().get(0).setSituacao("INELEGIVEL");

        assertTrue(controller.removerCandidato(101));
        assertEquals("REMOVIDO", acervo.getCandidatos().get(0).getSituacao());
        assertEquals(3, acervo.getCandidatos().size());
    }

    @Test
    public void removerCandidatoInexistenteTest() {
        Acervo acervo = new Acervo();
        CandidatoController controller = new CandidatoController(acervo);

        assertFalse(controller.removerCandidato(999));
    }
}

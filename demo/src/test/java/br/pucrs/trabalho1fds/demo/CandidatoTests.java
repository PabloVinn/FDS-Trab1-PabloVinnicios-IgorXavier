package br.pucrs.trabalho1fds.demo;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class CandidatoTests {

    @Test
    public void listarCandidatosTest() {
        DemoController controller = new DemoController(new Acervo());
        var resultado = controller.listaCandidatos();
        assertEquals(3, resultado.size());
        assertEquals("Agostine", resultado.get(0).get("nome"));
        assertEquals("Partido da Computação", resultado.get(0).get("nome_partido"));
        assertEquals("Porto Alegre", resultado.get(0).get("nome_localidade"));
    }

    @Test
    public void filtrarLocalidadeSituacaoTest() {
        DemoController controller = new DemoController(new Acervo());
        var resultado = controller.listaCandidatosLocalidadeSituacao("90000-000", "ELEGIVEL");
        assertEquals(2, resultado.size());
        assertEquals(101, resultado.get(0).get("numero"));
        assertEquals(202, resultado.get(1).get("numero"));
    }

    @Test
    public void localidadeSemCandidatosTest() {
        DemoController controller = new DemoController(new Acervo());
        assertEquals(0, controller.listaCandidatosLocalidadeSituacao("92000-000", "ELEGIVEL").size());
    }

    @Test
    public void cadastrarPrecandidatoTest() {
        Acervo acervo = new Acervo();
        DemoController controller = new DemoController(acervo);
        assertTrue(controller.cadCandidato(Map.of("numero", 404, "nome", "Ana", "codigo", 10, "cep", "92000-000")));
        assertEquals("PRECANDIDATO", acervo.buscarCandidato(404).getSituacao());
        assertEquals(4, controller.listaCandidatos().size());
    }

    @Test
    public void numeroRepetidoTest() {
        DemoController controller = new DemoController(new Acervo());
        assertFalse(controller.cadCandidato(Map.of("numero", 101, "nome", "Ana", "codigo", 10, "cep", "92000-000")));
        assertEquals(3, controller.listaCandidatos().size());
    }

    @Test
    public void partidoInexistenteTest() {
        DemoController controller = new DemoController(new Acervo());
        assertFalse(controller.cadCandidato(Map.of("numero", 404, "nome", "Ana", "codigo", 99, "cep", "92000-000")));
    }

    @Test
    public void aprovarPrecandidatoTest() {
        DemoController controller = new DemoController(new Acervo());
        controller.cadCandidato(Map.of("numero", 404, "nome", "Ana", "codigo", 10, "cep", "92000-000"));
        assertEquals("ELEGIVEL", controller.atualizaCandidato(404, "ELEGIVEL").get("situacao"));
    }

    @Test
    public void bloquearSituacaoInvalidaTest() {
        Acervo acervo = new Acervo();
        DemoController controller = new DemoController(acervo);
        assertNull(controller.atualizaCandidato(101, "BANANA"));
        assertEquals("ELEGIVEL", acervo.buscarCandidato(101).getSituacao());
    }

    @Test
    public void naoRemoverElegivelTest() {
        DemoController controller = new DemoController(new Acervo());
        assertFalse(controller.removeCandidato(101));
    }

    @Test
    public void removerLogicamenteTest() {
        Acervo acervo = new Acervo();
        DemoController controller = new DemoController(acervo);
        controller.atualizaCandidato(101, "INELEGIVEL");
        assertTrue(controller.removeCandidato(101));
        assertEquals("REMOVIDO", acervo.buscarCandidato(101).getSituacao());
        assertEquals(3, controller.listaCandidatos().size());
    }

    @Test
    public void naoReativarRemovidoTest() {
        DemoController controller = new DemoController(new Acervo());
        controller.atualizaCandidato(101, "INELEGIVEL");
        controller.removeCandidato(101);
        assertNull(controller.atualizaCandidato(101, "ELEGIVEL"));
    }

    @Test
    public void candidatoInexistenteTest() {
        DemoController controller = new DemoController(new Acervo());
        assertNull(controller.atualizaCandidato(999, "ELEGIVEL"));
        assertFalse(controller.removeCandidato(999));
    }
}

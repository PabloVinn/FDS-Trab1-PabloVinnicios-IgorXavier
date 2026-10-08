package br.pucrs.trabalho1fds.demo;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class VotacaoTests {

    @Test
    public void horariosLimiteTest() {
        DemoController controller = new DemoController(new Acervo());
        assertTrue(controller.cadVoto(Map.of("id", 1, "hora", 8, "numero", 101, "cep", "90000-000")));
        assertTrue(controller.cadVoto(Map.of("id", 2, "hora", 17, "numero", 101, "cep", "90000-000")));
        assertEquals(2L, controller.consultaCandidato(101).get("quantidade de votos válidos"));
    }

    @Test
    public void horarioForaPeriodoTest() {
        DemoController controller = new DemoController(new Acervo());
        assertTrue(controller.cadVoto(Map.of("id", 1, "hora", 7, "numero", 101, "cep", "90000-000")));
        assertTrue(controller.cadVoto(Map.of("id", 2, "hora", 18, "numero", 101, "cep", "90000-000")));
        assertEquals(2L, controller.consultaCandidato(101).get("quantidade de votos inválidos"));
    }

    @Test
    public void horaFracionadaTest() {
        DemoController controller = new DemoController(new Acervo());
        controller.cadVoto(Map.of("id", 1, "hora", 9.5, "numero", 101, "cep", "90000-000"));
        assertEquals(1L, controller.consultaCandidato(101).get("quantidade de votos válidos"));
    }

    @Test
    public void idRepetidoTest() {
        DemoController controller = new DemoController(new Acervo());
        var voto = Map.<String, Object>of("id", 1, "hora", 10, "numero", 101, "cep", "90000-000");
        assertTrue(controller.cadVoto(voto));
        assertFalse(controller.cadVoto(voto));
        assertEquals(1L, controller.consultaCandidato(101).get("quantidade de votos válidos"));
    }

    @Test
    public void numeroInexistenteTest() {
        DemoController controller = new DemoController(new Acervo());
        assertTrue(controller.cadVoto(Map.of("id", 1, "hora", 10, "numero", 999, "cep", "90000-000")));
        assertEquals(1L, controller.consultaCandidato(999).get("quantidade de votos inválidos"));
        assertEquals(0L, controller.consultaCandidato(999).get("quantidade de votos válidos"));
    }

    @Test
    public void candidatoInelegivelTest() {
        DemoController controller = new DemoController(new Acervo());
        controller.atualizaCandidato(101, "INELEGIVEL");
        controller.cadVoto(Map.of("id", 1, "hora", 10, "numero", 101, "cep", "90000-000"));
        assertEquals(1L, controller.consultaCandidato(101).get("quantidade de votos inválidos"));
    }

    @Test
    public void localidadeDiferenteTest() {
        DemoController controller = new DemoController(new Acervo());
        controller.cadVoto(Map.of("id", 1, "hora", 10, "numero", 101, "cep", "91000-000"));
        assertEquals(1L, controller.consultaCandidato(101).get("quantidade de votos inválidos"));
    }

    @Test
    public void excessoNaoInvalidaVotosAnterioresTest() {
        Acervo acervo = new Acervo();
        acervo.buscarLocalidade("90000-000").setQtdEleitores(1);
        DemoController controller = new DemoController(acervo);
        controller.cadVoto(Map.of("id", 1, "hora", 10, "numero", 101, "cep", "90000-000"));
        controller.cadVoto(Map.of("id", 2, "hora", 11, "numero", 101, "cep", "90000-000"));
        assertEquals(1L, controller.consultaCandidato(101).get("quantidade de votos válidos"));
        assertEquals(1L, controller.consultaCandidato(101).get("quantidade de votos inválidos"));
    }

    @Test
    public void cepInexistenteTest() {
        DemoController controller = new DemoController(new Acervo());
        assertFalse(controller.cadVoto(Map.of("id", 1, "hora", 10, "numero", 101, "cep", "00000-000")));
    }

    @Test
    public void consultaAposApuracaoPreservaContagemTest() {
        DemoController controller = new DemoController(new Acervo());
        controller.cadVoto(Map.of("id", 1, "hora", 10, "numero", 101, "cep", "90000-000"));
        controller.consultaEleito("90000-000");
        assertEquals(1L, controller.consultaCandidato(101).get("quantidade de votos válidos"));
    }
}

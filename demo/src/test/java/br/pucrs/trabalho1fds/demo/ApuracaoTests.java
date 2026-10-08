package br.pucrs.trabalho1fds.demo;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class ApuracaoTests {

    @Test
    public void contarVotosTest() {
        DemoController controller = new DemoController(new Acervo());
        controller.cadVoto(Map.of("id", 1, "hora", 10, "numero", 101, "cep", "90000-000"));
        controller.cadVoto(Map.of("id", 2, "hora", 7, "numero", 101, "cep", "90000-000"));
        assertEquals(1L, controller.consultaCandidato(101).get("quantidade de votos válidos"));
        assertEquals(1L, controller.consultaCandidato(101).get("quantidade de votos inválidos"));
    }

    @Test
    public void consultarLocalidadeTest() {
        DemoController controller = new DemoController(new Acervo());
        controller.cadVoto(Map.of("id", 1, "hora", 10, "numero", 101, "cep", "90000-000"));
        var resultado = controller.consultaLocalidade("90000-000");
        assertEquals(2, resultado.size());
        assertEquals(101, resultado.get(0).get("numero"));
        assertEquals(1L, resultado.get(0).get("quantidade de votos válidos"));
        assertEquals(0L, resultado.get(1).get("quantidade de votos válidos"));
    }

    @Test
    public void elegerMaioriaEAtualizarSituacoesTest() {
        Acervo acervo = new Acervo();
        DemoController controller = new DemoController(acervo);
        controller.cadVoto(Map.of("id", 1, "hora", 10, "numero", 101, "cep", "90000-000"));
        controller.cadVoto(Map.of("id", 2, "hora", 11, "numero", 101, "cep", "90000-000"));
        controller.cadVoto(Map.of("id", 3, "hora", 9, "numero", 202, "cep", "90000-000"));
        assertEquals(101, controller.consultaEleito("90000-000").get("numero"));
        assertEquals("ELEITO", acervo.buscarCandidato(101).getSituacao());
        assertEquals("NAOELEITO", acervo.buscarCandidato(202).getSituacao());
        assertEquals("ELEGIVEL", acervo.buscarCandidato(303).getSituacao());
        assertEquals(2L, controller.consultaEleito("90000-000").get("quantidade total de votos"));
    }

    @Test
    public void desempateComVotosForaDeOrdemTest() {
        DemoController controller = new DemoController(new Acervo());
        controller.cadVoto(Map.of("id", 1, "hora", 10, "numero", 101, "cep", "90000-000"));
        controller.cadVoto(Map.of("id", 2, "hora", 9, "numero", 101, "cep", "90000-000"));
        controller.cadVoto(Map.of("id", 3, "hora", 9.5, "numero", 202, "cep", "90000-000"));
        controller.cadVoto(Map.of("id", 4, "hora", 9.25, "numero", 202, "cep", "90000-000"));
        assertEquals(202, controller.consultaEleito("90000-000").get("numero"));
    }

    @Test
    public void semVotosNaoTemEleitoTest() {
        DemoController controller = new DemoController(new Acervo());
        assertNull(controller.consultaEleito("90000-000"));
        assertNull(controller.consultaEleito("00000-000"));
    }

    @Test
    public void maisVotadoPorPartidoTest() {
        DemoController controller = new DemoController(new Acervo());
        controller.cadCandidato(Map.of("numero", 404, "nome", "Ana", "codigo", 10, "cep", "92000-000"));
        controller.atualizaCandidato(404, "ELEGIVEL");
        controller.cadVoto(Map.of("id", 1, "hora", 10, "numero", 101, "cep", "90000-000"));
        controller.cadVoto(Map.of("id", 2, "hora", 10, "numero", 404, "cep", "92000-000"));
        controller.cadVoto(Map.of("id", 3, "hora", 11, "numero", 404, "cep", "92000-000"));
        var resultado = controller.listaMaisVotadosPartido();
        assertEquals(3, resultado.size());
        assertEquals(404, resultado.get(0).get("numero"));
        assertEquals(2L, resultado.get(0).get("quantidade de votos válidos"));
    }

    @Test
    public void inelegivelNaoPodeSerEleitoTest() {
        DemoController controller = new DemoController(new Acervo());
        controller.cadVoto(Map.of("id", 1, "hora", 10, "numero", 101, "cep", "90000-000"));
        controller.atualizaCandidato(101, "INELEGIVEL");
        assertNull(controller.consultaEleito("90000-000"));
    }
}

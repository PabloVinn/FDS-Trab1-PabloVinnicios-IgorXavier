package br.pucrs.trabalho1fds.demo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class VotacaoControllerTests {

    @Test
    public void cadastrarVotoTest() {
        Acervo acervo = new Acervo();
        VotacaoController controller = new VotacaoController(acervo);
        VotacaoController.CadastroVoto dados =
                new VotacaoController.CadastroVoto(1, 10, 101, "90000-000");

        assertTrue(controller.cadastrarVoto(dados));
        assertEquals(1, acervo.getVotos().size());
        assertTrue(acervo.getVotos().get(0).isValido());
    }

    @Test
    public void votoForaDoHorarioTest() {
        Acervo acervo = new Acervo();
        VotacaoController controller = new VotacaoController(acervo);
        VotacaoController.CadastroVoto dados =
                new VotacaoController.CadastroVoto(1, 7, 101, "90000-000");

        // O voto é registrado, mas fica marcado como inválido.
        assertTrue(controller.cadastrarVoto(dados));
        assertFalse(acervo.getVotos().get(0).isValido());
    }

    @Test
    public void votoIdRepetidoTest() {
        Acervo acervo = new Acervo();
        VotacaoController controller = new VotacaoController(acervo);
        VotacaoController.CadastroVoto dados =
                new VotacaoController.CadastroVoto(1, 10, 101, "90000-000");
        controller.cadastrarVoto(dados);

        assertFalse(controller.cadastrarVoto(dados));
        assertEquals(1, acervo.getVotos().size());
    }

    @Test
    public void votoCandidatoInexistenteTest() {
        Acervo acervo = new Acervo();
        VotacaoController controller = new VotacaoController(acervo);
        VotacaoController.CadastroVoto dados =
                new VotacaoController.CadastroVoto(1, 10, 999, "90000-000");
        controller.cadastrarVoto(dados);

        assertFalse(acervo.getVotos().get(0).isValido());
        assertEquals(999, acervo.getVotos().get(0).getNumeroCandidato());
    }

    @Test
    public void votoCandidatoInelegivelTest() {
        Acervo acervo = new Acervo();
        VotacaoController controller = new VotacaoController(acervo);
        acervo.getCandidatos().get(0).setSituacao("INELEGIVEL");
        VotacaoController.CadastroVoto dados =
                new VotacaoController.CadastroVoto(1, 10, 101, "90000-000");
        controller.cadastrarVoto(dados);

        assertFalse(acervo.getVotos().get(0).isValido());
    }

    @Test
    public void votoOutraLocalidadeTest() {
        Acervo acervo = new Acervo();
        VotacaoController controller = new VotacaoController(acervo);
        VotacaoController.CadastroVoto dados =
                new VotacaoController.CadastroVoto(1, 10, 101, "91000-000");
        controller.cadastrarVoto(dados);

        assertFalse(acervo.getVotos().get(0).isValido());
    }

    @Test
    public void votoAcimaDoLimiteTest() {
        Acervo acervo = new Acervo();
        VotacaoController controller = new VotacaoController(acervo);
        acervo.getLocalidades().get(0).setQtdEleitores(1);

        controller.cadastrarVoto(new VotacaoController.CadastroVoto(1, 10, 101, "90000-000"));
        controller.cadastrarVoto(new VotacaoController.CadastroVoto(2, 11, 101, "90000-000"));

        assertTrue(acervo.getVotos().get(0).isValido());
        assertFalse(acervo.getVotos().get(1).isValido());
    }
}

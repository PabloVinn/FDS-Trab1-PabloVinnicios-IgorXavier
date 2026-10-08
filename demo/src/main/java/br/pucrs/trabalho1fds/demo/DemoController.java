package br.pucrs.trabalho1fds.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DemoController.BASE_URL)
public class DemoController {

    @GetMapping({"", "/"})
    public String getMensagemInicial() {
        return "Aplicacao Spring-Boot funcionando!";
    }
}

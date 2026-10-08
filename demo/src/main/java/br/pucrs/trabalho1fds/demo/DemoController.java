package br.pucrs.trabalho1fds.demo;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/acmepolling")
public class DemoController {
 @GetMapping("/")
 public String getMensagemInicial() {
 return "Aplicacao Spring-Boot funcionando!";
 }

}
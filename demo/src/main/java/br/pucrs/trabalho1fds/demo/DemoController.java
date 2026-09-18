package br.pucrs.trabalho1fds.demo;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class ExemploController {
 @GetMapping("/")
 public String getMensagemInicial() {
 return "Aplicacao Spring-Boot funcionando!";
 }

}
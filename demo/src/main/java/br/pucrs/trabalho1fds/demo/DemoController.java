package br.pucrs.trabalho1fds.demo;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/acmepolling")
public class DemoController{
    private final Acervo acervo;

    public DemoController(Acervo acervo) {
        this.acervo = acervo;
    }
    
    //1
    @GetMapping("/cadastro/listacandidatos")
    public List<Map<String, Object>> listaCandidatos() {
        return acervo.listarCandidatos();
    }

    //2
    @GetMapping("/cadastro/listacandidatoslocalidade/{cep}/situacao/{situacao}")
    public List<Map<String, Object>> listaCandidatosLocalidadeSituacao(@PathVariable String cep, @PathVariable String situacao) {
        return acervo.listarCandidatosLocalidadeSituacao(cep, situacao);
    }

    //3
    @PostMapping("/cadastro/cadcandidato")
    public boolean cadCandidato(@RequestBody Map<String, Object> payload) {
        return acervo.cadastrarCandidato(payload);
    }

    //4
    @PostMapping("/votacao/cadvoto")
    public boolean cadVoto(@RequestBody Map<String, Object> payload) {
        return acervo.cadastrarVoto(payload);
    }

    //5
    @GetMapping("/apuracao/consultaeleito")
    public Map<String, Object> consultaEleito(@RequestParam String cep) {
        return acervo.consultarEleito(cep);
    }

    //6
    @GetMapping("/apuracao/consultacandidato")
    public Map<String, Object> consultaCandidato(@RequestParam int numero) {
        return acervo.consultarCandidato(numero);
    }

    //7
    @GetMapping("/apuracao/consultalocalidade")
    public List<Map<String, Object>> consultaLocalidade(@RequestParam String cep) {
        return acervo.consultarLocalidade(cep);
    }

    //8
    @GetMapping("/apuracao/listamaisvotadospartido")
    public List<Map<String, Object>> listaMaisVotadosPartido() {
        return acervo.listarMaisVotadosPartido();
    }

    //9
    @PutMapping("/cadastro/atualizacandidato/{numero}/situacao/{status}")
    public Map<String, Object> atualizaCandidato(@PathVariable int numero, @PathVariable String status) {
        return acervo.atualizarSituacao(numero, status);
    }

    //10
    @DeleteMapping("/cadastro/removecandidato")
    public boolean removeCandidato(@RequestBody Map<String, Object> payload) {
        int numero = Integer.parseInt(payload.get("numero").toString());
        return acervo.removerCandidato(numero);
    }




}
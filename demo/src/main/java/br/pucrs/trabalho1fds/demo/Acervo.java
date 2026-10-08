package br.pucrs.trabalho1fds.demo;

import java.util.*;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

@Component
public class Acervo {
    private List<Partido> partidos = new ArrayList<>();
    private List<Localidade> localidades = new ArrayList<>();
    private List<Candidato> candidatos = new ArrayList<>();
    private List<Voto> votos = new ArrayList<>();

    public Acervo() {
        Partido p1 = new Partido(10, "Partido da Computação");
        Partido p2 = new Partido(20, "Partido da Engenharia");
        Partido p3 = new Partido(30, "Partido dos Dados");
        partidos.addAll(Arrays.asList(p1, p2, p3));

        Localidade l1 = new Localidade("90000-000", "Porto Alegre", 100);
        Localidade l2 = new Localidade("91000-000", "Canoas", 50);
        Localidade l3 = new Localidade("92000-000", "Pelotas", 80);
        localidades.addAll(Arrays.asList(l1, l2, l3));

        
        candidatos.add(new Candidato(101, "Agostine", "ELEGIVEL", p1, l1));
        candidatos.add(new Candidato(202, "Marlon", "ELEGIVEL", p2, l1));
        candidatos.add(new Candidato(303, "Roberta", "ELEGIVEL", p3, l2));
    }

    public boolean ehVotoValido(Voto voto) {
        if (voto == null || voto.getLocalidade() == null) return false;

        // Horário permitido: entre 8:00 e 17:00
        if (voto.getHora() < 8 || voto.getHora() > 17) return false;

        // Candidato deve existir e estar ELEGIVEL
        Candidato c = voto.getCandidato();
        if (c == null) return false;
        if (!"ELEGIVEL".equalsIgnoreCase(c.getSituacao())) return false;

        // Candidato deve ser da mesma localidade do voto
        if (!c.getLocalidade().getCep().equalsIgnoreCase(voto.getLocalidade().getCep())) return false;

        // Quantidade total de votos na localidade não pode exceder o total de eleitores
        long votosNaLocalidade = votos.stream()
                .filter(v -> v.getLocalidade().getCep().equalsIgnoreCase(voto.getLocalidade().getCep()))
                .count();
        int posicaoVoto = votos.indexOf(voto) + 1;
        if (posicaoVoto > voto.getLocalidade().getQtdEleitores()) return false;

        return true;
    }

    public List<Candidato> getCandidatos() {
        // Aqui é para pegar os candidatos ja criado em acervos
        return candidatos;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public List<Localidade> getLocalidades() {
        return localidades;
    }
    
}

package br.pucrs.trabalho1fds.demo;

import java.util.*;
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
        partidos.add(p1);
        partidos.add(p2);
        partidos.add(p3);

        Localidade l1 = new Localidade("90000-000", "Porto Alegre", 100);
        Localidade l2 = new Localidade("91000-000", "Canoas", 50);
        Localidade l3 = new Localidade("92000-000", "Pelotas", 80);
        localidades.add(l1);
        localidades.add(l2);
        localidades.add(l3);

        
        candidatos.add(new Candidato(101, "Agostine", "ELEGIVEL", p1, l1));
        candidatos.add(new Candidato(202, "Marlon", "ELEGIVEL", p2, l1));
        candidatos.add(new Candidato(303, "Roberta", "ELEGIVEL", p3, l2));
    }

    public Candidato buscarCandidato(int numero) {
        for (Candidato c : candidatos) {
            if (c.getNumero() == numero) {
                return c;
            }
        }
        return null;
    }

    public Partido buscarPartido(int codigo) {
        for (Partido p : partidos) {
            if (p.getCodigo() == codigo) {
                return p;
            }
        }
        return null;
    }

    public Localidade buscarLocalidade(String cep) {
        for (Localidade l : localidades) {
            if (l.getCep().equalsIgnoreCase(cep)) {
                return l;
            }
        }
        return null;
    }

    public boolean ehVotoValido(Voto voto) {
        if (voto == null || voto.getLocalidade() == null || voto.getCandidato() == null) {
            return false;
        }

        
        if (voto.getHora() < 8 || voto.getHora() > 17) {
            return false;
        }

        Candidato c = voto.getCandidato();

        
        if (!"ELEGIVEL".equalsIgnoreCase(c.getSituacao())) {
            return false;
        }

        
        if (!c.getLocalidade().getCep().equalsIgnoreCase(voto.getLocalidade().getCep())) {
            return false;
        }

        
        int totalVotosNaLocalidade = 0;
        for (Voto v : votos) {
            if (v.getLocalidade().getCep().equalsIgnoreCase(voto.getLocalidade().getCep())) {
                totalVotosNaLocalidade++;
            }
        }

        // O novo voto ainda não está na lista. Os anteriores mantêm sua classificação.
        // Se a quantidade já chegou no limite, o próximo voto será inválido -
        if (totalVotosNaLocalidade >= voto.getLocalidade().getQtdEleitores()) {
            return false;
        }

        return true;
    }


    //1 endpoint
    public List<Map<String, Object>> listarCandidatos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Candidato c : candidatos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("numero", c.getNumero());
            map.put("nome", c.getNome());
            map.put("situacao", c.getSituacao());
            map.put("nome_partido", c.getPartido().getNome());
            map.put("nome_localidade", c.getLocalidade().getNome());
            lista.add(map);
        }
        return lista;
    }

    //2 endpoint
    public List<Map<String, Object>> listarCandidatosLocalidadeSituacao(String cep, String situacao) {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Candidato c : candidatos) {
            if (c.getLocalidade().getCep().equalsIgnoreCase(cep) && c.getSituacao().equalsIgnoreCase(situacao)) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("numero", c.getNumero());
                map.put("nome", c.getNome());
                lista.add(map);
            }
        }
        return lista;
    }

    //3 endpoint
    public boolean cadastrarCandidato(Map<String, Object> payload) {
        int numero = Integer.parseInt(payload.get("numero").toString());
        String nome = payload.get("nome").toString();
        int codigoPartido = Integer.parseInt(payload.get("codigo").toString());
        String cep = payload.get("cep").toString();

        if (buscarCandidato(numero) != null) {
            return false;
        }

        Partido p = buscarPartido(codigoPartido);
        Localidade l = buscarLocalidade(cep);

        if (p == null || l == null) {
            return false;
        }

        candidatos.add(new Candidato(numero, nome, "PRECANDIDATO", p, l));
        return true;
    }

    //4 endpoint
    public boolean cadastrarVoto(Map<String, Object> payload) {
        int id = Integer.parseInt(payload.get("id").toString());
        double hora = Double.parseDouble(payload.get("hora").toString());
        int numero = Integer.parseInt(payload.get("numero").toString());
        String cep = payload.get("cep").toString();

        // Percorre pelos votos para verificar se esse ID já foi cadastrado -
        for (Voto voto : votos) {
            if (voto.getId() == id) {
                return false;
            }
        }

        Localidade l = buscarLocalidade(cep);
        Candidato c = buscarCandidato(numero);

        if (l == null) {
            return false;
        }

        // Guarda o número mesmo quando o candidato não existe.
        Voto v = new Voto(id, hora, numero, c, l);
        // Salva se o voto é válido antes de colocar na lista -
        v.setValido(ehVotoValido(v));
        //NOTE: O voto inválido também fica salvo para aparecer nas consultas -
        votos.add(v);
        return true;
    }

    //5 endpoint
    public Map<String, Object> consultarEleito(String cep) {
        Candidato eleito = null;
        // Começa com zero para não eleger um candidato que não recebeu votos válidos -
        long maxVotosValidos = 0;
        double horaUltimoVotoMaisCedo = 17;

        for (Candidato c : candidatos) {
            String situacao = c.getSituacao();
            // Os estados finais permitem repetir a consulta de uma apuração.
            if (c.getLocalidade().getCep().equalsIgnoreCase(cep)
                    && ("ELEGIVEL".equalsIgnoreCase(situacao)
                    || "ELEITO".equalsIgnoreCase(situacao)
                    || "NAOELEITO".equalsIgnoreCase(situacao))) {
                long votosValidos = 0;
                // Guarda o maior horário, mesmo se os votos vierem fora de ordem -
                double ultimoVotoHora = 0;

                for (Voto v : votos) {
                    if (v.getCandidato() != null && v.getCandidato().getNumero() == c.getNumero()) {
                        // Usa a validade salva no cadastro, sem validar tudo novamente -
                        if (v.isValido()) {
                            votosValidos++;
                            // Se esse voto veio mais tarde, atualiza o último horário -
                            if (v.getHora() > ultimoVotoHora) {
                                ultimoVotoHora = v.getHora();
                            }
                        }
                    }
                }

                if (votosValidos > maxVotosValidos) {
                    maxVotosValidos = votosValidos;
                    horaUltimoVotoMaisCedo = ultimoVotoHora;
                    eleito = c;
                } else if (votosValidos == maxVotosValidos && votosValidos > 0) {
                    // Criterio de desempate: último voto mais cedo
                    if (ultimoVotoHora < horaUltimoVotoMaisCedo) {
                        horaUltimoVotoMaisCedo = ultimoVotoHora;
                        eleito = c;
                    }
                }
            }
        }

        if (eleito == null) {
            return null;
        }

        // Percorre pelos candidatos para colocar o resultado da apuração -
        for (Candidato c : candidatos) {
            String situacao = c.getSituacao();
            if (c.getLocalidade().getCep().equalsIgnoreCase(cep)
                    && ("ELEGIVEL".equalsIgnoreCase(situacao)
                    || "ELEITO".equalsIgnoreCase(situacao)
                    || "NAOELEITO".equalsIgnoreCase(situacao))) {
                if (c == eleito) {
                    // O vencedor dessa localidade fica como ELEITO -
                    c.setSituacao("ELEITO");
                } else {
                    // Os outros candidatos aptos dessa localidade ficam como NAOELEITO -
                    c.setSituacao("NAOELEITO");
                }
            }
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("numero", eleito.getNumero());
        res.put("nome", eleito.getNome());
        res.put("nome_partido", eleito.getPartido().getNome());
        res.put("quantidade total de votos", maxVotosValidos);
        return res;
    }

    //6 endpoint
    public Map<String, Object> consultarCandidato(int numero) {
        long validos = 0;
        long invalidos = 0;

        for (Voto v : votos) {
            // Compara pelo número informado, mesmo se o candidato não existir -
            if (v.getNumeroCandidato() == numero) {
                // Separa os válidos e inválidos usando o resultado salvo no voto -
                if (v.isValido()) {
                    validos++;
                } else {
                    invalidos++;
                }
            }
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("quantidade de votos válidos", validos);
        res.put("quantidade de votos inválidos", invalidos);
        return res;
    }


    //7 endpoint
    public List<Map<String, Object>> consultarLocalidade(String cep) {
        List<Map<String, Object>> lista = new ArrayList<>();

        for (Candidato c : candidatos) {
            if (c.getLocalidade().getCep().equalsIgnoreCase(cep)) {
                long validos = 0;
                long invalidos = 0;

                for (Voto v : votos) {
                    // Filtra pelo número do candidato dessa localidade -
                    if (v.getNumeroCandidato() == c.getNumero()) {
                        // Mantém a classificação feita quando o voto foi cadastrado -
                        if (v.isValido()) {
                            validos++;
                        } else {
                            invalidos++;
                        }
                    }
                }

                Map<String, Object> map = new LinkedHashMap<>();
                map.put("numero", c.getNumero());
                map.put("nome", c.getNome());
                map.put("quantidade de votos válidos", validos);
                map.put("quantidade de votos inválidos", invalidos);
                lista.add(map);
            }
        }
        return lista;
    }

    //8 endpoint
    public List<Map<String, Object>> listarMaisVotadosPartido() {
        List<Map<String, Object>> lista = new ArrayList<>();

        for (Partido p : partidos) {
            Candidato maisVotado = null;
            long maxValidos = -1;

            for (Candidato c : candidatos) {
                if (c.getPartido().getCodigo() == p.getCodigo()) {
                    long validos = 0;

                    for (Voto v : votos) {
                        if (v.getNumeroCandidato() == c.getNumero()) {
                            // Soma apenas os votos que foram salvos como válidos -
                            if (v.isValido()) {
                                validos++;
                            }
                        }
                    }

                    if (validos > maxValidos) {
                        maxValidos = validos;
                        maisVotado = c;
                    }
                }
            }

            if (maisVotado != null) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("nome_partido", p.getNome());
                map.put("numero", maisVotado.getNumero());
                map.put("nome", maisVotado.getNome());
                map.put("quantidade de votos válidos", maxValidos);
                lista.add(map);
            }
        }
        return lista;
    }


    //9 endpoint
    public Map<String, Object> atualizarSituacao(int numero, String status) {
        Candidato c = buscarCandidato(numero);
        // Valida se encontrou o candidato e se veio uma nova situação -
        if (c == null || status == null) {
            return null;
        }

        // Deixa o status em maiúsculas para comparar com as situações do candidato -
        status = status.toUpperCase(Locale.ROOT);
        String atual = c.getSituacao();
        boolean permitida = false;

        if ("PRECANDIDATO".equalsIgnoreCase(atual)) {
            // Pré-candidato pode ser aprovado, reprovado ou removido -
            permitida = status.equals("ELEGIVEL") || status.equals("INELEGIVEL")
                    || status.equals("REMOVIDO");
        } else if ("INELEGIVEL".equalsIgnoreCase(atual)) {
            // Inelegível pode voltar a ser elegível ou ser removido -
            permitida = status.equals("ELEGIVEL") || status.equals("REMOVIDO");
        } else if ("ELEGIVEL".equalsIgnoreCase(atual)) {
            // Elegível pode ficar inelegível ou receber o resultado da eleição -
            permitida = status.equals("INELEGIVEL") || status.equals("ELEITO")
                    || status.equals("NAOELEITO");
        }

        // Não aceita uma situação desconhecida nem altera os estados finais -
        if (!permitida) {
            return null;
        }

        // Atualiza a situação depois de verificar se essa mudança é permitida -
        c.setSituacao(status);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("numero", c.getNumero());
        map.put("nome", c.getNome());
        map.put("situacao", c.getSituacao());
        map.put("nome_partido", c.getPartido().getNome());
        map.put("nome_localidade", c.getLocalidade().getNome());
        return map;
    }


    //10 endpoint
    public boolean removerCandidato(int numero) {
        Candidato c = buscarCandidato(numero);
        if (c == null) {
            return false;
        }

        // Apenas pré-candidato e inelegível podem ser removidos -
        if (!"PRECANDIDATO".equalsIgnoreCase(c.getSituacao())
                && !"INELEGIVEL".equalsIgnoreCase(c.getSituacao())) {
            return false;
        }

        //NOTE: A remoção é lógica, então o candidato continua na lista -
        c.setSituacao("REMOVIDO");
        return true;
    }

    
}

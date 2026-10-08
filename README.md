# ACMEPolling

Backend em Java com Spring Boot para cadastro de candidatos, votação e apuração de resultados.

## Padrões de arquitetura

| Padrão | Onde foi utilizado |
| --- | --- |
| **Camadas (Layers)** | DemoController recebe as requisições; Acervo reúne as regras e as listas em memória; as entidades representam os dados. |
| **Cliente-Servidor** | O cliente envia requisições HTTP e a aplicação Spring Boot responde com JSON. |

## Padrão de projeto

| Padrão | Onde foi utilizado |
| --- | --- |
| **Façade** | Acervo oferece operações como cadastrarVoto e consultarEleito, escondendo do DemoController os detalhes das listas e das regras. |
| **Chain of Responsibility** | No método "ehVotoValido" ele faz uma cadeia de validações, a única diferença é que foi feito tudo dentro de um método e não uma classe separada. |

## Testes Postman

Com a aplicação rodando, execute na ordem abaixo. Reinicie antes de começar para limpar os cadastros e votos anteriores.

No Postman, selecione o método e copie a URL. Para POST e DELETE, use **Body → raw → JSON**, com `Content-Type: application/json`. Para GET e PUT, deixe **Body → none**.

### 1. GET — Listar candidatos

Retorna os candidatos com situação, partido e localidade.

```text
http://localhost:8080/acmepolling/cadastro/listacandidatos
```

### 2. GET — Filtrar por localidade e situação

Retorna os candidatos elegíveis de Porto Alegre. `situação` está codificada na URL.

```text
http://localhost:8080/acmepolling/cadastro/listacandidatoslocalidade/90000-000/situa%C3%A7%C3%A3o/ELEGIVEL
```

### 3. POST — Cadastrar candidato

Cadastra Ana em Pelotas como `PRECANDIDATO`. Esperado: `true`. `codigo` é o código do partido.

```text
http://localhost:8080/acmepolling/cadastro/cadcandidato
```

Corpo:

```json
{
  "numero": 404,
  "nome": "Ana",
  "codigo": 10,
  "cep": "92000-000"
}
```

### 4. PUT — Tornar o candidato elegível

Altera Ana para `ELEGIVEL` e retorna seu cadastro atualizado. **Sem corpo.**

```text
http://localhost:8080/acmepolling/cadastro/atualizacandidato/404/situacao/ELEGIVEL
```

### 5. POST — Registrar voto

Registra um voto válido para Ana. Esperado: `true`. Na votação, `true` significa registrado, não necessariamente válido.

```text
http://localhost:8080/acmepolling/votacao/cadvoto
```

Corpo:

```json
{
  "id": 1,
  "hora": 10,
  "numero": 404,
  "cep": "92000-000"
}
```

### 6. GET — Consultar votos do candidato

Esperado: Ana com 1 voto válido e 0 inválidos.

```text
http://localhost:8080/acmepolling/apuracao/consultacandidato?numero=404
```

### 7. GET — Consultar votação da localidade

Lista os candidatos de Pelotas e suas quantidades de votos válidos e inválidos.

```text
http://localhost:8080/acmepolling/apuracao/consultalocalidade?cep=92000-000
```

### 8. GET — Consultar os mais votados por partido

Retorna um candidato por partido. Ana deve aparecer pelo Partido da Computação com 1 voto válido.

```text
http://localhost:8080/acmepolling/apuracao/listamaisvotadospartido
```

### 9. GET — Apurar o eleito

Retorna Ana como eleita de Pelotas, com 1 voto válido, e altera sua situação para `ELEITO`.

```text
http://localhost:8080/acmepolling/apuracao/consultaeleito?cep=92000-000
```

### 10. PUT — Preparar outro candidato para remoção

Altera Roberta (303) para `INELEGIVEL`, situação que permite remoção. **Sem corpo.**

```text
http://localhost:8080/acmepolling/cadastro/atualizacandidato/303/situacao/INELEGIVEL
```

### 11. DELETE — Remover candidato logicamente

Altera Roberta para `REMOVIDO`, sem apagar seu cadastro. Esperado: `true`. Envie apenas o número no corpo.

```text
http://localhost:8080/acmepolling/cadastro/removecandidato
```

Corpo:

```json
303
```

Para conferir as situações finais, repita o GET do passo 1.

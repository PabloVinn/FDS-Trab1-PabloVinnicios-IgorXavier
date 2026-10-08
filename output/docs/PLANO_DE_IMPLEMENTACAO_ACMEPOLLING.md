# Plano de implementação do ACMEPolling

Atualizado em 8 de outubro de 2026. Os dez endpoints do ACMEPolling estão implementados. Este plano registra o que já foi feito e organiza o que falta para a entrega: validar as transições de situação, testar os endpoints, preparar o relatório e os diagramas e realizar a demonstração.

## Visão geral

| Situação | Marco | Resultado atual |
|---|---|---|
| Pronto | Base existente | Entidades e dados iniciais presentes |
| Pronto | Ambiente | Java 21 e compilação conferidos |
| Parcial | Modelo e organização | Voto e controllers prontos; transições pendentes |
| Pronto | Candidatos | Cinco endpoints implementados; falta validar transições |
| Pronto | Votação | Cadastro e classificação de votos implementados |
| Pronto | Apuração | Quatro consultas e desempate do eleito implementados |
| Pendente | Testes | Só o teste de inicialização está no projeto atual |
| Pendente | Relatório | Diagramas e explicação do código ainda precisam ser feitos |
| Pendente | Entrega | Validação dos dez endpoints e demonstração |

## 1 Estado atual

- [x] Projeto Spring Boot criado.
- [x] Classes `Candidato`, `Partido`, `Localidade` e `Voto`.
- [x] `Acervo` para armazenamento em memória.
- [x] Três partidos, três localidades e três candidatos elegíveis.
- [x] Dois candidatos na mesma localidade.
- [x] Cadastro e classificação de votos válidos e inválidos implementados.
- [x] Compilação validada com Java 21.
- [x] Dez endpoints do enunciado.
- [ ] Testes completos.
- [ ] Relatório e diagramas.

## 2 Ambiente configurado

### Atividades

- [x] JDK 21 disponível no ambiente.
- [x] Maven Wrapper executa com o Java configurado.
- [x] Confirmar as versões de `java` e `javac`.
- [x] Executar a compilação e o teste inicial.

### Validação

```powershell
java -version
javac -version
cd E:\igor\Pucrs\FDS-Trab1-PabloVinnicios-IgorXavier\demo
.\mvnw.cmd test
```

Java e javac foram conferidos com a versão 21. A compilação e o teste inicial passaram com `BUILD SUCCESS`. Continue executando os comandos de validação após alterar o código.

## 3 Modelo e organização do código

### Voto corrigido

- [x] Construtor recebe ID, hora, número do candidato, candidato e localidade.
- [x] Adicionar o campo `valido`.
- Opcional: adicionar `motivoInvalidacao`; esse campo não foi criado.
- [x] Garantir que votos inválidos sejam armazenados para permitir sua contagem.

```java
public Voto(int id, int hora, int numeroCandidato,
        Candidato candidato, Localidade localidade) {
    this.id = id;
    this.hora = hora;
    this.numeroCandidato = numeroCandidato;
    this.candidato = candidato;
    this.localidade = localidade;
}
```

Voto guarda o número informado mesmo quando o candidato não existe e tem o campo `valido`. O controller classifica o voto e o armazena no `Acervo`, inclusive quando for inválido.

### Situação do candidato

- [x] Situação mantida como `String`, seguindo o modelo atual.
- Opcional: substituir a `String` por `enum`; não é necessário para manter o código simples.
- [x] Fazer todo candidato novo iniciar como `PRECANDIDATO`.
- [ ] Implementar as transições permitidas.

Situações usadas: `PRECANDIDATO`, `ELEGIVEL`, `INELEGIVEL`, `ELEITO`, `NAOELEITO` e `REMOVIDO`.

Pendente: validar as transições abaixo. Hoje o PUT atribui diretamente o status recebido e o DELETE muda a situação para `REMOVIDO`.

- `PRECANDIDATO` para `ELEGIVEL`, `INELEGIVEL` ou `REMOVIDO`.
- `INELEGIVEL` para `ELEGIVEL` ou `REMOVIDO`.
- `ELEGIVEL` para `INELEGIVEL`, `ELEITO` ou `NAOELEITO`.
- `REMOVIDO` não volta a outro estado.

### Controllers separados

```text
DemoController       -> mensagem em /acmepolling
CandidatoController  -> consultas e cadastro de candidatos
VotacaoController    -> cadastro de votos
ApuracaoController   -> consultas de apuração
Acervo               -> listas e dados iniciais em memória
```

- [x] Classes mantidas no pacote `br.pucrs.trabalho1fds.demo`.
- [x] `CandidatoController`, `VotacaoController` e `ApuracaoController` criados.
- [x] Lógica implementada diretamente nos métodos dos controllers com `for` e `if`.
- [x] `Acervo` compartilhado entre os controllers por meio dos construtores.
- [x] Records usados para receber os JSONs de cadastro e montar as respostas.

A organização atual não tem camadas de service e repository separadas. Ao escrever o relatório, descreva os controllers, as entidades, os records e o `Acervo` que realmente existem no projeto.

## 4 Candidatos implementados

### Listar todos

`GET /acmepolling/cadastro/listacandidatos`

- [x] Retornar número, nome, situação, nome do partido e nome da localidade.
- [x] Record `CadastroCompleto` retorna os campos solicitados sem objetos aninhados.

```json
[{"numero":101,"nome":"Agostine","situacao":"ELEGIVEL","nome_partido":"Partido da Computação","nome_localidade":"Porto Alegre"}]
```

### Filtrar por localidade e situação

`GET /acmepolling/cadastro/listacandidatoslocalidade/{cep}/situação/{situacao}`

- [x] Filtrar pelo CEP e pela situação.
- [x] Retornar apenas número e nome.
- [ ] Testar lista vazia, CEP inexistente e situação inválida.

### Cadastrar candidato

`POST /acmepolling/cadastro/cadcandidato`

```json
{"numero":404,"nome":"Carolina","codigo":10,"cep":"92000-000"}
```

- [x] Rejeitar número repetido e nome vazio.
- [x] Verificar se partido e localidade existem.
- [x] Iniciar como `PRECANDIDATO`.
- [x] Retornar `true` ou `false`.

### Atualizar e remover

- `PUT /acmepolling/cadastro/atualizacandidato/{numero}/situacao/{status}`
- `DELETE /acmepolling/cadastro/removecandidato`

- [ ] Validar a transição de estado.
- [x] Retornar o candidato atualizado no PUT.
- [x] Na remoção, trocar a situação para `REMOVIDO` sem excluir da lista.
- [x] DELETE retorna `false` quando o candidato não existe; PUT retorna `null`.

## 5 Votação implementada

`POST /acmepolling/votacao/cadvoto`

```json
{"id":1,"hora":10,"numero":101,"cep":"90000-000"}
```

- [x] Localizar candidato e localidade.
- [x] Rejeitar ID repetido.
- [x] Classificar como inválido quando estiver fora de 8h a 17h.
- [x] Classificar como inválido quando o candidato não existir ou não for elegível.
- [x] Classificar como inválido quando candidato e voto forem de localidades diferentes.
- [x] Classificar como inválido quando o limite de eleitores tiver sido atingido.
- [x] Armazenar votos válidos e inválidos.

O limite é verificado pela quantidade de votos já registrados na localidade. O POST retorna `true` quando registra o voto, inclusive se ele for inválido; ID repetido, campos ausentes ou CEP desconhecido retornam `false`.

Pendente validar manualmente: votos às 8h e 17h; às 7h e 18h; candidato inexistente ou inelegível; CEP diferente; limite excedido; ID repetido.

## 6 Apuração implementada

### Votos de um candidato

`GET /acmepolling/apuracao/consultacandidato?numero=101`

- [x] Contar votos válidos.
- [x] Contar votos inválidos.
- [x] Consulta retorna as contagens pelo número informado; sem votos, ambas são zero.

### Votação da localidade

`GET /acmepolling/apuracao/consultalocalidade?cep=90000-000`

- [x] Retornar todos os candidatos da localidade.
- [x] Informar as duas contagens de cada candidato.
- [ ] Comparar o JSON com uma soma manual dos votos de teste.

### Eleito da localidade

`GET /acmepolling/apuracao/consultaeleito?cep=90000-000`

- [x] Conta votos válidos de candidatos elegíveis; aceita `ELEITO` e `NAOELEITO` para repetir a consulta.
- [x] Escolher a maior quantidade de votos.
- [x] Em empate, escolher quem recebeu o último voto mais cedo.
- [x] Atualizar o vencedor para `ELEITO` e os demais para `NAOELEITO`.

Validar o desempate com este cenário: candidato 101 recebe votos às 9h e 10h; candidato 202 recebe votos às 8h e 11h. O candidato 101 deve vencer. Sem candidato apto com votos válidos, a consulta retorna `null`.

### Mais votados por partido

`GET /acmepolling/apuracao/listamaisvotadospartido`

- [x] Agrupar candidatos por partido.
- [x] Contar somente votos válidos.
- [x] Selecionar o mais votado de cada partido.
- [ ] Testar empates; hoje o primeiro candidato encontrado é mantido quando as contagens são iguais.

## 7 Testes automatizados pendentes

O projeto atual contém apenas `DemoApplicationTests`, com o teste `contextLoads`. Esse teste passou, mas não testa os dez endpoints. Use JUnit 5 e MockMvc para criar a cobertura abaixo.

- [ ] Pelo menos um teste de sucesso para cada endpoint.
- [ ] Pelo menos um teste de erro para cada endpoint.
- [ ] Testes para todas as regras de voto.
- [ ] Testes das transições de situação.
- [ ] Teste do desempate.
- [ ] Teste da remoção lógica.
- [ ] Teste do limite de eleitores.
- [ ] Testes dos formatos JSON.

A meta de 25 a 35 testes é uma sugestão do plano. O enunciado exige testes de endpoints, sem fixar essa quantidade. Execute a suíte com:

```powershell
.\mvnw.cmd test
```

O resultado deve mostrar zero falhas, zero erros e `BUILD SUCCESS`.

## 8 Relatório e diagramas pendentes

### Diagrama de classes

- [ ] Mostrar entidades, atributos, relacionamentos e multiplicidades.
- [ ] Incluir `Voto`, seus novos campos, os controllers e os records usados nas respostas.

### Diagrama de estados

- [ ] Representar todas as situações e transições permitidas.

### Relatório

- [ ] Descrever o sistema e sua arquitetura.
- [ ] Inserir os diagramas.
- [ ] Explicar as regras de validação.
- [ ] Explicar o uso real de controllers, injeção pelo construtor, records e armazenamento no `Acervo`, sem afirmar que existem camadas não implementadas.
- [ ] Adicionar exemplos de requisições e evidências de testes.
- [ ] Identificar os integrantes.

## 9 Validação final e entrega pendentes

- [ ] Executar `.\mvnw.cmd clean test` e confirmar `BUILD SUCCESS`.
- [ ] Iniciar a aplicação do zero.
- [ ] Executar e conferir os dez endpoints.
- [ ] Confirmar que não existem erros no terminal.
- [ ] Enviar código, testes, relatório e diagramas ao GitHub.
- [ ] Compartilhar o repositório privado com `mhyamaguti`.
- [ ] Montar o arquivo compactado para o Moodle.
- [ ] Preparar dois computadores se o trabalho for em dupla.
- [ ] Ensaiar a demonstração.

## 10 Tutorial para executar e testar

### Iniciar o projeto

```powershell
cd E:\igor\Pucrs\FDS-Trab1-PabloVinnicios-IgorXavier\demo
java -version
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Aguarde `Started DemoApplication`. O servidor estará em `http://localhost:8080`. O terminal ficar ocupado enquanto o servidor estiver rodando; isso é normal.

Abra `http://localhost:8080/acmepolling`. A resposta esperada é `Aplicacao Spring-Boot funcionando!`.

### Testar uma consulta

```powershell
Invoke-RestMethod -Method Get `
  -Uri "http://localhost:8080/acmepolling/cadastro/listacandidatos"
```

### Testar o cadastro de candidato

```powershell
$corpo = @{
    numero = 404
    nome = "Carolina"
    codigo = 10
    cep = "92000-000"
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8080/acmepolling/cadastro/cadcandidato" `
  -ContentType "application/json" `
  -Body $corpo
```

### Parar o servidor

Pressione `Ctrl + C` no terminal em que o Spring Boot está sendo executado.

## 11 Rotina recomendada de desenvolvimento

Para cada pequena funcionalidade:

1. Salvar os arquivos.
2. Executar `.\mvnw.cmd test`.
3. Iniciar a aplicação.
4. Testar o endpoint alterado.
5. Conferir o JSON e pelo menos um caso de erro.
6. Fazer um commit.
7. Avançar para a próxima atividade.

Exemplos de commits:

```text
feat: implementa os dez endpoints do ACMEPolling
feat: separa os controllers de candidatos votação e apuração
test: adiciona testes dos endpoints
docs: adiciona diagramas e relatório
```

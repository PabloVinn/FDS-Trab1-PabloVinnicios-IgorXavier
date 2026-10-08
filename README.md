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


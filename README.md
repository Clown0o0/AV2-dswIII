# AutoManager — API de Clientes

[![CI](https://github.com/Clown0o0/AV2-dswIII/actions/workflows/ci.yml/badge.svg)](https://github.com/Clown0o0/AV2-dswIII/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-v17-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-v2.6.3-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring HATEOAS](https://img.shields.io/badge/Spring%20HATEOAS-v1.4.1-6DB33F?style=flat&logo=spring&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-v1.18.46-BC4521?style=flat)
![Maven](https://img.shields.io/badge/Maven-v3.8.4-C71A36?style=flat&logo=apachemaven&logoColor=white)
![MySQL Connector/J](https://img.shields.io/badge/MySQL%20Connector%2FJ-v8.0.28-4479A1?style=flat&logo=mysql&logoColor=white)

> API REST em **Spring Boot** para cadastro e gerenciamento de clientes, com seus documentos, endereço e telefones.

---

## Sumário

1. [Objetivo](#objetivo)
2. [Organização do código](#organização-do-código)
3. [Executando localmente](#executando-localmente)
4. [Níveis de maturidade (RMM)](#níveis-de-maturidade-rmm)
5. [Rotas](#rotas)
6. [Links HATEOAS](#links-hateoas)
7. [Exemplo de requisição](#exemplo-de-requisição)
8. [Exemplo de resposta](#exemplo-de-resposta)
9. [Formas de cadastro](#formas-de-cadastro)
10. [Validações e erros](#validações-e-erros)
11. [Notas](#notas)
12. [Autor](#autor)

---

## Objetivo

Construir uma API aplicando boas práticas de back-end:

- [x] Arquitetura em camadas (Controller → Service → Repository)
- [x] DTOs para os dados de entrada
- [x] Bean Validation
- [x] Tratamento centralizado de exceções
- [x] Relacionamentos JPA/Hibernate entre entidades
- [x] Os 4 níveis do Richardson Maturity Model (RMM), incluindo HATEOAS com Spring HATEOAS

## Organização do código

```
com.autobots.automanager
├── controles      # endpoints REST
├── service        # regras de negócio
├── repositorios   # acesso a dados (Spring Data JPA)
├── entidades      # modelos persistidos
├── dto            # objetos de entrada
├── modelo         # utilitários de atualização/seleção e AdicionadorLink* (HATEOAS)
└── exception      # handler global de erros
```

## Executando localmente

### Pré-requisitos

- **JDK 17 ou superior** instalado, com `JAVA_HOME` apontando para ele (`java -version` para conferir).
- **MySQL 8** rodando em `localhost:3306` — opcional se for usar H2 (veja abaixo).
- **Git**, para clonar o repositório.
- Não é preciso instalar o Maven: o projeto traz o wrapper (`mvnw` / `mvnw.cmd`).

### Passo a passo

**1) Clone o repositório e entre na pasta**

```bash
git clone https://github.com/Clown0o0/AV2-dswIII.git
cd AV2-dswIII
```

**2) Configure o banco** em `automanager/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/av1DW3?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=<sua_senha>
```

> O banco é criado automaticamente caso não exista — normalmente só é preciso ajustar usuário e senha.

**3) Suba a aplicação**

- Pela IDE: execute a classe `AutomanagerApplication`.
- Pelo terminal:

```bash
cd automanager
./mvnw spring-boot:run      # Linux/macOS
mvnw.cmd spring-boot:run    # Windows
```

**4) Confira se subiu**

A API fica disponível em `http://localhost:8080`. Ao iniciar, um cliente de exemplo é cadastrado automaticamente:

```bash
curl http://localhost:8080/cliente
```

> Requer **JDK 17** ou superior (testado no 17 e no 26). O `pom.xml` fixa o Lombok em `1.18.46` e o registra em `annotationProcessorPaths`, necessário a partir do JDK 23, que não executa mais annotation processors do classpath automaticamente.

**Sem MySQL?** Dá para rodar em memória com H2 passando as propriedades na linha de comando, sem alterar o `application.properties`:

```bash
cd automanager
./mvnw clean package
java -jar target/automanager-0.0.1-SNAPSHOT.jar --spring.datasource.url=jdbc:h2:mem:automanager --spring.datasource.driver-class-name=org.h2.Driver --spring.datasource.username=sa --spring.datasource.password=
```

**Testes automatizados** — `./mvnw clean package` (ou `./mvnw test`) roda os testes com H2 em memória, configurado por `@TestPropertySource`; não é preciso ter MySQL. A classe `ControlesTests` usa MockMvc e cobre, para cliente, documento, telefone e endereço: `POST` (`201` + `Location`), `GET` lista (`200`), `GET` por id (`200`), `PUT` (`200`), `DELETE` (`204`), `GET` após `DELETE` (`404`), `POST` inválido (`400`) e a presença de `_links.self` nas respostas, inclusive nos itens aninhados do cliente.

## Níveis de maturidade (RMM)

| Nível | Requisito                         | Como foi aplicado |
| ----- | --------------------------------- | ----------------- |
| 0     | Uso do protocolo HTTP             | API REST sobre HTTP com corpo JSON |
| 1     | URI única por recurso             | `/cliente`, `/documentos`, `/enderecos`, `/telefones`, cada registro em `/{recurso}/{id}` |
| 2     | Uso dos métodos e suas semânticas | `POST`, `GET`, `PUT`, `DELETE` com `ResponseEntity`: `201 Created` + header `Location`, `200 OK`, `204 No Content`, `400`, `404`, `409` |
| 3     | HATEOAS                           | Entidades estendem `RepresentationModel`; os `AdicionadorLink*` (pacote `modelo`) montam os links com `WebMvcLinkBuilder`; listagens devolvem `CollectionModel` |

## Rotas

#### Cliente — `/cliente`

| Verbo    | Caminho         | Descrição              | Status |
| -------- | --------------- | ---------------------- | ------ |
| `POST`   | `/cliente`      | cadastra cliente       | `201` |
| `GET`    | `/cliente`      | lista clientes         | `200` |
| `GET`    | `/cliente/{id}` | busca por id           | `200` |
| `PUT`    | `/cliente/{id}` | atualiza dados básicos e endereço (documentos e telefones pelas rotas próprias) | `200` |
| `DELETE` | `/cliente/{id}` | remove cliente         | `204` |

#### Documento — `/documentos`

| Verbo    | Caminho                   | Descrição                      | Status |
| -------- | ------------------------- | ------------------------------ | ------ |
| `POST`   | `/documentos/{clienteId}` | adiciona documento ao cliente  | `201` |
| `GET`    | `/documentos`             | lista documentos               | `200` |
| `GET`    | `/documentos/{id}`        | busca por id                   | `200` |
| `PUT`    | `/documentos/{id}`        | atualiza documento             | `200` |
| `DELETE` | `/documentos/{id}`        | remove documento               | `204` |

#### Endereço — `/enderecos`

| Verbo    | Caminho                  | Descrição                                    | Status |
| -------- | ------------------------ | -------------------------------------------- | ------ |
| `POST`   | `/enderecos/{clienteId}` | cadastra o endereço do cliente (409 se já tem) | `201` |
| `GET`    | `/enderecos`             | lista endereços                              | `200` |
| `GET`    | `/enderecos/{id}`        | busca por id                                 | `200` |
| `PUT`    | `/enderecos/{id}`        | atualiza endereço                            | `200` |
| `DELETE` | `/enderecos/{id}`        | remove endereço (desvincula do cliente)      | `204` |

#### Telefone — `/telefones`

| Verbo    | Caminho                  | Descrição                     | Status |
| -------- | ------------------------ | ----------------------------- | ------ |
| `POST`   | `/telefones/{clienteId}` | adiciona telefone ao cliente  | `201` |
| `GET`    | `/telefones`             | lista telefones               | `200` |
| `GET`    | `/telefones/{id}`        | busca por id                  | `200` |
| `PUT`    | `/telefones/{id}`        | atualiza telefone             | `200` |
| `DELETE` | `/telefones/{id}`        | remove telefone               | `204` |

Toda rota com `{id}` ou `{clienteId}` inexistente responde `404`. O `POST` devolve o recurso criado no corpo e a URI dele no header `Location`. Listagens vazias respondem `200` sem a chave `_embedded`.

## Links HATEOAS

As respostas usam o formato HAL (`application/hal+json`; com `Accept: application/json` o corpo é o mesmo e o `Content-Type` sai como `application/json`). Os links ficam em `_links` de cada recurso, inclusive nos objetos aninhados dentro do cliente.

| Recurso                       | Rel              | Aponta para               | Método esperado |
| ----------------------------- | ---------------- | ------------------------- | --------------- |
| todos                         | `self`           | `/{recurso}/{id}`         | `GET`    |
| todos                         | `clientes` / `documentos` / `enderecos` / `telefones` | coleção do recurso | `GET` |
| todos                         | `atualizar`      | `/{recurso}/{id}`         | `PUT`    |
| todos                         | `excluir`        | `/{recurso}/{id}`         | `DELETE` |
| cliente                       | `novo-documento` | `/documentos/{clienteId}` | `POST`   |
| cliente                       | `novo-telefone`  | `/telefones/{clienteId}`  | `POST`   |
| cliente (só sem endereço)     | `novo-endereco`  | `/enderecos/{clienteId}`  | `POST`   |
| documento, telefone, endereço | `cliente`        | `/cliente/{clienteId}`    | `GET`    |
| coleção (`GET /{recurso}`)    | `self`           | `/{recurso}`              | `GET`    |

O link `novo-endereco` só aparece enquanto o cliente não tem endereço, já que o cadastro de um segundo endereço responde `409`.

## Exemplo de requisição

`POST /cliente`

```json
{
  "nome": "Rodolfo Ferreira",
  "nomeSocial": "Rodolfo",
  "dataNascimento": "1998-03-10",
  "documentos": [
    { "tipo": "CPF", "numero": "12345678900" }
  ],
  "endereco": {
    "estado": "SP",
    "cidade": "São José dos Campos",
    "bairro": "Centro",
    "rua": "Rua A",
    "numero": "123",
    "codigoPostal": "12200000",
    "informacoesAdicionais": "Casa azul"
  },
  "telefones": [
    { "ddd": "12", "numero": "999999999" }
  ]
}
```

## Exemplo de resposta

Capturada da aplicação rodando: `GET /cliente/1` → `200 OK` (cliente criado na inicialização).

```json
{
  "id": 1,
  "nome": "Pedro Alcântara de Bragança e Bourbon",
  "nomeSocial": "Dom Pedro",
  "dataNascimento": "2002-06-15T05:15:10.536+00:00",
  "dataCadastro": "16/10/2026 02:15",
  "documentos": [
    {
      "id": 1,
      "tipo": "RG",
      "numero": "1500",
      "_links": {
        "self": { "href": "http://localhost:8080/documentos/1" },
        "documentos": { "href": "http://localhost:8080/documentos" },
        "atualizar": { "href": "http://localhost:8080/documentos/1" },
        "excluir": { "href": "http://localhost:8080/documentos/1" },
        "cliente": { "href": "http://localhost:8080/cliente/1" }
      }
    },
    {
      "id": 2,
      "tipo": "CPF",
      "numero": "00000000001",
      "_links": {
        "self": { "href": "http://localhost:8080/documentos/2" },
        "documentos": { "href": "http://localhost:8080/documentos" },
        "atualizar": { "href": "http://localhost:8080/documentos/2" },
        "excluir": { "href": "http://localhost:8080/documentos/2" },
        "cliente": { "href": "http://localhost:8080/cliente/1" }
      }
    }
  ],
  "endereco": {
    "id": 1,
    "estado": "Rio de Janeiro",
    "cidade": "Rio de Janeiro",
    "bairro": "Copacabana",
    "rua": "Avenida Atlântica",
    "numero": "1702",
    "codigoPostal": "22021001",
    "informacoesAdicionais": "Hotel Copacabana palace",
    "_links": {
      "self": { "href": "http://localhost:8080/enderecos/1" },
      "enderecos": { "href": "http://localhost:8080/enderecos" },
      "atualizar": { "href": "http://localhost:8080/enderecos/1" },
      "excluir": { "href": "http://localhost:8080/enderecos/1" },
      "cliente": { "href": "http://localhost:8080/cliente/1" }
    }
  },
  "telefones": [
    {
      "id": 1,
      "ddd": "21",
      "numero": "981234576",
      "_links": {
        "self": { "href": "http://localhost:8080/telefones/1" },
        "telefones": { "href": "http://localhost:8080/telefones" },
        "atualizar": { "href": "http://localhost:8080/telefones/1" },
        "excluir": { "href": "http://localhost:8080/telefones/1" },
        "cliente": { "href": "http://localhost:8080/cliente/1" }
      }
    }
  ],
  "_links": {
    "self": { "href": "http://localhost:8080/cliente/1" },
    "clientes": { "href": "http://localhost:8080/cliente" },
    "atualizar": { "href": "http://localhost:8080/cliente/1" },
    "excluir": { "href": "http://localhost:8080/cliente/1" },
    "novo-documento": { "href": "http://localhost:8080/documentos/1" },
    "novo-telefone": { "href": "http://localhost:8080/telefones/1" }
  }
}
```

`GET /telefones` → `200 OK` (listagem como `CollectionModel`):

```json
{
  "_embedded": {
    "telefones": [
      {
        "id": 1,
        "ddd": "21",
        "numero": "981234576",
        "_links": {
          "self": { "href": "http://localhost:8080/telefones/1" },
          "telefones": { "href": "http://localhost:8080/telefones" },
          "atualizar": { "href": "http://localhost:8080/telefones/1" },
          "excluir": { "href": "http://localhost:8080/telefones/1" },
          "cliente": { "href": "http://localhost:8080/cliente/1" }
        }
      }
    ]
  },
  "_links": {
    "self": { "href": "http://localhost:8080/telefones" }
  }
}
```

`POST /documentos/2` → `201 Created` com `Location: http://localhost:8080/documentos/4`.

## Formas de cadastro

**Tudo de uma vez** — envie cliente, documentos, endereço e telefones juntos em `POST /cliente`.

**Passo a passo** (útil para testes):

1. `POST /cliente` apenas com os dados básicos
2. `POST /documentos/{clienteId}` para cada documento
3. `POST /enderecos/{clienteId}` para o endereço
4. `POST /telefones/{clienteId}` para cada telefone

## Validações e erros

Campos obrigatórios são checados automaticamente (ex.: `tipo` e `numero` do documento). Falhas de validação retornam um objeto `campo → mensagem`:

```json
{ "tipo": "O tipo do documento deve ser informado" }
```

Demais erros passam pelo `GlobalExceptionHandler` e voltam padronizados:

```json
{ "Erro": "Nenhum cliente encontrado com o id 7" }
```

| Situação                                  | Status HTTP |
| ----------------------------------------- | ----------- |
| Campo obrigatório ausente / JSON inválido / id não numérico | `400` |
| Registro não encontrado                   | `404`       |
| Dado duplicado (ex.: número de documento) / cliente já possui endereço | `409` |
| Erro inesperado                           | `500`       |

## Notas

- `dataCadastro` é preenchida automaticamente na criação do cliente.
- Datas de cadastro são exibidas no formato `dd/MM/yyyy HH:mm` (fuso America/Sao_Paulo).
- A separação em camadas facilita uma futura evolução para microsserviços.

---

## Autor

Desenvolvido por **Rodolfo Ferreira** — [github.com/Clown0o0](https://github.com/Clown0o0)

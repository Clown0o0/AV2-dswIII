package com.autobots.automanager;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:automanager;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=" })
class ControlesTests {

    @Autowired
    private MockMvc mvc;

    private String criar(String rota, String corpo, String recurso) throws Exception {
        String location = mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).accept(MediaTypes.HAL_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/" + recurso + "/")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andReturn().getResponse().getHeader("Location");
        return URI.create(location).getPath();
    }

    private String criarCliente(String nome) throws Exception {
        return criar("/cliente", "{\"nome\":\"" + nome + "\",\"dataNascimento\":\"1990-04-10\"}", "cliente");
    }

    private String idDe(String location) {
        return location.substring(location.lastIndexOf('/') + 1);
    }

    private void verificarCrud(String rota, String colecao, String location, String atualizacao, String campo, String valor) throws Exception {
        mvc.perform(get(rota).accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded." + colecao).isArray())
                .andExpect(jsonPath("$._embedded." + colecao + "[0]._links.self.href").exists())
                .andExpect(jsonPath("$._links.self.href", endsWith(rota)));

        mvc.perform(get(location).accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", endsWith(location)))
                .andExpect(jsonPath("$._links.atualizar.href").exists())
                .andExpect(jsonPath("$._links.excluir.href").exists());

        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON).accept(MediaTypes.HAL_JSON).content(atualizacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + campo).value(valor))
                .andExpect(jsonPath("$._links.self.href").exists());

        mvc.perform(delete(location))
                .andExpect(status().isNoContent());

        mvc.perform(get(location).accept(MediaTypes.HAL_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void crudCliente() throws Exception {
        String location = criar("/cliente",
                "{\"nome\":\"Ana Souza\",\"dataNascimento\":\"1990-04-10\","
                        + "\"documentos\":[{\"tipo\":\"CPF\",\"numero\":\"11122233344\"}],"
                        + "\"telefones\":[{\"ddd\":\"12\",\"numero\":\"99999-0000\"}],"
                        + "\"endereco\":{\"cidade\":\"Jacarei\",\"rua\":\"Rua A\",\"numero\":\"10\"}}",
                "cliente");

        mvc.perform(get(location).accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.clientes.href").exists())
                .andExpect(jsonPath("$._links['novo-documento'].href").exists())
                .andExpect(jsonPath("$._links['novo-telefone'].href").exists())
                .andExpect(jsonPath("$._links['novo-endereco']").doesNotExist())
                .andExpect(jsonPath("$.documentos[0]._links.self.href").exists())
                .andExpect(jsonPath("$.documentos[0]._links.cliente.href", endsWith("/cliente/" + idDe(location))))
                .andExpect(jsonPath("$.telefones[0]._links.self.href").exists())
                .andExpect(jsonPath("$.endereco._links.self.href").exists());

        mvc.perform(get(location).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists());

        verificarCrud("/cliente", "clientes", location,
                "{\"nome\":\"Ana Souza Lima\",\"dataNascimento\":\"1990-04-10\"}", "nome", "Ana Souza Lima");
    }

    @Test
    void crudDocumento() throws Exception {
        String cliente = criarCliente("Cliente Documento");
        String location = criar("/documentos/" + idDe(cliente), "{\"tipo\":\"RG\",\"numero\":\"55.555.555-5\"}", "documentos");

        mvc.perform(get(location).accept(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._links.cliente.href", endsWith("/cliente/" + idDe(cliente))));

        verificarCrud("/documentos", "documentos", location, "{\"tipo\":\"RG\",\"numero\":\"66.666.666-6\"}", "numero", "66.666.666-6");
    }

    @Test
    void crudTelefone() throws Exception {
        String cliente = criarCliente("Cliente Telefone");
        String location = criar("/telefones/" + idDe(cliente), "{\"ddd\":\"11\",\"numero\":\"3333-4444\"}", "telefones");

        mvc.perform(get(location).accept(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._links.cliente.href", endsWith("/cliente/" + idDe(cliente))));

        verificarCrud("/telefones", "telefones", location, "{\"ddd\":\"11\",\"numero\":\"3333-5555\"}", "numero", "3333-5555");
    }

    @Test
    void crudEndereco() throws Exception {
        String cliente = criarCliente("Cliente Endereco");

        mvc.perform(get(cliente).accept(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._links['novo-endereco'].href").exists());

        String location = criar("/enderecos/" + idDe(cliente), "{\"cidade\":\"Sao Jose dos Campos\",\"rua\":\"Rua A\",\"numero\":\"10\"}", "enderecos");

        mvc.perform(get(location).accept(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._links.cliente.href", endsWith("/cliente/" + idDe(cliente))));

        verificarCrud("/enderecos", "enderecos", location, "{\"cidade\":\"Jacarei\",\"rua\":\"Rua B\",\"numero\":\"20\"}", "cidade", "Jacarei");
    }

    @Test
    void postInvalido() throws Exception {
        String cliente = criarCliente("Cliente Invalido");
        String id = idDe(cliente);

        mvc.perform(post("/cliente").contentType(MediaType.APPLICATION_JSON).content("{\"nomeSocial\":\"sem nome\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nome").exists());
        mvc.perform(post("/documentos/" + id).contentType(MediaType.APPLICATION_JSON).content("{\"tipo\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.numero").exists());
        mvc.perform(post("/telefones/" + id).contentType(MediaType.APPLICATION_JSON).content("{\"ddd\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ddd").exists());
        mvc.perform(post("/enderecos/" + id).contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"SP\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cidade").exists());
    }
}

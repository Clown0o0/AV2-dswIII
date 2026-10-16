package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.DocumentoControle;
import com.autobots.automanager.controles.EnderecoControle;
import com.autobots.automanager.controles.TelefoneControle;
import com.autobots.automanager.entidades.Cliente;

@Component
public class AdicionadorLinkCliente implements AdicionadorLink<Cliente> {

    @Autowired
    private AdicionadorLinkDocumento adicionadorDocumento;

    @Autowired
    private AdicionadorLinkEndereco adicionadorEndereco;

    @Autowired
    private AdicionadorLinkTelefone adicionadorTelefone;

    @Override
    public void adicionarLink(List<Cliente> lista) {
        lista.forEach(this::adicionarLink);
    }

    @Override
    public void adicionarLink(Cliente cliente) {
        long id = cliente.getId();
        cliente.add(linkTo(methodOn(ClienteControle.class).buscar(id)).withSelfRel());
        cliente.add(linkTo(methodOn(ClienteControle.class).listar()).withRel("clientes"));
        cliente.add(linkTo(methodOn(ClienteControle.class).atualizar(id, null)).withRel("atualizar"));
        cliente.add(linkTo(methodOn(ClienteControle.class).deletar(id)).withRel("excluir"));
        cliente.add(linkTo(methodOn(DocumentoControle.class).criar(id, null)).withRel("novo-documento"));
        cliente.add(linkTo(methodOn(TelefoneControle.class).criar(id, null)).withRel("novo-telefone"));
        if (cliente.getEndereco() == null) {
            cliente.add(linkTo(methodOn(EnderecoControle.class).criar(id, null)).withRel("novo-endereco"));
        } else {
            adicionadorEndereco.adicionarLink(cliente.getEndereco());
        }
        adicionadorDocumento.adicionarLink(cliente.getDocumentos());
        adicionadorTelefone.adicionarLink(cliente.getTelefones());
    }
}

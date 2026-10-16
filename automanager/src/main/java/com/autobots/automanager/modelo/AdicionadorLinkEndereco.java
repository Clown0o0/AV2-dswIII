package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.EnderecoControle;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@Component
public class AdicionadorLinkEndereco implements AdicionadorLink<Endereco> {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Override
    public void adicionarLink(List<Endereco> lista) {
        lista.forEach(this::adicionarLink);
    }

    @Override
    public void adicionarLink(Endereco endereco) {
        long id = endereco.getId();
        endereco.add(linkTo(methodOn(EnderecoControle.class).buscar(id)).withSelfRel());
        endereco.add(linkTo(methodOn(EnderecoControle.class).listar()).withRel("enderecos"));
        endereco.add(linkTo(methodOn(EnderecoControle.class).atualizar(id, null)).withRel("atualizar"));
        endereco.add(linkTo(methodOn(EnderecoControle.class).deletar(id)).withRel("excluir"));
        clienteRepositorio.findByEnderecoId(id).ifPresent(cliente ->
                endereco.add(linkTo(methodOn(ClienteControle.class).buscar(cliente.getId())).withRel("cliente")));
    }
}

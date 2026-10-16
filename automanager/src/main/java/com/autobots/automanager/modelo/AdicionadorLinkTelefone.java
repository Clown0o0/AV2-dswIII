package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.TelefoneControle;
import com.autobots.automanager.entidades.Telefone;

@Component
public class AdicionadorLinkTelefone implements AdicionadorLink<Telefone> {

    @Override
    public void adicionarLink(List<Telefone> lista) {
        lista.forEach(this::adicionarLink);
    }

    @Override
    public void adicionarLink(Telefone telefone) {
        long id = telefone.getId();
        telefone.add(linkTo(methodOn(TelefoneControle.class).buscar(id)).withSelfRel());
        telefone.add(linkTo(methodOn(TelefoneControle.class).listar()).withRel("telefones"));
        telefone.add(linkTo(methodOn(TelefoneControle.class).atualizar(id, null)).withRel("atualizar"));
        telefone.add(linkTo(methodOn(TelefoneControle.class).deletar(id)).withRel("excluir"));
        if (telefone.getCliente() != null) {
            telefone.add(linkTo(methodOn(ClienteControle.class).buscar(telefone.getCliente().getId())).withRel("cliente"));
        }
    }
}

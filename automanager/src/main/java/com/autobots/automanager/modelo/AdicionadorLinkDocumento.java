package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.DocumentoControle;
import com.autobots.automanager.entidades.Documento;

@Component
public class AdicionadorLinkDocumento implements AdicionadorLink<Documento> {

    @Override
    public void adicionarLink(List<Documento> lista) {
        lista.forEach(this::adicionarLink);
    }

    @Override
    public void adicionarLink(Documento documento) {
        long id = documento.getId();
        documento.add(linkTo(methodOn(DocumentoControle.class).buscar(id)).withSelfRel());
        documento.add(linkTo(methodOn(DocumentoControle.class).listar()).withRel("documentos"));
        documento.add(linkTo(methodOn(DocumentoControle.class).atualizar(id, null)).withRel("atualizar"));
        documento.add(linkTo(methodOn(DocumentoControle.class).deletar(id)).withRel("excluir"));
        if (documento.getCliente() != null) {
            documento.add(linkTo(methodOn(ClienteControle.class).buscar(documento.getCliente().getId())).withRel("cliente"));
        }
    }
}

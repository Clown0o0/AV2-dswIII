package com.autobots.automanager.controles;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.modelo.AdicionadorLinkDocumento;
import com.autobots.automanager.service.DocumentoService;

/**
 * Rotas de gerenciamento de documentos vinculados a um cliente.
 */
@RestController
@RequestMapping("/documentos")
public class DocumentoControle {

    @Autowired
    private DocumentoService service;

    @Autowired
    private AdicionadorLinkDocumento adicionadorLink;

    @PostMapping("/{clienteId}")
    public ResponseEntity<Documento> criar(@PathVariable Long clienteId, @RequestBody @Valid DocumentoDTO dto) {
        Documento documento = service.criar(clienteId, dto);
        adicionadorLink.adicionarLink(documento);
        return ResponseEntity.created(documento.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(documento);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<Documento>> listar() {
        List<Documento> lista = service.listar();
        adicionadorLink.adicionarLink(lista);
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(DocumentoControle.class).listar()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Documento> buscar(@PathVariable Long id) {
        Documento documento = service.buscar(id);
        adicionadorLink.adicionarLink(documento);
        return ResponseEntity.ok(documento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Documento> atualizar(@PathVariable Long id, @RequestBody @Valid DocumentoDTO dto) {
        Documento documento = service.atualizar(id, dto);
        adicionadorLink.adicionarLink(documento);
        return ResponseEntity.ok(documento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

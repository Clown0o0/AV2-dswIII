package com.autobots.automanager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.exception.RecursoNaoEncontradoException;
import com.autobots.automanager.modelo.DocumentoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;

/**
 * Regras de negócio relacionadas aos documentos.
 */
@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepositorio repositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    private final DocumentoAtualizador atualizador = new DocumentoAtualizador();

    public Documento criar(Long clienteId, DocumentoDTO dto) {
        Cliente dono = clienteRepositorio.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum cliente encontrado com o id " + clienteId));

        Documento documento = dto.paraEntidade();
        documento.setCliente(dono);

        return repositorio.save(documento);
    }

    public List<Documento> listar() {
        return repositorio.findAll();
    }

    public Documento buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum documento encontrado com o id " + id));
    }

    public Documento atualizar(Long id, DocumentoDTO dto) {
        Documento documento = buscar(id);
        atualizador.atualizar(documento, dto.paraEntidade());
        return repositorio.save(documento);
    }

    public void deletar(Long id) {
        repositorio.delete(buscar(id));
    }
}

package com.autobots.automanager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.dto.TelefoneDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.exception.RecursoNaoEncontradoException;
import com.autobots.automanager.modelo.TelefoneAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;

/**
 * Regras de negócio relacionadas aos telefones.
 */
@Service
public class TelefoneService {

    @Autowired
    private TelefoneRepositorio repositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    private final TelefoneAtualizador atualizador = new TelefoneAtualizador();

    public Telefone criar(Long clienteId, TelefoneDTO dto) {
        Cliente dono = clienteRepositorio.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum cliente encontrado com o id " + clienteId));

        Telefone telefone = dto.paraEntidade();
        telefone.setCliente(dono);

        return repositorio.save(telefone);
    }

    public List<Telefone> listar() {
        return repositorio.findAll();
    }

    public Telefone buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum telefone encontrado com o id " + id));
    }

    public Telefone atualizar(Long id, TelefoneDTO dto) {
        Telefone telefone = buscar(id);
        atualizador.atualizar(telefone, dto.paraEntidade());
        return repositorio.save(telefone);
    }

    public void deletar(Long id) {
        repositorio.delete(buscar(id));
    }
}

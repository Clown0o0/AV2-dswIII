package com.autobots.automanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.exception.ConflitoException;
import com.autobots.automanager.exception.RecursoNaoEncontradoException;
import com.autobots.automanager.modelo.EnderecoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

/**
 * Regras de negócio relacionadas ao endereço do cliente.
 */
@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepositorio repositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    private final EnderecoAtualizador atualizador = new EnderecoAtualizador();

    @Transactional
    public Endereco criar(Long clienteId, EnderecoDTO dto) {
        Cliente cliente = clienteRepositorio.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum cliente encontrado com o id " + clienteId));

        if (cliente.getEndereco() != null) {
            throw new ConflitoException("O cliente com o id " + clienteId + " já possui endereço");
        }

        Endereco endereco = repositorio.save(dto.paraEntidade());
        cliente.setEndereco(endereco);
        clienteRepositorio.save(cliente);
        return endereco;
    }

    public List<Endereco> listar() {
        return repositorio.findAll();
    }

    public Endereco buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum endereço encontrado com o id " + id));
    }

    public Endereco atualizar(Long id, EnderecoDTO dto) {
        Endereco endereco = buscar(id);
        atualizador.atualizar(endereco, dto.paraEntidade());
        return repositorio.save(endereco);
    }

    @Transactional
    public void deletar(Long id) {
        Endereco endereco = buscar(id);
        Optional<Cliente> dono = clienteRepositorio.findByEnderecoId(id);
        if (dono.isPresent()) {
            Cliente cliente = dono.get();
            cliente.setEndereco(null);
            clienteRepositorio.save(cliente);
        } else {
            repositorio.delete(endereco);
        }
    }
}

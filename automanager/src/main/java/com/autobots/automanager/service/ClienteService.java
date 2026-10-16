package com.autobots.automanager.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.exception.RecursoNaoEncontradoException;
import com.autobots.automanager.modelo.ClienteAtualizador;
import com.autobots.automanager.modelo.ClienteSelecionador;
import com.autobots.automanager.repositorios.ClienteRepositorio;

/**
 * Regras de negócio relacionadas ao cliente.
 */
@Service
public class ClienteService {

    @Autowired
    private ClienteRepositorio repositorio;

    @Autowired
    private ClienteSelecionador selecionador;

    private final ClienteAtualizador atualizador = new ClienteAtualizador();

    public Cliente criar(ClienteDTO dto) {
        Cliente cliente = montar(dto);
        cliente.setDataCadastro(new Date());
        return repositorio.save(cliente);
    }

    public List<Cliente> listar() {
        return repositorio.findAll();
    }

    public Cliente buscarPorId(Long id) {
        Cliente cliente = selecionador.selecionar(repositorio.findAll(), id);
        if (cliente == null) {
            throw new RecursoNaoEncontradoException("Nenhum cliente encontrado com o id " + id);
        }
        return cliente;
    }

    public Cliente atualizar(Long id, ClienteDTO dto) {
        Cliente cliente = buscarPorId(id);
        atualizador.atualizar(cliente, montar(dto));
        return repositorio.save(cliente);
    }

    public void deletar(Long id) {
        repositorio.delete(buscarPorId(id));
    }

    private Cliente montar(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setNomeSocial(dto.getNomeSocial());
        cliente.setDataNascimento(dto.getDataNascimento());

        if (dto.getDocumentos() != null) {
            dto.getDocumentos().forEach(item -> {
                Documento documento = item.paraEntidade();
                documento.setCliente(cliente);
                cliente.getDocumentos().add(documento);
            });
        }

        if (dto.getEndereco() != null) {
            cliente.setEndereco(dto.getEndereco().paraEntidade());
        }

        if (dto.getTelefones() != null) {
            dto.getTelefones().forEach(item -> {
                Telefone telefone = item.paraEntidade();
                telefone.setCliente(cliente);
                cliente.getTelefones().add(telefone);
            });
        }

        return cliente;
    }
}

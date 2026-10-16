package com.autobots.automanager.modelo;

import com.autobots.automanager.entidades.Cliente;

public class ClienteAtualizador {

    private StringVerificadorNulo verificador = new StringVerificadorNulo();
    private EnderecoAtualizador enderecoAtualizador = new EnderecoAtualizador();
    private DocumentoAtualizador documentoAtualizador = new DocumentoAtualizador();
    private TelefoneAtualizador telefoneAtualizador = new TelefoneAtualizador();

    private void atualizarDados(Cliente alvo, Cliente novo) {
        if (!verificador.verificar(novo.getNome())) {
            alvo.setNome(novo.getNome());
        }
        if (!verificador.verificar(novo.getNomeSocial())) {
            alvo.setNomeSocial(novo.getNomeSocial());
        }
        if (novo.getDataCadastro() != null) {
            alvo.setDataCadastro(novo.getDataCadastro());
        }
        if (novo.getDataNascimento() != null) {
            alvo.setDataNascimento(novo.getDataNascimento());
        }
    }

    public void atualizar(Cliente cliente, Cliente atualizacao) {
        atualizarDados(cliente, atualizacao);
        if (atualizacao.getEndereco() != null) {
            if (cliente.getEndereco() == null) {
                cliente.setEndereco(atualizacao.getEndereco());
            } else {
                enderecoAtualizador.atualizar(cliente.getEndereco(), atualizacao.getEndereco());
            }
        }
        documentoAtualizador.atualizar(cliente.getDocumentos(), atualizacao.getDocumentos());
        telefoneAtualizador.atualizar(cliente.getTelefones(), atualizacao.getTelefones());
    }
}

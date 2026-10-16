package com.autobots.automanager.dto;

import javax.validation.constraints.NotBlank;

import com.autobots.automanager.entidades.Endereco;

import lombok.Data;

/**
 * Dados de entrada de um endereço.
 */
@Data
public class EnderecoDTO {

    private String estado;

    @NotBlank(message = "A cidade deve ser informada")
    private String cidade;

    private String bairro;

    @NotBlank(message = "O número do endereço deve ser informado")
    private String numero;

    @NotBlank(message = "A rua deve ser informada")
    private String rua;

    private String codigoPostal;

    private String informacoesAdicionais;

    public Endereco paraEntidade() {
        Endereco endereco = new Endereco();
        endereco.setEstado(estado);
        endereco.setCidade(cidade);
        endereco.setBairro(bairro);
        endereco.setRua(rua);
        endereco.setNumero(numero);
        endereco.setCodigoPostal(codigoPostal);
        endereco.setInformacoesAdicionais(informacoesAdicionais);
        return endereco;
    }
}

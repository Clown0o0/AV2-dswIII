package com.autobots.automanager.dto;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Dados de entrada para cadastro/atualização de cliente.
 */
@Data
public class ClienteDTO {

    @NotBlank(message = "O nome do cliente deve ser informado")
    private String nome;

    private String nomeSocial;

    @NotNull(message = "A data de nascimento deve ser informada")
    private Date dataNascimento;

    @Valid
    private List<DocumentoDTO> documentos;

    @Valid
    private EnderecoDTO endereco;

    @Valid
    private List<TelefoneDTO> telefones;
}

package com.autobots.automanager.dto;

import javax.validation.constraints.NotBlank;

import com.autobots.automanager.entidades.Telefone;

import lombok.Data;

/**
 * Dados de entrada de um telefone.
 */
@Data
public class TelefoneDTO {

    @NotBlank(message = "O DDD do telefone deve ser informado")
    private String ddd;

    @NotBlank(message = "O número do telefone deve ser informado")
    private String numero;

    public Telefone paraEntidade() {
        Telefone telefone = new Telefone();
        telefone.setDdd(ddd);
        telefone.setNumero(numero);
        return telefone;
    }
}

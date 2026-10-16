package com.autobots.automanager.dto;

import javax.validation.constraints.NotBlank;

import com.autobots.automanager.entidades.Documento;

import lombok.Data;

/**
 * Dados de entrada de um documento.
 */
@Data
public class DocumentoDTO {

    @NotBlank(message = "O tipo do documento deve ser informado")
    private String tipo;

    @NotBlank(message = "O número do documento deve ser informado")
    private String numero;

    public Documento paraEntidade() {
        Documento documento = new Documento();
        documento.setTipo(tipo);
        documento.setNumero(numero);
        return documento;
    }
}

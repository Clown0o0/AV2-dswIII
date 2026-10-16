package com.autobots.automanager.modelo;

public class StringVerificadorNulo {

    /**
     * @return true quando o texto é nulo ou está em branco.
     */
    public boolean verificar(String dado) {
        return dado == null || dado.isBlank();
    }
}

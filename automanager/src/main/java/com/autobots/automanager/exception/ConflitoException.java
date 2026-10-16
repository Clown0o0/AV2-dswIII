package com.autobots.automanager.exception;

/**
 * Lançada quando a operação conflita com o estado atual do recurso (HTTP 409).
 */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}

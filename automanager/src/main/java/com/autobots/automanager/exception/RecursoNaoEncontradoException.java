package com.autobots.automanager.exception;

/**
 * Lançada quando um registro buscado pelo id não existe (HTTP 404).
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

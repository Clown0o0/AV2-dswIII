package com.autobots.automanager.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Centraliza o tratamento de erros da API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Erros de validação: retorna um mapa campo -> mensagem (400). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> falhas = new HashMap<>();

        ex.getBindingResult()
          .getFieldErrors()
          .forEach(campo -> falhas.put(campo.getField(), campo.getDefaultMessage()));

        return falhas;
    }

    /** Registro inexistente (404). */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return montarResposta(ex.getMessage());
    }

    /** JSON malformado ou com tipos inválidos (400). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarJsonInvalido(HttpMessageNotReadableException ex) {
        return montarResposta("Corpo da requisição inválido ou mal formatado");
    }

    /** Violação de restrição do banco, ex.: número de documento repetido (409). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> tratarConflito(DataIntegrityViolationException ex) {
        return montarResposta("Já existe um registro com esses dados");
    }

    /** Parâmetro de rota com tipo inválido, ex.: id não numérico (400). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return montarResposta("Valor inválido para o parâmetro '" + ex.getName() + "'");
    }

    /** Operação em conflito com o estado atual do recurso (409). */
    @ExceptionHandler(ConflitoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> tratarConflitoNegocio(ConflitoException ex) {
        return montarResposta(ex.getMessage());
    }

    /** Erros de execução em geral (500). */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> tratarRuntime(RuntimeException ex) {
        return montarResposta(ex.getMessage());
    }

    private Map<String, String> montarResposta(String mensagem) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("Erro", mensagem);
        return resposta;
    }
}

package br.com.bruno.spring_boot_essentials.exception;

public class ElemetoDuplicadoException extends RuntimeException {
    public ElemetoDuplicadoException(String message) {
        super(message);
    }
}

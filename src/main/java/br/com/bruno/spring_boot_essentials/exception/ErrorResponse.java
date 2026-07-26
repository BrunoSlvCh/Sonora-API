package br.com.bruno.spring_boot_essentials.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String erro
) {}

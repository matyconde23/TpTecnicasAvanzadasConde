package com.turnos.app.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CancelarTurnoException extends RuntimeException {
    public CancelarTurnoException(String message) {
        super(message);
    }

    public CancelarTurnoException(String message, Throwable cause) {
        super(message, cause);
    }
}
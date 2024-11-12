package com.turnos.app.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReservaTurnoException extends RuntimeException {
    public ReservaTurnoException(String message) {
        super(message);
    }

    public ReservaTurnoException(String message, Throwable cause) {
        super(message, cause);
    }
}

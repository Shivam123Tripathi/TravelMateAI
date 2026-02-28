package com.travelmateai.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when seats are not available for booking.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientSeatsException extends RuntimeException {

    public InsufficientSeatsException(String message) {
        super(message);
    }

    public InsufficientSeatsException(int requested, int available) {
        super(String.format("Cannot book %d seats. Only %d seats available.", requested, available));
    }
}

package common.ru.itmo.se.exceptions;

import lombok.Getter;

@Getter
public class OpeningServerSocketException extends RuntimeException {

    private final String message;

    public OpeningServerSocketException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}

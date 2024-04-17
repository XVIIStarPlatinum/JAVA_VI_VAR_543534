package common.ru.itmo.se.exceptions;

import lombok.Getter;

@Getter
public class ConnectionErrorException extends RuntimeException {

    private final String message;

    public ConnectionErrorException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}

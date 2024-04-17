package common.ru.itmo.se.exceptions;

import lombok.Getter;

@Getter
public class ClosingSocketException extends RuntimeException {

    private final String message;

    public ClosingSocketException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}

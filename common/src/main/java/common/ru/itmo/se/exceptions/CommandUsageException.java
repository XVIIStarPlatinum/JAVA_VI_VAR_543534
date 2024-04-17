package common.ru.itmo.se.exceptions;

import lombok.Getter;

@Getter
public class CommandUsageException extends RuntimeException {

    private final String message;

    public CommandUsageException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}

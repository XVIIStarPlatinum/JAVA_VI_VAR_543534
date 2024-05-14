package common.ru.itmo.se.exceptions;

import lombok.Getter;

/**
 * Exception class for connection error.
 */
@Getter
public class ConnectionErrorException extends RuntimeException {
    /**
     * This field holds the exception's message.
     * -- GETTER --
     * Getter method for the exception's message.
     */
    private final String message;
    /**
     * Constructs an EmptyCollectionException with the specified message and cause.
     *
     * @param message the specified message.
     * @param cause   the specified Throwable object.
     */
    public ConnectionErrorException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}

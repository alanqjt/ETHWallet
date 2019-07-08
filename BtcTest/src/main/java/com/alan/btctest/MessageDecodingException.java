package com.alan.btctest;

/**
 * Encoding exception.
 */
public class MessageDecodingException extends RuntimeException {
    public MessageDecodingException(String message) {
        super(message);
    }

    public MessageDecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}

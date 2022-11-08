package org.blockchain_innovation.accumulate.factombridge.impl;

public class KeyNotFoundException extends Exception {
    public KeyNotFoundException() {
    }

    public KeyNotFoundException(final String message) {
        super(message);
    }

    public KeyNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public KeyNotFoundException(final Throwable cause) {
        super(cause);
    }

    public KeyNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

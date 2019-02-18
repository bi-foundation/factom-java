package org.blockchain_innovation.factom.identiy.did.entry;

import did.parser.ParserException;

public class DIDRuntimeException extends RuntimeException {
    public DIDRuntimeException() {
    }

    public DIDRuntimeException(String message) {
        super(message);
    }

    public DIDRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DIDRuntimeException(Throwable cause) {
        super(cause);
    }

    public DIDRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static class ParseException extends DIDRuntimeException {
        public ParseException(ParserException cause) {
            super(cause);
        }
    }
}

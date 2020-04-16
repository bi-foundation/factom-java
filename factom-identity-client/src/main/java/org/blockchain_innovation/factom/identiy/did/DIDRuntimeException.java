package org.blockchain_innovation.factom.identiy.did;

import did.parser.ParserException;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;

public class DIDRuntimeException extends FactomRuntimeException {

    public DIDRuntimeException(String message) {
        super(message);
    }

    public DIDRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DIDRuntimeException(Throwable cause) {
        super(cause);
    }

    public static class ParseException extends DIDRuntimeException {
        public ParseException(ParserException cause) {
            super(cause);
        }
    }
}

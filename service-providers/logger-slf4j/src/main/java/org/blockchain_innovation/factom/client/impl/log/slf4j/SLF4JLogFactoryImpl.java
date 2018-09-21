package org.blockchain_innovation.factom.client.impl.log.slf4j;

import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.slf4j.LoggerFactory;

public class SLF4JLogFactoryImpl implements LogFactory {

    public static final String NAME = "SLF4J";

    @Override
    public Logger getInstance(String name) {
        return new SLF4JLoggerImpl(LoggerFactory.getLogger(name));
    }

    @Override
    public Logger getInstance(Class<?> clazz) {
        return new SLF4JLoggerImpl(LoggerFactory.getLogger(clazz));
    }

    @Override
    public String getEngine() {
        return NAME;
    }
}

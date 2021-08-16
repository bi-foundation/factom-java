package org.blockchain_innovation.factom.client.impl.log.slf4j;

import org.blockchain_innovation.factom.client.api.log.Logger;

public class SLF4JLoggerImpl implements Logger {

    private final org.slf4j.Logger impl;

    public SLF4JLoggerImpl(org.slf4j.Logger impl) {
        this.impl = impl;
    }

    @Override
    public void debug(String message, Object... args) {
        impl.debug(String.format(message, args));
    }

    @Override
    public void debug(String message, Throwable throwable, Object... args) {
        impl.debug(String.format(message, args), throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return impl.isDebugEnabled();
    }

    @Override
    public void info(String message, Object... args) {
        impl.info(String.format(message, args));
    }

    @Override
    public void info(String message, Throwable throwable, Object... args) {
        impl.info(String.format(message, args), throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return impl.isInfoEnabled();
    }

    @Override
    public void warn(String message, Object... args) {
        impl.warn(String.format(message, args));
    }

    @Override
    public void warn(String message, Throwable throwable, Object... args) {
        impl.warn(String.format(message, args), throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return impl.isWarnEnabled();
    }

    @Override
    public void error(String message, Object... args) {
        impl.error(String.format(message, args));
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        impl.error(String.format(message, args), throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return impl.isErrorEnabled();
    }
}
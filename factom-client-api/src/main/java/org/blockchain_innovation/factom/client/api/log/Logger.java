package org.blockchain_innovation.factom.client.api.log;

public interface Logger {

    void debug(String message, Object... args);

    void debug(String message, Throwable throwable, Object... args);

    boolean isDebugEnabled();

    void info(String message, Object... args);

    void info(String message, Throwable throwable, Object... args);

    boolean isInfoEnabled();

    void warn(String message, Object... args);

    void warn(String message, Throwable throwable, Object... args);

    boolean isWarnEnabled();

    void error(String message, Object... args);

    void error(String message, Throwable throwable, Object... args);

    boolean isErrorEnabled();
}

package org.blockchain_innovation.factom.client.api.log;

/**
 * The logger interface is used to support different logging frameworks as SPIs.
 */
public interface Logger {

    /**
     * Log a debug message.
     *
     * @param message The message to log.
     * @param args    Optional arguments and variables. It is up to the implementation whether the variables are supported.
     */
    void debug(String message, Object... args);


    /**
     * Log a debug message together with a throwable.
     *
     * @param message   The message to log.
     * @param throwable The throwable
     * @param args      Optional arguments and variables. It is up to the implementation whether the variables are supported.
     */
    void debug(String message, Throwable throwable, Object... args);

    /**
     * Returns whether debug logging level is enabled in the application.
     *
     * @return debug logging is enabled or not.
     */
    boolean isDebugEnabled();


    /**
     * Log an info message.
     *
     * @param message The message to log.
     * @param args    Optional arguments and variables. It is up to the implementation whether the variables are supported.
     */
    void info(String message, Object... args);

    /**
     * Log an info message together with a throwable.
     *
     * @param message   The message to log.
     * @param throwable The throwable
     * @param args      Optional arguments and variables. It is up to the implementation whether the variables are supported.
     */
    void info(String message, Throwable throwable, Object... args);


    /**
     * Returns whether info logging level is enabled in the application.
     *
     * @return info logging is enabled or not.
     */
    boolean isInfoEnabled();


    /**
     * Log a warning message.
     *
     * @param message The message to log.
     * @param args    Optional arguments and variables. It is up to the implementation whether the variables are supported.
     */
    void warn(String message, Object... args);

    /**
     * Log a warning message together with a throwable.
     *
     * @param message   The message to log.
     * @param throwable The throwable
     * @param args      Optional arguments and variables. It is up to the implementation whether the variables are supported.
     */
    void warn(String message, Throwable throwable, Object... args);


    /**
     * Returns whether warning logging level is enabled in the application.
     *
     * @return warning logging is enabled or not.
     */
    boolean isWarnEnabled();


    /**
     * Log an error message.
     *
     * @param message The message to log.
     * @param args    Optional arguments and variables. It is up to the implementation whether the variables are supported.
     */
    void error(String message, Object... args);

    /**
     * Log an error message together with a throwable.
     *
     * @param message   The message to log.
     * @param throwable The throwable
     * @param args      Optional arguments and variables. It is up to the implementation whether the variables are supported.
     */
    void error(String message, Throwable throwable, Object... args);


    /**
     * Returns whether error logging level is enabled in the application.
     *
     * @return error logging is enabled or not.
     */
    boolean isErrorEnabled();
}

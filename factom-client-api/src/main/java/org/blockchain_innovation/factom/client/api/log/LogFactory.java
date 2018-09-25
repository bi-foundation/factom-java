package org.blockchain_innovation.factom.client.api.log;

import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;

import java.util.ServiceLoader;

/**
 * A logger abstraction (Service Provider Interface), to allow plugging in different logging libraries and not let this library pull in any unwanted logging library.
 */
public interface LogFactory {

    /**
     * Gets the logger by name. This is used internally and not called by the end user.
     *
     * @param name The logger name.
     * @return The logger connected to the name
     */
    Logger newInstance(String name);

    /**
     * Gets the logger by class. This is used internally and not called by the end user.
     *
     * @param clazz The class to retrieve the logger for.
     * @return The logger connected to the class.
     */
    Logger newInstance(Class<?> clazz);

    /**
     * Gets the engine name of the logger implementation.
     *
     * @return
     */
    String getEngine();

    /**
     * Allows the retrieval of the logger implemententation for the name. This is the logger name and not the SPI engine name! This is the method the end user calls.
     *
     * @param name The logger name.
     * @return The logger from the SPI implementation connected to the logger name
     */
    static Logger getLogger(String name) {
        return Provider.getFactory().newInstance(name);
    }

    /**
     * Allows the retrieval of the logger implemententation for the name. This is the logger name and not the SPI engine name! This is the method the end user calls.
     *
     * @param clazz The logger class.
     * @return The logger from the SPI implementation connected to the logger class
     */
    static Logger getLogger(Class<?> clazz) {
        return Provider.getFactory().newInstance(clazz);
    }

    /**
     * The SPI work is being done in the provider
     */
    class Provider {
        static LogFactory logFactory = serviceLoader(false).iterator().next();

        static {
            assertRegistered(false);
        }

        /**
         * Get the correct LogFactory implementation from the SPI implementation on the classpath
         *
         * @return
         */
        public static LogFactory getFactory() {
            return logFactory;
        }

        /**
         * Gets the logfactory implementation by SPI enging name
         *
         * @param engine The engine SPI name of the implementation. eg: SLF4J
         * @return
         */
        public static LogFactory getFactory(String engine) {
            for (LogFactory logFactory : serviceLoader(false)) {
                if (logFactory.getEngine().equalsIgnoreCase(engine)) {
                    return logFactory;
                }

            }
            throw new FactomRuntimeException(String.format("Could not find Log Factory engine %s. Please make sure a Factom logger jar is on the classpath.", engine));
        }

        /**
         * Asserts whether at least one LogFactory is registered.
         *
         * @param throwErrors Throw exceptions when nothing is found
         */
        private static void assertRegistered(boolean throwErrors) {
            ServiceLoader<LogFactory> loggers = serviceLoader(false);
            if (!loggers.iterator().hasNext()) {
                loggers.reload();
                if (!loggers.iterator().hasNext()) {
                    String error = "No Factom Logger class has been registered. Please make sure a Factom logger jar is on the classpath. Logging will be disabled";
                    if (throwErrors) {
                        throw new FactomRuntimeException(error);
                    } else {
                        System.err.println(error);
                    }

                }
            }
        }

        /**
         * Rertieve the service loader.
         *
         * @param reload Refresh the loader
         * @return
         */
        private static ServiceLoader<LogFactory> serviceLoader(boolean reload) {
            ServiceLoader<LogFactory> loader = ServiceLoader.load(LogFactory.class);
            if (reload) {
                loader.reload();
            }
            return loader;
        }

    }
}

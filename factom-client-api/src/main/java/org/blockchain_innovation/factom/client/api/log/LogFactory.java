package org.blockchain_innovation.factom.client.api.log;

import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;

import java.util.ServiceLoader;

public interface LogFactory {

    Logger getInstance(String name);

    Logger getInstance(Class<?> clazz);

    String getEngine();

    static Logger getLogger(String name) {
        return Provider.getFactory().getInstance(name);
    }

    static Logger getLogger(Class<?> clazz) {
        return Provider.getFactory().getInstance(clazz);
    }

    class Provider {
        static LogFactory logFactory;
        static {
            assertRegistered(false);
        }

        public static LogFactory getFactory() {
            if (logFactory == null) {
                logFactory = serviceLoader(false).iterator().next();
            }
            return logFactory;
        }

        public static LogFactory getFactory(String engine) {
            for (LogFactory logFactory : serviceLoader(false)) {
                if (logFactory.getEngine().equalsIgnoreCase(engine)) {
                    return logFactory;
                }

            }
            throw new FactomRuntimeException(String.format("Could not find Log Factory engine %s. Please make sure a Factom logger jar is on the classpath.", engine));
        }

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

        private static ServiceLoader<LogFactory> serviceLoader(boolean reload) {
            ServiceLoader<LogFactory> loader = ServiceLoader.load(LogFactory.class);
            if (reload) {
                loader.reload();
            }
            return loader;
        }

    }
}

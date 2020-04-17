/*
 * Copyright 2018 Blockchain Innovation Foundation <https://blockchain-innovation.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.blockchain_innovation.factom.client.impl.settings;


import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.blockchain_innovation.factom.client.api.ops.StringUtils;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class RpcSettingsImpl implements RpcSettings {
    private SubSystem subSystem;
    private Server server;
    private static final Logger logger = LogFactory.getLogger(RpcSettings.class);


    public RpcSettingsImpl(SubSystem subSystem, Server server) {
        this.subSystem = subSystem;
        this.server = server;
    }

    public RpcSettingsImpl(SubSystem subSystem, Properties properties) {
        this(subSystem, new ServerImpl(subSystem, properties));
    }

    public RpcSettingsImpl(SubSystem subSystem, Map<String, String> properties) {
        this(subSystem, new ServerImpl(subSystem, properties));
    }


    protected static String constructKey(SubSystem subSystem, String key) {
        return (subSystem.configKey() + "." + key).toLowerCase(Locale.getDefault());
    }

    @Override
    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public Proxy getProxy() {
        return null;
    }


    @Override
    public SubSystem getSubSystem() {
        return subSystem;
    }

    public RpcSettings setSubSystem(SubSystem subSystem) {
        this.subSystem = subSystem;
        return this;
    }


    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public static class ServerImpl implements Server {

        private URL url;
        private String username;
        private String password;
        private int timeout = 30;

        public ServerImpl(SubSystem subSystem) {
            initProperties(subSystem, new Properties());
        }

        public ServerImpl(SubSystem subSystem, Properties properties) {
            initProperties(subSystem, properties);
        }

        public ServerImpl(SubSystem subSystem, Map<String, String> map) {
            Properties properties = new Properties();
            map.entrySet().forEach(entry ->
                    properties.setProperty(entry.getKey().replaceAll("_", "."), entry.getValue())
            );
            initProperties(subSystem, properties);
        }

        private void initProperties(SubSystem subSystem, Properties properties) {
            // Defaults for URLs point to openAPI for factomd and a local walletd
            setURL(getFromPropertiesOrEnvironment(subSystem, "url", properties, subSystem == SubSystem.FACTOMD ? "https://api.factomd.net/v2" : "http://localhost:8089/v2"));

            setTimeout(getFromPropertiesOrEnvironment(subSystem, "timeout", properties, "30"));
            setUsername(getFromPropertiesOrEnvironment(subSystem, "username", properties, null));
            setPassword(getFromPropertiesOrEnvironment(subSystem, "password", properties, null));
        }

        private String getFromPropertiesOrEnvironment(SubSystem subSystem, String propertyKey, Properties properties, String defaultValue) {
            String key = constructKey(subSystem, propertyKey);
            String value = properties.getProperty(key);
            if (StringUtils.isEmpty(value)) {
                value = System.getProperty(key);
                if (StringUtils.isEmpty(value)) {
                    value = System.getProperty(key.replaceAll("\\.", "_"));
                }
            }
            if (StringUtils.isEmpty(value)) {
                value = System.getenv(key);
                if (StringUtils.isEmpty(value)) {
                    value = System.getenv(key.replaceAll("\\.", "_"));
                }
            }
            if (StringUtils.isEmpty(value)) {
                value = defaultValue;
            }
            logger.info(subSystem.configKey() + " (config): " + key + "=" + (key.contains("pass") ? "xxxx" : value));
            return value;
        }

        @Override
        public URL getURL() {
            return url;
        }

        public Server setURL(String url) {
            try {
                setURL(new URL(url));
            } catch (MalformedURLException e) {
                throw new FactomRuntimeException.AssertionException("Invalid URL supplied for connection: " + url, e);
            }
            return this;
        }

        public Server setURL(URL url) {
            this.url = url;
            return this;
        }

        @Override
        public String getUsername() {
            return username;
        }

        public Server setUsername(String username) {
            this.username = username;
            return this;
        }

        @Override
        public String getPassword() {
            return password;
        }

        public Server setPassword(String password) {
            this.password = password;
            return this;
        }

        @Override
        public int getTimeout() {
            return timeout;
        }

        public Server setTimeout(String timeout) {
            if (StringUtils.isNotEmpty(timeout)) {
                this.timeout = Integer.parseInt(timeout);
            }
            return this;
        }
    }
}

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


import org.blockchain_innovation.factom.client.api.SigningMode;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.ops.StringUtils;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class RpcSettingsImpl implements RpcSettings {
    private SubSystem subSystem;
    private Server server;
    private static final Logger logger = LogFactory.getLogger(RpcSettings.class);
    private SigningMode signingMode;
    private Address defaultECAddress;

    public RpcSettingsImpl(SubSystem subSystem, Server server) {
        this.subSystem = subSystem;
        this.server = server;
        this.signingMode = SigningMode.ONLINE_WALLETD;
    }

    private RpcSettingsImpl(SubSystem subSystem, Server server, Properties properties) {
        this.subSystem = subSystem;
        this.server = server;
        this.signingMode = getSigningModeFromPropertiesOrEnvironment(properties, server.getNetworkName() == null ? Optional.empty() : server.getNetworkName());
        this.defaultECAddress = getDefaultECAddressFromPropertiesOrEnvironment(properties, server.getNetworkName() == null ? Optional.empty() : server.getNetworkName());
    }

    public RpcSettingsImpl(SubSystem subSystem, Server server, SigningMode signingMode) {
        this.subSystem = subSystem;
        this.server = server;
        this.signingMode = signingMode;
    }


    @Deprecated
    public RpcSettingsImpl(SubSystem subSystem, Properties properties) {
        this(subSystem, properties, Optional.empty());
    }

    public RpcSettingsImpl(SubSystem subSystem, Properties properties, Optional<String> networkName) {
        this(subSystem, new ServerImpl(subSystem, properties, networkName), properties);
    }

    @Deprecated
    public RpcSettingsImpl(SubSystem subSystem, Map<String, String> properties) {
        this(subSystem, properties, Optional.empty());
    }

    public RpcSettingsImpl(SubSystem subSystem, Map<String, String> properties, Optional<String> networkName) {
        this(subSystem, new ServerImpl(subSystem, properties, networkName), mapToProperties(properties));
    }


    @Override
    public Server getServer() {
        return server;
    }

    public RpcSettingsImpl setServer(Server server) {
        this.server = server;
        return this;
    }

    @Override
    public Proxy getProxy() {
        return null;
    }


    @Override
    public SubSystem getSubSystem() {
        return subSystem;
    }

    public RpcSettingsImpl setSubSystem(SubSystem subSystem) {
        this.subSystem = subSystem;
        return this;
    }

    @Override
    public SigningMode getSigningMode() {
        return signingMode;
    }

    @Override
    public Optional<Address> getDefaultECAddress() {
        return Optional.ofNullable(defaultECAddress);
    }

    public RpcSettingsImpl setDefaultECAddress(String address) {
        return StringUtils.isEmpty(address) ? this : setDefaultECAddress(Address.fromString(address));
    }

    public RpcSettingsImpl setDefaultECAddress(Address address) {
        if (address != null && !(address.getType() == AddressType.ENTRY_CREDIT_SECRET || address.getType() == AddressType.ENTRY_CREDIT_PUBLIC)) {
            throw new FactomRuntimeException("Invalid address type supplied for the default EC address: " + address.getType());
        }
        this.defaultECAddress = address;
        return this;
    }

    public RpcSettingsImpl setSigningMode(SigningMode signingMode) {
        this.signingMode = signingMode;
        return this;
    }


    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public static class ServerImpl implements Server {

        private Properties properties;

        private URL url;
        private String username;
        private String password;
        private int timeout = 30;
        private Optional<String> networkName;

        @Deprecated
        public ServerImpl(SubSystem subSystem) {
            this(subSystem, Optional.empty());
        }

        public ServerImpl(SubSystem subSystem, Optional<String> networkName) {
            this.networkName = networkName;
            initProperties(subSystem, new Properties(), networkName);
        }

        @Deprecated
        public ServerImpl(SubSystem subSystem, Properties properties) {
            this(subSystem, properties, Optional.empty());
        }

        public ServerImpl(SubSystem subSystem, Properties properties, Optional<String> networkName) {
            this.networkName = networkName;
            initProperties(subSystem, properties, networkName);
        }

        @Deprecated
        public ServerImpl(SubSystem subSystem, Map<String, String> map) {
            this(subSystem, map, Optional.empty());
        }

        public ServerImpl(SubSystem subSystem, Map<String, String> map, Optional<String> networkName) {
            this.networkName = networkName;
            Properties properties = mapToProperties(map);
            initProperties(subSystem, properties, networkName);
        }

        private Properties initProperties(SubSystem subSystem, Properties properties, Optional<String> networkName) {
            this.properties = properties;
            // Defaults for URLs point to openAPI for factomd and a local walletd
            setURL(getFromPropertiesOrEnvironment(subSystem, "url", properties, subSystem == SubSystem.FACTOMD ? "https://api.factomd.net/v2" : "http://localhost:8089/v2", networkName));

            setTimeout(getFromPropertiesOrEnvironment(subSystem, "timeout", properties, "30", networkName));
            setUsername(getFromPropertiesOrEnvironment(subSystem, "username", properties, null, networkName));
            setPassword(getFromPropertiesOrEnvironment(subSystem, "password", properties, null, networkName));
            return properties;
        }

        protected Properties getProperties() {
            return properties;
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

        @Override
        public Optional<String> getNetworkName() {
            return networkName;
        }

        public Server setNetworkName(String networkName) {
            this.networkName = Optional.ofNullable(networkName);
            return this;
        }
    }


    @Deprecated
    protected static String constructKey(SubSystem subSystem, String key) {
        return constructKey(subSystem, key, Optional.empty());
    }


    protected static Properties mapToProperties(Map<String, String> map) {
        Properties properties = new Properties();
        map.entrySet().forEach(entry ->
                properties.setProperty(entry.getKey().replaceAll("_", "."), entry.getValue())
        );
        return properties;
    }


    protected static String getFromPropertiesOrEnvironment(SubSystem subSystem, String propertyKey, Properties properties, String defaultValue, Optional<String> networkName) {
        String key = constructKey(subSystem, propertyKey, networkName);
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

    protected static SigningMode getSigningModeFromPropertiesOrEnvironment(Properties properties, Optional<String> networkName) {
        return SigningMode.fromModeString(
                getFromPropertiesOrEnvironment(SubSystem.WALLETD, "signing-mode", properties, SigningMode.ONLINE_WALLETD.name(), networkName));
    }

    protected static Address getDefaultECAddressFromPropertiesOrEnvironment(Properties properties, Optional<String> networkName) {
        String address = getFromPropertiesOrEnvironment(SubSystem.WALLETD, "ec-address", properties, null, networkName);
        if (address == null) {
            address = getFromPropertiesOrEnvironment(SubSystem.WALLETD, "entry-credit-address", properties, null, networkName);
        }
        return address == null ? null : Address.fromString(address);
    }

    protected static String constructKey(SubSystem subSystem, String key, Optional<String> networkName) {
        return String.format("%s%s.%s", (networkName.isPresent() && !"".equals(networkName.get().trim())) ? networkName.get() + "." : "", subSystem.configKey(), key).toLowerCase(Locale.getDefault());
    }


}

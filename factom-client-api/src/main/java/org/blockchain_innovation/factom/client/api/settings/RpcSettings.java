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

package org.blockchain_innovation.factom.client.api.settings;

import org.blockchain_innovation.factom.client.api.SigningMode;
import org.blockchain_innovation.factom.client.api.model.ECAddress;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;

/**
 * The settings like URL, proxy for the factomd and walletd clients.
 */
public interface RpcSettings {
    SigningMode getSigningMode();
    Optional<ECAddress> getDefaultECAddress();

    /**
     * The subsystem to apply this configuration to.
     */
    enum SubSystem {
        FACTOMD, WALLETD, INDEXDB;

        /**
         * Translates the sybsystem to a configuration value key.
         *
         * @return configKey
         */
        public String configKey() {
            return name().toLowerCase(Locale.getDefault());
        }
    }

    /**
     * Get the appropriate subsystem.
     *
     * @return The subsystem.
     */
    SubSystem getSubSystem();

    /**
     * Get the server settings.
     *
     * @return Server settings.
     */
    Server getServer();

    /**
     * Gets proxy settings.
     *
     * @return Proxy settings.
     */
    Proxy getProxy();

    /**
     * Represents the server settings for the factomd/walletd clients.
     */
    interface Server {
        /**
         * Gets the URL of factomd/walletd V2 API the clients needs to connect to.
         *
         * @return The URL of factomd/walletd V2 API.
         */
        URL getURL();

        /**
         * Gets the username for Basic Auth to the API.
         *
         * @return The username.
         */
        String getUsername();

        /**
         * Gets the password for Basic Auth to the API.
         *
         * @return The password.
         */
        String getPassword();

        /**
         * Gets the timeout for connections to factomd/walletd.
         *
         * @return The timeout for connections.
         */
        int getTimeout();

        Optional<String> getNetworkName();
    }

    /**
     * Represnets the proxy settings for the client.
     */
    interface Proxy {
        /**
         * Gets the host of the proxy server.
         *
         * @return The proxy server.
         */
        String getHost();

        /**
         * The port of the proxy server.
         *
         * @return Proxy server port.
         */
        int getPort();
    }
}

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

package org.blockchain_innovation.factom.client.settings;

import org.blockchain_innovation.factom.client.data.FactomRuntimeException;
import org.blockchain_innovation.factom.client.data.conversion.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class RpcSettingsImpl implements RpcSettings {
    private SubSystem subSystem;
    private Server server;


    public RpcSettingsImpl(SubSystem subSystem, Properties properties) {
        setSubSystem(subSystem);
        setServer(new ServerImpl(subSystem, properties));
    }

    protected static String constructKey(SubSystem subSystem, String key) {
        return (subSystem.configKey() + "." + key).toLowerCase();
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


    public static class ServerImpl implements Server {

        private URL url;
        private String username;
        private String password;
        private int timeout = 30;

        public ServerImpl(SubSystem subSystem) {
            setURL("http://localhost:" + (subSystem == SubSystem.FACTOMD ? 8088 : 8089) + "/v2");
        }

        public ServerImpl(SubSystem subSystem, Properties properties) {
            setURL(properties.getProperty(constructKey(subSystem, "url"), "http://localhost:" + (subSystem == SubSystem.FACTOMD ? 8088 : 8089) + "/v2"));

            setTimeout(properties.getProperty(constructKey(subSystem, "timeout"), "30"));
            setUsername(properties.getProperty(constructKey(subSystem, "username"), null));
            setPassword(properties.getProperty(constructKey(subSystem, "password"), null));
        }

        @Override
        public URL getURL() {
            return url;
        }

        public Server setURL(String url) {
            try {
                setURL(new URL(url));
            } catch (MalformedURLException e) {
                throw new FactomRuntimeException.AssertionException("Invalid URL supplied for connection: " + url);
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

package org.blockchain_innovation.factom.client.spring.settings;

import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@Configuration
@ConfigurationProperties
public class SpringRpcSettings {
    private final Map<RpcSettings.SubSystem, RpcSettings> settings = new HashMap<>();

    private Factomd factomd;
    private Walletd walletd;

    public static class Factomd extends RpcSettingsImpl.ServerImpl implements RpcSettings.Server {
        private int threads = 5;

        public int getThreads() {
            return threads;
        }

        public void setThreads(int threads) {
            this.threads = threads;
        }

        public Factomd() {
            super(RpcSettings.SubSystem.FACTOMD);
        }
    }

    public static class Walletd extends RpcSettingsImpl.ServerImpl implements RpcSettings.Server {
        private int threads = 5;

        public int getThreads() {
            return threads;
        }

        public void setThreads(int threads) {
            this.threads = threads;
        }

        public Walletd() {
            super(RpcSettings.SubSystem.WALLETD);
        }
    }

    public void setFactomd(Factomd factomd) {
        this.factomd = factomd;
    }

    public void setWalletd(Walletd walletd) {
        this.walletd = walletd;
    }


    public Factomd getFactomdServer() {
        if(factomd == null){
            throw new FactomException.ClientException("Please configure Factomd settings");
        }
        return factomd;
    }

    public Walletd getWalletdServer() {
        if(walletd == null){
            throw new FactomException.ClientException("Please configure Walletd settings");
        }
        return walletd;
    }

    protected RpcSettings getRpcSettings(RpcSettings.SubSystem subSystem) {
        if (!settings.containsKey(subSystem)) {
            Properties properties = new Properties();
//            addProperty(properties, subSystem, "url", url);
//            addProperty(properties, subSystem, "username", username);
//            addProperty(properties, subSystem, "password", password);
//            addProperty(properties, subSystem, "timeout", timeout);

            settings.put(subSystem, new RpcSettingsImpl(subSystem, properties));
        }

        return settings.get(subSystem);
    }


    protected void addProperty(Properties properties, RpcSettings.SubSystem subSystem, String key, Object value) {
        if (value != null) {
            properties.put(constructKey(subSystem, key), value);
        }
    }

    protected String constructKey(RpcSettings.SubSystem subSystem, String key) {
        return (subSystem + "." + key).toLowerCase(Locale.getDefault());
    }

}

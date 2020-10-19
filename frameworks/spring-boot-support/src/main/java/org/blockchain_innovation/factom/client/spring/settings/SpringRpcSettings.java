package org.blockchain_innovation.factom.client.spring.settings;

import org.blockchain_innovation.factom.client.api.SigningMode;
import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.Networks;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Configuration
@ConfigurationProperties
public class SpringRpcSettings {
    private final Map<RpcSettings.SubSystem, RpcSettings> settings = new HashMap<>();

    private Factomd factomd;
    private Walletd walletd;
    private transient Optional<String> networkName = Optional.empty();

    private String ecAddress;

    public Optional<String> getNetworkName() {
        return networkName;
    }

    public void setNetworkName(Optional<String> networkName) {
        Networks.init(new Properties());
        this.networkName = networkName;
        if (factomd != null) {
            syncNames(factomd);
        }
        if (walletd != null) {
            syncNames(walletd);
        }
    }

    public String getEcAddress() {
        return ecAddress;
    }

    public void setEcAddress(String ecAddress) {
        this.ecAddress = ecAddress;
    }

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
        private SigningMode signingMode = SigningMode.ONLINE_WALLETD;

        public int getThreads() {
            return threads;
        }

        public void setThreads(int threads) {
            this.threads = threads;
        }

        public SigningMode getSigningMode() {
            return signingMode;
        }

        public void setSigningMode(String signingMode) {
            setSigningMode(SigningMode.fromModeString(signingMode));
        }

        public void setSigningMode(SigningMode signingMode) {
            this.signingMode = signingMode;
        }

        public Walletd() {
            super(RpcSettings.SubSystem.WALLETD);
        }
    }

    public void setFactomd(Factomd factomd) {
        syncNames(factomd);
        this.factomd = factomd;
    }

    public void setWalletd(Walletd walletd) {
        syncNames(walletd);

        this.walletd = walletd;
    }

    private void syncNames(RpcSettingsImpl.ServerImpl server) {
        if ((getNetworkName() == null || !getNetworkName().isPresent()) && server.getNetworkName() != null && server.getNetworkName().isPresent()) {
            setNetworkName(server.getNetworkName());
        } else if ((server.getNetworkName() == null || !server.getNetworkName().isPresent()) && getNetworkName() != null && getNetworkName().isPresent()) {
            server.setNetworkName(getNetworkName().get());
        }
        if (server.getNetworkName() != null && server.getNetworkName().isPresent() && getNetworkName() != null && getNetworkName().isPresent() && !server.getNetworkName().get().equalsIgnoreCase(getNetworkName().get())) {
            throw new FactomRuntimeException.AssertionException("Cannot use different network names for a walletd and factomd client that belong together");
        }
    }


    public Factomd getFactomd() {
        if (factomd == null) {
            throw new FactomException.ClientException("Please configure Factomd settings");
        }
        return factomd;
    }

    public Walletd getWalletd() {
        if (walletd == null) {
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

            settings.put(subSystem, new RpcSettingsImpl(subSystem, properties, networkName));
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

package org.blockchain_innovation.factom.client.impl;

import org.blockchain_innovation.factom.client.api.FactomdClient;
import org.blockchain_innovation.factom.client.api.LowLevelClient;
import org.blockchain_innovation.factom.client.api.SigningMode;
import org.blockchain_innovation.factom.client.api.WalletdClient;
import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;


public class Networks {

    public static final String MAINNET = "mainnet";
    public static final String TESTNET = "testnet";
    private static Properties properties;

    private static final Set<String> networkNames = new HashSet<>();

    private static final Map<String, FactomdClient> factomdClients = new HashMap<>();
    private static final Map<String, WalletdClient> walletdClients = new HashMap<>();

    private static final Logger logger = LogFactory.getLogger(Networks.class);

    protected Networks() {
    }

    public static void register(WalletdClient walletdClient) {
        assertSettings(walletdClient.lowLevelClient().getSettings());
        String networkName = getKey(walletdClient.lowLevelClient());
        walletdClients.put(networkName, walletdClient);
        networkNames.add(networkName);
        logger.info(String.format("Network: %s, registered walletd client %s", networkName,
                walletdClient.lowLevelClient().getSettings().getSigningMode() == SigningMode.OFFLINE ? " (offline mode)" : " using " + walletdClient.lowLevelClient().getSettings().getServer().getURL()));
    }

    public static void register(FactomdClient factomdClient) {
        assertSettings(factomdClient.lowLevelClient().getSettings());
        String networkName = getKey(factomdClient.lowLevelClient());
        factomdClients.put(networkName, factomdClient);
        networkNames.add(networkName);
        logger.info(String.format("Network: %s, registered factomd client using %s", networkName, factomdClient.lowLevelClient().getSettings().getServer().getURL()));
    }

    public static boolean hasFactomd(String networkName) {
        String key = networkKey(Optional.ofNullable(networkName));
        return factomdClients.containsKey(key);
    }

    public static boolean hasWalletd(String networkName) {
        String key = networkKey(Optional.ofNullable(networkName));
        return walletdClients.containsKey(key);
    }

    private static String getKey(LowLevelClient lowLevelClient) {
        Optional<String> networkName = lowLevelClient.getSettings().getServer().getNetworkName();
        if (networkName == null || !networkName.isPresent()) {
            return "";
        }
        return networkName.get();
    }

    public static void init(Path propertiesFile) {
        init(propertiesFile.toFile());
    }

    public static void init(File propertiesFile) {
        try (InputStream is = new FileInputStream(propertiesFile)) {
            Networks.properties = new Properties();
            Networks.properties.load(is);
        } catch (IOException e) {
            throw new FactomRuntimeException(e);
        }
    }

    public static void init(Properties properties) {
        if (Networks.properties != properties && Networks.properties != null) {
            properties.forEach((key, value) -> Networks.properties.put(key, value));
        } else {
            Networks.properties = properties;
        }
    }

    public static Optional<Address> getDefaultECAddress(Optional<String> networkName) {
        RpcSettings settings = walletd(networkName).lowLevelClient().getSettings();
        if (settings.getSigningMode() == SigningMode.OFFLINE) {
            return settings.getDefaultECAddress();
        }

        return Optional.empty();
    }

    public static Address getECAddress(Optional<String> networkName, Optional<Address> optionalECAddressToUse) {
        return optionalECAddressToUse.orElseGet(() ->
                getDefaultECAddress(networkName).orElseThrow(
                        () -> new FactomRuntimeException.AssertionException("Need to either configure an EC address or supply an EC address")));
    }


    public static FactomdClient factomd(Optional<String> networkName) {
        String key = networkKey(networkName);

        if (!factomdClients.containsKey(key)) {
            FactomdClientImpl factomdClient = createFactomdClient(networkName, key);
            register(factomdClient);
        }

        return factomdClients.get(key);
    }


    public static WalletdClient walletd(Optional<String> networkName) {
        return walletd(networkName, Optional.empty());
    }


    public static WalletdClient walletd(Optional<String> networkName, Optional<SigningMode> explicitSigningMode) {
        String key = networkKey(networkName);

        if (!walletdClients.containsKey(key)) {
            WalletdClientImpl walletdClient = createWalletdClient(networkName, explicitSigningMode, key);
            register(walletdClient);
        }

        return walletdClients.get(key);
    }


    public static Set<String> getNetworkNames() {
        return Collections.unmodifiableSet(networkNames);
    }


    private static FactomdClientImpl createFactomdClient(Optional<String> networkName, String key) {
        logger.info(String.format("Network: %s, factomd client not registered yet, starting registration.", key));
        RpcSettings rpcSettings = new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, properties, networkName);
        FactomdClientImpl factomdClient = new FactomdClientImpl();
        factomdClient.setSettings(rpcSettings);
        return factomdClient;
    }

    private static WalletdClientImpl createWalletdClient(Optional<String> networkName, Optional<SigningMode> explicitSigningMode, String key) {
        logger.info(String.format("Network: %s, walletd client not registered yet, starting registration.", key));
        RpcSettings rpcSettings = new RpcSettingsImpl(RpcSettings.SubSystem.WALLETD, properties, networkName);
        SigningMode signingMode = explicitSigningMode.orElse(rpcSettings.getSigningMode());
        WalletdClientImpl walletdClient;
        if (signingMode != SigningMode.OFFLINE) {
            walletdClient = new WalletdClientImpl();
        } else {
            try {
                walletdClient = (WalletdClientImpl) Class.forName("org.blockchain_innovation.factom.client.impl.OfflineWalletdClientImpl").newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new FactomRuntimeException("Could not find offline walletd client on classpath. " + e.getMessage(), e);
            }
        }
        walletdClient.setSettings(rpcSettings);
        return walletdClient;
    }

    private static String networkKey(Optional<String> networkName) {
        return networkName.orElse("").toLowerCase();
    }

    private static void assertSettings(RpcSettings settings) {
        if (settings == null) {
            throw new FactomException.ClientException("Client needs RpcSetting present before being registered");
        }
    }

}

package org.blockchain_innovation.factom.client.spring;

import jakarta.annotation.PostConstruct;
import org.blockchain_innovation.accumulate.factombridge.impl.FactomdAccumulateClientImpl;
import org.blockchain_innovation.accumulate.factombridge.impl.Networks;
import org.blockchain_innovation.factom.client.api.FactomdClient;
import org.blockchain_innovation.factom.client.api.SigningMode;
import org.blockchain_innovation.factom.client.api.WalletdClient;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.spring.settings.SpringRpcSettings;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.blockchain_innovation.accumulate.factombridge.impl.Networks.MAINNET;


@Component
public class SpringNetworks {

    @Value("${factom.network-names}")
    private List<String> networkNames;
    private final Environment environment;

    private final ObjectProvider<FactomdClient> factomdProvider;
    private final ObjectProvider<WalletdClient> offlineWalletdProvider;

    @Autowired
    @Inject
    public SpringNetworks(Environment environment, @Named("factomdClient") ObjectProvider<FactomdClient> factomdProvider, @Named("offlineWalletdClient") ObjectProvider<WalletdClient> offlineWalletdProvider, @Named("onlineWalletdClient") ObjectProvider<WalletdClient> onlineWalletdProvider) {
        this.environment = environment;
        this.factomdProvider = factomdProvider;
        this.offlineWalletdProvider = offlineWalletdProvider;
    }

    @PostConstruct
    private void initNetworks() {
        for (String networkName : networkNames) {
            factomd(Optional.of(networkName));
            walletd(Optional.of(networkName));
        }
    }


    public FactomdClient factomd(Optional<String> networkName) {
        String id = networkName.orElse(MAINNET).toLowerCase();

        if (!Networks.hasFactomd(id)) {
            SpringRpcSettings springRpcSettings = rpcSettings(networkName);
            FactomdAccumulateClientImpl factomdClient = (FactomdAccumulateClientImpl) factomdProvider.getObject(springRpcSettings);
            Networks.register(factomdClient);
        }

        return Networks.factomd(Optional.of(id));
    }


    public WalletdClient walletd(Optional<String> network) {
        return walletd(network, Optional.empty());
    }


    public WalletdClient walletd(Optional<String> network, Optional<SigningMode> explicitSigningMode) {
        String id = network.orElse(MAINNET).toLowerCase();

        if (!Networks.hasWalletd(id)) {
            SpringRpcSettings springRpcSettings = rpcSettings(network);
            SigningMode signingMode = explicitSigningMode.orElse(springRpcSettings.getWalletd().getSigningMode());
            WalletdClient walletdClient;
            if (signingMode == SigningMode.OFFLINE) {
                walletdClient = offlineWalletdProvider.getObject(springRpcSettings);
            } else {
                throw new FactomRuntimeException("Online wallet not supported for Accumulate implementation");
            }
            Networks.register(walletdClient);
        }

        return Networks.walletd(Optional.of(id));
    }


    public SpringRpcSettings rpcSettings(Optional<String> network) {
        try {
            Binder binder = Binder.get(environment);
            String id = network.orElse(MAINNET);
            SpringRpcSettings springRpcSettings = binder.bind(id, SpringRpcSettings.class).get();
            springRpcSettings.setNetworkName(Optional.of(id));
            return springRpcSettings;
        } catch (NoSuchElementException nse) {
            throw new FactomRuntimeException.AssertionException(String.format("Network '%s' was not correctly configured on this rosetta node!", network.orElse(MAINNET)));
        }
    }


    public List<String> getNetworkNames() {
        return Collections.unmodifiableList(networkNames);
    }
}

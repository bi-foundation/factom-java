package org.blockchain_innovation.factom.client.spring;

import org.blockchain_innovation.factom.client.api.EntryApi;
import org.blockchain_innovation.factom.client.api.FactomdClient;
import org.blockchain_innovation.factom.client.api.SigningMode;
import org.blockchain_innovation.factom.client.api.WalletdClient;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.EntryApiImpl;
import org.blockchain_innovation.factom.client.impl.FactomdClientImpl;
import org.blockchain_innovation.factom.client.impl.OfflineWalletdClientImpl;
import org.blockchain_innovation.factom.client.impl.WalletdClientImpl;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.blockchain_innovation.factom.client.spring.settings.SpringRpcSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.Executors;

@Configuration
@Import(SpringRpcSettings.class)
public class FactomConfiguration {

    @Autowired
    private SpringRpcSettings springRpcSettings;

    @Bean
    @Scope("prototype")
    public EntryApi entryApi() {
        EntryApiImpl entryApi = new EntryApiImpl();
        return entryApi;
    }

    @Bean
    @Scope("prototype")
    public FactomdClient factomdClient() {
        return factomdClient(springRpcSettings);
    }

    @Bean
    @Scope("prototype")
    public FactomdClient factomdClient(SpringRpcSettings specificSettings) {
        FactomdClientImpl factomdClient = new FactomdClientImpl();
        factomdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, specificSettings.getFactomdServer()));
        factomdClient.setExecutorService(Executors.newFixedThreadPool(specificSettings.getFactomdServer().getThreads()));
        return factomdClient;
    }

    @Bean
    @Scope("prototype")
    public WalletdClient walletdClient() {
        return walletdClient(springRpcSettings);
    }

    @Bean
    @Scope("prototype")
    public WalletdClient walletdClient(SpringRpcSettings specificSettings) {
        SigningMode signingMode = specificSettings.getWalletdServer().getSigningMode();
        WalletdClientImpl walletdClient;
        if (signingMode == SigningMode.ONLINE_WALLETD) {
            walletdClient = new WalletdClientImpl();
        } else {
            walletdClient = new OfflineWalletdClientImpl();
        }
        walletdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.WALLETD, specificSettings.getWalletdServer()));
        walletdClient.setExecutorService(Executors.newFixedThreadPool(specificSettings.getWalletdServer().getThreads()));
        return walletdClient;
    }
}

package org.blockchain_innovation.factom.client.spring;

import org.blockchain_innovation.accumulate.factombridge.impl.EntryApiImpl;
import org.blockchain_innovation.accumulate.factombridge.impl.FactomdAccumulateClientImpl;
import org.blockchain_innovation.accumulate.factombridge.impl.OfflineWalletdClientImpl;
import org.blockchain_innovation.accumulate.factombridge.impl.WalletdClientImpl;
import org.blockchain_innovation.accumulate.factombridge.impl.settings.RpcSettingsImpl;
import org.blockchain_innovation.factom.client.api.EntryApi;
import org.blockchain_innovation.factom.client.api.FactomdClient;
import org.blockchain_innovation.factom.client.api.WalletdClient;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.spring.settings.SpringRpcSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.Executors;

@Configuration
@Import(SpringRpcSettings.class)
public class FactomConfiguration {

    @Bean
    @Scope("prototype")
    public EntryApi entryApi() {
        return new EntryApiImpl();
    }


    @Bean
    @Scope("prototype")
    public FactomdClient factomdClient(SpringRpcSettings specificSettings) {
        FactomdAccumulateClientImpl factomdClient = new FactomdAccumulateClientImpl();
        RpcSettingsImpl settings = new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, specificSettings.getFactomd());
        settings.setDefaultECAddress(specificSettings.getEcAddress());
        factomdClient.setSettings(settings);
        factomdClient.setExecutorService(Executors.newFixedThreadPool(specificSettings.getFactomd().getThreads()));
        return factomdClient;
    }

    @Bean
    @Scope("prototype")
    public WalletdClient offlineWalletdClient(SpringRpcSettings specificSettings) {
        return walletdClient(specificSettings, new OfflineWalletdClientImpl());
    }

    private WalletdClient walletdClient(SpringRpcSettings specificSettings, WalletdClientImpl walletdClient) {
        RpcSettingsImpl settings = new RpcSettingsImpl(RpcSettings.SubSystem.WALLETD, specificSettings.getWalletd());
        settings.setDefaultECAddress(specificSettings.getEcAddress());
        walletdClient.setSettings(settings);
        walletdClient.setExecutorService(Executors.newFixedThreadPool(specificSettings.getWalletd().getThreads()));
        return walletdClient;
    }
}

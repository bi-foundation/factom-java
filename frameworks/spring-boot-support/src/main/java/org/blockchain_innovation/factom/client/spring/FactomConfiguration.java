package org.blockchain_innovation.factom.client.spring;

import org.blockchain_innovation.factom.client.api.EntryApi;
import org.blockchain_innovation.factom.client.api.FactomdClient;
import org.blockchain_innovation.factom.client.api.WalletdClient;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.EntryApiImpl;
import org.blockchain_innovation.factom.client.impl.FactomdClientImpl;
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
    public EntryApi entryApi(){
        EntryApiImpl entryApi = new EntryApiImpl();
        return entryApi;
    }

    @Bean
    @Scope("prototype")
    public FactomdClient factomdClient() {
        FactomdClientImpl factomdClient = new FactomdClientImpl();
        factomdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, springRpcSettings.getFactomdServer()));
        factomdClient.setExecutorService(Executors.newFixedThreadPool(springRpcSettings.getFactomdServer().getThreads()));
        return factomdClient;
    }

    @Bean
    @Scope("prototype")
    public WalletdClient walletdClient() {
        WalletdClientImpl walletdClient = new WalletdClientImpl();
        walletdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.WALLETD, springRpcSettings.getWalletdServer()));
        walletdClient.setExecutorService(Executors.newFixedThreadPool(springRpcSettings.getWalletdServer().getThreads()));
        return walletdClient;
    }
}

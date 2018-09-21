package org.blockchain_innovation.factom.client.jee.cdi;

import org.blockchain_innovation.factom.client.api.FactomdClient;
import org.blockchain_innovation.factom.client.api.WalletdClient;
import org.blockchain_innovation.factom.client.api.json.JsonConverter;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.EntryApiImpl;
import org.blockchain_innovation.factom.client.impl.FactomdClientImpl;
import org.blockchain_innovation.factom.client.impl.WalletdClientImpl;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
public class ManagedClientProducers {

    @Resource
    ManagedExecutorService managedExecutorService;

    @Inject
    private CommitAndRevealEvents commitAndRevealEvents;

    @Inject
    private JsonConverter jsonConverter;

    private FactomdClientImpl factomdClient;
    private WalletdClientImpl walletdClient;
    private Map<RpcSettings.SubSystem, RpcSettings> settings = new HashMap<>();
    private EntryApiImpl entryApi;

    private boolean enableCommitRevealEvents = false;

    public ManagedClientProducers addSettings(RpcSettings subsystemSettings) {
        settings.put(subsystemSettings.getSubSystem(), subsystemSettings);
        return this;
    }

    public RpcSettings getSettings(RpcSettings.SubSystem subSystem) {
        if (settings.containsKey(subSystem)) {
            return settings.get(subSystem);
        }
        return settings.get(null);
    }

    public ManagedClientProducers enableCommitAndRevealEvents(boolean enable) {
        this.enableCommitRevealEvents = enable;
        return this;
    }


    @Produces
    @ManagedClient
    public FactomdClient getFactomdClient() {
        if (factomdClient == null) {
            this.factomdClient = new FactomdClientImpl();
            factomdClient.setSettings(getSettings(RpcSettings.SubSystem.FACTOMD));
            factomdClient.setExecutorService(managedExecutorService);
        }
        return factomdClient;
    }

    @Produces
    @ManagedClient
    public WalletdClient getWalletdClient() {
        if (walletdClient == null) {
            this.walletdClient = new WalletdClientImpl();
            walletdClient.setSettings(getSettings(RpcSettings.SubSystem.WALLETD));
            walletdClient.setExecutorService(managedExecutorService);
        }
        return walletdClient;
    }


    @Produces
    @ManagedClient
    public EntryApiImpl getEntryAPI() {
        if (entryApi == null) {
            this.entryApi = new EntryApiImpl();
            entryApi.setFactomdClient(getFactomdClient());
            entryApi.setWalletdClient(getWalletdClient());
            if (enableCommitRevealEvents) {
                entryApi.addListener(commitAndRevealEvents);
            }
        }
        return entryApi;
    }

    public JsonConverter getJsonConverter() {
        return jsonConverter;
    }
}

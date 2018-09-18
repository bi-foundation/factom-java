package org.blockchain_innovation.factom.client.jee.cdi;

import org.blockchain_innovation.factom.client.api.FactomdClient;
import org.blockchain_innovation.factom.client.api.LowLevelClient;
import org.blockchain_innovation.factom.client.api.WalletdClient;
import org.blockchain_innovation.factom.client.api.json.JsonConverter;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.EntryApiImpl;
import org.blockchain_innovation.factom.client.impl.FactomdClientImpl;
import org.blockchain_innovation.factom.client.impl.WalletdClientImpl;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SessionScoped
public class ManagedClientProducers implements Serializable {

    @Inject
    private CommitAndRevealEvents commitAndRevealEvents;

    @Inject
    private JsonConverter jsonConverter;

    private FactomdClient factomdClient;
    private WalletdClient walletdClient;
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


    @Produces @ManagedClient
    public FactomdClient getFactomdClient() {
        if (factomdClient == null) {
            this.factomdClient = new FactomdClientImpl();
            ((LowLevelClient) factomdClient).setSettings(getSettings(RpcSettings.SubSystem.FACTOMD));
        }
        return factomdClient;
    }

    @Produces @ManagedClient
    public WalletdClient getWalletdClient() {
        if (walletdClient == null) {
            this.walletdClient = new WalletdClientImpl();
            ((LowLevelClient) walletdClient).setSettings(getSettings(RpcSettings.SubSystem.WALLETD));
        }
        return walletdClient;
    }


    @Produces @ManagedClient
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

package org.factomprotocol.identity.server.controller;

import did.DIDDocument;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.EntryApiImpl;
import org.blockchain_innovation.factom.client.impl.FactomdClientImpl;
import org.blockchain_innovation.factom.client.impl.OfflineWalletdClientImpl;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.blockchain_innovation.factom.identiy.did.LowLevelIdentityClient;
import org.blockchain_innovation.factom.identiy.did.parse.RuleException;
import org.factomprotocol.identity.did.model.IdentityResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

@Controller
public class UniversalResolverController implements UniversalResolverApi {
    protected final EntryApiImpl offlineEntryClient = new EntryApiImpl();
    protected final OfflineWalletdClientImpl offlineWalletdClient = new OfflineWalletdClientImpl();
    protected final FactomdClientImpl factomdClient = new FactomdClientImpl();
    protected final LowLevelIdentityClient identityClient = new LowLevelIdentityClient();


    @Override
    public ResponseEntity<DIDDocument> _resolve(String identifier, Optional<String> accept) {
        try {
            IdentityResponse identityResponse = identityClient.resolveIdentity(identifier);
            return ResponseEntity.of(Optional.of(identityClient.convertIdentityToDid(identifier, identityResponse)));
        } catch (RuleException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void setup() throws IOException {

        factomdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, getProperties()));
        offlineEntryClient.setFactomdClient(factomdClient);
        offlineEntryClient.setWalletdClient(offlineWalletdClient);
        identityClient.setEntryApi(offlineEntryClient);
    }

    protected Properties getProperties() throws IOException {
        Properties properties = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("settings.properties");
        properties.load(is);
        is.close();
        return properties;
    }
}

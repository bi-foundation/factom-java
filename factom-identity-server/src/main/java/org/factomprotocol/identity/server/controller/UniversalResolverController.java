package org.factomprotocol.identity.server.controller;

import did.DIDDocument;
import org.blockchain_innovation.factom.client.spring.settings.SpringRpcSettings;
import org.blockchain_innovation.factom.identiy.did.IdentityClient;
import org.blockchain_innovation.factom.identiy.did.entry.EntryValidation;
import org.blockchain_innovation.factom.identiy.did.parse.RuleException;
import org.factomprotocol.identity.did.model.IdentityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Controller
public class UniversalResolverController implements UniversalResolverApi {
    protected IdentityClient identityClient;

    @Autowired
    private SpringRpcSettings settings;

    @Value("${walletd.mode}")
    private IdentityClient.Mode mode = IdentityClient.Mode.OFFLINE_SIGNING;

    @PostConstruct
    public void init() {
       this.identityClient = new IdentityClient.Builder()
               .mode(mode)
               .property("factomd.url", settings.getFactomdServer().getURL().toString())
               .property("walletd.url", settings.getWalletdServer().getURL().toString())
               .build();
    }


    @Override
    public ResponseEntity<DIDDocument> _resolve(String identifier, Optional<String> accept) {
        try {
            IdentityResponse identityResponse = identityClient.getIdentityResponse(identifier, EntryValidation.IGNORE_ERROR, Optional.empty(), Optional.empty());
            return ResponseEntity.of(Optional.of(identityClient.factory().toDid(identifier, identityResponse)));
        } catch (RuleException e) {
            throw new RuntimeException(e);
        }
    }

}

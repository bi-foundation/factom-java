package org.blockchain_innovation.factom.identiy.did.entry;

import com.github.jsonldjava.core.JsonLdError;
import did.DID;
import did.DIDDocument;
import did.parser.ParserException;
import org.blockchain_innovation.factom.client.api.EntryApi;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryResponse;

import java.io.IOException;
import java.util.List;

public class DIDEntryClient {

    private EntryApi entryApi;

    public DIDEntryClient() {
    }

    public DIDEntryClient(EntryApi entryApi) {
        setEntryApi(entryApi);
    }

    public DIDEntryClient setEntryApi(EntryApi entryApi) {
        this.entryApi = entryApi;
        return this;
    }


    public EntryApi getEntryApi() {
        if (entryApi == null) {
            throw new FactomRuntimeException.AssertionException("DIDEntryClient needs an entry API to function. Please provide one");
        }
        return entryApi;
    }

    /**
     * Get all entries related to a FactomDID reference.
     *
     * @param didReference The FactomDID reference
     * @return
     */
    public List<EntryResponse> getAllByDidReference(String didReference) {
        try {
            return getAllEntriesByIdentifier(DID.fromString(didReference).getDid());
        } catch (ParserException e) {
            throw new DIDRuntimeException.ParseException(e);
        }
    }


    public List<EntryResponse> getAllEntriesByIdentifier(String identifier) {
        return getEntryApi().allEntries(FactomDID.FCTR_V1.getTargetId(identifier)).join();
    }

    /**
     * Create a FactomDID chain by the provided (valid entry)
     *
     * @param didDocument
     * @param nonce
     * @param address     The paying EC address
     * @return
     */
    public CommitAndRevealChainResponse create(DIDDocument didDocument, byte[] nonce, Address address) {
        String chainId = FactomDID.FCTR_V1.determineChainId(nonce);
        if (!chainId.equals(FactomDID.FCTR_V1.getTargetId((String) didDocument.getJsonLdObject().get(DIDDocument.JSONLD_TERM_ID)))) {
            throw new DIDRuntimeException(String.format("Provided DID %s and determined chain id %s for the supplied nonce do no match", didDocument.getId(), chainId));
        }

        Entry entry = new Entry();
        entry.setExternalIds(FactomDID.FCTR_V1.createDIDExternalIds(nonce));
        try {
            entry.setContent(didDocument.toJson());
        } catch (IOException | JsonLdError e) {
            throw new DIDRuntimeException(e);
        }

        Chain chain = new Chain().setFirstEntry(entry);
        if (getEntryApi().chainExists(chain).join()) {
            throw new FactomRuntimeException.AssertionException(String.format("Factom DID chain for id '%s' already exists", chainId));
        }
        return getEntryApi().commitAndRevealChain(chain, address).join();
    }


}

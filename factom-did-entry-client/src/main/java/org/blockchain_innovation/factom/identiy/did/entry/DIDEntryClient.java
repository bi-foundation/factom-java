package org.blockchain_innovation.factom.identiy.did.entry;

import did.DID;
import did.parser.ParserException;
import org.blockchain_innovation.factom.client.api.EntryApi;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryResponse;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;

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
     * Get all entries related to a DID reference.
     *
     * @param didReference The DID reference
     * @return
     */
    public List<EntryResponse> getAllByDidReference(String didReference) {
        try {
            return getAllByDid(DID.fromString(didReference).getDid());
        } catch (ParserException e) {
            throw new DIDRuntimeException.ParseException(e);
        }
    }


    public List<EntryResponse> getAllByDid(String did) {
        return getEntryApi().allEntries(did).join();
    }

    /**
     * Create a DID chain by the provided (valid entry)
     *
     * @param entry
     * @param address The paying EC address
     * @return
     */
    public CommitAndRevealChainResponse create(Entry entry, Address address) {
        Chain chain = new Chain().setFirstEntry(entry);
        if (getEntryApi().chainExists(chain).join()) {
            throw new FactomRuntimeException.AssertionException(String.format("DID chain for id '%s' already exists", new EntryOperations().calculateChainId(chain.getFirstEntry().getExternalIds())));
        }
        return getEntryApi().commitAndRevealChain(chain, address).join();
    }


}

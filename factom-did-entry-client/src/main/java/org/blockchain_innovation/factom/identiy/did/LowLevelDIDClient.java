package org.blockchain_innovation.factom.identiy.did;

import did.DID;
import did.parser.ParserException;
import org.blockchain_innovation.factom.client.api.EntryApi;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryResponse;
import org.blockchain_innovation.factom.identiy.did.entry.CreateFactomDIDEntry;
import org.blockchain_innovation.factom.identiy.did.entry.DeactivateFactomDIDEntry;
import org.blockchain_innovation.factom.identiy.did.entry.UpdateFactomDIDEntry;

import java.util.List;

public class LowLevelDIDClient {

    private EntryApi entryApi;

    public LowLevelDIDClient() {
    }

    public LowLevelDIDClient(EntryApi entryApi) {
        setEntryApi(entryApi);
    }

    public LowLevelDIDClient setEntryApi(EntryApi entryApi) {
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
            return getAllEntriesByIdentifier(DID.fromString(didReference).getDidString());
        } catch (ParserException e) {
            throw new DIDRuntimeException.ParseException(e);
        }
    }


    public List<EntryResponse> getAllEntriesByIdentifier(String identifier) {
        return getEntryApi().allEntries(DIDVersion.FACTOM_V1_JSON.getMethodSpecificId(identifier)).join();
    }

    /**
     * Create a FactomDID chain by the provided (valid entry)
     *
     * @param createDidEntry
     * @param ecAddress      The paying EC address
     * @return
     */
    public CommitAndRevealChainResponse create(CreateFactomDIDEntry createDidEntry, Address ecAddress) {
        Chain chain = new Chain().setFirstEntry(createDidEntry.toEntry());
        if (getEntryApi().chainExists(chain).join()) {
            throw new FactomRuntimeException.AssertionException(String.format("Factom DID chain for id '%s' already exists", createDidEntry.getChainId()));
        }
        return getEntryApi().commitAndRevealChain(chain, ecAddress).join();
    }


    /**
     * Update a FactomDID chain by the provided (valid entry)
     *
     * @param updateEntry
     * @param ecAddress   The paying EC address
     * @return
     */
    public CommitAndRevealEntryResponse update(UpdateFactomDIDEntry updateEntry, Address ecAddress) {
      /*  if (getEntryApi().allEntryBlocks(chainId).join().size() == 0) {
            throw new FactomRuntimeException.AssertionException(String.format("Factom DID chain for id '%s' did not exist", chainId));
        }*/
        return getEntryApi().commitAndRevealEntry(updateEntry.toEntry(), ecAddress).join();
    }


    /**
     * Deactivate a FactomDID chain by the provided (valid entry)
     *
     * @param deactivateEntry
     * @param ecAddress       The paying EC address
     * @return
     */
    public CommitAndRevealEntryResponse deactivate(DeactivateFactomDIDEntry deactivateEntry, Address ecAddress) {
        return getEntryApi().commitAndRevealEntry(deactivateEntry.toEntry(), ecAddress).join();
    }
}

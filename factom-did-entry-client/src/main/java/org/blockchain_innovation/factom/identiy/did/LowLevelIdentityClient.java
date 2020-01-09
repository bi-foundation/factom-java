package org.blockchain_innovation.factom.identiy.did;

import did.*;
import did.parser.ParserException;
import org.blockchain_innovation.factom.client.api.EntryApi;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryResponse;
import org.blockchain_innovation.factom.client.api.ops.EncodeOperations;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.identiy.did.entry.*;
import org.blockchain_innovation.factom.identiy.did.parse.RuleException;
import org.factomprotocol.identity.did.model.BlockInfo;
import org.factomprotocol.identity.did.model.IdentityEntry;
import org.factomprotocol.identity.did.model.IdentityResponse;
import org.factomprotocol.identity.did.model.Metadata;

import java.util.*;

public class LowLevelIdentityClient {
    private static final IdentityEntryFactory ENTRY_FACTORY = new IdentityEntryFactory();
    private static final EncodeOperations ENCODE = new EncodeOperations();
    IdAddressKeyOps idAddressOps = new IdAddressKeyOps();

    private EntryApi entryApi;

    public LowLevelIdentityClient() {
    }

    public LowLevelIdentityClient(EntryApi entryApi) {
        setEntryApi(entryApi);
    }

    public LowLevelIdentityClient setEntryApi(EntryApi entryApi) {
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
     * @param validate     Validate the entries
     * @return
     */
    public List<FactomIdentityEntry<?>> getAllByDidReference(String didReference, EntryValidation validate) throws RuleException {
        try {
            return getAllEntriesByIdentifier(DID.fromString(didReference).getDidString(), validate);
        } catch (ParserException e) {
            throw new DIDRuntimeException.ParseException(e);
        }
    }

    public DIDDocument convertIdentityToDid(String identifier, IdentityResponse identityResponse) throws RuleException {
        String did = identifier;
       if (!identifier.startsWith("did:")) {
            did = "did:factom:" + identifier;
        }
        DIDURL didurl = DIDURL.fromString(did);
        List<Map<String, Object>> publicKeys = new LinkedList<>();
        List<String> authentications = new ArrayList<>();
        List<String> idPubs = identityResponse.getIdentity().getKeys();


        for (int i = 0; i < idPubs.size(); i++) {
            var idPub = idPubs.get(i);
            var controller = didurl.getDid().getDidString();
            var id = String.format("%s#key-%d", controller, i);
            var keyBytes = idAddressOps.toEd25519PublicKey(idPub).getAbyte();
            var hexKey = Encoding.HEX.encode(keyBytes);
            var b58Key = Encoding.BASE58.encode(keyBytes);
            Map<String, Object> keyAttrs = new HashMap<>();
            Map<String, Object> authAttrs = new HashMap<>();


            keyAttrs.put(DIDDocument.JSONLD_TERM_TYPE, "Ed25519VerificationKey2018");

            keyAttrs.put(DIDDocument.JSONLD_TERM_ID, id);
            keyAttrs.put(DIDDocument.JSONLD_TERM_PUBLICKEYBASE58, b58Key);
            keyAttrs.put(DIDDocument.JSONLD_TERM_PUBLICKEYHEX, hexKey);

            keyAttrs.put("controller", controller);
            authAttrs.put("controller", controller);
            if (i > 0) {
                authentications.add(id);
            }
            publicKeys.add(PublicKey.build(keyAttrs).getJsonLdObject());
        }

        // We build using the LdObjects ourselves as the convenience method does not do everything and ojects are not mutable anymore
        DIDDocument didDocument = DIDDocument.build(didurl.getDid().getDidString(), null, null, null);
        didDocument.setJsonLdObjectKeyValue(DIDDocument.JSONLD_TERM_AUTHENTICATION, authentications);
        didDocument.setJsonLdObjectKeyValue(DIDDocument.JSONLD_TERM_PUBLICKEY, publicKeys);

        return didDocument;
    }

    public IdentityResponse resolveIdentity(String identifier) throws RuleException {

        List<FactomIdentityEntry<?>> entries = getAllEntriesByIdentifier(identifier, EntryValidation.THROW_ERROR);
        if (entries == null || entries.size() == 0) {
            throw new RuleException("Identity for %s could not be resolved", identifier);
        }
        Metadata metadata = new Metadata();
        IdentityEntry identityEntry = null;
        for (FactomIdentityEntry<?> entry : entries) {
            if (identityEntry == null) {
                if (entry.getOperationValue() != OperationValue.IDENTITY_CHAIN_CREATION) {
                    throw new RuleException("Identity chain %s did not start with an Identity Creation entry", identifier);
                }
                identityEntry = ((CreateIdentityContentEntry) entry).getContent();
                metadata.creation(entry.getBlockInfo().get());
                metadata.update(entry.getBlockInfo().get());
                continue;
            } else if (entry.getOperationValue() != OperationValue.IDENTITY_CHAIN_REPLACE_KEY) {
                continue;
            }
            ReplaceKeyIdentityChainEntry replaceKeyEntry = (ReplaceKeyIdentityChainEntry) entry;
            if (!idAddressOps.verifyKeyReplacementSignature(replaceKeyEntry)) {
                continue;
            }
            try {
                List<String> newKeys = idAddressOps.createNewKeyReplacementList(identityEntry.getKeys(), replaceKeyEntry.getOldKey(), replaceKeyEntry.getNewKey(), replaceKeyEntry.getSignerKey());
                identityEntry.setKeys(newKeys);
                metadata.update(replaceKeyEntry.getBlockInfo().get());
            } catch (FactomRuntimeException re) {

            }
        }
        if (identityEntry == null) {
            throw new RuleException("Identity chain %s did not start with an Identity Creation entry", identifier);
        }
        IdentityResponse identityResponse = new IdentityResponse();
        identityResponse.setIdentity(identityEntry);
        identityResponse.setMetadata(metadata);
        return identityResponse;
    }


    public List<FactomIdentityEntry<?>> getAllEntriesByIdentifier(String identifier, EntryValidation validate) throws RuleException {
        List<FactomIdentityEntry<?>> entries = new ArrayList<>();
        String chainId = identifier;
        if (identifier.startsWith("did:factom:")) {
            chainId = DIDVersion.FACTOM_V1_JSON.getMethodSpecificId(identifier);
        }
        List<EntryBlockResponse> entryBlockResponses = getEntryApi().allEntryBlocks(chainId).join();
        for (EntryBlockResponse entryBlockResponse : entryBlockResponses) {
            EntryBlockResponse.Header header = entryBlockResponse.getHeader();

            int size = entryBlockResponse.getEntryList().size();
            for (int i = size - 1; i >= 0; i--) {
                EntryBlockResponse.Entry entryBlockEntry = entryBlockResponse.getEntryList().get(i);
                BlockInfo blockInfo = new BlockInfo()
                        .blockHeight(header.getDirectoryBlockHeight())
                        .blockTimestamp(header.getTimestamp())
                        .entryTimestamp(entryBlockEntry.getTimestamp())
                        .entryHash(entryBlockEntry.getEntryHash());

                EntryResponse entryResponse = getEntryApi().getFactomdClient().entry(entryBlockEntry.getEntryHash()).join().getResult();
                Entry entry = ENCODE.decodeHex(
                        new Entry()
                                .setChainId(entryResponse.getChainId())
                                .setExternalIds(entryResponse.getExtIds())
                                .setContent(entryResponse.getContent()));


                try {
                    entries.add(ENTRY_FACTORY.from(entry, blockInfo, validate));
                } catch (RuleException re) {

                }
            }
        }
        if (entries.size() > 1) {
            Collections.reverse(entries);
        }
        return entries;
    }


    /**
     * Create a FactomDID chain by the provided (valid entry)
     *
     * @param createDidEntry
     * @param ecAddress      The paying EC address
     * @return
     */
    public CommitAndRevealChainResponse create(CreateFactomDIDEntry createDidEntry, Address ecAddress) {
        Chain chain = new Chain().setFirstEntry(createDidEntry.toEntry(Optional.empty()));
        if (getEntryApi().chainExists(chain).join()) {
            throw new FactomRuntimeException.AssertionException(String.format("Factom DID chain for id '%s' already exists", createDidEntry.getChainId()));
        }
        return getEntryApi().commitAndRevealChain(chain, ecAddress).join();
    }

    /**
     * Create an identity chain
     *
     * @param identityChainEntry
     * @param ecAddress          The paying EC address
     * @return
     */
    public CommitAndRevealChainResponse create(CreateIdentityRequestEntry identityChainEntry, Address ecAddress) {
//        List<String> externalIds = new ArrayList<>();
       /* externalIds.add(OperationValue.IDENTITY_CHAIN_CREATION.getOperation());

        externalIds.add(identityChainEntry.getNonce());
        if (identityChainEntry.getTags() != null) {
            externalIds.addAll(identityChainEntry.getTags());
        }*/
        Entry entry = identityChainEntry.toEntry(Optional.empty());
//        entry.setExternalIds(externalIds);
//        entry.setChainId(Encoding.HEX.encode(new EntryOperations().calculateChainId(externalIds)));
        Chain chain = new Chain().setFirstEntry(entry);
        if (getEntryApi().chainExists(chain).join()) {
            throw new FactomRuntimeException.AssertionException(String.format("Factom identity chain for id '%s' already exists", entry.getChainId()));
        }
        return getEntryApi().commitAndRevealChain(chain, ecAddress).join();
    }


    /**
     * Create an identity chain
     *
     * @param identityContentEntry
     * @param ecAddress            The paying EC address
     * @return
     */
    public CommitAndRevealChainResponse create(CreateIdentityContentEntry identityContentEntry, Address ecAddress, String... tags) {
        Entry entry = identityContentEntry.toEntry(Optional.empty());
        List<String> externalIds = new ArrayList<>();
        if (tags.length == 0 && (identityContentEntry.getAdditionalTags() == null || identityContentEntry.getAdditionalTags().isEmpty())) {
            throw new DIDRuntimeException("Need at least one chain name for an identity chain. None provided");
        }

        if (tags.length > 0) {
            externalIds = Arrays.asList(tags);
            if (identityContentEntry.getAdditionalTags() != null && identityContentEntry.getAdditionalTags().size() > 0) {
                throw new DIDRuntimeException("You cannot specify both tags in the model and in the create call. Need one of the two not both");
            }
        } else {
            externalIds = identityContentEntry.getExternalIds();
        }

        entry.setExternalIds(externalIds);
        Chain chain = new Chain().setFirstEntry(entry);
        if (getEntryApi().chainExists(chain).join()) {
            throw new FactomRuntimeException.AssertionException(String.format("Factom identity chain for id '%s' already exists", identityContentEntry.getChainId()));
        }
        if (getEntryApi().chainExists(chain).join()) {
            throw new DIDRuntimeException(String.format("Chain %s already exists. A created Idenity chain always needs a new chain", chain.getFirstEntry().getChainId()));
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
        return getEntryApi().commitAndRevealEntry(updateEntry.toEntry(Optional.empty()), ecAddress).join();
    }


    /**
     * Update a FactomDID chain by the provided (valid entry)
     *
     * @param updateEntry
     * @param ecAddress   The paying EC address
     * @return
     */
    public CommitAndRevealEntryResponse update(ReplaceKeyIdentityChainEntry updateEntry, Address ecAddress) {
        return getEntryApi().commitAndRevealEntry(updateEntry.toEntry(Optional.empty()), ecAddress).join();
    }

    /**
     * Deactivate a FactomDID chain by the provided (valid entry)
     *
     * @param deactivateEntry
     * @param ecAddress       The paying EC address
     * @return
     */
    public CommitAndRevealEntryResponse deactivate(DeactivateFactomDIDEntry deactivateEntry, Address ecAddress) {
        return getEntryApi().commitAndRevealEntry(deactivateEntry.toEntry(Optional.empty()), ecAddress).join();
    }
}

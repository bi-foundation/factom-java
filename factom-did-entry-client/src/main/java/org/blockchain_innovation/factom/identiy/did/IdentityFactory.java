package org.blockchain_innovation.factom.identiy.did;

import did.DIDDocument;
import did.DIDURL;
import did.PublicKey;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.identiy.did.entry.CreateIdentityContentEntry;
import org.blockchain_innovation.factom.identiy.did.entry.FactomIdentityEntry;
import org.blockchain_innovation.factom.identiy.did.entry.ReplaceKeyIdentityChainEntry;
import org.blockchain_innovation.factom.identiy.did.parse.RuleException;
import org.factomprotocol.identity.did.model.IdentityEntry;
import org.factomprotocol.identity.did.model.IdentityResponse;
import org.factomprotocol.identity.did.model.Metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IdentityFactory {
    private static final IdAddressKeyOps ADDRESSES = new IdAddressKeyOps();

    public IdentityResponse toIdentity(String identifier, List<FactomIdentityEntry<?>> entries) throws RuleException {

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
            if (!ADDRESSES.verifyKeyReplacementSignature(replaceKeyEntry)) {
                continue;
            }
            try {
                List<String> newKeys = ADDRESSES.createNewKeyReplacementList(identityEntry.getKeys(), replaceKeyEntry.getOldKey(), replaceKeyEntry.getNewKey(), replaceKeyEntry.getSignerKey());
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

    public DIDDocument toDid(String identifier, IdentityResponse identityResponse) throws RuleException {
        String did = identifier;
        if (!identifier.startsWith("did:")) {
            did = "did:factom:" + identifier;
        }
        DIDURL didurl = DIDURL.fromString(did);
        List<Map<String, Object>> publicKeys = new LinkedList<>();
        List<String> authentications = new ArrayList<>();
        List<String> idPubs = identityResponse.getIdentity().getKeys();


        for (int i = 0; i < idPubs.size(); i++) {
            String idPub = idPubs.get(i);
            String controller = didurl.getDid().getDidString();
            String id = String.format("%s#key-%d", controller, i);
            byte[] keyBytes = ADDRESSES.toEd25519PublicKey(idPub).getAbyte();
            String hexKey = Encoding.HEX.encode(keyBytes);
            String b58Key = Encoding.BASE58.encode(keyBytes);
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
}

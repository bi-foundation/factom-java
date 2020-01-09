package org.blockchain_innovation.factom.identiy.did.entry;

import org.apache.commons.lang3.ArrayUtils;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.blockchain_innovation.factom.identiy.did.OperationValue;
import org.blockchain_innovation.factom.identiy.did.parse.operations.DIDV1CreationCompoundRule;
import org.blockchain_innovation.factom.identiy.did.parse.operations.FactomIdentityChainCreationCompoundRule;
import org.factomprotocol.identity.did.model.BlockInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ResolvedFactomDIDEntry<T> extends AbstractFactomIdentityEntry<T> {
    private String nonce;
    private List<String> additionalTags;

    public ResolvedFactomDIDEntry(DIDVersion didVersion, T content, String nonce, String... additionalTags) {
        super(DIDVersion.FACTOM_IDENTITY_CHAIN == didVersion ? OperationValue.IDENTITY_CHAIN_CREATION : OperationValue.DID_MANAGEMENT, didVersion, content, additionalTags.length == 0 ? new String[]{nonce} : ArrayUtils.insert(0, additionalTags, nonce));
        this.nonce = nonce;
        if (additionalTags != null) {
            this.additionalTags = Arrays.asList(additionalTags);
        }
    }

    public ResolvedFactomDIDEntry(Entry entry, Class<T> tClass, BlockInfo blockInfo) {
        super(entry, tClass, blockInfo);
        this.nonce = entry.getExternalIds().get(1);
        int size = entry.getExternalIds().size();
        if (size > 2) {
            this.additionalTags = entry.getExternalIds().subList(2, size);
        }
    }

    public Chain toChain() {
        Chain chain = new Chain();
        chain.setFirstEntry(toEntry(Optional.empty()));
        return chain;
    }

    public String getNonce() {
        return nonce;
    }

    @Override
    public Entry toEntry(Optional<String> chainId) {
        return super.toEntry(chainId).setChainId(chainId.orElse(getChainId()));
    }

    @Override
    public void initValidationRules() {
        if (getOperationValue() == OperationValue.IDENTITY_CHAIN_CREATION) {
            addValidationRule(new FactomIdentityChainCreationCompoundRule(toChain()));
        } else if (getOperationValue() == OperationValue.DID_MANAGEMENT) {
            addValidationRule(new DIDV1CreationCompoundRule(toChain()));
        }
    }

    public String getChainId() {
        return Encoding.HEX.encode(ENTRY_OPS.calculateChainId(getExternalIds()));
    }

    public List<String> getAdditionalTags() {
        return additionalTags;
    }

    public void setAdditionalTags(List<String> additionalTags) {
        this.additionalTags = additionalTags;
    }
}

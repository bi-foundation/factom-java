package org.blockchain_innovation.factom.identiy.did.entry;

import com.google.gson.Gson;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.ops.EncodeOperations;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;
import org.factom_protocol.identifiers.did.invoker.JSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class FactomDIDEntry<T> {
    protected static final Gson GSON = JSON.createGson().create();
    protected static final EntryOperations ENTRY_OPS = new EntryOperations();
    protected static final EncodeOperations ENCODE_OPS = new EncodeOperations();

    protected DIDVersion didVersion;
    private OperationValue operationValue;
    protected List<String> extIds = new ArrayList<>();
    protected T content;

    protected FactomDIDEntry(T content, List<String> extIds) {
        this.content = content;
        if (extIds == null || extIds.size() <= 2) {
            throw new DIDRuntimeException("Factom DID Model needs at least 2 extIds");
        }
        this.extIds = extIds;

        this.operationValue = OperationValue.fromOperation(extIds.get(0));
        if (DIDVersion.FACTOM_V1_JSON.getProtocolVersion().equals(extIds.get(1))) {
            this.didVersion = DIDVersion.FACTOM_V1_JSON;
        }

    }

    protected FactomDIDEntry(OperationValue operationValue, DIDVersion didVersion, T content, String... additionalTags) {
        this.didVersion = didVersion;
        this.operationValue = operationValue;
        this.content = content;
        extIds.add(operationValue.getOperation());
        extIds.add(didVersion.getProtocolVersion());
        if (additionalTags != null) {
            extIds.addAll(Arrays.asList(additionalTags));
        }
    }

    protected FactomDIDEntry(Entry entry, Class<T> tClass) {
        this(GSON.fromJson(ENCODE_OPS.decodeHex(entry.getContent()), tClass), ENCODE_OPS.decodeHex(entry.getExternalIds()));
    }

    public List<String> getExternalIds() {
        return extIds;
    }

    public T getContent() {
        return content;
    }

    public OperationValue getOperationValue() {
        return operationValue;
    }

    public Entry toEntry() {
        return new Entry().setContent(GSON.toJson(content)).setExternalIds(extIds);
    }

}

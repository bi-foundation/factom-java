package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.json.JsonConverter;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.ops.EncodeOperations;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;
import org.blockchain_innovation.factom.client.api.ops.StringUtils;
import org.blockchain_innovation.factom.identiy.did.DIDRuntimeException;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.blockchain_innovation.factom.identiy.did.OperationValue;
import org.blockchain_innovation.factom.identiy.did.json.RegisterJsonMappings;
import org.blockchain_innovation.factom.identiy.did.parse.Rule;
import org.blockchain_innovation.factom.identiy.did.parse.RuleException;
import org.factomprotocol.identity.did.model.BlockInfo;

import java.util.*;

public abstract class AbstractFactomIdentityEntry<T> implements FactomIdentityEntry<T> {
    protected static final JsonConverter JSON = RegisterJsonMappings.register(JsonConverter.Provider.newInstance());
    protected static final EntryOperations ENTRY_OPS = new EntryOperations();
    protected static final EncodeOperations ENCODE_OPS = new EncodeOperations();
    protected String chainId;

    protected DIDVersion didVersion;
    private OperationValue operationValue;
    protected List<String> extIds = new ArrayList<>();
    protected T content;
    private Class<T> tClass;
    private List<Rule> rules = new ArrayList<>();


    protected Optional<BlockInfo> blockInfo = Optional.empty();


    protected AbstractFactomIdentityEntry(String chainId, T content, List<String> extIds) {
        this.chainId = chainId;
        this.content = content;
        if (content != null) {
            this.tClass = (Class<T>) content.getClass();
        }
        if (extIds == null || extIds.size() <= 2) {
            throw new DIDRuntimeException("Factom DID Model needs at least 2 extIds");
        }
        this.extIds = extIds;
        this.operationValue = OperationValue.fromOperation(extIds.get(0));

        if (operationValue == OperationValue.IDENTITY_CHAIN_CREATION) {
            this.didVersion = DIDVersion.FACTOM_IDENTITY_CHAIN;
        } else if (DIDVersion.FACTOM_V1_JSON.getProtocolVersion().equals(extIds.get(1))) {
            this.didVersion = DIDVersion.FACTOM_V1_JSON;
        }
    }

    protected AbstractFactomIdentityEntry(OperationValue operationValue, DIDVersion didVersion, T content, String... additionalTags) {
        this.didVersion = didVersion;
        this.operationValue = operationValue;
        this.content = content;
        if (content != null) {
            this.tClass = (Class<T>) content.getClass();
        }
        extIds.add(operationValue.getOperation());
        if (DIDVersion.FACTOM_V1_JSON == didVersion) {
            extIds.add(didVersion.getProtocolVersion());
        }
        if (additionalTags != null) {
            extIds.addAll(Arrays.asList(additionalTags));
        }
    }

    protected AbstractFactomIdentityEntry(Entry entry, Class<T> tClass, BlockInfo blockInfo) {
        this(entry.getChainId(), StringUtils.isEmpty(entry.getContent()) ? null : JSON.fromJson(entry.getContent(), tClass), entry.getExternalIds());
        this.blockInfo = Optional.of(blockInfo);
    }


    @Override
    public List<String> getExternalIds() {
        return extIds;
    }

    @Override
    public T getContent() {
        return content;
    }

    @Override
    public OperationValue getOperationValue() {
        return operationValue;
    }

    @Override
    public Entry toEntry(Optional<String> chainId) {
        return new Entry().
                setChainId(chainId.orElse(this.chainId)).
                setContent(content == null ? null : JSON.toGenericJson(content, tClass)).
                setExternalIds(extIds);
    }

    protected List<Rule> addValidationRule(Rule rule) {
        rules.add(rule);
        return getValidationRules();
    }

    protected List<Rule> addValidationRules(List<Rule> rules) {
        this.rules.addAll(rules);
        return getValidationRules();
    }

    public List<Rule> getValidationRules() {
        return Collections.unmodifiableList(rules);
    }

    public String getChainId() {
        return chainId;
    }

    public abstract void initValidationRules();

    @Override
    public void validate() throws RuleException {
        for (Rule rule : rules) {
            rule.execute();
        }
    }

    @Override
    public DIDVersion getDidVersion() {
        return didVersion;
    }


    @Override
    public Optional<BlockInfo> getBlockInfo() {
        return blockInfo;
    }

}

package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryResponse;
import org.blockchain_innovation.factom.identiy.did.DIDRuntimeException;
import org.blockchain_innovation.factom.identiy.did.OperationValue;
import org.blockchain_innovation.factom.identiy.did.parse.RuleException;
import org.factomprotocol.identity.did.model.BlockInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class IdentityEntryFactory {

    public List<FactomIdentityEntry<?>> from(Map<EntryResponse, BlockInfo> entryResponses, EntryValidation validate) throws RuleException {
        if (entryResponses == null) {
            return new ArrayList<>();
        }
        return entryResponses.entrySet().stream().map(entryResponse -> {
            try {
                return from(entryResponse.getKey(), entryResponse.getValue(), validate);
            } catch (RuleException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public FactomIdentityEntry<?> from(EntryResponse entryResponse, BlockInfo blockInfo, EntryValidation validate) throws RuleException {
        if (entryResponse == null) {
            throw new RuleException("Cannot create an identity entry from an empty response");
        }
        return from(new Entry()
                        .setChainId(entryResponse.getChainId())
                        .setExternalIds(entryResponse.getExtIds())
                        .setContent(entryResponse.getContent()),
                blockInfo, validate
        );
    }

    public FactomIdentityEntry<?> from(Entry entry, BlockInfo blockInfo, EntryValidation validate) throws RuleException {
        if (entry == null) {
            throw new RuleException("Cannot create an identity entry when no entry is supplied");
        } else if (entry.getExternalIds() == null || entry.getExternalIds().size() == 0) {
            throw new RuleException("Cannot create an identity entry when an entry has no external Ids");
        }
        String firstExtId = entry.getExternalIds().get(0);
        try {
            OperationValue operationValue = OperationValue.fromOperation(firstExtId);
            FactomIdentityEntry<?> identityEntry = null;

            switch (operationValue) {
                case IDENTITY_CHAIN_CREATION:
                    identityEntry = new CreateIdentityContentEntry(entry, blockInfo);
                    break;
                case IDENTITY_CHAIN_REPLACE_KEY:
                    identityEntry = new ReplaceKeyIdentityChainEntry(entry, blockInfo);
                    break;
                case DID_MANAGEMENT:
                    identityEntry = new CreateFactomDIDEntry(entry, blockInfo);
                    break;
                case DID_UPDATE:
                    identityEntry = new UpdateFactomDIDEntry(entry, blockInfo);
                    break;
                case DID_DEACTIVATION:
                    identityEntry = new DeactivateFactomDIDEntry(entry, blockInfo);
                    break;
                case DID_METHOD_VERSION_UPGRADE:
                    throw new RuleException("DID method version upgrade not implemented yet");
            }

            if (validate != EntryValidation.SKIP_VALIDATION) {
                try {
                    identityEntry.validate();
                } catch (RuleException re) {
                    if (validate == EntryValidation.THROW_ERROR) {
                        throw re;
                    }
                }
            }
            return identityEntry;
        } catch (DIDRuntimeException dre) {
            throw new RuleException(dre);
        }
    }
}

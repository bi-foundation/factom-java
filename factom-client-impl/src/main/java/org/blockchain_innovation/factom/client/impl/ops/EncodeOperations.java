package org.blockchain_innovation.factom.client.impl.ops;

import org.blockchain_innovation.factom.client.api.Encoding;
import org.blockchain_innovation.factom.client.api.FactomException;
import org.blockchain_innovation.factom.client.api.StringUtils;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;

import java.util.List;
import java.util.stream.Collectors;

public class EncodeOperations {

    public Chain encodeHex(Chain chain) {
        if (chain == null || chain.getFirstEntry() == null) {
            throw new FactomException.ClientException("chain with first entry should given");
        }

        Chain encodedChain = new Chain();
        Chain.Entry firstEntry = new Chain.Entry();
        firstEntry.setContent(encodeHex(chain.getFirstEntry().getContent()));
        firstEntry.setExternalIds(encodeHex(chain.getFirstEntry().getExternalIds()));
        encodedChain.setFirstEntry(firstEntry);
        return encodedChain;
    }

    public Entry encodeHex(Entry entry) {
        if (entry == null || StringUtils.isEmpty(entry.getChainId())) {
            throw new FactomException.ClientException("entry with chain id should be given");
        }

        Entry encodedEntry = new Entry();
        encodedEntry.setChainId(entry.getChainId());
        encodedEntry.setContent(encodeHex(entry.getContent()));
        encodedEntry.setExternalIds(encodeHex(entry.getExternalIds()));
        return encodedEntry;
    }

    public List<String> encodeHex(List<String> list) {
        return list.stream().map(this::encodeHex).collect(Collectors.toList());
    }

    public String encodeHex(String value) {
        return Encoding.HEX.encode(Encoding.UTF_8.decode(value));
    }
}

package org.blockchain_innovation.factom.client.api.ops;

import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Named
@Singleton
public class EncodeOperations {

    /**
     * Encode a Chain. The content and external ids from the first entry will be encoded from UFT-8 to HEX
     *
     * @param chain
     * @return
     */
    public Chain encodeHex(Chain chain) {
        if (chain == null || chain.getFirstEntry() == null) {
            throw new FactomRuntimeException.AssertionException(String.format("Invalid chain. First entry is required in chain '%s'", chain));
        }

        Chain encodedChain = new Chain();
        Entry firstEntry = new Entry();
        firstEntry.setContent(encodeHex(chain.getFirstEntry().getContent()));
        firstEntry.setExternalIds(encodeHex(chain.getFirstEntry().getExternalIds()));
        encodedChain.setFirstEntry(firstEntry);
        return encodedChain;
    }

    /**
     * Encode an Entry to HEX. The content and external ids are encoded from UTF-8 to HEX
     *
     * @param entry
     * @return
     */
    public Entry encodeHex(Entry entry) {
        if (entry == null || StringUtils.isEmpty(entry.getChainId())) {
            throw new FactomRuntimeException.AssertionException(String.format("Invalid entry. Chain id is required in entry '%s'", entry));
        }

        Entry encodedEntry = new Entry();
        encodedEntry.setChainId(entry.getChainId());
        encodedEntry.setContent(encodeHex(entry.getContent()));
        encodedEntry.setExternalIds(encodeHex(entry.getExternalIds()));
        return encodedEntry;
    }

    /**
     * Encode each UTF-8 value in a list to HEX
     *
     * @param list
     * @return
     */
    public List<String> encodeHex(List<String> list) {
        return list.stream().map(this::encodeHex).collect(Collectors.toList());
    }

    /**
     * Encode a UFT-8 value to HEX
     *
     * @param value
     * @return
     */
    public String encodeHex(String value) {
        return Encoding.HEX.encode(Encoding.UTF_8.decode(value));
    }
}

package org.blockchain_innovation.factom.client.api.ops;

import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryResponse;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named
@Singleton
/**
 * Allows Hex encoding of commonly used values
 */
public class EncodeOperations {

    private static final long MSB = 0x8000_0000_0000_0000L;

    /**
     * Encode a Chain. The content and external ids from the first entry will be encoded from UFT-8 to HEX.
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
     * Decode a Chain. The content and external ids from the first entry will be decoded from HEX to UTF-8.
     *
     * @param chain
     * @return
     */
    public Chain decodeHex(Chain chain) {
        if (chain == null || chain.getFirstEntry() == null) {
            throw new FactomRuntimeException.AssertionException(String.format("Invalid chain. First entry is required in chain '%s'", chain));
        }

        Chain decodedChain = new Chain();
        Entry firstEntry = new Entry();
        firstEntry.setContent(decodeHex(chain.getFirstEntry().getContent()));
        firstEntry.setExternalIds(decodeHex(chain.getFirstEntry().getExternalIds()));
        decodedChain.setFirstEntry(firstEntry);
        return decodedChain;
    }

    /**
     * Encode an Entry to HEX. The content and external ids are encoded from UTF-8 to HEX.
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
     * Decode an Entry from HEX. The content and external ids are encoded from HEX to URF-8.
     *
     * @param entry
     * @return
     */
    public Entry decodeHex(Entry entry) {
        if (entry == null || StringUtils.isEmpty(entry.getChainId())) {
            throw new FactomRuntimeException.AssertionException(String.format("Invalid entry. Chain id is required in entry '%s'", entry));
        }

        Entry decodedEntry = new Entry();
        decodedEntry.setChainId(entry.getChainId());
        decodedEntry.setContent(decodeHex(entry.getContent()));
        decodedEntry.setExternalIds(decodeHex(entry.getExternalIds()));
        return decodedEntry;
    }

    /**
     * Encode an EntryResponse to HEX. The content and external ids are encoded from UTF-8 to HEX.
     *
     * @param entryResponse
     * @return
     */
    public EntryResponse encodeHex(EntryResponse entryResponse) {
        if (entryResponse == null || StringUtils.isEmpty(entryResponse.getChainId())) {
            throw new FactomRuntimeException.AssertionException(String.format("Invalid entry response. Chain id is required in entry response '%s'", entryResponse));
        }

        return new EntryResponse(entryResponse.getChainId(), encodeHex(entryResponse.getExtIds()), encodeHex(entryResponse.getContent()));
    }

    /**
     * Decode an EntryResponse from HEX. The content and external ids are encoded from HEX to URF-8.
     *
     * @param entryResponse
     * @return
     */
    public EntryResponse decodeHex(EntryResponse entryResponse) {
        if (entryResponse == null || StringUtils.isEmpty(entryResponse.getChainId())) {
            throw new FactomRuntimeException.AssertionException(String.format("Invalid entry response. Chain id is required in entry '%s'", entryResponse));
        }
        return new EntryResponse(entryResponse.getChainId(), decodeHex(entryResponse.getExtIds()), entryResponse.getContent() == null ? null : decodeHex(entryResponse.getContent()));
    }

    /**
     * Encode each UTF-8 value in a list to HEX.
     *
     * @param utf8Values
     * @return
     */
    public List<String> encodeHex(List<String> utf8Values) {
        return utf8Values.stream().map(this::encodeHex).collect(Collectors.toList());
    }

    /**
     * Decode each HEX value in a list to UTF-8.
     *
     * @param hexValues
     * @return
     */
    public List<String> decodeHex(List<String> hexValues) {
        return hexValues.stream().map(this::decodeHex).collect(Collectors.toList());
    }

    /**
     * Encode a UFT-8 value to HEX.
     *
     * @param utf8Value
     * @return
     */
    public String encodeHex(String utf8Value) {
        return StringUtils.isEmpty(utf8Value) ? utf8Value : Encoding.HEX.encode(Encoding.UTF_8.decode(utf8Value));
    }

    /**
     * Decode a HEX value to UTF-8.
     *
     * @param hexValue
     * @return
     */
    public String decodeHex(String hexValue) {
        return StringUtils.isEmpty(hexValue) ? hexValue : Encoding.UTF_8.encode(Encoding.HEX.decode(hexValue));
    }

    // See https://github.com/FactomProject/factomd/blob/master/common/primitives/varint.go#L81 for original implementation
    // encodeVarInt encodes an integer/long as a variable int into the given data buffer.
    public byte[] encodeVarInt(long input) {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            long value = input;
            boolean start = false;

            if (input == 0) {
                bos.write(0);
            }
            if ((input & MSB) != 0) {
                bos.write(0x81);
                start = true;
            }

            for (int i = 0; i < 9; i++) {
                int b = (int) (value >> 56);
                if (b != 0 || start) {
                    start = true;
                    if (i != 8) {
                        b = b | 0x80;
                    } else {
                        b = b & 0x07f;
                    }
                    bos.write(b);
                }
                value = value << 7;
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new FactomRuntimeException.AssertionException(e);
        }
    }

    public byte[] decodeAddress(String humanReadable) {
        return Arrays.copyOfRange(Encoding.BASE58.decode(humanReadable), 2, 34);
    }

}

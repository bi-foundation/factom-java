/*
 * Copyright 2018 Blockchain Innovation Foundation <https://blockchain-innovation.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.blockchain_innovation.factom.client.api.ops;

import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryResponse;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Named
@Singleton
public class EntryOperations {
    private final ByteOperations byteOps = new ByteOperations();

    /**
     * Calculates the chain Id from an entry response object
     *
     * @param entryResponse Entry response
     * @return
     */
    public byte[] calculateChainId(EntryResponse entryResponse) {
        return calculateChainId(entryResponse.getExtIds());
    }

    /**
     * Calculates the chain Id from a list of external Ids
     *
     * @param externalIds The external ids
     * @return
     */
    public byte[] calculateChainId(List<String> externalIds) {
        byte[] bytes = new byte[0];
        if (externalIds != null) {
            for (String externalId : externalIds) {
                bytes = byteOps.concat(bytes, Digests.SHA_256.digest(externalId));
            }
        }
        // TODO: 14-8-2018 Check empty/null list
        byte[] chainId = Digests.SHA_256.digest(bytes);
        return chainId;
    }

    /**
     * Calculates the entryHash of the first entry in a chain.
     * This is only applicable to the first entry, hence you do not have to provide the chain Id
     *
     * @param externalIds The external Ids
     * @param content     The content
     * @return The entryHash
     */
    public byte[] calculateFirstEntryHash(List<String> externalIds, String content) {
        return calculateEntryHash(externalIds, content, null);
    }


    /**
     * Calculates the entryHash of an entry in a chain.
     *
     * @param externalIds The external Ids
     * @param content     The content
     * @return The entryHash
     * @Param chainId The chain Id
     */
    public byte[] calculateEntryHash(List<String> externalIds, String content, String chainId) {
        byte[] entryBytes = entryToBytes(externalIds, content, chainId);
        byte[] bytes = byteOps.concat(Digests.SHA_512.digest(entryBytes), entryBytes);
        return Digests.SHA_256.digest(bytes);

    }

    /**
     * Converts the first entry of a chain to a byte array. This is needed in some lower level messages
     *
     * @param externalIds The external Ids
     * @param content     The content
     * @return
     */
    public byte[] firstEntryToBytes(List<String> externalIds, String content) {
        return entryToBytes(externalIds, content, null);
    }

    /**
     * Converts an entry of a chain to a byte array. This is needed in some lower level messages
     *
     * @param externalIds The external Ids
     * @param content     The content
     * @return
     * @Param chainId The chain Id
     */
    public byte[] entryToBytes(List<String> externalIds, String content, String chainId) {
        byte[] chainIdBytes;
        byte[] bytes = new byte[0];
        if (StringUtils.isNotEmpty(chainId)) {
            chainIdBytes = Encoding.HEX.decode(chainId);
        } else {
            chainIdBytes = calculateChainId(externalIds);
        }

        // Version 0
        bytes = byteOps.concat(bytes, (byte) 0);
        bytes = byteOps.concat(bytes, chainIdBytes);
        bytes = byteOps.concat(bytes, externalIdsToBytes(externalIds));
        if (StringUtils.isNotEmpty(content)) {
            bytes = byteOps.concat(bytes, Encoding.UTF_8.decode(content));
        }
        return bytes;

    }


    /**
     * Converts the external Ids to a byte-array. Part of the entry to byte conversion
     *
     * @param externalIds
     * @return
     */
    protected byte[] externalIdsToBytes(List<String> externalIds) {
        if (externalIds == null || externalIds.isEmpty()) {
            return new byte[]{(byte) 0};
        }
        byte[] bytes = new byte[0];
        short externalIdLength = 0;

        for (String externalId : externalIds) {
            if (externalId == null) {
                throw new FactomRuntimeException.AssertionException("External Id needs a value or not be in the list at all");
            }
            byte[] extIdAsBytes = Encoding.UTF_8.decode(externalId);
            int length = extIdAsBytes.length;
            bytes = byteOps.concat(bytes, byteOps.toShortBytes(length));
            bytes = byteOps.concat(bytes, extIdAsBytes);

            // We need to add 2 to store the next section's externalID length value
            externalIdLength += length + 2;
        }
        bytes = byteOps.concat(byteOps.toShortBytes(externalIdLength), bytes);
        return bytes;
    }
}

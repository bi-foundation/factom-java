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
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class EntryOperationsTest {

    private static final EntryOperations OPS = new EntryOperations();

    public static final String CHAIN_ID = "502b99271ff6a3f8509ba2764e1e1c1482ad800140c17b25e165837ab5320501";
    public static final String ENTRY_HASH_NO_CONTENT = "322b18e1f267202565bf0d1ee03865f076fd22357ff6dc46c7a6dfef454f3871";
    public static final String ENTRY_HASH_WITH_CONTENT = "ed6d1ac92095db94be1f179d10a275e9468187ffde4eac91164b707793050f30";
    public static final String CHAIN_FIRST_EXTERNAL_ID = "first external id";
    public static final String CHAIN_SECOND_EXTERNAL_ID = "second external id";
    public static final String ENTRY_CONTENT = "Test Entry Content";

    public static final String NULL_ENTRY_CHAIN_ID = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

    static final List<String> EXTERNAL_IDS = Arrays.asList(CHAIN_FIRST_EXTERNAL_ID, CHAIN_SECOND_EXTERNAL_ID);

    @Test
    public void testFirstChainIds() {
        Assert.assertEquals(CHAIN_ID, Encoding.HEX.encode(OPS.calculateChainId(EXTERNAL_IDS)));
        Assert.assertEquals(ENTRY_HASH_NO_CONTENT, Encoding.HEX.encode(OPS.calculateFirstEntryHash(EXTERNAL_IDS, null)));
        Assert.assertEquals(ENTRY_HASH_WITH_CONTENT, Encoding.HEX.encode(OPS.calculateFirstEntryHash(EXTERNAL_IDS, ENTRY_CONTENT)));
        Assert.assertEquals(ENTRY_HASH_NO_CONTENT, Encoding.HEX.encode(OPS.calculateEntryHash(EXTERNAL_IDS, null, CHAIN_ID)));
        Assert.assertEquals(ENTRY_HASH_WITH_CONTENT, Encoding.HEX.encode(OPS.calculateEntryHash(EXTERNAL_IDS, ENTRY_CONTENT, CHAIN_ID)));
    }

    @Test
    public void testNulls() {
        String chainId = Encoding.HEX.encode(OPS.calculateChainId((List<String>) null));
        Assert.assertEquals(NULL_ENTRY_CHAIN_ID, chainId);
        String firstEntryHash = Encoding.HEX.encode(OPS.calculateFirstEntryHash(null, null));

        Assert.assertEquals("f64d788c2d8ae0549e8424060d4271f42f89b445b4b534e9aad5529bedfe9d61", firstEntryHash);
        Assert.assertEquals(firstEntryHash, Encoding.HEX.encode(OPS.calculateEntryHash(null, null, null)));
        Assert.assertEquals(firstEntryHash, Encoding.HEX.encode(OPS.calculateEntryHash(null, null, NULL_ENTRY_CHAIN_ID)));

        try {
            OPS.calculateChainId(Arrays.asList(CHAIN_FIRST_EXTERNAL_ID, CHAIN_SECOND_EXTERNAL_ID, null));
            Assert.fail("Assertion exception should be thrown when a null external id is passed in the list");
        } catch (FactomRuntimeException.AssertionException e) {
        }
        try {
            OPS.externalIdsToBytes(Arrays.asList(CHAIN_FIRST_EXTERNAL_ID, CHAIN_SECOND_EXTERNAL_ID, null));
            Assert.fail("Assertion exception should be thrown when a null external id is passed in the list");
        } catch (FactomRuntimeException.AssertionException e) {
        }
        try {
            OPS.firstEntryToBytes(Arrays.asList(CHAIN_FIRST_EXTERNAL_ID, CHAIN_SECOND_EXTERNAL_ID, null), CHAIN_ID);
            Assert.fail("Assertion exception should be thrown when a null external id is passed in the list");
        } catch (FactomRuntimeException.AssertionException e) {
        }
    }

}

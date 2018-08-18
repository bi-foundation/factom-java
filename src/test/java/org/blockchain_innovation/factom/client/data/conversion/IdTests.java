package org.blockchain_innovation.factom.client.data.conversion;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class IdTests {

    private static final EntryOperations OPS = new EntryOperations();

    public static final String CHAIN_ID = "502b99271ff6a3f8509ba2764e1e1c1482ad800140c17b25e165837ab5320501";
    public static final String ENTRY_HASH_NO_CONTENT = "322b18e1f267202565bf0d1ee03865f076fd22357ff6dc46c7a6dfef454f3871";
    public static final String ENTRY_HASH_WITH_CONTENT = "ed6d1ac92095db94be1f179d10a275e9468187ffde4eac91164b707793050f30";
    public static final String CHAIN_FIRST_EXTERNAL_ID = "first external id";
    public static final String CHAIN_SECOND_EXTERNAL_ID = "second external id";
    public static final String ENTRY_CONTENT = "Test Entry Content";

    static List<String> EXTERNAL_IDS = Arrays.asList(CHAIN_FIRST_EXTERNAL_ID, CHAIN_SECOND_EXTERNAL_ID);


    @Test
    public void testFirstChainIds() {
        Assert.assertEquals(CHAIN_ID, OPS.calculateChainId(EXTERNAL_IDS));
        Assert.assertEquals(ENTRY_HASH_NO_CONTENT, OPS.calculateFirstEntryHash(EXTERNAL_IDS, null));
        Assert.assertEquals(ENTRY_HASH_WITH_CONTENT, OPS.calculateFirstEntryHash(EXTERNAL_IDS, ENTRY_CONTENT));
        Assert.assertEquals(ENTRY_HASH_NO_CONTENT, OPS.calculateEntryHash(EXTERNAL_IDS, null, CHAIN_ID));
        Assert.assertEquals(ENTRY_HASH_WITH_CONTENT, OPS.calculateEntryHash(EXTERNAL_IDS, ENTRY_CONTENT, CHAIN_ID));
    }

    // TODO: 14-8-2018 Add more tests

}
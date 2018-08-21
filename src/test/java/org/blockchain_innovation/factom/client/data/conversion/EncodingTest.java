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

package org.blockchain_innovation.factom.client.data.conversion;

import org.blockchain_innovation.factom.client.data.FactomRuntimeException;
import org.junit.Assert;
import org.junit.Test;

public class EncodingTest {
    private static final String EXAMPLE = "This is an example";

    @Test
    public void utf8Tests() {
        byte[] decoded = Encoding.UTF_8.decode(EXAMPLE);
        Assert.assertNotNull(decoded);
        Assert.assertEquals(18, decoded.length);

        String encoded = Encoding.UTF_8.encode(decoded);
        Assert.assertNotNull(encoded);
        Assert.assertEquals(EXAMPLE, encoded);

        Assert.assertNull(Encoding.UTF_8.encode(null));
        Assert.assertNull(Encoding.UTF_8.decode(null));

    }


    @Test
    public void hexTests() {
        String hexEncoded = Encoding.HEX.encode(Encoding.UTF_8.decode(EXAMPLE));
        Assert.assertNotNull(hexEncoded);
        Assert.assertEquals(36, hexEncoded.length());
        Assert.assertEquals(hexEncoded.toLowerCase(), hexEncoded);

        byte[] hexDecoded = Encoding.HEX.decode(hexEncoded);
        Assert.assertNotNull(hexDecoded);
        Assert.assertEquals(18, hexDecoded.length);
        Assert.assertArrayEquals(hexDecoded, Encoding.HEX.decode(hexEncoded.toUpperCase()));

        try {
            Encoding.HEX.decode("INVALID");
            Assert.fail("Encoding invalid hex value should throw assertionException");
        } catch (FactomRuntimeException.AssertionException e) {

        }

        try {
            Encoding.HEX.encode(null);
            Assert.fail("Encoding null hex value should throw assertionException");
        } catch (FactomRuntimeException.AssertionException e) {

        }
        try {
            Encoding.HEX.decode(null);
            Assert.fail("Decoding null hex value should throw assertionException");
        } catch (FactomRuntimeException.AssertionException e) {
        }
    }

    @Test
    public void base64Tests() {
        String base64Encoded = Encoding.BASE64.encode(Encoding.UTF_8.decode(EXAMPLE));
        Assert.assertNotNull(base64Encoded);
        Assert.assertEquals(24, base64Encoded.length());

        byte[] base64Decoded = Encoding.BASE64.decode(base64Encoded);
        Assert.assertNotNull(base64Decoded);
        Assert.assertArrayEquals(Encoding.UTF_8.decode(EXAMPLE), base64Decoded);

    }
}

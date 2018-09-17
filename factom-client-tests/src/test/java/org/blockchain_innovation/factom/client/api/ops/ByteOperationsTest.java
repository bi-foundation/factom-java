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


import org.blockchain_innovation.factom.client.api.FactomRuntimeException;
import org.bouncycastle.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class ByteOperationsTest {
    private static final ByteOperations OPS = new ByteOperations();
    private static final byte[] EXAMPLE = "This is an example".getBytes();

    @Test(expected = FactomRuntimeException.AssertionException.class)
    public void testNullInt() {
        OPS.toShortBytes((Integer) null);
    }

    @Test(expected = FactomRuntimeException.AssertionException.class)
    public void testIntBiggerThanShort() {
        OPS.toShortBytes(Short.MAX_VALUE + 1);
    }


    @Test
    public void appendTest() {
        Assert.assertArrayEquals(EXAMPLE, OPS.concat(EXAMPLE, (String) null));
        Assert.assertArrayEquals(EXAMPLE, OPS.concat(EXAMPLE, (byte[]) null));
        Assert.assertArrayEquals(EXAMPLE, OPS.concat(EXAMPLE, new byte[]{}));
        Assert.assertArrayEquals(Arrays.concatenate(EXAMPLE, EXAMPLE), OPS.concat(EXAMPLE, EXAMPLE));
        Assert.assertArrayEquals(Arrays.concatenate(EXAMPLE, EXAMPLE), OPS.concat(EXAMPLE, new String(EXAMPLE, StandardCharsets.UTF_8)));
    }
}

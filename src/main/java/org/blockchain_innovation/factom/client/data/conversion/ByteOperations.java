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

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteOperations {

    /**
     * Concatenate two byte arrays
     *
     * @param first
     * @param append
     * @return
     */
    public byte[] concat(byte[] first, byte[] append) {
        if (append == null || append.length == 0) {
            return Arrays.copyOf(first, first.length);
        }
        byte[] result = Arrays.copyOf(first, first.length + append.length);
        System.arraycopy(append, 0, result, first.length, append.length);
        return result;
    }


    /**
     * Concatenate a byte array with a single byte
     *
     * @param first
     * @param append
     * @return
     */
    public byte[] concat(byte[] first, byte append) {
        byte[] result = Arrays.copyOf(first, first.length + 1);
        Array.setByte(result, first.length, append);
        return result;
    }

    public byte[] concat(byte[] first, String append) {
        if (append == null) {
            return first;
        }
        return concat(first, append.getBytes());
    }

    /**
     * Convert a int value (within short range) to byte array
     *
     * @param value
     * @return
     */
    public byte[] toShortBytes(Integer value) {
        if (value == null) {
            return toShortBytes((Short) null);
        } else if (value > Short.MAX_VALUE) {
            throw new FactomRuntimeException.AssertionException("Value " + value + " is to big to store in a short, which is the target datatype");
        }
        return toShortBytes(value.shortValue());
    }


    /**
     * Convert a short value to byte array
     *
     * @param value
     * @return
     */
    public byte[] toShortBytes(Short value) {
        if (value == null) {
            throw new FactomRuntimeException.AssertionException("Null short value not allowed");
        }
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(value);
        return buffer.array();
    }
}

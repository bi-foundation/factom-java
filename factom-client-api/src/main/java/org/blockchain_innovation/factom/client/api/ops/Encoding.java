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

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

public enum Encoding {


    HEX {
        @Override
        public String encode(byte[] input) {
            assertNotNull(input);
            return DatatypeConverter.printHexBinary(input).toLowerCase();
        }

        @Override
        public byte[] decode(String hex) {
            assertNotNull(hex);
            try {
                return DatatypeConverter.parseHexBinary(hex);
            } catch (IllegalArgumentException e) {
                throw new FactomRuntimeException.AssertionException(e);
            }
        }
    },


    BASE64 {
        @Override
        public String encode(byte[] input) {
            return DatatypeConverter.printBase64Binary(input);
        }

        @Override
        public byte[] decode(String base64) {
            return DatatypeConverter.parseBase64Binary(base64);
        }
    },

    BASE58 {
        @Override
        public String encode(byte[] input) {
            return Base58.encode(input);
        }

        @Override
        public byte[] decode(String base64) {
            return Base58.decode(base64);
        }
    },

    UTF_8 {
        @Override
        public String encode(byte[] toEncode) {
            if (toEncode == null) {
                return null;
            }
            return new String(toEncode, StandardCharsets.UTF_8);
        }

        @Override
        public byte[] decode(String toDecode) {
            if (toDecode == null) {
                return null;
            }
            return toDecode.getBytes(StandardCharsets.UTF_8);
        }
    };

    public abstract String encode(byte[] toEncode);

    public abstract byte[] decode(String toDecode);

    protected void assertNotNull(String input) {
        if (StringUtils.isEmpty(input)) {
            throw new FactomRuntimeException.AssertionException(String.format("Input value needs to be not null/empty for %s en- or decoding", name()));
        }
    }

    protected void assertNotNull(byte[] input) {
        if (input == null) {
            throw new FactomRuntimeException.AssertionException(String.format("Input value needs to be not null/empty for %s en- or decoding", name()));
        }
    }
}

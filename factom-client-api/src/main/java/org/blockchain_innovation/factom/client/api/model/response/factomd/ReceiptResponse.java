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

package org.blockchain_innovation.factom.client.api.model.response.factomd;

import java.util.List;

public class ReceiptResponse {

    private Receipt receipt;

    public Receipt getReceipt() {
        return receipt;
    }

    public class Receipt {
        private Entry entry;
        private List<MerkleBranch> merklebranch;
        private String entryblockkeymr;
        private String directoryblockkeymr;
        private String bitcointransactionhash;
        private String bitcoinblockhash;

        public Entry getEntry() {
            return entry;
        }

        public List<MerkleBranch> getMerkleBranch() {
            return merklebranch;
        }

        public String getEntryBlockKeyMR() {
            return entryblockkeymr;
        }

        public String getDirectoryBlockKeyMR() {
            return directoryblockkeymr;
        }

        public String getBitcoinTransactionHash() {
            return bitcointransactionhash;
        }

        public String getBitcoinBlockHash() {
            return bitcoinblockhash;
        }

        public class Entry {
            private String entryhash;

            public String getEntryHash() {
                return entryhash;
            }
        }

        public class MerkleBranch {
            private String left;
            private String right;
            private String top;

            public String getLeft() {
                return left;
            }

            public String getRight() {
                return right;
            }

            public String getTop() {
                return top;
            }
        }
    }
}

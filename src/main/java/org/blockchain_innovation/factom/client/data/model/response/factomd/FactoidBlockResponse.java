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

package org.blockchain_innovation.factom.client.data.model.response.factomd;

import java.util.List;

public class FactoidBlockResponse {

    private FactoidBlock fblock;
    private String rawdata;

    public class FactoidBlock {
        private String bodymr;
        private String prevkeymr;
        private String prevledgerkeymr;
        private int exchrate;
        private int dbheight;
        private List<Transaction> transactions;
        private String chainid;
        private String keymr;
        private String ledgerkeymr;

        public class Transaction {
            private String txid;
            private int blockheight;
            private long millitimestamp;
            private List<Input> inputs;
            private List<Output> outputs;
            private List<String> outecs;
            private List<SigBlock> sigblocks;

            public abstract class IO {
                private int amount;
                private String address;
                private String useraddress;
            }

            public class Input extends IO { }

            public class Output extends IO { }

            public class SigBlock {
                private List<String> signatures;
            }
        }
    }
}

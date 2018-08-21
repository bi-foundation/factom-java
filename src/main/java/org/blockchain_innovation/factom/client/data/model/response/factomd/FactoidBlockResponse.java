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

    public FactoidBlock getFactoidBlock() {
        return fblock;
    }

    public String getRawData() {
        return rawdata;
    }

    public class FactoidBlock {
        private String bodymr;
        private String prevkeymr;
        private String prevledgerkeymr;
        private long exchrate;
        private long dbheight;
        private List<Transaction> transactions;
        private String chainid;
        private String keymr;
        private String ledgerkeymr;

        public String getBodyMR() {
            return bodymr;
        }

        public String getPreviousKeyMR() {
            return prevkeymr;
        }

        public String getPreviousLedgerKeyMR() {
            return prevledgerkeymr;
        }

        public long getExchangeRate() {
            return exchrate;
        }

        public long getDirectoryBlockHeight() {
            return dbheight;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public String getChainId() {
            return chainid;
        }

        public String getKeyMR() {
            return keymr;
        }

        public String getLedgerKeyMR() {
            return ledgerkeymr;
        }

        public class Transaction {
            private String txid;
            private long blockheight;
            private long millitimestamp;
            private List<Input> inputs;
            private List<Output> outputs;
            private List<OutputEntryCredit> outecs;
            private List<SigBlock> sigblocks;

            public String getTxId() {
                return txid;
            }

            public long getBlockHeight() {
                return blockheight;
            }

            public long getMilliTimestamp() {
                return millitimestamp;
            }

            public List<Input> getInputs() {
                return inputs;
            }

            public List<Output> getOutputs() {
                return outputs;
            }

            public List<OutputEntryCredit> getOutputEntryCredits() {
                return outecs;
            }

            public List<SigBlock> getSignatureBlocks() {
                return sigblocks;
            }

            public abstract class IO {
                private long amount;
                private String address;
                private String useraddress;

                public long getAmount() {
                    return amount;
                }

                public String getAddress() {
                    return address;
                }

                public String getUserAddress() {
                    return useraddress;
                }
            }

            public class Input extends IO {
            }

            public class Output extends IO {
            }

            public class OutputEntryCredit extends IO {
            }

            public class SigBlock {
                private List<String> signatures;

                public List<String> getSignatures() {
                    return signatures;
                }
            }
        }
    }
}

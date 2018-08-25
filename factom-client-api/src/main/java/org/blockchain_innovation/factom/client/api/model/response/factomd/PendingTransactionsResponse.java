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

import java.util.ArrayList;
import java.util.List;

public class PendingTransactionsResponse extends ArrayList<PendingTransactionsResponse.PendingTransaction> {

    public static class PendingTransaction {

        private String transactionid;
        private String status;
        private List<Input> inputs;
        private List<Output> outputs;
        private List<EntryCreditOutput> ecoutputs;
        private long fees;

        public String getTransactionId() {
            return transactionid;
        }

        public String getStatus() {
            return status;
        }

        public List<Input> getInputs() {
            return inputs;
        }

        public List<Output> getOutputs() {
            return outputs;
        }

        public List<EntryCreditOutput> getEntryCreditOutputs() {
            return ecoutputs;
        }

        public long getFees() {
            return fees;
        }

        public static abstract class IO {
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

        public static class Input extends IO {

        }


        public static class Output extends IO {

        }

        public static class EntryCreditOutput extends IO {

        }
    }
}

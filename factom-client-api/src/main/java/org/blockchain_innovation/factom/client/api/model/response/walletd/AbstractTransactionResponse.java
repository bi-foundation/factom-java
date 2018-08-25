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

package org.blockchain_innovation.factom.client.api.model.response.walletd;

import java.util.List;

public abstract class AbstractTransactionResponse {

    private boolean signed;
    private String name;
    private long timestamp;
    private long totalecoutputs;
    private long totalinputs;
    private long totaloutputs;

    private List<Input> inputs;
    private List<Output> outputs;
    private List<EntryCreditOutput> ecoutputs;

    public boolean isSigned() {
        return signed;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getTotalEntryCreditOutputs() {
        return totalecoutputs;
    }

    public long getTotalInputs() {
        return totalinputs;
    }

    public long getTotalOutputs() {
        return totaloutputs;
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

    public  static abstract class IO {
        private String address;
        private long amount;

        public String getAddress() {
            return address;
        }

        public long getAmount() {
            return amount;
        }
    }

    public static class Input extends IO {
    }

    public static class Output extends IO {
    }

    public static class EntryCreditOutput extends IO {
    }
}

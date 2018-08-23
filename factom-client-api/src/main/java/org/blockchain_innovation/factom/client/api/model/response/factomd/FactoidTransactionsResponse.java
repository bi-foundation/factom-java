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

public class FactoidTransactionsResponse {

    private String txid;
    private long transactiondate;
    private String transactiondatestring;
    private long blockdate;
    private String blockdatestring;
    private Status status;

    public String getTxId() {
        return txid;
    }

    public long getTransactionDate() {
        return transactiondate;
    }

    public String getTransactionDateString() {
        return transactiondatestring;
    }

    public long getBlockDate() {
        return blockdate;
    }

    public String getBlockDateString() {
        return blockdatestring;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        Unknown,
        NotConfirmed,
        TransactionACK,
        DBlockConfirmed
    }
}

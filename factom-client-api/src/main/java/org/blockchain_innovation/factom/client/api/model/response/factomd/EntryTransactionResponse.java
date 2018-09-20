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

import java.io.Serializable;

public class EntryTransactionResponse implements Serializable {

    private String committxid;
    private String entryhash;
    private CommitData commitdata;
    private EntryData entrydata;

    public String getCommitTxId() {
        return committxid;
    }

    public String getEntryHash() {
        return entryhash;
    }

    public CommitData getCommitData() {
        return commitdata;
    }

    public EntryData getEntryData() {
        return entrydata;
    }

    public enum Status {
        Unknown,
        NotConfirmed,
        TransactionACK,
        DBlockConfirmed
    }

    public static class CommitData implements Serializable {

        private Status status;

        public Status getStatus() {
            return status;
        }
    }

    public static class EntryData implements Serializable {
        private String status;

        public String getStatus() {
            return status;
        }
    }
}

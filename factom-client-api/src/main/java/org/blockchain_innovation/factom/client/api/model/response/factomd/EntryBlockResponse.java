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
import java.util.List;

public class EntryBlockResponse implements Serializable {

    private Header header;
    private List<Entry> entrylist;

    public Header getHeader() {
        return header;
    }

    public List<Entry> getEntryList() {
        return entrylist;
    }

    public static class Header implements Serializable {
        private long blocksequencenumber;
        private String chainid;
        private String prevkeymr;
        private long dbheight;

        public long getBlockSequenceNumber() {
            return blocksequencenumber;
        }

        public String getChainId() {
            return chainid;
        }

        public String getPreviousKeyMR() {
            return prevkeymr;
        }

        public long getDirectoryBlockHeight() {
            return dbheight;
        }
    }

    public static class Entry implements Serializable {
        private String entryhash;
        private long timestamp;

        public String getEntryHash() {
            return entryhash;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}

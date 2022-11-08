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

    public EntryBlockResponse() {
    }

    public EntryBlockResponse(final Header header, final List<Entry> entrylist) {
        this.header = header;
        this.entrylist = entrylist;
    }

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
        private long timestamp;

        public long getTimestamp() {
            return timestamp;
        }

        public long getBlockSequenceNumber() {
            return blocksequencenumber;
        }


        public String getChainId() {
            return chainid;
        }


        public void setChainid(final String chainid) {
            this.chainid = chainid;
        }

        public void setPrevkeymr(final String prevkeymr) {
            this.prevkeymr = prevkeymr;
        }

        public void setDbheight(final long dbheight) {
            this.dbheight = dbheight;
        }

        public String getPreviousKeyMR() {
            return prevkeymr;
        }

        public long getDirectoryBlockHeight() {
            return dbheight;
        }

        public void setBlocksequencenumber(final long blocksequencenumber) {
            this.blocksequencenumber = blocksequencenumber;
        }

        public void setTimestamp(final long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public static class Entry implements Serializable {
        public Entry() {
        }

        public Entry(final String entryhash, final EntryResponse entryResponse) {
            this.entryhash = entryhash;
            this.entryResponse = entryResponse;
        }

        private String entryhash;
        private long timestamp;

        private EntryResponse entryResponse;

        public String getEntryHash() {
            return entryhash;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public EntryResponse getEntryResponse() {
            return entryResponse;
        }

        public void setEntryhash(final String entryhash) {
            this.entryhash = entryhash;
        }

        public void setTimestamp(final long timestamp) {
            this.timestamp = timestamp;
        }

        public void setEntryResponse(final EntryResponse entryResponse) {
            this.entryResponse = entryResponse;
        }
    }
}

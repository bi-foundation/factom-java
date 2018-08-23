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

public class DirectoryBlockHeightResponse {

    private DirectoryBlock dblock;
    private String rawdata;

    public DirectoryBlock getDirectoryBlock() {
        return dblock;
    }

    public String getRawData() {
        return rawdata;
    }

    public class DirectoryBlock {
        private Header header;
        private List<Entry> dbentries;

        private String dbhash;
        private String keymr;

        public Header getHeader() {
            return header;
        }

        public List<Entry> getDirectoryBlockEntries() {
            return dbentries;
        }

        public String getDirectoryBlockHash() {
            return dbhash;
        }

        public String getKeyMR() {
            return keymr;
        }

        public class Header {
            private int version;
            private long networkid;
            private String bodymr;
            private String prevkeymr;
            private String prevfullhash;
            private long timestamp;
            private long dbheight;
            private long blockcount;
            private String chainid;

            public int getVersion() {
                return version;
            }

            public long getNetworkId() {
                return networkid;
            }

            public String getBodyMR() {
                return bodymr;
            }

            public String getPreviousKeyMR() {
                return prevkeymr;
            }

            public String getPreviousFullHash() {
                return prevfullhash;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public long getDirectoryBlockHeight() {
                return dbheight;
            }

            public long getBlockCount() {
                return blockcount;
            }

            public String getChainId() {
                return chainid;
            }
        }

        public class Entry {
            private String chainid;
            private String keymr;

            public String getChainId() {
                return chainid;
            }

            public String getKeyMR() {
                return keymr;
            }
        }
    }
}

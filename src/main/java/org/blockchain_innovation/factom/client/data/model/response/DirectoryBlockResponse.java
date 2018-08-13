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

package org.blockchain_innovation.factom.client.data.model.response;

import java.util.List;

public class DirectoryBlockResponse {


    private DirectoryBlock dblock;
    private String rawdata;


    public class DirectoryBlock {
        private Header header;
        private List<Entry> dbentries;

        private String dbhash;
        private String keymr;

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
        }

        public class Entry {
            private String chainid;
            private String keymr;
        }

    }


}

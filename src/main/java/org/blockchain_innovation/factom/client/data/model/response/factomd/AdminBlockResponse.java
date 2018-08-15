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

public class AdminBlockResponse {

    private AdminBlock ablock;
    private String rawdata;


    public class AdminBlock {
        private Header header;
        private List<Entry> abentries;

        private String backreferencehash;
        private String lookuphash;

        public class Header {

            private String prevbackrefhash;
            private long dbheight;
            private int headerexpansionsize;
            private String headerexpansionarea;
            private long messagecount;
            private long bodysize;
            private String adminchainid;
            private String chainid;
        }

        public class Entry {
            private String identityadminchainid;
            private DBSig prevdbsig;
            private String minutenumber;

            public class DBSig {
                private String pub;
                private String sig;
            }
        }

    }


}

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

public class EntryCreditBlockResponse implements Serializable {

    private EntryCreditBlock ecblock;
    private String rawdata;

    public EntryCreditBlock getEntryCreditBlock() {
        return ecblock;
    }

    public String getRawData() {
        return rawdata;
    }

    public static class EntryCreditBlock implements Serializable {
        private Header header;
        private Body body;

        public Header getHeader() {
            return header;
        }

        public Body getBody() {
            return body;
        }

        public static class Header implements Serializable {
            private String bodyhash;
            private String prevheaderhash;
            private String prevfullhash;
            private long dbheight;
            private String headerexpansionarea;
            private long bodysize;
            private String chainid;
            private String ecchainid;

            public String getBodyHash() {
                return bodyhash;
            }

            public String getPreviousHeaderHash() {
                return prevheaderhash;
            }

            public String getPreviousFullHash() {
                return prevfullhash;
            }

            public long getDirectoryBlockHeight() {
                return dbheight;
            }

            public String getHeaderExpansionArea() {
                return headerexpansionarea;
            }

            public long getBodySize() {
                return bodysize;
            }

            public String getChainId() {
                return chainid;
            }

            public String getEntryCreditChainId() {
                return ecchainid;
            }
        }

        public static class Body implements Serializable {
            private List<Entry> entries;

            public List<Entry> getEntries() {
                return entries;
            }

            public static class Entry implements Serializable {
                private long serverindexnumber;
                private long number;
                private int version;
                private String millitime;
                private String entryhash;
                private long credits;
                private String ecpubkey;
                private String sig;

                public long getServerIndexNumber() {
                    return serverindexnumber;
                }

                public long getNumber() {
                    return number;
                }

                public int getVersion() {
                    return version;
                }

                public String getMilliTime() {
                    return millitime;
                }

                public String getEntryHash() {
                    return entryhash;
                }

                public long getCredits() {
                    return credits;
                }

                public String getEntryCreditPublicKey() {
                    return ecpubkey;
                }

                public String getSiganture() {
                    return sig;
                }
            }
        }
    }
}

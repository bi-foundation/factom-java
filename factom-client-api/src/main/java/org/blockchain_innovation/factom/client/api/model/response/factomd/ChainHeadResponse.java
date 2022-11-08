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

public class ChainHeadResponse implements Serializable {

    private final String entryHash;

    private String keyMr;

    private boolean chaininprocesslist;
    public String getKeyMR() {
        return keyMr;
    }

    public boolean isChainInProcessList() {
        return chaininprocesslist;
    }

    public String getEntryHash() {
        return entryHash;
    }

    public ChainHeadResponse(final String keyMr, final boolean chaininprocesslist, final String entryHash) {
        this.keyMr = keyMr;
        this.chaininprocesslist = chaininprocesslist;
        this.entryHash = entryHash;
    }

}

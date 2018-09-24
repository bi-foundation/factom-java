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

package org.blockchain_innovation.factom.client.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Entry implements Serializable {

    private String chainid;
    private List<String> extids;
    private String content;

    public String getChainId() {
        return chainid;
    }

    public Entry setChainId(String chainid) {
        this.chainid = chainid;
        return this;
    }

    public List<String> getExternalIds() {
        return extids;
    }

    public Entry setExternalIds(List<String> externalIds) {
        this.extids = externalIds;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Entry setContent(String content) {
        this.content = content;
        return this;
    }

    public static class Builder {

        private List<String> externalIds;
        private String content;
        private String chainId;

        public Builder() {
            this.externalIds = new ArrayList<>();
        }

        public Builder(List<String> externalIds) {
            this.externalIds = externalIds;
        }

        public Builder(List<String> externalIds, String content) {
            this.externalIds = externalIds;
            this.content = content;
        }

        public Builder(List<String> externalIds, String content, String chainId) {
            this.externalIds = externalIds;
            this.content = content;
            this.chainId = chainId;
        }

        public String getChainId() {
            return chainId;
        }

        public Builder setChainId(String chainId) {
            this.chainId = chainId;
            return this;
        }

        public Builder addExternalIds(String externalId) {
            externalIds.add(externalId);
            return this;
        }

        public Builder setExternalIds(List<String> externalIds) {
            this.externalIds = externalIds;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Entry build() {
            Entry entry = new Entry();
            entry.setChainId(chainId);
            entry.setContent(content);
            entry.setExternalIds(externalIds);
            return entry;
        }
    }
}

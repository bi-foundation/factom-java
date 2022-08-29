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

/**
 * Represents an entry in the Factom blockchain.
 */
public class Entry implements Serializable {

    private String chainid;
    private List<String> extids = new ArrayList<>();
    private String content;

    /**
     * Get the associated id of the chain of this entry.
     *
     * @return chain Id.
     */
    public String getChainId() {
        return chainid;
    }

    /**
     * Sets the associated id of the chain (to which chain does this entry belong?).
     *
     * @param chainid The chain Id.
     * @return This entry.
     */
    public Entry setChainId(String chainid) {
        this.chainid = chainid;
        return this;
    }

    /**
     * Get a list of external ids for this entry.
     *
     * @return The external Ids.
     */
    public List<String> getExternalIds() {
        return extids;
    }

    /**
     * Set the list of external Ids for this entry.
     *
     * @param externalIds The external Ids.
     * @return This entry.
     */
    public Entry setExternalIds(List<String> externalIds) {
        this.extids = externalIds;
        return this;
    }

    /**
     * Get the content of this entry.
     *
     * @return The conent.
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the content of this entry.
     *
     * @param content The content.
     * @return This entry.
     */
    public Entry setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * A builder class that helps in building entries.
     */
    public static class Builder {

        private List<String> externalIds;
        private String content;
        private String chainId;

        /**
         * Create an emoty builder object.
         */
        public Builder() {
            this.externalIds = new ArrayList<>();
        }

        /**
         * Create a builder with a list of external Ids.
         *
         * @param externalIds This external Ids to include.
         */
        public Builder(List<String> externalIds) {
            this.externalIds = externalIds;
        }

        /**
         * Create a builder with a list of external Ids and a content field.
         *
         * @param externalIds The external Ids to include.
         * @param content     The content field to include.
         */
        public Builder(List<String> externalIds, String content) {
            this.externalIds = externalIds;
            this.content = content;
        }

        /**
         * Create a builder with a list of external Ids, a content field and a chain Id.
         *
         * @param externalIds The external Ids to include.
         * @param content     The content field to include.
         * @param chainId     The chain Id.
         */
        public Builder(List<String> externalIds, String content, String chainId) {
            this.externalIds = externalIds;
            this.content = content;
            this.chainId = chainId;
        }

        /**
         * Get the chain Id for eht entry to be build.
         *
         * @return The chain Id.
         */
        public String getChainId() {
            return chainId;
        }

        /**
         * Set a chain Id for the entry to be build.
         *
         * @param chainId The chain Id to set.
         * @return This builder.
         */
        public Builder setChainId(String chainId) {
            this.chainId = chainId;
            return this;
        }

        /**
         * Add an external Id for the entry to be build.
         *
         * @param externalId The external Id.
         * @return This builder.
         */
        public Builder addExternalIds(String externalId) {
            externalIds.add(externalId);
            return this;
        }

        /**
         * Set the external Ids for the entry to be build.
         *
         * @param externalIds The external Ids to set.
         * @return This builder.
         */
        public Builder setExternalIds(List<String> externalIds) {
            this.externalIds = externalIds;
            return this;
        }

        /**
         * Set a content field for the entry to be build.
         *
         * @param content The content to set.
         * @return This builder.
         */
        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        /**
         * Build the entry.
         *
         * @return The build entry.
         */
        public Entry build() {
            Entry entry = new Entry();
            entry.setChainId(chainId);
            entry.setContent(content);
            entry.setExternalIds(externalIds);
            return entry;
        }
    }
}

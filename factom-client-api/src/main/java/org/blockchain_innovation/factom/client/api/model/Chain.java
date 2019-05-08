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
 * Represents a chain in the Factom blockchain.
 */
public class Chain implements Serializable {

    private Entry firstentry;

    /**
     * Get the first entry of the chain (the entry constitutes the start of the chain).
     * @return First entry of the chain.
     */
    public Entry getFirstEntry() {
        return firstentry;
    }

    /**
     * Sets the first entry of the chain (the entry constitutes the start of the chain).
     * @param firstEntry The first entry of the chain.
     * @return This chain.
     */
    public Chain setFirstEntry(Entry firstEntry) {
        this.firstentry = firstEntry;
        return this;
    }

    /**
     * A builder class to aid in building chain objects.
     */
    public static class Builder {

        private List<String> externalIds;
        private String content;

        public Builder() {
            this.externalIds = new ArrayList<>();
        }

        /**
         * Create a builder with a list of external Ids for the first entry.
         * @param externalIds External Ids.
         */
        public Builder(List<String> externalIds) {
            this.externalIds = externalIds;
        }


        /**
         * Create a builder with a list of external Ids and content field for the first entry.
         * @param externalIds External Ids.
         * @param content Content
         */
        public Builder(List<String> externalIds, String content) {
            this.externalIds = externalIds;
            this.content = content;
        }


        /**
         * Create a builder with a single external Id for the first entry.
         * @param externalId single external Id.
         */
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

        public Chain build() {
            Entry firstEntry = new Entry();
            firstEntry.setExternalIds(externalIds);
            firstEntry.setContent(content);

            Chain chain = new Chain();
            chain.setFirstEntry(firstEntry);

            return chain;
        }
    }
}

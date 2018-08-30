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

import org.blockchain_innovation.factom.client.api.Encoding;
import org.blockchain_innovation.factom.client.api.FactomException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Chain {

    private Entry firstentry;

    public Entry getFirstEntry() {
        return firstentry;
    }

    public Chain setFirstEntry(Entry firstEntry) {
        this.firstentry = firstEntry;
        return this;
    }

    public static class Builder {

        private List<String> externalIds;
        private String content;

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

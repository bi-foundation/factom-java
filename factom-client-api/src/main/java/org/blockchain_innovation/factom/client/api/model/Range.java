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

/**
 * Represents a range (start till end).
 */
public class Range implements Serializable {

    private int start;
    private int end;

    /**
     * Get the start.
     *
     * @return start value.
     */
    public int getStart() {
        return start;
    }

    /**
     * Set the start.
     *
     * @param start start value.
     * @return This range object.
     */
    public Range setStart(int start) {
        this.start = start;
        return this;
    }

    /**
     * Get the end.
     *
     * @return end value.
     */
    public int getEnd() {
        return end;
    }

    /**
     * Set the end.
     *
     * @param end end value.
     * @return This range object.
     */
    public Range setEnd(int end) {
        this.end = end;
        return this;
    }
}

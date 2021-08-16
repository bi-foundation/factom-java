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

package org.blockchain_innovation.factom.client.api.ops;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
/**
 * String Util class for common string functions to make sure we do not have to rely on outside libs like commons-stringutils
 */
public class StringUtils {
    /**
     * Returns true whenever the input string is null or equals an empty string.
     *
     * @param input
     * @return
     */
    public static boolean isEmpty(String input) {
        return input == null || "".equals(input);
    }

    /**
     * Returns true whenever the input string is not null and does not equal an empty string.
     *
     * @param input
     * @return
     */
    public static boolean isNotEmpty(String input) {
        return !isEmpty(input);
    }
}

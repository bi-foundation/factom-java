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

package org.blockchain_innovation.factom.client.api.rpc;

import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.Range;
import org.blockchain_innovation.factom.client.api.ops.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * The Factom APIs use JSON-RPC, which is a remote procedure call protocol encoded in JSON.
 * This RPC request object represents the the request of a API call to the factomd or walletd node
 */
public class RpcRequest implements Serializable {
    private static final String VERSION = "2.0";
    private final String jsonrpc = VERSION;
    private final RpcMethod method;
    private int id;
    private Map<String, Object> params;

    /**
     * Rpc Request without parameters.
     *
     * @param rpcMethod The Rpc method.
     */
    public RpcRequest(RpcMethod rpcMethod) {
        this.method = rpcMethod;
    }


    /**
     * Rpc request with variable amount of parameters (at least one).
     *
     * @param rpcMethod   The Rpc method.
     * @param firstParam  The first parameter.
     * @param extraParams Additional paramaters if any.
     */
    public RpcRequest(RpcMethod rpcMethod, Param<?> firstParam, Param<?>... extraParams) {
        this(rpcMethod);
        addParam(firstParam);
        Arrays.stream(extraParams).forEach(this::addParam);
    }


    /**
     * Rpc Request with parameters.
     *
     * @param rpcMethod The rpc method.
     * @param params    Zero or more parameters.
     */
    public RpcRequest(RpcMethod rpcMethod, Collection<Param<?>> params) {
        this(rpcMethod);
        setParams(params);
    }


    /**
     * Get the Rpc Method associated with the request.
     *
     * @return Rpc method
     */
    public RpcMethod getMethod() {
        return method;
    }

    /**
     * Get the id of the request. The Id can be set by the user to correlate requests and responses. A response will contain the id as well.
     *
     * @return The user specified id or 0.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the id of the request. The Id can be set by the user to correlate requests and responses. A response will contain the id as well.
     *
     * @param id The id to correlate responses.
     * @return This request.
     */
    public RpcRequest setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * The json rpc version ("2.0").
     *
     * @return version "2.0"
     */
    public String getJsonRPC() {
        return jsonrpc;
    }


    /**
     * Get the parameters for this request as map of string keys and object values, as used when serializing.
     *
     * @return The supplied parameters.
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * Sets the parameters for this request.
     *
     * @param params The parameters.
     * @return This request.
     */
    public RpcRequest setParams(Collection<Param<?>> params) {
        if (params == null) {
            this.params = null;
        } else {
            this.params = new HashMap<>();
            params.forEach(param -> param.addToMap(this.params));
        }
        return this;
    }

    /**
     * Returns the params as a list using their original internal Param object.
     *
     * @return Params objects in a list.
     */
    private List<Param<?>> getParamsAsList() {
        if (params == null) {
            return null;
        }
        List<Param<?>> paramList = new ArrayList<>();
        params.forEach((key, value) -> paramList.add(new Param<>(key, value)));
        return paramList;
    }

    /**
     * Add a single Parameter to the list of parameters.
     *
     * @param param The parameter to add.
     * @return This request.
     */
    public RpcRequest addParam(Param<?> param) {
        if (param == null) {
            throw new FactomRuntimeException("Cannot add a null param to an RPC method");
        } else if (params == null) {
            this.params = new HashMap<>();
        }
        this.params = param.addToMap(params);
        return this;
    }

    /**
     * Make sure the method is set and when the request contains parameters that these are valid as well.
     *
     * @return This request.
     */
    protected RpcRequest assertValid() {
        if (getMethod() == null) {
            throw new FactomRuntimeException("Cannot build a request without an RPC method specified");
        }
        if (getParamsAsList() == null) {
            return this;
        }
        getParamsAsList().forEach(RpcRequest.Param::assertValid);
        return this;
    }


    /**
     * A Parameter that has a key an typed value object.
     *
     * @param <T> The value type of the parameter.
     */
    public static class Param<T> {
        private String key;
        private T value;

        /**
         * Create a param using key and value.
         *
         * @param key   The key.
         * @param value The value.
         */
        protected Param(String key, T value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Gets the key of the parameter.
         *
         * @return The key.
         */
        public String getKey() {
            return key;
        }

        /**
         * Allows to replace the key after construction.
         *
         * @param key The new key.
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         * Get the typed value of the parameter.
         *
         * @return The value.
         */
        public T getValue() {
            return value;
        }

        /**
         * Allows to change a value of a parameter after construction.
         *
         * @param value The new value.
         */
        public void setValue(T value) {
            this.value = value;
        }

        /**
         * Makes sure the parameter is value (contains a key and value).
         */
        public void assertValid() {
            if (StringUtils.isEmpty(getKey())) {
                throw new FactomRuntimeException("A RPC param cannot have a null key");
            } else if (getValue() == null || StringUtils.isEmpty(getValue().toString())) {
                throw new FactomRuntimeException("A RPC param cannot have a null value");
            }
        }

        /**
         * Add current parameter to a map. Always use the result value. This method works by reference, unless you supply a null input of course.
         *
         * @param map The map where this param should be added to.
         * @return The result map. Will be the input map with the Param added if the input map was not null.
         */
        protected Map<String, Object> addToMap(Map<String, Object> map) {
            Map<String, Object> result = map;
            if (result == null) {
                result = new HashMap<>();
            }
            result.put(getKey(), getValue());
            return result;
        }
    }

    /**
     * String implementation of Param.
     */
    public static class StringParam extends Param<String> {
        public StringParam(String key, String value) {
            super(key, value);
        }
    }


    /**
     * Number implementation of Param.
     */
    public static class NumberParam extends Param<Number> {
        public NumberParam(String key, Number value) {
            super(key, value);
        }
    }


    /**
     * Chain implementation of Param.
     */
    public static class ChainParam extends Param<Chain> {
        public ChainParam(String key, Chain value) {
            super(key, value);
        }
    }


    /**
     * Entry implementation of Param.
     */
    public static class EntryParam extends Param<Entry> {
        public EntryParam(String key, Entry value) {
            super(key, value);
        }
    }


    /**
     * Address list implementation of Param.
     */
    public static class AddressListParam extends Param<List<Address>> {
        public AddressListParam(String key, List<Address> value) {
            super(key, value);
        }
    }

    /**
     * Range (begin, end) implementation of Param.
     */
    public static class RangeParam extends Param<Range> {
        public RangeParam(String key, Range value) {
            super(key, value);
        }
    }

    /**
     * Builder for Rpc Requests.
     */
    public static class Builder {
        private final RpcMethod method;
        // We allow both using the builder or not
        private int id;
        private List<Param<?>> params;

        /**
         * Create a builder from using an Rpc method.
         *
         * @param rpcMethod The Rpc method.
         */
        public Builder(RpcMethod rpcMethod) {
            this.method = rpcMethod;
            clearParams();
        }

        /**
         * Set the id of the request. The Id can be set by the user to correlate requests and responses. A response will contain the id as well.
         *
         * @param id {@link #setId(int)}
         * @return This builder.
         * @see #setId(int)
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Adds a generic typed Parameter to the request.
         *
         * @param param The param.
         * @return This builder.
         */
        public Builder param(RpcRequest.Param<?> param) {
            this.params.add(param);
            return this;
        }

        /**
         * Adds a number Parameter to the request.
         *
         * @param paramKey   The parameter key.
         * @param paramValue The param number value.
         * @return This builder.
         */
        public Builder param(String paramKey, Number paramValue) {
            param(new RpcRequest.NumberParam(paramKey, paramValue));
            return this;
        }

        /**
         * Adds a string Parameter to the request.
         *
         * @param paramKey   The parameter key.
         * @param paramValue The param string value.
         * @return This builder.
         */
        public Builder param(String paramKey, String paramValue) {
            param(new RpcRequest.StringParam(paramKey, paramValue));
            return this;
        }

        /**
         * Adds a chain Parameter to the request.
         *
         * @param paramKey   The parameter key.
         * @param paramValue The param chain value.
         * @return This builder.
         */
        public Builder param(String paramKey, Chain paramValue) {
            param(new RpcRequest.ChainParam(paramKey, paramValue));
            return this;
        }

        /**
         * Adds an entry Parameter to the request.
         *
         * @param paramKey   The parameter key.
         * @param paramValue The param entry value.
         * @return This builder.
         */
        public Builder param(String paramKey, Entry paramValue) {
            param(new RpcRequest.EntryParam(paramKey, paramValue));
            return this;
        }

        /**
         * Adds an address list Parameter to the request.
         *
         * @param paramKey   The parameter key.
         * @param paramValue The param address list value.
         * @return This builder.
         */
        public Builder param(String paramKey, List<Address> paramValue) {
            param(new AddressListParam(paramKey, paramValue));
            return this;
        }

        /**
         * Adds a range Parameter to the request.
         *
         * @param paramKey   The parameter key.
         * @param paramValue The param range value.
         * @return This builder.
         */
        public Builder param(String paramKey, Range paramValue) {
            param(new RpcRequest.RangeParam(paramKey, paramValue));
            return this;
        }

        /**
         * Clears all parameers gathered so far.
         *
         * @return This builder.
         */
        public Builder clearParams() {
            this.params = new ArrayList<>();
            return this;
        }


        /**
         * Builds a new Rpc Request from current builder.
         *
         * @return The Rpc request.
         */
        public RpcRequest build() {
            RpcRequest rpcRequest = new RpcRequest(method);
            rpcRequest.setId(id);
            if (params != null) {
                rpcRequest.setParams(params);
            }
            return rpcRequest.assertValid();
        }
    }
}


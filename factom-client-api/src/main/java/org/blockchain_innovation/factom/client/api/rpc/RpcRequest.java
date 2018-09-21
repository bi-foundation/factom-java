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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpcRequest {
    private static final String VERSION = "2.0";
    private final String jsonrpc = VERSION;
    private RpcMethod method;
    private int id;
    private Map<String, Object> params;

    public RpcRequest(RpcMethod rpcMethod) {
        this.method = rpcMethod;
    }

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public RpcRequest(RpcMethod rpcMethod, Param<?> param) {
        this(rpcMethod);
        addParam(param);
    }

    public RpcRequest(RpcMethod rpcMethod, Param<?> firstParam, Param<?>... extraParams) {
        this(rpcMethod, firstParam);
        Arrays.stream(extraParams).forEach(this::addParam);
    }

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public RpcRequest(RpcMethod rpcMethod, Collection<Param<?>> params) {
        this(rpcMethod);
        setParams(params);
    }


    public RpcMethod getMethod() {
        return method;
    }

    public int getId() {
        return id;
    }

    public RpcRequest setId(int id) {
        this.id = id;
        return this;
    }

    public String getJsonRPC() {
        return jsonrpc;
    }


    public Map<String, Object> getParams() {
        return params;
    }

    public RpcRequest setParams(Collection<Param<?>> params) {
        if (params == null) {
            this.params = null;
        } else {
            this.params = new HashMap<>();
            params.forEach(param -> param.addToMap(this.params));
        }
        return this;
    }

    private List<Param<?>> getParamsAsList() {
        if (params == null) {
            return null;
        }
        List<Param<?>> paramList = new ArrayList<>();
        params.forEach((key, value) -> paramList.add(new Param<>(key, value)));
        return paramList;
    }

    public RpcRequest addParam(Param<?> param) {
        if (param == null) {
            throw new FactomRuntimeException("Cannot add a null param to an RPC method");
        } else if (params == null) {
            this.params = new HashMap<>();
        }
        this.params = param.addToMap(params);
        return this;
    }

    private RpcRequest assertValid() {
        if (getMethod() == null) {
            throw new FactomRuntimeException("Cannot build a request without an RPC method specified");
        }
        if (getParamsAsList() == null) {
            return this;
        }
        getParamsAsList().forEach(RpcRequest.Param::assertValid);
        return this;
    }


    public static class Param<T> {
        private String key;
        private T value;

        protected Param(String key, T value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public void assertValid() {
            if (StringUtils.isEmpty(getKey())) {
                throw new FactomRuntimeException("A RPC param cannot have a null key");
            }
            // TODO: 11/08/2018 Null values allowed?
        }

        protected Map<String, Object> addToMap(Map<String, Object> map) {
            Map<String, Object> result = map;
            if (result == null) {
                result = new HashMap<>();
            }
            result.put(getKey(), getValue());
            return result;
        }
    }

    public static class StringParam extends Param<String> {
        public StringParam(String key, String value) {
            super(key, value);
        }
    }

    public static class NumberParam extends Param<Number> {
        public NumberParam(String key, Number value) {
            super(key, value);
        }
    }

    public static class ChainParam extends Param<Chain> {
        public ChainParam(String key, Chain value) {
            super(key, value);
        }
    }

    public static class EntryParam extends Param<Entry> {
        public EntryParam(String key, Entry value) {
            super(key, value);
        }
    }

    public static class AddressesParam extends Param<List<Address>> {
        public AddressesParam(String key, List<Address> value) {
            super(key, value);
        }
    }

    public static class RangeParam extends Param<Range> {
        public RangeParam(String key, Range value) {
            super(key, value);
        }
    }

    public static class Builder {
        private final RpcMethod method;
        // We allow both using the builder or not
        private int id;
        private List<Param<?>> params;

        public Builder(RpcMethod rpcMethod) {
            this.method = rpcMethod;
            clearParams();
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder param(RpcRequest.Param<?> param) {
            this.params.add(param);
            return this;
        }

        public Builder param(String paramKey, Number paramValue) {
            param(new RpcRequest.NumberParam(paramKey, paramValue));
            return this;
        }

        public Builder param(String paramKey, String paramValue) {
            param(new RpcRequest.StringParam(paramKey, paramValue));
            return this;
        }

        public Builder param(String paramKey, Chain paramValue) {
            param(new RpcRequest.ChainParam(paramKey, paramValue));
            return this;
        }

        public Builder param(String paramKey, Entry paramValue) {
            param(new RpcRequest.EntryParam(paramKey, paramValue));
            return this;
        }

        public Builder param(String paramKey, List<Address> paramValue) {
            param(new RpcRequest.AddressesParam(paramKey, paramValue));
            return this;
        }

        public Builder param(String paramKey, Range paramValue) {
            param(new RpcRequest.RangeParam(paramKey, paramValue));
            return this;
        }

        public Builder clearParams() {
            this.params = new ArrayList<>();
            return this;
        }


        public RpcRequest build() {
            RpcRequest rpcRequest = new RpcRequest(method);
            rpcRequest.setId(id);
            if (params != null) {
                rpcRequest.setParams(params);
            }

            return rpcRequest;
        }
    }
}


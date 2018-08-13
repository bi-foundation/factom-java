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

package org.blockchain_innovation.factom.client.data.model.rpc;

import org.blockchain_innovation.factom.client.data.FactomRuntimeException;
import org.blockchain_innovation.factom.client.data.conversion.StringUtils;

import java.util.*;

public class RpcRequest {
    private int id;
    private final String jsonrpc = "2.0";
    private final RpcMethod method;
    private Map<String, Object> params;


    private RpcRequest(Builder builder) {
        this.method = builder.method;
        setId(builder.id);
        if (builder.params != null) {
            setParams(builder.params);
        }
    }

    public RpcRequest(RpcMethod rpcMethod) {
        this.method = rpcMethod;
    }

    public RpcRequest(RpcMethod rpcMethod, Param<?> param) {
        this(rpcMethod);
        addParam(param);
    }

    public RpcRequest(RpcMethod rpcMethod, Param<?> firstParam, Param<?>... extraParams) {
        this(rpcMethod, firstParam);
        Arrays.stream(extraParams).forEach(param -> addParam(param));
    }

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

    public String getJsonrpc() {
        return jsonrpc;
    }


    public List<Param<?>> getParams() {
        if (params == null) {
            return null;
        }
        List<Param<?>> paramList = new ArrayList<>();
        params.forEach((key, value) -> paramList.add(new Param<>(key, value)));
        return paramList;
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

    public RpcRequest addParam(Param<?> param) {
        if (param == null) {
            throw new FactomRuntimeException("Cannot add a null param to an RPC method");
        } else if (params == null) {
            this.params = new HashMap<>();
        }
        param.addToMap(params);
        return this;
    }

    private RpcRequest assertValid() {
        if (getMethod() == null) {
            throw new FactomRuntimeException("Cannot build a request without an RPC method specified");
        }
        if (getParams() == null) {
            return this;
        }
        getParams().forEach(RpcRequest.Param::assertValid);
        return this;
    }


    public static class Param<T> {
        private String key;
        private T value;

        protected Param(String key, T value) {
            setKey(key);
            setValue(value);
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
            map.put(getKey(), getValue());
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

    public static class Builder {
        // We allow both using the builder or not
        private int id;
        private RpcMethod method;
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
            params.add(param);
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

        public Builder clearParams() {
            this.params = new ArrayList<>();
            return this;
        }


        public RpcRequest build() {
            return new RpcRequest(this).assertValid();
        }
    }
}

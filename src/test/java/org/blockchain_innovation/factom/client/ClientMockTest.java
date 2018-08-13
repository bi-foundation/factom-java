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

package org.blockchain_innovation.factom.client;

import com.google.common.base.Charsets;
import org.blockchain_innovation.factom.client.data.FactomException;
import org.blockchain_innovation.factom.client.data.conversion.json.GsonConverter;
import org.blockchain_innovation.factom.client.data.conversion.json.JsonConverter;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcMethod;
import org.junit.*;
import org.mockserver.junit.MockServerRule;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ClientMockTest {


    private final FactomdClient client = new FactomdClient();

    @Rule
    public final MockServerRule mockServerRule = new MockServerRule(this, 2020);


    @Before
    public void setup() throws MalformedURLException {
        client.setUrl(new URL("http://localhost:2020/v2"));

        //Fixme
        GsonConverter gsonConverter = new GsonConverter();

        mockServerRule.getClient()
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/v2")
                                .withHeader("Content-Type", "application/json")
                        .withBody(JsonConverter.Registry.sharedInstance().toJson(RpcMethod.HEIGHTS.toRequest()))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(new StringWriter()
                                        .append("{\n")
                                        .append("   \"jsonrpc\":\"2.0\",\n")
                                        .append("   \"id\":0,\n")
                                        .append("   \"result\":{\n")
                                        .append("      \"message\":\"Chain Commit Success\",\n")
                                        .append("      \"txid\":\"76e123d133a841fe3e08c5e3f3d392f8431f2d7668890c03f003f541efa8fc61\",\n")
                                        .append("      \"entryhash\":\"f5c956749fc3eba4acc60fd485fb100e601070a44fcce54ff358d60669854734\",\n")
                                        .append("      \"chainid\":\"f9164cd66af9d5773b4523a510b5eefb9a5e626480feeb6671ef2d17510ca300\"\n")
                                        .append("   }\n")
                                        .append("}")
                                        .toString()
                                        , Charsets.UTF_8
                                )
                );
    }

    @Test
    public void testExchange() throws FactomException.ClientException, MalformedURLException {

        FactomResponse<List> response = client.exchange(RpcMethod.HEIGHTS.toRequest(), List.class).getFactomResponse();
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getRpcResponse());
        Assert.assertNotNull(response.getResult());
        Assert.assertEquals(0, response.getRpcResponse().getId());
        Assert.assertEquals("2.0", response.getRpcResponse().getJsonrpc());
        System.out.println(response.getRpcResponse());

    }

}

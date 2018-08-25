package org.blockchain_innovation.factom.client.impl.json.gson;

import org.blockchain_innovation.factom.client.api.json.JsonConverter;
import org.blockchain_innovation.factom.client.api.model.response.factomd.DirectoryBlockResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcMethod;
import org.blockchain_innovation.factom.client.api.rpc.RpcRequest;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

public class JsonConverterGSONTest {

    private static final JsonConverterGSON CONV = new JsonConverterGSON();


    @Test
    public void testRegistration() {
        Assert.assertNotNull(JsonConverter.Registry.newInstance());
        Assert.assertNotNull(JsonConverter.Registry.sharedInstance());
        Assert.assertEquals(CONV.getClass(), JsonConverter.Registry.newInstance().getClass());
        Assert.assertEquals(CONV.getClass(), JsonConverter.Registry.sharedInstance().getClass());
        Assert.assertTrue(JsonConverter.Registry.sharedInstance() == JsonConverter.Registry.sharedInstance());
        Assert.assertFalse(JsonConverter.Registry.newInstance() == JsonConverter.Registry.newInstance());
    }

    @Test
    public void testPropertiesReqToJson() {
        String json = CONV.toJson(RpcMethod.PROPERTIES.toRequest());
        Assert.assertEquals(PROPERTIES_REQ, json);
    }

    @Test
    public void testFactomChainHeadReqToJson() {
        RpcRequest rpcRequest = RpcMethod.CHAIN_HEAD.toRequestBuilder().id(5).param("chainid", "TEST").build();
        String json = CONV.toJson(rpcRequest);
        Assert.assertEquals(CHAINHEAD_REQ, json);
    }


    @Test
    public void testFactomChainHeadReqToJsonWithPrettyPrintDisabled() {
        RpcRequest rpcRequest = RpcMethod.CHAIN_HEAD.toRequestBuilder().id(5).param("chainid", "TEST").build();
        Properties properties = new Properties();
        properties.setProperty("json.prettyprint", "false");
        properties.setProperty("json.lenient", "false");
        CONV.configure(properties);
        String json = CONV.toJson(rpcRequest);
        properties.clear();
        Assert.assertNotEquals(CHAINHEAD_REQ, json);
        Assert.assertEquals("{\"jsonrpc\":\"2.0\",\"method\":\"chain-head\",\"id\":5,\"params\":{\"chainid\":\"TEST\"}}", json);
    }

    @Test
    public void testPrettyPrint() {
        String json = CONV.prettyPrint("{three:lines}");
        Assert.assertEquals(
                "{\n" +
                        "  \"three\": \"lines\"\n" +
                        "}",
                json);

        // Setting prettyprint to false, but the prettyprint function should always prettyprint of course
        Properties properties = new Properties();
        properties.setProperty("json.prettyprint", "false");
        properties.setProperty("json.lenient", "false");
        CONV.configure(properties);
        json = CONV.prettyPrint("{three:lines}");
        properties.clear();
        CONV.configure(null);

        Assert.assertEquals(
                "{\n" +
                        "  \"three\": \"lines\"\n" +
                        "}",
                json);

    }

    @Test
    public void testErrorFromJson() {
        RpcErrorResponse errorResponse = CONV.errorFromJson(METHOD_NOT_FOUND_RESP);

        Assert.assertNotNull(errorResponse);
        Assert.assertEquals("2.0", errorResponse.getJsonrpc());
        Assert.assertEquals(51, errorResponse.getId());
        Assert.assertNotNull(errorResponse.getError());
        Assert.assertEquals(-32601, errorResponse.getError().getCode());
        Assert.assertEquals("Method not found", errorResponse.getError().getMessage());
    }

    @Test
    public void testRespFromJson() {
        RpcResponse<DirectoryBlockResponse> response = CONV.fromJson(DIRECTORY_BLOCK_RESP, DirectoryBlockResponse.class);
        Assert.assertNotNull(response);
        Assert.assertEquals("2.0", response.getJsonrpc());
        Assert.assertEquals(3, response.getId());
        Assert.assertNotNull(response.getResult());
        DirectoryBlockResponse.Header header = response.getResult().getHeader();
        Assert.assertNotNull(header);
        Assert.assertEquals("7d15d82e70201e960655ce3e7cf475c9da593dfb82c6dca6377349bd148bf001", header.getPreviousBlockKeyMR());
        Assert.assertEquals(72497, header.getSequenceNumber());
        Assert.assertEquals(1484858820, header.getTimestamp());

        List<DirectoryBlockResponse.Entry> entries = response.getResult().getEntryblockList();
        Assert.assertNotNull(entries);
        Assert.assertEquals(3, entries.size());

        DirectoryBlockResponse.Entry entry = entries.get(1);
        Assert.assertEquals("000000000000000000000000000000000000000000000000000000000000000c", entry.getChainId());
        Assert.assertEquals("5f8c98930a1874a46b47b65b9376a02fbff65b760f6866519799d69e2bc019ee", entry.getKeyMR());


    }


    private static final String PROPERTIES_REQ =
            "{\n" +
                    "  \"jsonrpc\": \"2.0\",\n" +
                    "  \"method\": \"properties\",\n" +
                    "  \"id\": 0\n" +
                    "}";

    private static final String CHAINHEAD_REQ = "{\n" +
            "  \"jsonrpc\": \"2.0\",\n" +
            "  \"method\": \"chain-head\",\n" +
            "  \"id\": 5,\n" +
            "  \"params\": {\n" +
            "    \"chainid\": \"TEST\"\n" +
            "  }\n" +
            "}";

    private static final String METHOD_NOT_FOUND_RESP = "{\n" +
            "    \"jsonrpc\": \"2.0\",\n" +
            "    \"id\": 51,\n" +
            "    \"error\": {\n" +
            "        \"code\": -32601,\n" +
            "        \"message\": \"Method not found\"\n" +
            "    }\n" +
            "}";

    private static final String DIRECTORY_BLOCK_RESP = "{  \n" +
            "   \"jsonrpc\":\"2.0\",\n" +
            "   \"id\":3,\n" +
            "   \"result\":{  \n" +
            "      \"header\":{  \n" +
            "         \"prevblockkeymr\":\"7d15d82e70201e960655ce3e7cf475c9da593dfb82c6dca6377349bd148bf001\",\n" +
            "         \"sequencenumber\":72497,\n" +
            "         \"timestamp\":1484858820\n" +
            "      },\n" +
            "      \"entryblocklist\":[  \n" +
            "         {  \n" +
            "            \"chainid\":\"000000000000000000000000000000000000000000000000000000000000000a\",\n" +
            "            \"keymr\":\"3faa880a97ef6ce1feca643cffa015dd6be6a597b3f9260e408c5ac9351d1f8d\"\n" +
            "         },\n" +
            "         {  \n" +
            "            \"chainid\":\"000000000000000000000000000000000000000000000000000000000000000c\",\n" +
            "            \"keymr\":\"5f8c98930a1874a46b47b65b9376a02fbff65b760f6866519799d69e2bc019ee\"\n" +
            "         },\n" +
            "         {  \n" +
            "            \"chainid\":\"000000000000000000000000000000000000000000000000000000000000000f\",\n" +
            "            \"keymr\":\"8c6fed0f41317cc45201b5b170a9ac5bc045029e39a90b6061211be2c0678718\"\n" +
            "         }\n" +
            "      ]\n" +
            "   }\n" +
            "}";
}

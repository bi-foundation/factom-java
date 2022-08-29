package org.blockchain_innovation.accumulate.factombridge.impl;

import io.accumulatenetwork.sdk.api.v2.AccumulateAsyncApi;
import io.accumulatenetwork.sdk.api.v2.TransactionQueryResult;
import io.accumulatenetwork.sdk.commons.codec.DecoderException;
import io.accumulatenetwork.sdk.commons.codec.binary.Hex;
import io.accumulatenetwork.sdk.generated.apiv2.TxnQuery;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

public class FactomToAccumulateBridge {
    private static final Logger logger = LogFactory.getLogger(FactomToAccumulateBridge.class);

    private AccumulateAsyncApi accumulateApi;

    FactomToAccumulateBridge() {
    }

    void configure(final RpcSettings settings) {
        switch (settings.getSubSystem()) {
            case FACTOMD:
                try {
                    accumulateApi = new AccumulateAsyncApi(settings.getServer().getURL().toURI());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                break;
            case WALLETD:
                break;
        }
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> commitChain(final String message) {
        /**
         * IN
          chain = {Chain@2198}
           firstentry = {Entry@2223}
            chainid = null
            extids = {Arrays$ArrayList@2224}  size = 2
             0 = "ChainEntryIT"
             1 = "Thu Aug 25 11:28:11 CEST 2022"
            content = "ChainEntry integration test content"

         OUT
         rpcResponse = {RpcResponse@2212}
         id = 0
         jsonrpc = null
         result = {ComposeResponse@2213}
         commit = {ComposeResponse$Commit@2214}
         jsonrpc = "2.0"
         id = 0
         params = {ComposeResponse$Commit$Params@2219}
         message = "000182d4549e245d6db974275641e04b9b23b66a6c42521b26f8430c2a5562d665edb4487b962af886b6f715550291419a1f2ff5111df40a58d87ecc28b8bec31df1974b3f40e1bd378de1950dc6700c592949fd0d4d51b58818e6625aae61a14c722e2d9b85f50bf48167f7f868d6163b4afb49a357829d9bdb2de092374d357424e2318c6f894a5fd696da71429862843ecd8f7b1342f3111cb88c25bd38a955a542d22045f04ea9a2377096075e0e89b1da8a6a3916407cc5db191764e6c436f97d1e540ed803"
         method = "commit-chain"
         reveal = {ComposeResponse$Reveal@2215}
         jsonrpc = "2.0"
         id = 0
         params = {ComposeResponse$Reveal$Params@2217}
         entry = "00cebbfb60b8ed685c968330606b9109252204d2f286bf9f71af6911abaad8b9c9002d000c436861696e456e7472794954001d546875204175672032352031313a32383a313120434553542032303232436861696e456e74727920696e746567726174696f6e207465737420636f6e74656e74"
         method = "reveal-chain"
         */

        return CompletableFuture.supplyAsync(() -> {


        try {
            final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(Hex.decodeHex(message)));
            final byte version = inputStream.readByte();
            final byte[] timeStamp = new byte[6];
            inputStream.read(timeStamp);
            final byte[] chainIdHash = new byte[32];
            inputStream.read(chainIdHash);
            final byte[] chainWeld = new byte[32];
            inputStream.read(chainWeld);
            final byte[] entryHash = new byte[32];
            inputStream.read(entryHash);
            final byte cost = inputStream.readByte();
            FactomResponse<Result> factomResponse
            return new CommitChainResponse();
        } catch (DecoderException | IOException e) {
            throw new RuntimeException(e);
        }
        });
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> revealChain(String entry, boolean logErrors) {
        return accumulateApi.getTx(new TxnQuery())
                .thenApply(txQueryResult -> toFactomResponse(txQueryResult));
    }

    private <RpcResult> FactomResponse<RpcResult> toFactomResponse(TransactionQueryResult txQueryResult) {
        switch (txQueryResult.getTxType()) {
            case UNKNOWN:
                break;
            case CREATE_IDENTITY:
                break;
            case CREATE_TOKEN_ACCOUNT:
                break;
            case SEND_TOKENS:
                break;
            case CREATE_DATA_ACCOUNT:
                break;
            case WRITE_DATA:
                break;
            case WRITE_DATA_TO:
                break;
            case ACME_FAUCET:
                break;
            case CREATE_TOKEN:
                break;
            case ISSUE_TOKENS:
                break;
            case BURN_TOKENS:
                break;
            case CREATE_LITE_TOKEN_ACCOUNT:
                break;
            case CREATE_KEY_PAGE:
                break;
            case CREATE_KEY_BOOK:
                break;
            case ADD_CREDITS:
                break;
            case UPDATE_KEY_PAGE:
                break;
            case LOCK_ACCOUNT:
                break;
            case UPDATE_ACCOUNT_AUTH:
                break;
            case UPDATE_KEY:
                break;
            case REMOTE:
                break;
            case SYNTHETIC_CREATE_IDENTITY:
                break;
            case SYNTHETIC_WRITE_DATA:
                break;
            case SYNTHETIC_DEPOSIT_TOKENS:
                break;
            case SYNTHETIC_DEPOSIT_CREDITS:
                break;
            case SYNTHETIC_BURN_TOKENS:
                break;
            case SYNTHETIC_FORWARD_TRANSACTION:
                break;
            case SYSTEM_GENESIS:
                break;
            case DIRECTORY_ANCHOR:
                break;
            case BLOCK_VALIDATOR_ANCHOR:
                break;
            case SYSTEM_WRITE_DATA:
                break;
        }
        return null;
    }
}

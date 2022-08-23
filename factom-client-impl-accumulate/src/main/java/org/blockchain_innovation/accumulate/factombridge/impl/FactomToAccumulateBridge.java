package org.blockchain_innovation.accumulate.factombridge.impl;

import io.accumulatenetwork.sdk.api.v2.AccumulateAsyncApi;
import io.accumulatenetwork.sdk.api.v2.TransactionQueryResult;
import io.accumulatenetwork.sdk.generated.apiv2.TxnQuery;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;

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

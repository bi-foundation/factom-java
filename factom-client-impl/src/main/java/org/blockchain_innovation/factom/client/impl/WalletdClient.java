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

package org.blockchain_innovation.factom.client.impl;

import org.blockchain_innovation.factom.client.api.AddressType;
import org.blockchain_innovation.factom.client.api.FactomException;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.Range;
import org.blockchain_innovation.factom.client.api.model.response.walletd.AddressResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.AddressesResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.BlockHeightTransactionsResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.DeleteTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ExecutedTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.GetHeightResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.PropertiesResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.TransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.TransactionsResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.WalletBackupResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcMethod;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WalletdClient extends AbstractClient {

    public CompletableFuture<FactomResponse<TransactionResponse>> addEntryCreditOutput(String txName, String address, long amount) throws FactomException.ClientException {
        AddressType.ENTRY_CREDIT_PUBLIC.assertValid(address);
        return exchange(RpcMethod.ADD_ENTRY_CREDIT_OUTPUT.toRequestBuilder().param("tx-name", txName).param("address", address).param("amount", amount), TransactionResponse.class);
    }

    public CompletableFuture<FactomResponse<ExecutedTransactionResponse>> addFee(String txName, String address) throws FactomException.ClientException {
        AddressType.FACTOID_PUBLIC.assertValid(address);
        return exchange(RpcMethod.ADD_FEE.toRequestBuilder().param("tx-name", txName).param("address", address), ExecutedTransactionResponse.class);
    }

    public CompletableFuture<FactomResponse<ExecutedTransactionResponse>> addInput(String txName, String address, long amount) throws FactomException.ClientException {
        AddressType.FACTOID_PUBLIC.assertValid(address);
        return exchange(RpcMethod.ADD_INPUT.toRequestBuilder().param("tx-name", txName).param("address", address).param("amount", amount), ExecutedTransactionResponse.class);
    }

    public CompletableFuture<FactomResponse<ExecutedTransactionResponse>> addOutput(String txName, String address, long amount) throws FactomException.ClientException {
        AddressType.FACTOID_PUBLIC.assertValid(address);
        return exchange(RpcMethod.ADD_OUTPUT.toRequestBuilder().param("tx-name", txName).param("address", address).param("amount", amount), ExecutedTransactionResponse.class);
    }

    public CompletableFuture<FactomResponse<AddressResponse>> address(String address) throws FactomException.ClientException {
        AddressType.assertValidAddress(address);
        return exchange(RpcMethod.ADDRESS.toRequestBuilder().param("address", address), AddressResponse.class);
    }

    public CompletableFuture<FactomResponse<AddressesResponse>> allAddresses() throws FactomException.ClientException {
        return exchange(RpcMethod.ALL_ADDRESSES.toRequestBuilder(), AddressesResponse.class);
    }

    public CompletableFuture<FactomResponse<ComposeResponse>> composeChain(Chain chain, String entryCreditAddress) throws FactomException.ClientException {
        AddressType.ENTRY_CREDIT_PUBLIC.assertValid(entryCreditAddress);
        Chain encodedChain = encodeOperations.encodeHex(chain);
        return exchange(RpcMethod.COMPOSE_CHAIN.toRequestBuilder().param("chain", encodedChain).param("ecpub", entryCreditAddress), ComposeResponse.class);
    }

    public CompletableFuture<FactomResponse<ComposeResponse>> composeEntry(Entry entry, String entryCreditAddress) throws FactomException.ClientException {
        AddressType.ENTRY_CREDIT_PUBLIC.assertValid(entryCreditAddress);
        Entry encodedEntry = encodeOperations.encodeHex(entry);
        return exchange(RpcMethod.COMPOSE_ENTRY.toRequestBuilder().param("entry", encodedEntry).param("ecpub", entryCreditAddress), ComposeResponse.class);
    }

    public CompletableFuture<FactomResponse<ComposeTransactionResponse>> composeTransaction(String txName) throws FactomException.ClientException {
        return exchange(RpcMethod.COMPOSE_TRANSACTION.toRequestBuilder().param("tx-name", txName), ComposeTransactionResponse.class);
    }

    public CompletableFuture<FactomResponse<DeleteTransactionResponse>> deleteTransaction(String txName) throws FactomException.ClientException {
        return exchange(RpcMethod.DELETE_TRANSACTION.toRequestBuilder().param("tx-name", txName), DeleteTransactionResponse.class);
    }

    public CompletableFuture<FactomResponse<AddressResponse>> generateEntryCreditAddress() throws FactomException.ClientException {
        return exchange(RpcMethod.GENERATE_ENTRY_CREDIT_ADDRESS.toRequestBuilder(), AddressResponse.class);
    }

    public CompletableFuture<FactomResponse<AddressResponse>> generateFactoidAddress() throws FactomException.ClientException {
        return exchange(RpcMethod.GENERATE_FACTOID_ADDRESS.toRequestBuilder(), AddressResponse.class);
    }

    public CompletableFuture<FactomResponse<GetHeightResponse>> getHeight() throws FactomException.ClientException {
        return exchange(RpcMethod.GET_HEIGHT.toRequestBuilder(), GetHeightResponse.class);
    }

    public CompletableFuture<FactomResponse<AddressesResponse>> importAddresses(List<Address> addresses) throws FactomException.ClientException {
        addresses.forEach(a -> AddressType.assertValidAddress(a.getValue()));
        for (Address address : addresses) {
            AddressType.assertValidAddress(address.getValue());
        }
        return exchange(RpcMethod.IMPORT_ADDRESSES.toRequestBuilder().param("addresses", addresses), AddressesResponse.class);
    }

    public CompletableFuture<FactomResponse<AddressResponse>> importKoinify(String words) throws FactomException.ClientException {
        return exchange(RpcMethod.IMPORT_KOINIFY.toRequestBuilder().param("words", words), AddressResponse.class);
    }

    public CompletableFuture<FactomResponse<TransactionResponse>> newTransaction(String txName) throws FactomException.ClientException {
        return exchange(RpcMethod.NEW_TRANSACTION.toRequestBuilder().param("tx-name", txName), TransactionResponse.class);
    }

    public CompletableFuture<FactomResponse<PropertiesResponse>> properties() throws FactomException.ClientException {
        return exchange(RpcMethod.PROPERTIES.toRequestBuilder(), PropertiesResponse.class);
    }

    public CompletableFuture<FactomResponse<ExecutedTransactionResponse>> signTransaction(String txName) throws FactomException.ClientException {
        return exchange(RpcMethod.SIGN_TRANSACTION.toRequestBuilder().param("tx-name", txName), ExecutedTransactionResponse.class);
    }

    public CompletableFuture<FactomResponse<ExecutedTransactionResponse>> subFee(String txName, String address) throws FactomException.ClientException {
        AddressType.FACTOID_PUBLIC.assertValid(address);
        return exchange(RpcMethod.SUB_FEE.toRequestBuilder().param("tx-name", txName).param("address", address), ExecutedTransactionResponse.class);
    }

    public CompletableFuture<FactomResponse<TransactionsResponse>> tmpTransactions() throws FactomException.ClientException {
        return exchange(RpcMethod.TMP_TRANSACTIONS.toRequestBuilder(), TransactionsResponse.class);
    }

    public CompletableFuture<FactomResponse<BlockHeightTransactionsResponse>> transactionsByRange(Range range) throws FactomException.ClientException {
        return exchange(RpcMethod.TRANSACTIONS.toRequestBuilder().param("range", range), BlockHeightTransactionsResponse.class);
    }

    public CompletableFuture<FactomResponse<TransactionsResponse>> transactionsByTransactionId(String txid) throws FactomException.ClientException {
        return exchange(RpcMethod.TRANSACTIONS.toRequestBuilder().param("txid", txid), TransactionsResponse.class);
    }

    public CompletableFuture<FactomResponse<BlockHeightTransactionsResponse>> transactionsByAddress(String address) throws FactomException.ClientException {
        AddressType.assertValidAddress(address);
        return exchange(RpcMethod.TRANSACTIONS.toRequestBuilder().param("address", address), BlockHeightTransactionsResponse.class);
    }

    public CompletableFuture<FactomResponse<WalletBackupResponse>> walletBackup() throws FactomException.ClientException {
        return exchange(RpcMethod.WALLET_BACKUP.toRequestBuilder(), WalletBackupResponse.class);
    }
}

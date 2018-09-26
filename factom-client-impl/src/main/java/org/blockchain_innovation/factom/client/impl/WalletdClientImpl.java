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

import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.WalletdClient;
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
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.rpc.RpcMethod;

import javax.inject.Named;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Named
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
public class WalletdClientImpl extends AbstractClient implements WalletdClient {

    @Override
    public CompletableFuture<FactomResponse<TransactionResponse>> addEntryCreditOutput(String txName, Address address, long amount) {
        AddressType.ENTRY_CREDIT_PUBLIC.assertValid(address);
        return exchange(RpcMethod.ADD_ENTRY_CREDIT_OUTPUT.toRequestBuilder().param("tx-name", txName).param("address", address.getValue()).param("amount", amount), TransactionResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<ExecutedTransactionResponse>> addFee(String txName, Address address) {
        AddressType.FACTOID_PUBLIC.assertValid(address);
        return exchange(RpcMethod.ADD_FEE.toRequestBuilder().param("tx-name", txName).param("address", address.getValue()), ExecutedTransactionResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<ExecutedTransactionResponse>> addInput(String txName, Address address, long amount) {
        AddressType.FACTOID_PUBLIC.assertValid(address);
        return exchange(RpcMethod.ADD_INPUT.toRequestBuilder().param("tx-name", txName).param("address", address.getValue()).param("amount", amount), ExecutedTransactionResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<ExecutedTransactionResponse>> addOutput(String txName, Address address, long amount) {
        AddressType.FACTOID_PUBLIC.assertValid(address);
        return exchange(RpcMethod.ADD_OUTPUT.toRequestBuilder().param("tx-name", txName).param("address", address.getValue()).param("amount", amount), ExecutedTransactionResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<AddressResponse>> address(Address address) {
        return exchange(RpcMethod.ADDRESS.toRequestBuilder().param("address", address.getValue()), AddressResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<AddressesResponse>> allAddresses() {
        return exchange(RpcMethod.ALL_ADDRESSES.toRequestBuilder(), AddressesResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<ComposeResponse>> composeChain(Chain chain, Address entryCreditAddress) {
        AddressType.ENTRY_CREDIT_PUBLIC.assertValid(entryCreditAddress);
        Chain encodedChain = encodeOperations.encodeHex(chain);
        return exchange(RpcMethod.COMPOSE_CHAIN.toRequestBuilder().param("chain", encodedChain).param("ecpub", entryCreditAddress.getValue()), ComposeResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<ComposeResponse>> composeEntry(Entry entry, Address entryCreditAddress) {
        AddressType.ENTRY_CREDIT_PUBLIC.assertValid(entryCreditAddress);
        Entry encodedEntry = encodeOperations.encodeHex(entry);
        return exchange(RpcMethod.COMPOSE_ENTRY.toRequestBuilder().param("entry", encodedEntry).param("ecpub", entryCreditAddress.getValue()), ComposeResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<ComposeTransactionResponse>> composeTransaction(String txName) {
        return exchange(RpcMethod.COMPOSE_TRANSACTION.toRequestBuilder().param("tx-name", txName), ComposeTransactionResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<DeleteTransactionResponse>> deleteTransaction(String txName) {
        return exchange(RpcMethod.DELETE_TRANSACTION.toRequestBuilder().param("tx-name", txName), DeleteTransactionResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<AddressResponse>> generateEntryCreditAddress() {
        return exchange(RpcMethod.GENERATE_ENTRY_CREDIT_ADDRESS.toRequestBuilder(), AddressResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<AddressResponse>> generateFactoidAddress() {
        return exchange(RpcMethod.GENERATE_FACTOID_ADDRESS.toRequestBuilder(), AddressResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<GetHeightResponse>> getHeight() {
        return exchange(RpcMethod.GET_HEIGHT.toRequestBuilder(), GetHeightResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<AddressesResponse>> importAddresses(List<Address> addresses) {
        for (Address address : addresses) {
            AddressType.assertVisibility(address.getValue(), AddressType.Visibility.PRIVATE);
        }
        return exchange(RpcMethod.IMPORT_ADDRESSES.toRequestBuilder().param("addresses", addresses), AddressesResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<AddressResponse>> importKoinify(String words) {
        return exchange(RpcMethod.IMPORT_KOINIFY.toRequestBuilder().param("words", words), AddressResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<TransactionResponse>> newTransaction(String txName) {
        return exchange(RpcMethod.NEW_TRANSACTION.toRequestBuilder().param("tx-name", txName), TransactionResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<PropertiesResponse>> properties() {
        return exchange(RpcMethod.PROPERTIES.toRequestBuilder(), PropertiesResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<ExecutedTransactionResponse>> signTransaction(String txName) {
        return exchange(RpcMethod.SIGN_TRANSACTION.toRequestBuilder().param("tx-name", txName), ExecutedTransactionResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<ExecutedTransactionResponse>> subFee(String txName, Address address) {
        AddressType.FACTOID_PUBLIC.assertValid(address);
        return exchange(RpcMethod.SUB_FEE.toRequestBuilder().param("tx-name", txName).param("address", address.getValue()), ExecutedTransactionResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<TransactionsResponse>> tmpTransactions() {
        return exchange(RpcMethod.TMP_TRANSACTIONS.toRequestBuilder(), TransactionsResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<BlockHeightTransactionsResponse>> transactionsByRange(Range range) {
        return exchange(RpcMethod.TRANSACTIONS.toRequestBuilder().param("range", range), BlockHeightTransactionsResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<TransactionsResponse>> transactionsByTransactionId(String txid) {
        return exchange(RpcMethod.TRANSACTIONS.toRequestBuilder().param("txid", txid), TransactionsResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<BlockHeightTransactionsResponse>> transactionsByAddress(Address address) {
        return exchange(RpcMethod.TRANSACTIONS.toRequestBuilder().param("address", address.getValue()), BlockHeightTransactionsResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<WalletBackupResponse>> walletBackup() {
        return exchange(RpcMethod.WALLET_BACKUP.toRequestBuilder(), WalletBackupResponse.class);
    }
}

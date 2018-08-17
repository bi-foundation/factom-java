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

import org.blockchain_innovation.factom.client.data.FactomException;
import org.blockchain_innovation.factom.client.data.model.Address;
import org.blockchain_innovation.factom.client.data.model.Chain;
import org.blockchain_innovation.factom.client.data.model.Entry;
import org.blockchain_innovation.factom.client.data.model.Range;
import org.blockchain_innovation.factom.client.data.model.response.walletd.AddressResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.AddressesResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.BlockHeightTransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.ComposeTransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.DeleteTransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.ExecutedTransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.GetHeightResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.PropertiesResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.TransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.TransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.WalletBackupResponse;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcMethod;

import java.util.List;

public class WalletdClient extends AbstractClient {

    public FactomResponse<TransactionResponse> addEntryCreditOutput(String txName, String address, int amount) throws FactomException.ClientException {
        return exchange(RpcMethod.ADD_ENTRY_CREDIT_OUTPUT.toRequestBuilder().param("tx-name", txName).param("address", address).param("amount", amount), TransactionResponse.class);
    }

    public FactomResponse<ExecutedTransactionResponse> addFee(String txName, String address) throws FactomException.ClientException {
        return exchange(RpcMethod.ADD_FEE.toRequestBuilder().param("tx-name", txName).param("address", address), ExecutedTransactionResponse.class);
    }

    public FactomResponse<ExecutedTransactionResponse> addInput(String txName, String address, int amount) throws FactomException.ClientException {
        return exchange(RpcMethod.ADD_INPUT.toRequestBuilder().param("tx-name", txName).param("address", address).param("amount", amount), ExecutedTransactionResponse.class);
    }

    public FactomResponse<ExecutedTransactionResponse> addOutput(String txName, String address, int amount) throws FactomException.ClientException {
        return exchange(RpcMethod.ADD_OUTPUT.toRequestBuilder().param("tx-name", txName).param("address", address).param("amount", amount), ExecutedTransactionResponse.class);
    }

    public FactomResponse<AddressResponse> address(String address) throws FactomException.ClientException {
        return exchange(RpcMethod.ADDRESS.toRequestBuilder().param("address", address), AddressResponse.class);
    }

    public FactomResponse<AddressesResponse> allAddresses() throws FactomException.ClientException {
        return exchange(RpcMethod.ALL_ADDRESSES.toRequestBuilder(), AddressesResponse.class);
    }

    public FactomResponse<ComposeResponse> composeChain(Chain chain, String entryCreditPublicKey) throws FactomException.ClientException {
        return exchange(RpcMethod.COMPOSE_CHAIN.toRequestBuilder().param("chain", chain).param("ecpub", entryCreditPublicKey), ComposeResponse.class);
    }

    public FactomResponse<ComposeResponse> composeEntry(Entry entry, String entryCreditPublicKey) throws FactomException.ClientException {
        return exchange(RpcMethod.COMPOSE_ENTRY.toRequestBuilder().param("entry", entry).param("ecpub", entryCreditPublicKey), ComposeResponse.class);
    }

    public FactomResponse<ComposeTransactionResponse> composeTransaction(String txName) throws FactomException.ClientException {
        return exchange(RpcMethod.COMPOSE_TRANSACTION.toRequestBuilder().param("tx-name", txName), ComposeTransactionResponse.class);
    }

    public FactomResponse<DeleteTransactionResponse> deleteTransaction(String txName) throws FactomException.ClientException {
        return exchange(RpcMethod.DELETE_TRANSACTION.toRequestBuilder().param("tx-name", txName), DeleteTransactionResponse.class);
    }

    public FactomResponse<AddressResponse> generateEntryCreditAddress() throws FactomException.ClientException {
        return exchange(RpcMethod.GENERATE_ENTRY_CREDIT_ADDRESS.toRequestBuilder(), AddressResponse.class);
    }

    public FactomResponse<AddressResponse> generateFactoidAddress() throws FactomException.ClientException {
        return exchange(RpcMethod.GENERATE_FACTOID_ADDRESS.toRequestBuilder(), AddressResponse.class);
    }

    public FactomResponse<GetHeightResponse> getHeight() throws FactomException.ClientException {
        return exchange(RpcMethod.GET_HEIGHT.toRequestBuilder(), GetHeightResponse.class);
    }

    public FactomResponse<AddressesResponse> importAddresses(List<Address> addresses) throws FactomException.ClientException {
        return exchange(RpcMethod.IMPORT_ADDRESSES.toRequestBuilder().param("addresses", addresses), AddressesResponse.class);
    }

    public FactomResponse<AddressResponse> importKoinify(String words) throws FactomException.ClientException {
        return exchange(RpcMethod.IMPORT_KOINIFY.toRequestBuilder().param("words", words), AddressResponse.class);
    }

    public FactomResponse<TransactionResponse> newTransaction(String txName) throws FactomException.ClientException {
        return exchange(RpcMethod.NEW_TRANSACTION.toRequestBuilder().param("tx-name", txName), TransactionResponse.class);
    }

    public FactomResponse<PropertiesResponse> properties() throws FactomException.ClientException {
        return exchange(RpcMethod.PROPERTIES.toRequestBuilder(), PropertiesResponse.class);
    }

    public FactomResponse<ExecutedTransactionResponse> signTransaction(String txName) throws FactomException.ClientException {
        return exchange(RpcMethod.SIGN_TRANSACTION.toRequestBuilder().param("tx-name", txName), ExecutedTransactionResponse.class);
    }

    public FactomResponse<ExecutedTransactionResponse> subFee(String txName, String address) throws FactomException.ClientException {
        return exchange(RpcMethod.SUB_FEE.toRequestBuilder().param("tx-name", txName).param("address", address), ExecutedTransactionResponse.class);
    }

    public FactomResponse<TransactionsResponse> tmpTransactions() throws FactomException.ClientException {
        return exchange(RpcMethod.TMP_TRANSACTIONS.toRequestBuilder(), TransactionsResponse.class);
    }

    public FactomResponse<BlockHeightTransactionsResponse> transactionsByRange(Range range) throws FactomException.ClientException {
        return exchange(RpcMethod.TRANSACTIONS.toRequestBuilder().param("range", range), BlockHeightTransactionsResponse.class);
    }

    public FactomResponse<TransactionsResponse> transactionsByTransaction(String transactionId) throws FactomException.ClientException {
        return exchange(RpcMethod.TRANSACTIONS.toRequestBuilder().param("txid", transactionId), TransactionsResponse.class);
    }

    public FactomResponse<BlockHeightTransactionsResponse> transactionsByAddress(String address) throws FactomException.ClientException {
        return exchange(RpcMethod.TRANSACTIONS.toRequestBuilder().param("address", address), BlockHeightTransactionsResponse.class);
    }

    public FactomResponse<WalletBackupResponse> walletBackup() throws FactomException.ClientException {
        return exchange(RpcMethod.WALLET_BACKUP.toRequestBuilder(), WalletBackupResponse.class);
    }
}

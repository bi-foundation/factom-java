package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.Range;
import org.blockchain_innovation.factom.client.api.model.response.walletd.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WalletdClient {
    /**
     * When adding entry credit outputs, the amount given is in factoshis, not entry credits. This means math is required to determine the correct amount of factoshis to pay to get X EC.
     * (ECRate * ECTotalOutput)
     * In our case, the rate is 1000, meaning 1000 entry credits per factoid. We added 10 entry credits, so we need 1,000 * 10 = 10,000 factoshis
     * To get the ECRate search in the search bar above for “entry-credit-rate”
     *
     * @param txName
     * @param address
     * @param amount
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<TransactionResponse>> addEntryCreditOutput(String txName, String address, long amount) throws FactomException.ClientException;

    /**
     * Addfee is a shortcut and safeguard for adding the required additional factoshis to covert the fee. The fee is displayed in the returned transaction after each step, but addfee should be used instead of manually adding the additional input. This will help to prevent overpaying.
     * Addfee will complain if your inputs and outputs do not match up. For example, in the steps above we added the inputs first. This was done intentionally to show a case of overpaying. Obviously, no one wants to overpay for a transaction, so addfee has returned an error and the message: ‘Inputs and outputs don’t add up’. This is because we have 2,000,000,000 factoshis as input and only 1,000,000,000 + 10,000 as output. Let’s correct the input by doing 'add-input’, and putting 1000010000 as the amount for the address. It will overwrite the previous input.
     * Curl to do that:
     * <pre>curl -X POST --data-binary '{"jsonrpc":"2.0","id":0,"method":"add-input","params": {"tx-name":"TX_NAME","address":"FA2jK2HcLnRdS94dEcU27rF3meoJfpUcZPSinpb7AwQvPRY6RL1Q","amount":1000010000}}' \ -H 'content-type:text/plain;' http://localhost:8089/v2</pre>
     * Run the addfee again, and the feepaid and feerequired will match up
     *
     * @param txName
     * @param address
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<ExecutedTransactionResponse>> addFee(String txName, String address) throws FactomException.ClientException;

    /**
     * Adds an input to the transaction from the given address. The public address is given, and the wallet must have the private key associated with the address to successfully sign the transaction.
     * The input is measured in factoshis, so to send ten factoids, you must input 1,000,000,000 factoshis (without commas in JSON)
     *
     * @param txName
     * @param address
     * @param amount
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<ExecutedTransactionResponse>> addInput(String txName, String address, long amount) throws FactomException.ClientException;

    /**
     * Adds a factoid address output to the transaction. Keep in mind the output is done in factoshis. 1 factoid is 1,000,000,000 factoshis.
     * So to send ten factoids, you must send 1,000,000,000 factoshis (no commas in JSON).
     *
     * @param txName
     * @param address
     * @param amount
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<ExecutedTransactionResponse>> addOutput(String txName, String address, long amount) throws FactomException.ClientException;

    /**
     * Retrieve the public and private parts of a Factoid or Entry Credit address stored in the wallet.
     *
     * @param address
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<AddressResponse>> address(String address) throws FactomException.ClientException;

    /**
     * Retrieve all of the Factoid and Entry Credit addresses stored in the wallet.
     *
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<AddressesResponse>> allAddresses() throws FactomException.ClientException;

    /**
     * This method, compose-chain, will return the appropriate API calls to create a chain in factom. You must first call the commit-chain, then the reveal-chain API calls. To be safe, wait a few seconds after calling commit.
     * Notes:
     * Ensure that all data given in the firstentry fields are encoded in hex. This includes the content section.
     *
     * @param chain
     * @param entryCreditAddress
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<ComposeResponse>> composeChain(Chain chain, String entryCreditAddress) throws FactomException.ClientException;

    /**
     * This method, compose-entry, will return the appropriate API calls to create an entry in factom. You must first call the commit-entry, then the reveal-entry API calls. To be safe, wait a few seconds after calling commit.
     * Notes:
     * Ensure all data given in the entry fields are encoded in hex. This includes the content section.
     *
     * @param entry
     * @param entryCreditAddress
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<ComposeResponse>> composeEntry(Entry entry, String entryCreditAddress) throws FactomException.ClientException;

    /**
     * Compose transaction marshals the transaction into a hex encoded string. The string can be inputted into the factomd API factoid-submit to be sent to the network.
     *
     * @param txName
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<ComposeTransactionResponse>> composeTransaction(String txName) throws FactomException.ClientException;

    /**
     * Deletes a working transaction in the wallet. The full transaction will be returned, and then deleted
     *
     * @param txName
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<DeleteTransactionResponse>> deleteTransaction(String txName) throws FactomException.ClientException;

    /**
     * Create a new Entry Credit Address and store it in the wallet.
     *
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<AddressResponse>> generateEntryCreditAddress() throws FactomException.ClientException;

    /**
     * Create a new Factoid Address and store it in the wallet.
     *
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<AddressResponse>> generateFactoidAddress() throws FactomException.ClientException;

    /**
     * Get the current hight of blocks that have been cached by the wallet while syncing.
     *
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<GetHeightResponse>> getHeight() throws FactomException.ClientException;

    /**
     * Import Factoid and/or Entry Credit address secret keys into the wallet.
     *
     * @param addresses
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<AddressesResponse>> importAddresses(List<Address> addresses) throws FactomException.ClientException;

    /**
     * Import a Koinify crowd sale address into the wallet. In our examples we used the word “yellow” twelve times, note that in your case the master passphrase will be different.
     *
     * @param words
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<AddressResponse>> importKoinify(String words) throws FactomException.ClientException;

    /**
     * This will create a new transaction. The txid is in flux until the final transaction is signed. Until then, it should not be used or recorded.
     * When dealing with transactions all factoids are represented in factoshis. 1 factoid is 1e8 factoshis, meaning you can never send anything less than 0 to a transaction (0.5).
     *
     * @param txName
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<TransactionResponse>> newTransaction(String txName) throws FactomException.ClientException;

    /**
     * Retrieve current properties of factom-walletd, including the wallet and wallet API versions.
     *
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<PropertiesResponse>> properties() throws FactomException.ClientException;

    /**
     * Signs the transaction. It is now ready to be executed.
     *
     * @param txName
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<ExecutedTransactionResponse>> signTransaction(String txName) throws FactomException.ClientException;

    /**
     * When paying from a transaction, you can also make the receiving transaction pay for it. Using sub fee, you can use the receiving address in the parameters, and the fee will be deducted from their output amount.
     * This allows a wallet to send all it’s factoids, by making the input and output the remaining balance, then using sub fee on the output address.
     *
     * @param txName
     * @param address
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<ExecutedTransactionResponse>> subFee(String txName, String address) throws FactomException.ClientException;

    /**
     * Lists all the current working transactions in the wallet. These are transactions that are not yet sent.
     *
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<TransactionsResponse>> tmpTransactions() throws FactomException.ClientException;

    /**
     * This will retrieve all transactions within a given block height range.
     *
     * @param range
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<BlockHeightTransactionsResponse>> transactionsByRange(Range range) throws FactomException.ClientException;

    /**
     * This will retrieve a transaction by the given TxID. This call is the fastest way to retrieve a transaction, but it will not display the height of the transaction. If a height is in the response, it will be 0. To retrieve the height of a transaction, use the 'By Address’ method
     * This call in the backend actually pushes the request to factomd. For a more informative response, it is advised to use the factomd transaction method
     *
     * @param txid
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<TransactionsResponse>> transactionsByTransactionId(String txid) throws FactomException.ClientException;

    /**
     * Retrieves all transactions that involve a particular address.
     *
     * @param address
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<BlockHeightTransactionsResponse>> transactionsByAddress(String address) throws FactomException.ClientException;

    /**
     * Return the wallet seed and all addresses in the wallet for backup and offline storage.
     *
     * @return
     * @throws FactomException.ClientException
     */
    CompletableFuture<FactomResponse<WalletBackupResponse>> walletBackup() throws FactomException.ClientException;
}

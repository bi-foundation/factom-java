package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.response.factomd.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * This is the factomd client that allows you to access all Rpc Methods of factomd.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface FactomdClient {
    /**
     * Retrieve the lowlevel client (this object). This allows you to directly interact with request/response exchanges and set settings, urls etc.
     *
     * @return The lowlevel client.
     */
    LowLevelClient lowLevelClient();

    /**
     * Retrieve administrative blocks for any given height.
     * <p>
     * The admin block contains data related to the identities within the factom system and the decisions the system makes as it builds the block chain.
     * The ‘abentries’ (admin block entries) in the JSON response can be of various types, the most common is a directory block signature (DBSig).
     * A majority of the federated servers sign every directory block, meaning every block after m5 will contain 5 DBSigs in each admin block.
     * <p>
     *
     * @param height The height at which you want to retrieve the adminblock by.
     * @return The adminblock response promise.
     */
    CompletableFuture<FactomResponse<AdminBlockResponse>> adminBlockByHeight(long height);


    /**
     * Retrieve a specified admin block given its merkle root key.
     * <p>
     * The admin block contains data related to the identities within the factom system and the decisions the system makes as it builds the block chain.
     * The ‘abentries’ (admin block entries) in the JSON response can be of various types, the most common is a directory block signature (DBSig).
     * A majority of the federated servers sign every directory block, meaning every block after m5 will contain 5 DBSigs in each admin block.
     * <p>
     *
     * @param keyMR the Merkle Root Key.
     * @return The adminblock response promise.
     */
    CompletableFuture<FactomResponse<AdminBlockResponse>> adminBlockByKeyMerkleRoot(String keyMR);

    /**
     * This api call is used to find the status of a transaction, whether it be a factoid, reveal entry, or commit entry. When using this, you must specify the type of the
     * transaction by giving the chainid field 1 of 3 values:
     * <p>
     * - f for factoid transactions
     * - c for entry credit transactions (commit entry/chain)
     * - ################################################################ for reveal entry/chain
     * Where # is the ChainID of the entry
     * </p>
     * <p>
     * The status types returned are as follows:
     * "Unknown" : Not found anywhere
     * "NotConfirmed" : Found on local node, but not in network (Holding Map)
     * "TransactionACK" : Found in network, but not written to the blockchain yet (ProcessList)
     * "DBlockConfirmed" : Found in Blockchain
     * You may also provide the full marshaled transaction, instead of a hash, and it will be hashed for you.
     * </p>
     * The responses vary based on the type
     * <p>The hash field for a factoid transaction is equivalent to txid. To indicate the hash is a factoid transaction, put f in the chainid field and the txid in the hash field.
     * The response will look different than entry related ack calls.
     * Extra notes:
     * Why f? It is short for 000000000000000000000000000000000000000000000000000000000000000f, which is the chainid for all factoid blocks. All factoid transactions are placed in
     * the factoid (assuming they are valid)</p>
     * <p>
     * Requesting an entry requires you to specify if the hash you provide is a commit or an entry hash. The chainid field is used to specify this. If you are searching for a
     * commit, put c as the chainid field, otherwise, put the chainid that the entry belongs too.
     * For commit/reveal acks, the response has 2 sections, one for the commit, one for the reveal. If you provide the entryhash and chainid, both will be filled (if found). If you
     * only provide the commit txid and c as the chainid, then only the commitdata is guaranteed to come back with data. The committxid and entryhash fields correspond to the
     * commitdata and entrydata objects.
     * Extra notes:
     * Why c? It is short for 000000000000000000000000000000000000000000000000000000000000000c, which is the chainid for all entry credit blocks. All commits are placed in the
     * entry credit block (assuming they are valid and are properly paid for)
     * </p>
     *
     * @param hash           txid for factoid trans, entryhash in other cases.
     * @param chainId        f for factoid trans, c for entry credit trans, chain Id for reveal entry/chain.
     * @param rpcResultClass The result class depending on the transactiontype.
     * @param <T>            The result class type.
     * @return The Transaction response promise.
     */
    <T> CompletableFuture<FactomResponse<T>> ackTransactions(String hash, String chainId, Class<T> rpcResultClass);

    /**
     * The hash field for a factoid transaction is equivalent to txid.
     * The response will look different than entry related ack calls.
     * <p>
     * The status types returned are as follows:
     * "Unknown" : Not found anywhere
     * "NotConfirmed" : Found on local node, but not in network (Holding Map)
     * "TransactionACK" : Found in network, but not written to the blockchain yet (ProcessList)
     * "DBlockConfirmed" : Found in Blockchain
     * You may also provide the full marshaled transaction, instead of a hash, and it will be hashed for you.
     * </p>
     * The responses vary based on the type
     *
     * @param txId txid for factoid trans.
     * @return The Transaction response promise.
     */
    CompletableFuture<FactomResponse<FactoidTransactionsResponse>> ackFactoidTransactions(String txId);

    /**
     * Requesting an entry requires you to specify if the hash you provide is a commit or an entry hash. The chainid field is used to specify this. If you are searching for a
     * commit, put c as the chainid field, otherwise, put the chainid that the entry belongs too.
     * For commit/reveal acks, the response has 2 sections, one for the commit, one for the reveal. If you provide the entryhash and chainid, both will be filled (if found). If you
     * only provide the commit txid and c as the chainid, then only the commitdata is guaranteed to come back with data. The committxid and entryhash fields correspond to the
     * commitdata and entrydata objects.
     * Extra notes:
     * Why c? It is short for 000000000000000000000000000000000000000000000000000000000000000c, which is the chainid for all entry credit blocks. All commits are placed in the
     * entry credit block (assuming they are valid and are properly paid for)
     *
     * @param hash The hash (see remarks above).
     * @return The Factoid Transaction response promise.
     */
    CompletableFuture<FactomResponse<EntryTransactionResponse>> ackEntryTransactions(String hash);

    /**
     * Return the keymr of the head of the chain for a chain ID (the unique hash created when the chain was created).
     *
     * @param chainId The chain Id.
     * @return The chain head response promise.
     */
    CompletableFuture<FactomResponse<ChainHeadResponse>> chainHead(String chainId);
    CompletableFuture<FactomResponse<ChainHeadResponse>> chainHead(String chainId, boolean throwErrorOnChainNotFound);
    CompletableFuture<FactomResponse<QueryChainResponse>> accumulateAllEntries(String chainId);


    /**
     * Send a Chain Commit Message to factomd to create a new Chain. The commit chain hex encoded string is documented here: <a
     * href="https://github.com/FactomProject/FactomDocs/blob/master/factomDataStructureDetails.md#chain-commit">Github Documentation</a>
     * The commit-chain API takes a specifically formated message encoded in hex that includes signatures. If you have a factom-walletd instance running, you can construct this
     * commit-chain API call with compose-chain which takes easier to construct arguments.
     * The compose-chain api call has two api calls in it’s response: commit-chain and reveal-chain. To successfully create a chain, the reveal-chain must be called after the
     * commit-chain.
     * <p>
     * Notes:
     * It is possible to be unable to send a commit, if the commit already exists (if you try to send it twice). This is a mechanism to prevent you from double spending. If you
     * encounter this error, just skip to the reveal-chain. The error format can be found here: repeated-commit
     * </p>
     *
     * @param message The Chain Commit Message.
     * @return The Commit Chain response promise.
     */
    CompletableFuture<FactomResponse<CommitChainResponse>> commitChain(String message);

    /**
     * Send an Entry Commit Message to factom to create a new Entry. The entry commit hex encoded string is documented here: <a
     * href="https://github.com/FactomProject/FactomDocs/blob/master/factomDataStructureDetails.md#entry-commit">Github Documentation</a>
     * The commit-entry API takes a specifically formated message encoded in hex that includes signatures. If you have a factom-walletd instance running, you can construct this
     * commit-entry API call with compose-entry which takes easier to construct arguments.
     * The compose-entry api call has two api calls in it’s response: commit-entry and reveal-entry. To successfully create an entry, the reveal-entry must be called after the
     * commit-entry.
     * <p>
     * Notes:
     * It is possible to be unable to send a commit, if the commit already exists (if you try to send it twice). This is a mechanism to prevent you from double spending. If you
     * encounter this error, just skip to the reveal-entry. The error format can be found here: repeated-commit.
     * </p>
     *
     * @param message The entry commit message.
     * @return The Commit Entry response promise.
     */
    CompletableFuture<FactomResponse<CommitEntryResponse>> commitEntry(String message);


    /**
     * The current-minute API call returns:
     * - leaderheight returns the current block height.
     * - directoryblockheight returns the last saved height.
     * - minute returns the current minute number for the open entry block.
     * - currentblockstarttime returns the start time for the current block.
     * - currentminutestarttime returns the start time for the current minute.
     * - currenttime returns the current nodes understanding of current time.
     * - directoryblockinseconds returns the number of seconds per block.
     * - stalldetected returns if factomd thinks it has stalled.
     * - faulttimeout returns the number of seconds before leader node is faulted for failing to provide a necessary message.
     * - roundtimeout returns the number of seconds between rounds of an election during a fault.
     *
     * @return The current minute response promise.
     */
    CompletableFuture<FactomResponse<CurrentMinuteResponse>> currentMinute();

    /**
     * Retrieve a directory block given only its height.
     * The header of the directory block will contain information regarding the previous directory block’s keyMR, directory block height, and the timestamp.
     *
     * @param height The height of the blockchain at which the the directory block should be retrieved.
     * @return The directory block height response.
     */
    CompletableFuture<FactomResponse<DirectoryBlockHeightResponse>> directoryBlockByHeight(long height);

    /**
     * Every directory block has a KeyMR (Key Merkle Root), which can be used to retrieve it. The response will contain information that can be used to navigate through all
     * transactions (entry and factoid) within that block.
     * The header of the directory block will contain information regarding the previous directory block’s keyMR, directory block height, and the timestamp.
     *
     * @param keyMR Key Merkle Root.
     * @return The directory block promise.
     */
    CompletableFuture<FactomResponse<DirectoryBlockResponse>> directoryBlockByKeyMerkleRoot(String keyMR);

    /**
     * The directory block head is the last known directory block by factom, or in other words, the most recently recorded block. This can be used to grab the latest block and the
     * information required to traverse the entire blockchain.
     *
     * @return The directory block head promise.
     */
    CompletableFuture<FactomResponse<DirectoryBlockHeadResponse>> directoryBlockHead();

    /**
     * Retrieve the entry credit block for any given height. These blocks contain entry credit transaction information.
     *
     * @param height The blockchain height to retrieve the entry credit block for
     * @return The entry credit block response promise.
     */
    CompletableFuture<FactomResponse<EntryCreditBlockResponse>> entryCreditBlockByHeight(int height);

    /**
     * Get an Entry from factomd specified by the Entry Hash.
     *
     * @param entryHash The entry Hash.
     * @return The Entry response promise.
     */
    CompletableFuture<FactomResponse<EntryResponse>> entry(String entryHash);

    /**
     * Retrieve a specified entry block given its merkle root key. The entry block contains 0 to many entries.
     *
     * @param keyMR Key Merkle Root.
     * @return The Entry Block promise.
     */
    CompletableFuture<FactomResponse<EntryBlockResponse>> entryBlockByKeyMerkleRoot(String keyMR);

    /**
     * Return its current balance for a specific entry credit address.
     *
     * @param entryCreditAddress The entry credit address.
     * @return The Entry Credit balance promise.
     */
    CompletableFuture<FactomResponse<EntryCreditBalanceResponse>> entryCreditBalance(Address entryCreditAddress);

    /**
     * Retrieve a specified entrycredit block given its merkle root key. The numbers are minute markers.
     *
     * @param keymr Key merkle root.
     * @return The Entry Credit Block promise.
     */
    CompletableFuture<FactomResponse<EntryCreditBlockResponse>> entryCreditBlock(String keymr);

    /**
     * Returns the number of Factoshis (Factoids *10^-8) that purchase a single Entry Credit. The minimum factoid fees are also determined by this rate, along with how complex the
     * factoid transaction is.
     *
     * @return The Entry Credit Rate promise.
     */
    CompletableFuture<FactomResponse<EntryCreditRateResponse>> entryCreditRate();

    /**
     * This call returns the number of Factoshis (Factoids *10^-8) that are currently available at the address specified.
     *
     * @param factoidAddress The factoidAddress.
     * @return The Factoid Balance promise.
     */
    CompletableFuture<FactomResponse<FactoidBalanceResponse>> factoidBalance(Address factoidAddress);

    /**
     * Retrieve a specified factoid block given its merkle root key.
     *
     * @param keymr key merkle root.
     * @return The Factod Block promise.
     */
    CompletableFuture<FactomResponse<FactoidBlockResponse>> factoidBlock(String keymr);

    /**
     * Submit a factoid transaction. The transaction hex encoded string is documented here: Github Documentation
     * The factoid-submit API takes a specifically formatted message encoded in hex that includes signatures.
     * If you have a factom-walletd instance running, you can construct this factoid-submit API call with compose-transaction which takes easier to construct arguments.
     *
     * @param factoidTransaction The factoid transaction.
     * @return The Factoid Submit promise.
     */
    CompletableFuture<FactomResponse<FactoidSubmitResponse>> factoidSubmit(String factoidTransaction);

    /**
     * Retrieve the factoid block for any given height. These blocks contain factoid transaction information.
     *
     * @param height The height to retrieve the factoid block at
     * @return
     */
    CompletableFuture<FactomResponse<FactoidBlockResponse>> factoidBlockByHeight(int height);

    /**
     * Returns various heights that allows you to view the state of the blockchain. The heights returned provide a lot of information regarding the state of factomd, but not all
     * are needed by most applications. The heights also indicate the most recent block, which could not be complete, and still being built. The heights mean as follows:
     * <p>
     * directoryblockheight : The current directory block height of the local factomd node.
     * leaderheight : The current block being worked on by the leaders in the network. This block is not yet complete, but all transactions submitted will go into this block
     * (depending on network conditions, the transaction may be delayed into the next block)
     * entryblockheight : The height at which the factomd node has all the entry blocks. Directory blocks are obtained first, entry blocks could be lagging behind the directory
     * block when syncing.
     * entryheight : The height at which the local factomd node has all the entries. If you added entries at a block height above this, they will not be able to be retrieved by the
     * local factomd until it syncs further.
     * A fully synced node should show the same number for all, (except between minute 0 and 1, when leaderheight will be 1 block ahead.)
     * </p>
     *
     * @return The heights.
     */
    CompletableFuture<FactomResponse<HeightsResponse>> heights();

    /**
     * Returns an array of the entries that have been submitted but have not been recorded into the blockchain.
     *
     * @param height The height to retrieve the pending entries at.
     * @return The pending entries.
     */
    CompletableFuture<FactomResponse<PendingEntriesResponse>> pendingEntries(int height);

    /**
     * Returns an array of factoid transactions that have not yet been recorded in the blockchain, but are known to the system.
     *
     * @param height The height to retrieve the pending entries at.
     * @return The pending transactions.
     */
    CompletableFuture<FactomResponse<PendingTransactionsResponse>> pendingTransactions(int height);

    /**
     * Retrieve current properties of the Factom system, including the software and the API versions.
     *
     * @return The properties of factomd.
     */
    CompletableFuture<FactomResponse<PropertiesResponse>> properties();

    /**
     * Retrieve an entry or transaction in raw format, the data is a hex encoded string.
     *
     * @param hash The hash.
     * @return Raw Entry or transaction data.
     */
    CompletableFuture<FactomResponse<RawDataResponse>> rawData(String hash);

    /**
     * Retrieve a receipt providing cryptographically verifiable proof that information was recorded in the factom blockchain and that this was subsequently anchored in the bitcoin
     * blockchain.
     *
     * @param hash The hash.
     * @return A receipt (verifiable proof) of receipt and anchoring.
     */
    CompletableFuture<FactomResponse<ReceiptResponse>> receipt(String hash);

    /**
     * Reveal the First Entry in a Chain to factomd after the Commit to complete the Chain creation. The reveal-chain hex encoded string is documented here: Github Documentation
     * The reveal-chain API takes a specifically formatted message encoded in hex that includes signatures. If you have a factom-walletd instance running, you can construct this
     * reveal-chain API call with compose-chain which takes easier to construct arguments.
     * The compose-chain api call has two api calls in its response: commit-chain and reveal-chain. To successfully create a chain, the reveal-chain must be called after the
     * commit-chain.
     *
     * @param entry The first entry of the chain used to reveal the chain.
     * @return Reveal promise.
     */
    CompletableFuture<FactomResponse<RevealResponse>> revealChain(String entry);

    /**
     * Reveal an Entry to factomd after the Commit to complete the Entry creation. The reveal-entry hex encoded string is documented here: Github Documentation
     * The reveal-entry API takes a specifically formatted message encoded in hex that includes signatures. If you have a factom-walletd instance running, you can construct this
     * reveal-entry API call with compose-entry which takes easier to construct arguments.
     * The compose-entry api call has two api calls in it’s response: commit-entry and reveal-entry. To successfully create an entry, the reveal-entry must be called after the
     * commit-entry.
     *
     * @param entry The entry to reveal.
     * @return Reveal promise.
     */
    CompletableFuture<FactomResponse<RevealResponse>> revealEntry(String entry);

    /**
     * Send a raw hex encoded binary message to the Factom network. This is mostly just for debugging and testing.
     * <p>
     * To Check Commit Chain Example
     * Example Commit Chain Request
     * <p>
     * {
     * "jsonrpc": "2.0",
     * "id": 0,
     * "method": "send-raw-message",
     * "params": {
     * "message": "00015fc6dbeaccfab82063af4a2890f89c243a9a3db2cce041e9352a1df32731d302917c38b229985e890c7d0d4c76e84a283011ba165ccee3524dd91fb417c2550c6d1c42d3bd23af5f7c05a89c0097eed7378c60b8bcc89a284094a81da85fb8faab7b2972470cb64dfb9c542844a0724222d53b86c85baa6fe49cc01fb5e8d26e08ce4690b0e3933bf1f6c5c15b28a33eb504f87c07f7bb51691b90cb3326d62b4b97802db3c6dccc9b0108f2c06cac0b7968e9f1f6aabb126f9aa58bc8eae21f2383729cb703"
     * }
     * }
     * Example Commit Chain Response
     * <p>
     * {
     * "jsonrpc": "2.0",
     * "id": 0,
     * "result": {
     * "message": "Successfully sent the message"
     * }
     * }
     * Entry Hash : 23af5f7c05a89c0097eed7378c60b8bcc89a284094a81da85fb8faab7b297247
     *
     * @param message The raw message.
     * @return Raw message response.
     */
    CompletableFuture<FactomResponse<SendRawMessageResponse>> sendRawMessage(String message);

    /**
     * Retrieve details of a factoid transaction using a transaction’s hash (or corresponding transaction id).
     * Note that information regarding the
     * directory block height,
     * directory block keymr, and
     * transaction block keymr
     * are also included.
     * <p>
     * The "blockheight" parameter in the response will always be 0 when using this call, refer to "includedindirectoryblockheight" if you need the height.
     * </p>
     * <p>
     * Note: This call will also accept an entry hash as input, in which case the returned data concerns the entry. The returned fields and their format are shown in the 2nd
     * Example Response at right.
     * Note: If the input hash is non-existent, the returned fields will be as follows:
     * </p>
     * "includedintransactionblock":""
     * "includedindirectoryblock":""
     * "includedindirectoryblockheight":-1
     *
     * @param hash The hash or id of a transaction.
     * @return The transaction promise.
     */
    CompletableFuture<FactomResponse<TransactionResponse>> transaction(String hash);

}

package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.data.FactomException;
import org.blockchain_innovation.factom.client.data.model.Chain;
import org.blockchain_innovation.factom.client.data.model.Entry;
import org.blockchain_innovation.factom.client.data.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.settings.RpcSettings;
import org.blockchain_innovation.factom.client.settings.RpcSettingsImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EntryClientApi extends AbstractClient {

    private static final int ENTRY_REVEAL_WAIT = 2000;
    private WalletdClient walletdClient;
    private FactomdClient factomdClient;

    public WalletdClient getWalletdClient() throws FactomException.ClientException {
        if (walletdClient == null) {
            walletdClient = new WalletdClient();
            walletdClient.setSettings(getSettings());
        }
        return walletdClient;
    }

    public FactomdClient getFactomdClient() throws FactomException.ClientException {
        if (factomdClient == null) {
            factomdClient = new FactomdClient();
            factomdClient.setSettings(getSettings());
        }
        return factomdClient;
    }

    public void requestChain(Chain chain, String entryCreditAddress) throws FactomException.ClientException {
        FactomResponse<ComposeResponse> composeResponse = getWalletdClient().composeChain(chain, entryCreditAddress);
        ComposeResponse composeChain = composeResponse.getResult();

        String commitChainMessage = composeChain.getCommit().getParams().getMessage();
        String revealChainEntry = composeChain.getReveal().getParams().getEntry();

        FactomResponse<CommitChainResponse> commitChainResponse = getFactomdClient().commitChain(commitChainMessage);
        FactomResponse<RevealResponse> revealChainResponse = getFactomdClient().revealChain(revealChainEntry);
    }

    public void requestEntry(Entry entry, String entryCreditAddress) throws FactomException.ClientException {
        FactomResponse<ComposeResponse> composeResponse = getWalletdClient().composeEntry(entry, entryCreditAddress);

        ComposeResponse composeEntry = composeResponse.getResult();
        String commitEntryMessage = composeEntry.getCommit().getParams().getMessage();
        String revealCommitMessage = composeEntry.getReveal().getParams().getEntry();

        FactomResponse<CommitEntryResponse> commitEntryResponse = getFactomdClient().commitEntry(commitEntryMessage);
        FactomResponse<RevealResponse> revealResponse = getFactomdClient().revealChain(revealCommitMessage);
    }

    private void wa11it(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
        }
    }

    private boolean waitOnConfirmation(EntryTransactionResponse.Status desiredStatus, String chainId, String entryHash, int maxSeconds) throws InterruptedException, FactomException.ClientException {
        int seconds = 0;
        while (seconds < maxSeconds) {
            System.out.println("At verification second: " + seconds);
            FactomResponse<EntryTransactionResponse> transactionsResponse = getFactomdClient().ackTransactions(entryHash, chainId, EntryTransactionResponse.class);
            EntryTransactionResponse entryTransaction = transactionsResponse.getResult();
            System.out.println("---");
            EntryTransactionResponse.Status status = entryTransaction.getCommitData().getStatus();
            if (seconds > 12 && seconds % 6 == 0 && EntryTransactionResponse.Status.TransactionACK != status) {
                System.err.println("Transaction still not in desired status after: " + seconds + "State: " + status + ". Probably will not succeed!");
            } else if (desiredStatus == status) {
                return true;
            }
            seconds++;
            Thread.sleep(1000);
        }
        return false;
    }
}

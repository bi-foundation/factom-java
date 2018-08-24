package org.blockchain_innovation.factom.client.api.model.response;

import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;

public class CommitChain {
    CommitChainResponse commitChainResponse;
    RevealResponse revealResponse;

    public CommitChainResponse getCommitChainResponse() {
        return commitChainResponse;
    }

    public CommitChain setCommitChainResponse(CommitChainResponse commitChainResponse) {
        this.commitChainResponse = commitChainResponse;
        return this;
    }

    public RevealResponse getRevealResponse() {
        return revealResponse;
    }

    public CommitChain setRevealResponse(RevealResponse revealResponse) {
        this.revealResponse = revealResponse;
        return this;
    }
}

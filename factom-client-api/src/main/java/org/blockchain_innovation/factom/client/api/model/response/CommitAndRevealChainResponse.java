package org.blockchain_innovation.factom.client.api.model.response;

import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;

public class CommitAndRevealChainResponse {
    private CommitChainResponse commitChainResponse;
    private RevealResponse revealResponse;

    public CommitChainResponse getCommitChainResponse() {
        return commitChainResponse;
    }

    public CommitAndRevealChainResponse setCommitChainResponse(CommitChainResponse commitChainResponse) {
        this.commitChainResponse = commitChainResponse;
        return this;
    }

    public RevealResponse getRevealResponse() {
        return revealResponse;
    }

    public CommitAndRevealChainResponse setRevealResponse(RevealResponse revealResponse) {
        this.revealResponse = revealResponse;
        return this;
    }

    @Override
    public String toString() {
        return "CommitAndRevealChainResponse{" +
                "commitChainResponse=" + commitChainResponse +
                ", revealResponse=" + revealResponse +
                '}';
    }
}

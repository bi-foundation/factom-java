package org.blockchain_innovation.factom.client.api.model.response;

import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;

import java.io.Serializable;

public class CommitAndRevealEntryResponse implements Serializable {
    private CommitEntryResponse commitEntryResponse;
    private RevealResponse revealResponse;

    public CommitEntryResponse getCommitEntryResponse() {
        return commitEntryResponse;
    }

    public CommitAndRevealEntryResponse setCommitEntryResponse(CommitEntryResponse commitEntryResponse) {
        this.commitEntryResponse = commitEntryResponse;
        return this;
    }

    public RevealResponse getRevealResponse() {
        return revealResponse;
    }

    public CommitAndRevealEntryResponse setRevealResponse(RevealResponse revealResponse) {
        this.revealResponse = revealResponse;
        return this;
    }
}

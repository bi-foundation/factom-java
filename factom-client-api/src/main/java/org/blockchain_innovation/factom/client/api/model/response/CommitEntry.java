package org.blockchain_innovation.factom.client.api.model.response;

import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;

public class CommitEntry {
    CommitEntryResponse commitEntryResponse;
    RevealResponse revealResponse;

    public CommitEntryResponse getCommitEntryResponse() {
        return commitEntryResponse;
    }

    public CommitEntry setCommitEntryResponse(CommitEntryResponse commitEntryResponse) {
        this.commitEntryResponse = commitEntryResponse;
        return this;
    }

    public RevealResponse getRevealResponse() {
        return revealResponse;
    }

    public CommitEntry setRevealResponse(RevealResponse revealResponse) {
        this.revealResponse = revealResponse;
        return this;
    }
}

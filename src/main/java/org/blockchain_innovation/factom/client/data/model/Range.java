package org.blockchain_innovation.factom.client.data.model;

public class Range {

    private int start;
    private int end;

    public int getStart() {
        return start;
    }

    public Range setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public Range setEnd(int end) {
        this.end = end;
        return this;
    }
}

package org.blockchain_innovation.factom.client.api.model.response.factomd;

import java.io.Serializable;

public class CurrentMinuteResponse implements Serializable {
    private long leaderheight;
    private long directoryblockheight;
    private int minute;
    private long currentblockstarttime;
    private long currentminutestarttime;
    private long currenttime;
    private int directoryblockinseconds;
    private boolean stalldetected;
    private int faulttimeout;
    private int roundtimeout;

    public long getLeaderHeight() {
        return leaderheight;
    }

    public long getDirectoryBlockHeight() {
        return directoryblockheight;
    }

    public int getMinute() {
        return minute;
    }

    public long getCurrentBlockStartTime() {
        return currentblockstarttime;
    }

    public long getCurrentMinuteStartTime() {
        return currentminutestarttime;
    }

    public long getCurrentTime() {
        return currenttime;
    }

    public int getDirectoryBlockInSeconds() {
        return directoryblockinseconds;
    }

    public boolean isStallDetected() {
        return stalldetected;
    }

    public int getFaultTimeout() {
        return faulttimeout;
    }

    public int getRoundTimeout() {
        return roundtimeout;
    }
}

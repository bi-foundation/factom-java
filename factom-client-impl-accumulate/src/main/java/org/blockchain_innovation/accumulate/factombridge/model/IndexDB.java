package org.blockchain_innovation.accumulate.factombridge.model;

public enum IndexDB {
    ACCUMULATE_INDEX("accumulate-index"), FACTOM_ENTRIES("factom-entries");

    private String value;

    IndexDB(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package org.blockchain_innovation.factom.identiy.did.entry;

public enum OperationValue {
    DID_MANAGEMENT("DIDManagement"), DID_UPDATE("DIDUpdate"), DID_METHOD_VERSION_UPGRADE("DIDMethodVersionUpgrade"), DID_DEACTIVATION("DIDDeactivation");

    private final String operation;

    OperationValue(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}

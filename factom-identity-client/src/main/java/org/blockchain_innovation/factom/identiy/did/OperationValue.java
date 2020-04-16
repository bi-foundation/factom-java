package org.blockchain_innovation.factom.identiy.did;

public enum OperationValue {
    DID_MANAGEMENT("DIDManagement"), DID_UPDATE("DIDUpdate"), DID_METHOD_VERSION_UPGRADE("DIDMethodVersionUpgrade"), DID_DEACTIVATION("DIDDeactivation"), IDENTITY_CHAIN_CREATION("IdentityChain"), IDENTITY_CHAIN_REPLACE_KEY("ReplaceKey");

    private final String operation;

    OperationValue(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public static OperationValue fromOperation(String operation) {
        for (OperationValue operationValue : values()) {
            if (operationValue.getOperation().equalsIgnoreCase(operation)) {
                return operationValue;
            }
        }
        throw new DIDRuntimeException("No operation value found for :" + operation);
    }
}

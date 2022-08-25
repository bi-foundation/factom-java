package org.blockchain_innovation.factom.client.api.model;

import org.blockchain_innovation.factom.client.api.model.types.AddressType;

public interface Address {
    String getValue();

    AddressType getType();
}

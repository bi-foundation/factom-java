package org.blockchain_innovation.factom.identiy.did.parse;

import org.blockchain_innovation.factom.client.api.ops.StringUtils;

import static org.blockchain_innovation.factom.identiy.did.entry.DIDVersion.FACTOM_V1;

/**
 * A rule that checks whether a Factom Method Specific Id (:factom: part in the DID Url) is present
 */
public class FactomMethodSpecificIdRule extends AbstractGenericRule<String> {

    private final String didUrl;

    public FactomMethodSpecificIdRule(String didUrl) {
        this.didUrl = didUrl;
    }

    @Override
    public String execute() throws RuleException {
        if (StringUtils.isEmpty(getDidUrl())) {
            throw new RuleException("A Factom DID cannot have an empty DID scheme");
        }

        String methodSpecificId = FACTOM_V1.getMethodSpecificId(getDidUrl());
        if (StringUtils.isEmpty(methodSpecificId)) {
            throw new RuleException("Invalid Factom DID specified: %s", getDidUrl());
        }
        return methodSpecificId;
    }

    public String getDidUrl() {
        return didUrl;
    }
}

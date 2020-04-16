package org.blockchain_innovation.factom.identiy.did.parse;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.identiy.did.OperationValue;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A rule that asserts whether a certain operation value is set as first external Id from a given list of supported values
 */
public class AssertOperationRule extends AbstractEntryRule<String> {

    private final Set<OperationValue> operationValues;

    public AssertOperationRule(Entry entry, Set<OperationValue> operationValues) {
        super(entry);
        this.operationValues = operationValues;
    }

    public AssertOperationRule(Entry entry, OperationValue operationValue) {
        this(entry, EnumSet.of(operationValue));
    }

    @Override
    public String execute() throws RuleException {
        if (getOperationValues() == null || getOperationValues().size() == 0) {
            throw new RuleException("An operation rule cannot work without an operation value present");
        }

        assertEntry();
        new ExternalIdsSizeRule(getEntry(), Optional.of(1)).execute();

        String firstExtId = getEntry().getExternalIds().get(0);
        if (!getOperationValues().stream().filter(operationValue -> operationValue.getOperation().equals(firstExtId)).findFirst().isPresent()) {
            throw new RuleException("First external id '%s' found in the chain, was not in the list of required operations '%s'", firstExtId, getOperationValues().stream().map(OperationValue::getOperation).collect(Collectors.joining(",")));
        }
        return firstExtId;
    }

    public Set<OperationValue> getOperationValues() {
        return operationValues;
    }
}

package org.blockchain_innovation.factom.identiy.did.parse;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.ops.StringUtils;

import java.util.Optional;

/**
 * A rule that asserts whether the 2nd external Id from an antry denoting the scheme version is supported
 */
public class EntrySchemeVersionRule extends AbstractEntryRule<String> {


    private final String semver;

    public EntrySchemeVersionRule(Entry entry, String semver) {
        super(entry);
        this.semver = semver;
    }

    @Override
    public String execute() throws RuleException {
        if (StringUtils.isEmpty(getSemver())) {
            throw new RuleException("A semantic version needs to be defined for the Entry scheme version rule");
        }

        assertEntry();
        new ExternalIdsSizeRule(getEntry(), Optional.of(2)).execute();

        String extId = getEntry().getExternalIds().get(1);
        if (!getSemver().equals(extId)) {
            throw new RuleException("Entry scheme version in external id with version '%s' was not equal to required version '%s'", extId, getSemver());
        }
        return extId;
    }

    public String getSemver() {
        return semver;
    }
}

package org.blockchain_innovation.factom.client.api;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AddressType {
    FACTOID_PUBLIC("FA", Visibility.PUBLIC), FACTOID_SECRET("Fs", Visibility.PRIVATE),
    ENTRY_CREDIT_PUBLIC("EC", Visibility.PUBLIC), ENTRY_CREDIT_SECRET("Es", Visibility.PRIVATE);

    private final String prefix;
    private final Visibility visibility;

    AddressType(String prefix, Visibility visibility) {
        this.prefix = prefix;
        this.visibility = visibility;
    }

    public enum Visibility {
        PUBLIC, PRIVATE
    }

    public String getPrefix() {
        return prefix;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public boolean isPublic() {
        return getVisibility() == Visibility.PUBLIC;
    }

    public boolean isPrivate() {
        return getVisibility() == Visibility.PRIVATE;
    }

    public boolean isValid(String address) {
        return isValidAddress(address) && address.startsWith(getPrefix());
    }

    public void assertValid(String address) {
        assertValidAddress(address);
        if (!address.startsWith(getPrefix())) {
            throw new FactomRuntimeException.AssertionException(String.format("Type of address '%s' is not a valid", address));
        }
    }

    public static boolean isValidAddress(String address) {
        try {
            assertValidAddress(address);
        } catch (FactomRuntimeException.AssertionException e) {
            return false;
        }
        return true;
    }

    public static void assertVisibility(String address, Visibility visibility) throws FactomRuntimeException.AssertionException {
        assertValidAddress(address);
        AddressType addressType = AddressType.getType(address);
        if (addressType.getVisibility() != visibility) {
            throw new FactomRuntimeException.AssertionException(String.format("Visibility of address '%s' is not the desired %s", address, visibility));
        }
    }

    public static void assertValidAddress(String address) throws FactomRuntimeException.AssertionException {
        if (StringUtils.isEmpty(address) || address.length() <= 2) {
            throw new FactomRuntimeException.AssertionException(String.format("Address '%s' is not a valid address", address));
        } else if (!getValidPrefixes().contains(address.substring(0, 2))) {
            throw new FactomRuntimeException.AssertionException(String.format("Address '%s' does not start with a valid prefix", address));
        }
        byte[] addressBytes = Encoding.BASE58.decode(address);
        if (addressBytes.length != 38) {
            throw new FactomRuntimeException.AssertionException(String.format("Address '%s' is not 38 bytes long!", address));
        }
        byte[] sha256d = Digests.SHA_256.doubleDigest(Arrays.copyOf(addressBytes, 34));
        byte[] checksum = Arrays.copyOf(sha256d, 4);
        if (!Arrays.equals(checksum, Arrays.copyOfRange(addressBytes, 34, 38))) {
            throw new FactomRuntimeException.AssertionException(String.format("Address '%s' checksum mismatch!", address));
        }
    }

    public static void assertValidAddress(String address, AddressType type) throws FactomRuntimeException.AssertionException {
        assertValidAddress(address);
        if (AddressType.getType(address) != type) {
            throw new FactomRuntimeException.AssertionException(String.format("Address %s is not of type %s", address, type.name()));
        }
    }

    public static AddressType getType(String address) {
        assertValidAddress(address);
        for (AddressType type : values()) {
            if (address.startsWith(type.getPrefix())) {
                return type;
            }
        }
        // Not possible anyway:
        throw new FactomRuntimeException.AssertionException("Could not deduct address type for " + address);
    }

    public static String getPrefix(String address) {
        assertValidAddress(address);
        return address.substring(0, 2);
    }

    public static List<String> getValidPrefixes() {
        return Arrays.stream(values()).map(AddressType::getPrefix).collect(Collectors.toList());
    }
}

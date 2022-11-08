package org.blockchain_innovation.factom.client.api.model.types;

import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.ops.Digests;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.ops.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The different supported Factom addresses.
 */
public enum AddressType {
    FACTOID_PUBLIC("FA", "5fb1", Visibility.PUBLIC),
    FACTOID_SECRET("Fs", "6478", Visibility.PRIVATE),

    ENTRY_CREDIT_PUBLIC("EC", "592a", Visibility.PUBLIC),
    ENTRY_CREDIT_SECRET("Es", "5db6", Visibility.PRIVATE),
    IDENTITY_PUBLIC1("id1", "3fbeba", Visibility.PUBLIC),
    IDENTITY_PRIVATE1("sk1", "4db6c9", Visibility.PRIVATE),
    IDENTITY_PUBLIC2("id2", "3fbed8", Visibility.PUBLIC),
    IDENTITY_PRIVATE2("sk2", "4db6e7", Visibility.PRIVATE),
    IDENTITY_PUBLIC3("id3", "3fbef6", Visibility.PUBLIC),
    IDENTITY_PRIVATE3("sk3", "4db705", Visibility.PRIVATE),
    IDENTITY_PUBLIC4("id4", "3fbf14", Visibility.PUBLIC),
    IDENTITY_PRIVATE4("sk4", "4db723", Visibility.PRIVATE),
    IDENTITY_IDPUB("idpub", "0345ef9de0", Visibility.PUBLIC),
    IDENTITY_IDSEC("idsec", "0345f3d0d6", Visibility.PRIVATE),
    LITE_TOKEN_ACCOUNT("acc:/", "", Visibility.PRIVATE),
    LITE_IDENTITY("acc:/", "", Visibility.PRIVATE);


    private final String humanReadablePrefix;
    private final String addressPrefix;
    private final Visibility visibility;

    AddressType(String humanReadablePrefix, String addressPrefix, Visibility visibility) {
        this.humanReadablePrefix = humanReadablePrefix;
        this.addressPrefix = addressPrefix;
        this.visibility = visibility;
    }

    /**
     * Checks whether the supplied address is a valid and supported address.
     *
     * @param address The address to check.
     * @return Whether the address is valid and supported.
     */
    public static boolean isValidAddress(String address) {
        try {
            assertValidAddress(address);
        } catch (FactomRuntimeException.AssertionException e) {
            return false;
        }
        return true;
    }

    /**
     * Asserts whether the supplied address has the desired visibility. This allows easy checking for public or private addresses.
     *
     * @param address    The address to check.
     * @param visibility The desired visibility to check against.
     * @throws FactomRuntimeException.AssertionException Whenever the assertion fails (other visibility then expected).
     */
    public static void assertVisibility(String address, Visibility visibility) throws FactomRuntimeException.AssertionException {
        assertValidAddress(address);
        AddressType addressType = AddressType.getType(address);
        if (addressType.getVisibility() != visibility) {
            throw new FactomRuntimeException.AssertionException(String.format("Visibility of address '%s' is not the desired %s", address, visibility));
        }
    }

    /**
     * Asserts that the supplied address string is valid and supported.
     *
     * @param address The address to check.
     * @throws FactomRuntimeException.AssertionException Whenever the address is invalid or not supported.
     */
    public static void assertValidAddress(String address) throws FactomRuntimeException.AssertionException {
        if (StringUtils.isEmpty(address) || address.length() <= 10) {
            throw new FactomRuntimeException.AssertionException(String.format("Address '%s' is not a valid address", address));
        } else if (!getValidPrefixes().contains(address.substring(0, 2))
                && !getValidPrefixes().contains(address.substring(0, 3))
                && !getValidPrefixes().contains(address.substring(0, 5))) {
            throw new FactomRuntimeException.AssertionException(String.format("Address '%s' does not start with a valid humanReadablePrefix", address));
        }
        if(!address.toLowerCase().startsWith("acc://")) {
            int splitPos  = address.indexOf('/');
            final String addressOnly = splitPos > -1 ? address.substring(0, splitPos) : address;
            byte[] addressBytes = Encoding.BASE58.decode(addressOnly);
            int length = addressBytes.length;
            if (length == 38 || length == 39 || length == 41) {
                byte[] sha256d = Digests.SHA_256.doubleDigest(Arrays.copyOf(addressBytes, length - 4));
                byte[] checksum = Arrays.copyOf(sha256d, 4);
//            if (length == 41) {
//                checksum = Arrays.copyOfRange(sha256d, 32 - 4, 32);
//            }
                if (!Arrays.equals(checksum, Arrays.copyOfRange(addressBytes, length - 4, length))) {
                    throw new FactomRuntimeException.AssertionException(String.format("Address '%s' checksum mismatch!", address));
                }
            } else {
                throw new FactomRuntimeException.AssertionException(String.format("Address '%s' is not 38 bytes long!", address));
            }

        }
    }

    /**
     * Asserts the address is valid and of the desired address type.
     *
     * @param address The address to check.
     * @param types    The desired type.
     * @throws FactomRuntimeException.AssertionException Whenever the address is invalid, not supported or not of the desired type.
     */
    public static void assertValidAddress(String address, AddressType... types) throws FactomRuntimeException.AssertionException {
        assertValidAddress(address);
        boolean found = false;
        for(final AddressType type : types) {
            if (AddressType.getType(address) == type) {
                found = true;
            }
        }
        if (!found) {
            throw new FactomRuntimeException.AssertionException(String.format("Address %s is not of types %s", address, types));
        }
    }

    /**
     * Asserts the address is valid and of the desired address type.
     *
     * @param address The address to check.
     * @param types    The desired type.
     * @throws FactomRuntimeException.AssertionException Whenever the address is invalid, not supported or not of the desired type.
     */
    public static void assertValidAddress(Address address, AddressType... types) throws FactomRuntimeException.AssertionException {
        boolean found = false;
        for(final AddressType type : types) {
            if (address.getType() == type) {
                found = true;
            }
        }
        if (!found) {
            throw new FactomRuntimeException.AssertionException(String.format("Address %s is not of types %s", address, types));
        }
    }

    /**
     * Get the address type from the address string.
     *
     * @param address
     * @return
     */
    public static AddressType getType(String address) {
        assertValidAddress(address);
        for (AddressType type : values()) {
            if (address.startsWith(type.getHumanReadablePrefix())) {
                return type;
            }
        }
        // Not possible anyway:
        throw new FactomRuntimeException.AssertionException("Could not deduct address type for " + address);
    }

    /**
     * Get the human readable prefix (2 or 3 characters) from the supplied address.
     *
     * @param address The address.
     * @return The human readable prefix.
     */
    public static String getPrefix(String address) {
        assertValidAddress(address);
        byte[] rawAddress = Encoding.BASE58.decode(address);
        if (rawAddress.length == 41) {
            return address.substring(0, 5);
        } else if (rawAddress.length == 39) {
            return address.substring(0, 3);
        } else {
            return address.substring(0, 2);
        }
    }

    /**
     * Get the valid and supported prefixes.
     *
     * @return The prefixes.
     */
    public static List<String> getValidPrefixes() {
        return Arrays.stream(values()).map(AddressType::getHumanReadablePrefix).collect(Collectors.toList());
    }

    /**
     * The 2 or 3 character human reedable prefix.
     *
     * @return The prefix.
     */
    public String getHumanReadablePrefix() {
        return humanReadablePrefix;
    }

    /**
     * The raw prefix (bytes).
     *
     * @return the prefix.
     */
    public byte[] getAddressPrefix() {
        return Encoding.HEX.decode(addressPrefix);
    }

    /**
     * The visibility associated with the address.
     *
     * @return The visibility.
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * Whether the address is a public address (visibility).
     *
     * @return whether the address is public or not.
     */
    public boolean isPublic() {
        return getVisibility() == Visibility.PUBLIC;
    }


    /**
     * Whether the address is a private address (visibility).
     *
     * @return whether the address is private or not.
     */
    public boolean isPrivate() {
        return getVisibility() == Visibility.PRIVATE;
    }

    /**
     * Returns whether this address type corresponds to the suoplied address.
     *
     * @param address The address to check against this type.
     * @return Whether this type is valid for the address.
     */

    public boolean isValid(String address) {
        return isValidAddress(address) && address.startsWith(getHumanReadablePrefix());
    }

    /**
     * Assert the supplied address is valid for this address type.
     *
     * @param address The address to check,
     */
    public void assertValid(Address address) {
        if (this != address.getType()) {
            throw new FactomRuntimeException.AssertionException(String.format("Type of address '%s' is not a valid", address));
        }
    }

    /**
     * Assert the supplied address is valid for this address type.
     *
     * @param address The address to check.
     */
    public void assertValid(String address) {
        assertValidAddress(address);
        if (!address.startsWith(getHumanReadablePrefix())) {
            throw new FactomRuntimeException.AssertionException(String.format("Type of address '%s' is not a valid", address));
        }
    }

    /**
     * Denotes an address visibility.
     */
    public enum Visibility {
        PUBLIC, PRIVATE
    }
}

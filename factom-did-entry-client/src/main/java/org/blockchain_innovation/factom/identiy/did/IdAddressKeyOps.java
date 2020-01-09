package org.blockchain_innovation.factom.identiy.did;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.ops.*;
import org.blockchain_innovation.factom.identiy.did.entry.ReplaceKeyIdentityChainEntry;
import org.factomprotocol.identity.did.model.FactomKey;
import org.factomprotocol.identity.did.model.KeyType;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IdAddressKeyOps {
    private static final ByteOperations OPS = new ByteOperations();

    public boolean verifyEd25519(byte[] data, byte[] signature, EdDSAPublicKey publicKey) {
        try {
            EdDSAEngine engine = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
            engine.initVerify(publicKey);
            return engine.verifyOneShot(data, signature);
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            throw new DIDRuntimeException(e);
        }
    }

    public byte[] signEd25519(byte[] digest, EdDSAPrivateKey privateKey) {
        try {
            EdDSAEngine engine = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
            engine.initSign(privateKey);
            return engine.signOneShot(digest);
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            throw new DIDRuntimeException(e);
        }
    }

    public byte[] signKeyReplacement(String chainId, String oldIdPub, String newIdPub, EdDSAPrivateKey privateKey) {
        assertValidAddress(oldIdPub);
        assertValidAddress(newIdPub);
        String input = new StringBuilder().append(chainId).append(oldIdPub).append(newIdPub).toString();
        return signEd25519(input.getBytes(StandardCharsets.UTF_8), privateKey);
    }

    public boolean verifyKeyReplacementSignature(ReplaceKeyIdentityChainEntry entry) {
        return verifyKeyReplacementSignature(entry.getChainId(), entry.getOldKey(), entry.getNewKey(), entry.getSignature(), toEd25519PublicKey(entry.getSignerKey()));
    }

    public boolean verifyKeyReplacementSignature(String chainId, String oldIdPub, String newIdPub, String signature, EdDSAPublicKey publicKey) {
        assertValidAddress(oldIdPub);
        assertValidAddress(newIdPub);
        String input = new StringBuilder().append(chainId).append(oldIdPub).append(newIdPub).toString();
        return verifyEd25519(input.getBytes(StandardCharsets.UTF_8), Encoding.HEX.decode(signature), publicKey);
    }

    public boolean isValidAddress(String idAddress) {
        return AddressType.isValidAddress(idAddress) && (AddressType.getType(idAddress) == AddressType.IDENTITY_IDPUB || AddressType.getType(idAddress) == AddressType.IDENTITY_IDSEC);
    }

    public void assertValidAddress(String idAddress) {
        if (!isValidAddress(idAddress)) {
            throw new FactomRuntimeException.AssertionException("Invalid idpub address value supplied: " + idAddress);
        }
    }
    public List<String> createNewKeyReplacementList(List<String> oldKeys, String keyToBeReplaced, String keyReplacement, String signerKey) {
        assertValidReplacement(oldKeys, keyToBeReplaced, keyReplacement, signerKey);
        int replaceIndex = getReplaceIndex(oldKeys, keyToBeReplaced, signerKey);
        List<String> keys = new ArrayList<>(oldKeys.size());
        keys.addAll(oldKeys);
        keys.remove(replaceIndex);
        keys.add(replaceIndex, keyReplacement);
        return keys;
    }

    public void assertValidReplacement(List<String> oldKeys, String keyToBeReplaced, String keyReplacement, String signerKey) {
        assertValidAddress(keyToBeReplaced);
        assertValidAddress(keyReplacement);
        assertValidAddress(signerKey);
        if (oldKeys == null || oldKeys.isEmpty()) {
            throw new FactomRuntimeException.AssertionException("Cannot replace keys when there was no key hierarchy");
        } else if (StringUtils.isEmpty(keyToBeReplaced)) {
            throw new FactomRuntimeException.AssertionException(String.format("Cannot replace an empty key"));
        } else if (!oldKeys.contains(keyToBeReplaced)) {
            throw new FactomRuntimeException.AssertionException(String.format("Cannot replace key %s, that was not in the list of current keys", keyReplacement));
        } else if (oldKeys.contains(keyReplacement)) {
            throw new FactomRuntimeException.AssertionException(String.format("Cannot replace key %s with %s, that is already in the list of current keys", keyToBeReplaced, keyReplacement));
        } else if (keyToBeReplaced.equalsIgnoreCase(keyReplacement)) {
            throw new FactomRuntimeException.AssertionException(String.format("Cannot replace key %s with itself", keyReplacement));
        } else if (!oldKeys.contains(signerKey)) {
            throw new FactomRuntimeException.AssertionException(String.format("Cannot replace key %s with signer %s not in current key is", keyReplacement, signerKey));
        } else if (keyToBeReplaced.equalsIgnoreCase(signerKey)) {
            throw new FactomRuntimeException.AssertionException(String.format("Cannot replace key %s with signer %s itself", keyReplacement, signerKey));
        }

        getReplaceIndex(oldKeys, keyToBeReplaced, signerKey);
    }

    private int getReplaceIndex(List<String> oldKeys, String keyToBeReplaced, String signerKey) {
        int signerIndex = Integer.MAX_VALUE;
        int replaceIndex = Integer.MIN_VALUE;
        for (int index = 0; index < oldKeys.size(); index++) {
            String key = oldKeys.get(index);
            if (key.equalsIgnoreCase(signerKey) && signerIndex == Integer.MAX_VALUE) {
                signerIndex = index;
            } else if (key.equalsIgnoreCase(keyToBeReplaced) && replaceIndex == Integer.MIN_VALUE) {
                replaceIndex = index;
            }
        }
        if (signerIndex >= replaceIndex) {
            throw new FactomRuntimeException.AssertionException(String.format("Cannot replace key %s, index %d with a signer key %s with a higher index %d", keyToBeReplaced, replaceIndex, signerKey, signerIndex));
        }
        return replaceIndex;
    }

    public EdDSAPrivateKey toEd25519PrivateKey(String idSecAddress) {
        if (!AddressType.getType(idSecAddress).isPrivate()) {
            throw new FactomRuntimeException.AssertionException("Private key can only be extracted from an idsec address. Supplied: " + idSecAddress);
        }
        return (EdDSAPrivateKey) toEd25519Key(idSecAddress);
    }

    public EdDSAPublicKey toEd25519PublicKey(String idAddress) {
        Key key = toEd25519Key(idAddress);
        if (AddressType.getType(idAddress).isPublic()) {
            return (EdDSAPublicKey) key;
        } else {
            return new EdDSAPublicKey(new EdDSAPublicKeySpec(
                    ((EdDSAPrivateKey) key).getA(), EdDSANamedCurveTable.ED_25519_CURVE_SPEC));
        }
    }

    public Key toEd25519Key(String idAddress) {
        byte[] keyBytes = toEd25519KeyBytes(idAddress);
        EdDSANamedCurveSpec curveSpec = EdDSANamedCurveTable.ED_25519_CURVE_SPEC;

        if (AddressType.getType(idAddress).isPrivate()) {
            EdDSAPrivateKeySpec keySpec = new EdDSAPrivateKeySpec(keyBytes, curveSpec);
            return new EdDSAPrivateKey(keySpec);
        } else {
            EdDSAPublicKeySpec keySpec = new EdDSAPublicKeySpec(keyBytes, curveSpec);
            return new EdDSAPublicKey(keySpec);
        }
    }

    public byte[] toEd25519KeyBytes(String idAddress) {
        assertValidAddress(idAddress);

        // checksum and prefix have already been checked by the assert method above
        byte[] addressBytes = Base58.decode(idAddress);
        byte[] key = Arrays.copyOfRange(addressBytes, 5, 37);
        return key;
    }

    public String toIdPubAddress(FactomKey factomKey) {
        return toIdPubAddress(factomKey.getType(), factomKey.getPublicValue(), Encoding.BASE58);
    }

    public String toIdPubAddress(KeyType keyType, String value, Encoding encoding) {
        return toIdPubOrSecAddress(keyType, AddressType.IDENTITY_IDPUB, value, encoding);
    }

    public String toIdSecAddress(KeyType keyType, String value, Encoding encoding) {
        return toIdPubOrSecAddress(keyType, AddressType.IDENTITY_IDSEC, value, encoding);
    }

    public String toIdPubAddress(KeyType keyType, byte[] value) {
        return toIdPubOrSecAddress(keyType, AddressType.IDENTITY_IDPUB, value);
    }

    public String toIdSecAddress(KeyType keyType, byte[] value) {
        return toIdPubOrSecAddress(keyType, AddressType.IDENTITY_IDSEC, value);
    }

    private String toIdPubOrSecAddress(KeyType keyType, AddressType addressType, String value, Encoding encoding) {
        if (keyType == KeyType.IDPUB) {
            assertValidAddress(value);
            return toIdPubOrSecAddress(keyType, addressType, Base58.decode(value));
        } else if ((keyType == KeyType.ED25519VERIFICATIONKEY)) {
            return toIdPubOrSecAddress(keyType, addressType, encoding.decode(value));
        }
        throw new FactomRuntimeException.AssertionException("Only Idpub and ED25519 key types supported for idpub conversions for now. Not supported: " + keyType.getValue());
    }


    private String toIdPubOrSecAddress(KeyType keyType, AddressType addressType, byte[] value) {
        if (keyType == KeyType.IDPUB) {
            String address = Base58.encode(value);
            assertValidAddress(address);

            return address;
        } else if (keyType == KeyType.ED25519VERIFICATIONKEY) {
            if (value == null || value.length != 32) {
                throw new FactomRuntimeException.AssertionException("Cannot construct an IdPub address from and ED25519 key that is not 32 bytes");
            }

            byte[] prefixWithPubKey = OPS.concat(addressType.getAddressPrefix(), value);
            byte[] doubleDigest = Digests.SHA_256.doubleDigest(prefixWithPubKey);
            byte[] checkSum = Arrays.copyOfRange(doubleDigest, 0, 4);

            return Base58.encode(OPS.concat(prefixWithPubKey, checkSum));
        }

        throw new FactomRuntimeException.AssertionException("Only Idpub and ED25519 key types supported for idpub conversions for now. Not supported: " + keyType.getValue());
    }


}

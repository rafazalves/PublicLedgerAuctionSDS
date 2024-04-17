package org.blockchain;

import java.security.*;

public class Wallet {

    private KeyPair walletKeyPair;

    public Wallet() {
        generateKeyPair();
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512);
            walletKeyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey() {
        return walletKeyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return walletKeyPair.getPrivate();
    }

}
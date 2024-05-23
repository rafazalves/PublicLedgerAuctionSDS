package org.blockchain;

import java.security.*;

public class Wallet {

    private KeyPair walletKeyPair;
    private float stake; // Amount of cryptocurrency held

    public Wallet(float stake) {
        generateKeyPair();
        this.stake = stake;
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

    public float getStake() {
        return stake;
    }

    public void setStake(float stake) {
        this.stake = stake;
    }

}
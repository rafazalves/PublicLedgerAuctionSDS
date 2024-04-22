package org.blockchain.consensus;

import java.security.*;

public class Validator {

    private KeyPair validatorKeyPair;
    private PublicKey publicKey;
    private float stake; // Amount of cryptocurrency held by the validator

    public Validator(float stake) {
        generateKeyPair();
        this.publicKey = publicKey;
        this.stake = stake;
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512);
            validatorKeyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey() {
        return validatorKeyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return validatorKeyPair.getPrivate();
    }

    public float getStake() {
        return stake;
    }

    public void setStake(float stake) {
        this.stake = stake;
    }
}
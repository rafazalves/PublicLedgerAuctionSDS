package org.blockchain.transaction;

import org.blockchain.utils.Utils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.blockchain.utils.Utils.applySha256;

public class Transaction {

    private String hash;
    private PublicKey sender;
    private PublicKey recipient;
    public byte[] signature;
    private float amount;
    private long transactionTimestamp;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private static int avoidSameHash = 0;

    public Transaction(PublicKey sender, PublicKey recipient, float amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.transactionTimestamp = new Date().getTime();
        this.hash = calculateHashData();
    }

    public PublicKey getSender() {
        return sender;
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public float getAmount() {
        return amount;
    }

    public String getHash() {
        return hash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public long getTransactionTimestamp() {
        return transactionTimestamp;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Timestamp: ").append(sdf.format(transactionTimestamp)).append("\n");
        sb.append("Sender: ").append(Utils.getStringFromKey(sender)).append("\n");
        sb.append("Recipient: ").append(Utils.getStringFromKey(recipient)).append("\n");
        sb.append("Amount: ").append(amount).append("\n");
        sb.append("Hash: ").append(hash).append("\n");
        return sb.toString();
    }

    public String calculateHashData() {
        avoidSameHash ++;
        String dataToHash = Utils.getStringFromKey(sender) + Utils.getStringFromKey(recipient) + Float.toString(amount) + Long.toString(transactionTimestamp) + avoidSameHash;
        return applySha256(dataToHash);
    }

    //Signs with private key
    public void generateSignature(PrivateKey privateKey) {
        String data = Utils.getStringFromKey(sender) + Utils.getStringFromKey(recipient) + amount;
        signature = Utils.signString(privateKey,data);
    }
}
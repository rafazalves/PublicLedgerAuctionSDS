package org.blockchain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.blockchain.consensus.ProofOfStake;
import org.blockchain.consensus.ProofOfWork;
import org.blockchain.consensus.Validator;
import org.blockchain.transaction.Transaction;
import static org.blockchain.utils.Utils.applySha256;
import static org.blockchain.validators.TransactionValidator.isValidTransaction;

public class Block {
    private int id;
    private long blockTimestamp;
    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private int nonce;
    private String hash;
    private String previousHash;
    private String merkleRoot;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private List<Validator> validators = new ArrayList<>(); // List of validators and their stakes

    public Block(int id, String previousHash, ArrayList<Transaction> transactions) {
        this.id = id;
        this.blockTimestamp = new Date().getTime();
        this.transactions = transactions;
        this.nonce = 0;
        this.merkleRoot = computeMerkleRoot(this.getTransactions());
        this.hash = calculateHash();
        this.previousHash = previousHash;
    }

    public int getId() {
        return id;
    }

    public long getblockTimestamp() {
        return blockTimestamp;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String calculatedHashData) {
        this.hash = calculatedHashData;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(String computeMerkleRoot) {
        this.merkleRoot = computeMerkleRoot;
    }

    public List<Validator> getValidators() {
        return validators;
    }

    public void addValidator(Validator validator) {
        this.validators.add(validator);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(id).append("\n");
        sb.append("BlockTimeStamp: ").append(sdf.format(blockTimestamp)).append("\n");
        sb.append("Nonce: ").append(nonce).append("\n");
        sb.append("Previous Hash: ").append(previousHash).append("\n");
        sb.append("Hash: ").append(hash).append("\n");
        sb.append("Transactions: [\n");
        if (transactions.isEmpty()) {
            sb.append("No Transactions Yet\n");
        } else {
            for (Transaction transaction : transactions) {
                sb.append(transaction.toString()).append("\n");
            }
        }
        sb.append(" ]\n");
        return sb.toString();
    }

    public String calculateHash() {
        String calHash = applySha256(previousHash + blockTimestamp + nonce + transactions + merkleRoot);
        return calHash;
    }

    public static String computeMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();

        List<String> previousTreeLayer = new ArrayList<String>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getHash());
        }
        List<String> treeLayer = previousTreeLayer;

        while(count > 1) {
            treeLayer = new ArrayList<String>();
            for(int i=1; i < previousTreeLayer.size(); i+=2) {
                treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }

    public void mineBlock(Blockchain blockchain, int consensus, int difficulty) {
        switch (consensus) {
            case 1: // Proof-of-Work (PoW)
                ProofOfWork.mineBlock(this, difficulty);
                break;

            case 2: // Proof-of-Stake (PoS)
                ProofOfStake.mineBlock(this, blockchain);

            default: // Make default case Proof-of-Work
                ProofOfWork.mineBlock(this, difficulty);
                break;
        }
    }

    public boolean addTransaction(Transaction transaction) {
        if(transaction == null) return false;
        // Ignores First Block of the BlockChain (hash == "0")
        if((previousHash != "0")) {
            if(!isValidTransaction(transaction)) {
                System.out.println("Transaction failed. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction successful. Added to the block.");
        return true;
    }
}


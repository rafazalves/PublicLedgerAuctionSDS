package org.blockchain;

import org.blockchain.transaction.Transaction;
import org.blockchain.transaction.TransactionPool;
import org.blockchain.validators.BlockChainValidator;

import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

// FILE TO TEST BLOCKCHAIN IMPLEMENTATION
public class Main {

    private static Blockchain createTestBlockchainPoW() {

        TransactionPool transactionPool = new TransactionPool();

        // Create Wallets for 2 "Users"
        Wallet user1 = new Wallet(1000);
        Wallet user2 = new Wallet(800);

        // Initialize Blockchain with PoW
        Blockchain blockchain = new Blockchain(0,3, null);

        // Create Transaction from user1 to user2
        Transaction transaction1 = new Transaction(user1.getPublicKey(), user2.getPublicKey(), 50);
        // Sign Transaction 1
        transaction1.generateSignature(user1.getPrivateKey());

        // Create Block 1 and add the Transaction 1 to the block
        Block block1 = new Block(blockchain.getLatestBlock().getId() + 1, blockchain.getLatestBlock().getHash(), new ArrayList<Transaction>());
        if(block1.addTransaction(transaction1)) {
            transactionPool.addTransaction(transaction1);
        }
        blockchain.addBlock(block1);

        // Create Transaction from user2 to user1
        Transaction transaction2 = new Transaction(user2.getPublicKey(), user1.getPublicKey(),15);
        // Sign Transaction 2
        transaction2.generateSignature(user2.getPrivateKey());

        // Create Block 2 and add the Transaction 2 to the block
        Block block2 = new Block(blockchain.getLatestBlock().getId() + 1, blockchain.getLatestBlock().getHash(), new ArrayList<Transaction>());
        if(block2.addTransaction(transaction2)) {
            transactionPool.addTransaction(transaction2);
        }
        blockchain.addBlock(block2);

        return blockchain;
    }

    private static Blockchain createTestBlockchainPoS() {

        TransactionPool transactionPool = new TransactionPool();
        List<Wallet> validators = new ArrayList<>();

        // Create Wallets for 4 "Users"
        Wallet user1 = new Wallet(1000);
        Wallet user2 = new Wallet(800);
        Wallet user3 = new Wallet(600);
        Wallet user4 = new Wallet(300);

        validators.add(user1);
        validators.add(user2);
        validators.add(user3);

        // Initialize Blockchain with PoS
        Blockchain blockchain = new Blockchain(2,3, validators);
        blockchain.addValidators(user4);
        
        // Create Transaction from user1 to user2
        Transaction transaction1 = new Transaction(user1.getPublicKey(), user2.getPublicKey(), 50);
        // Sign Transaction 1
        transaction1.generateSignature(user1.getPrivateKey());

        // Create Block 1 and add the Transaction 1 to the block
        Block block1 = new Block(blockchain.getLatestBlock().getId() + 1, blockchain.getLatestBlock().getHash(), new ArrayList<Transaction>());
        if(block1.addTransaction(transaction1)) {
            transactionPool.addTransaction(transaction1);
        }
        blockchain.addBlock(block1);

        // Create Transaction from user2 to user1
        Transaction transaction2 = new Transaction(user2.getPublicKey(), user1.getPublicKey(),15);
        // Sign Transaction 2
        transaction2.generateSignature(user2.getPrivateKey());

        // Create Block 2 and add the Transaction 2 to the block
        Block block2 = new Block(blockchain.getLatestBlock().getId() + 1, blockchain.getLatestBlock().getHash(), new ArrayList<Transaction>());
        if(block2.addTransaction(transaction2)) {
            transactionPool.addTransaction(transaction2);
        }
        blockchain.addBlock(block2);

        return blockchain;
    }
    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException, InterruptedException {

        // New blockchain using PoW
        Blockchain blockchainPoW = createTestBlockchainPoW();
        System.out.println(blockchainPoW);

        // Check if the blockchain is valid
        System.out.println("Is blockchain valid? " + BlockChainValidator.isChainValid(blockchainPoW, blockchainPoW.getDifficulty()));

        System.out.println("|-------------------------------------------------------------------|");

        // New blockchain using PoS
        Blockchain blockchainPoS = createTestBlockchainPoS();
        System.out.println(blockchainPoS);

        // Check if the blockchain is valid
        System.out.println("Is blockchain valid? " + BlockChainValidator.isChainValid(blockchainPoS, blockchainPoS.getDifficulty()));
    }
}

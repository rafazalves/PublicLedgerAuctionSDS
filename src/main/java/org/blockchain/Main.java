package org.blockchain;

import org.blockchain.consensus.Validator;
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
        Wallet user1 = new Wallet();
        Wallet user2 = new Wallet();

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
        List<Validator> validators = new ArrayList<>();

        // Create Wallets for 2 "Users"
        Wallet user1 = new Wallet();
        Wallet user2 = new Wallet();

        // Create Validators
        Validator validator1 = new Validator(1000);
        Validator validator2 = new Validator(800);
        Validator validator3 = new Validator(600);
        Validator validator4 = new Validator(300);
        validators.add(validator1);
        validators.add(validator2);
        validators.add(validator3);

        // Initialize Blockchain with PoW
        Blockchain blockchain = new Blockchain(2,3, validators);
        blockchain.addValidators(validator4);
        
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

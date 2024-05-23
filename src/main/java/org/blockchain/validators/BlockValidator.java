package org.blockchain.validators;

import org.blockchain.Block;
import org.blockchain.Blockchain;
import org.blockchain.Wallet;
import org.blockchain.transaction.Transaction;
import org.blockchain.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.blockchain.utils.Utils.applySha256;


public class BlockValidator {
    public static boolean isValidBlock(Block block, int difficulty, Block previousBlock, int consensus) {
        // Check if the previous hash of the current block matches the hash of the previous block
        if (!block.getPreviousHash().equals(previousBlock.getHash())) {
            return false;
        }

        if(consensus!=2){
            // Validate the proof-of-work
            if (!isValidPoW(block, difficulty)) {
                return false;
            }
        }

        // Verify the Merkle root
        if (!isValidMerkleRoot(block)) {
            return false;
        }

        // Check the timestamp
        if (!isValidTimestamp(block, previousBlock)) {
            return false;
        }

        return true;
    }

    public static boolean isValidPoW(Block block, int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        String blockHash = block.getHash();
        return blockHash.substring(0, difficulty).equals(target);
    }

    private static boolean isValidMerkleRoot(Block block) {
        List<Transaction> transactions = block.getTransactions();
        String calculatedMerkleRoot = Block.computeMerkleRoot(new ArrayList<>(transactions));
        return calculatedMerkleRoot.equals(block.getMerkleRoot());
    }

    private static boolean isValidTimestamp(Block block, Block previousBlock) {
        long currentTimestamp = block.getblockTimestamp();
        long previousTimestamp = previousBlock.getblockTimestamp();
        return currentTimestamp > previousTimestamp && currentTimestamp <= System.currentTimeMillis();
    }

    public static boolean isValidPoS(Block block, Wallet validator, Blockchain blockchain) {
        // Verify that the chosen validator is not null
        if (validator == null) {
            return false;
        }

        // Verify the signature or proof of ownership of the chosen validator
        if (!validateValidatorSignature(block, validator)) {
            return false;
        }

        return true;
    }
    // Method to validate the signature or proof of ownership of the chosen validator
    private static boolean validateValidatorSignature(Block block, Wallet chosenValidator) {
        // Construct the data to be hashed based on the chosen validator
        String dataToHash = Integer.toString(block.getId()) + Long.toString(block.getblockTimestamp()) +
                block.getPreviousHash() + Integer.toString(block.getNonce()) +
                block.getMerkleRoot() + Utils.getStringFromKey(chosenValidator.getPublicKey());

        // Calculate and return the hash
        return applySha256(dataToHash).equals(block.getHash());
    }
}

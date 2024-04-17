package org.blockchain.validators;

import org.blockchain.Block;
import org.blockchain.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;


public class BlockValidator {
    /**
     * Validates a block.
     *
     * @param block The block to be validated.
     * @param difficulty The difficulty level for mining blocks if PoW.
     * @param previousBlock The previous block in the blockchain.
     * @return True if the block is valid, false otherwise.
     */
    public static boolean isValidBlock(Block block, int difficulty, Block previousBlock) {
        // Check if the previous hash of the current block matches the hash of the previous block
        if (!block.getPreviousHash().equals(previousBlock.getHash())) {
            return false;
        }

        // Validate the proof-of-work
        if (!isValidPoW(block, difficulty)) {
            return false;
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
    
}

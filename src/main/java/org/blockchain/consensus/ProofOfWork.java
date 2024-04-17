package org.blockchain.consensus;

import org.blockchain.Block;
import static org.blockchain.utils.Utils.applySha256;
import org.blockchain.transaction.Transaction;
import org.blockchain.validators.BlockValidator;

public class ProofOfWork {

    public static String calculateHashPoW(Block block) {
        block.setMerkleRoot(block.computeMerkleRoot(block.getTransactions()));
        String dataToHash = Integer.toString(block.getId()) + Long.toString(block.getblockTimestamp()) + block.getPreviousHash() + Integer.toString(block.getNonce()) + block.getMerkleRoot();
        for (Transaction transaction : block.getTransactions()) {
            dataToHash += transaction.getHash();
        }
        return applySha256(dataToHash);
    }
    
    public static void mineBlock(Block block, int difficulty) {
        while (!BlockValidator.isValidPoW(block, difficulty)) {
            block.setNonce(block.getNonce() + 1);
            block.setHash(calculateHashPoW(block));
        }
    }
}

package org.blockchain.consensus;

import org.blockchain.Block;
import static org.blockchain.utils.Utils.applySha256;

public class ProofOfStake { //FALTA IMPLEMENTAR
    public static String calculateHashPoS(Block block) {
        String dataToHash = Integer.toString(block.getId()) + Long.toString(block.getblockTimestamp()) + block.getPreviousHash() + Integer.toString(block.getNonce()) + block.getMerkleRoot();
        return applySha256(dataToHash);
    }

    public static void mineBlock(Block block) {
        String hash = calculateHashPoS(block);
        block.setHash(hash);
    }
}
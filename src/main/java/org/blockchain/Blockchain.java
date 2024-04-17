package org.blockchain;

import org.blockchain.validators.BlockValidator;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private int difficulty;
    private List<Block> blockchainBlocks = new ArrayList<>();
    private int consensus;

    public List<Block> getblockchainBlocks() {
        return blockchainBlocks;
    }

    public Block getLatestBlock() {
        return blockchainBlocks.get(blockchainBlocks.size() - 1);
    }

    public Blockchain(int consensus, int difficulty) {
        this.difficulty = difficulty;
        this.consensus = consensus;

        Block genesisBlock = new Block(0, "0", new ArrayList<>()); // Create genesisBlock
        genesisBlock.mineBlock(consensus, difficulty);; // Mine the genesis block during initialization
        blockchainBlocks.add(genesisBlock) ;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Difficulty: ").append(difficulty).append("\n");
        if(consensus == 2) sb.append("Consensus: PoS").append("\n-----------\n"); //IMPLEMENTAR ESTA PARTE
        else sb.append("Consensus: PoW").append("\n-----------\n");
        sb.append("Blockchain Blocks: [\n");
        for (Block block : blockchainBlocks) {
            sb.append(block.toString()).append("\n");
        }
        sb.append("]\n");
        return sb.toString();
    }

    public void addBlock(Block newBlock) {
        newBlock.mineBlock(consensus, difficulty);
        if(BlockValidator.isValidBlock(newBlock, difficulty, blockchainBlocks.get(blockchainBlocks.size() - 1))){
            blockchainBlocks.add(newBlock);
        }else{
            System.out.println("Block Validation Fail");
        }
    }
}

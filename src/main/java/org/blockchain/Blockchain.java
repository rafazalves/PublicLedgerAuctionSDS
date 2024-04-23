package org.blockchain;

import org.blockchain.consensus.Validator;
import org.blockchain.validators.BlockValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Blockchain {
    private int difficulty;
    private List<Block> blockchainBlocks = new ArrayList<>();
    private int consensus;
    private List<Validator> validators = new ArrayList<>(); // List of validators in the network

    public List<Block> getblockchainBlocks() {
        return blockchainBlocks;
    }

    public Block getLatestBlock() {
        return blockchainBlocks.get(blockchainBlocks.size() - 1);
    }

    public int getDifficulty(){
        return this.difficulty;
    }

    public Blockchain(int consensus, int difficulty, List<Validator> validators) {
        this.difficulty = difficulty;
        this.consensus = consensus;
        this.validators = validators;

        Block genesisBlock = new Block(0, "0", new ArrayList<>()); // Create genesisBlock
        genesisBlock.mineBlock(this, consensus, difficulty);; // Mine the genesis block during initialization
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
        newBlock.mineBlock(this, consensus, difficulty);
        if(BlockValidator.isValidBlock(newBlock, difficulty, blockchainBlocks.get(blockchainBlocks.size() - 1), consensus)){
            blockchainBlocks.add(newBlock);
        }else{
            System.out.println("Block Validation Fail");
        }
    }

    public List<Validator> getValidators() {
        return validators;
    }

    public void addValidators(Validator validator) {
        validators.add(validator);
    }

    // Method to select validators based on their stakes for forging new blocks
    public List<Validator> selectValidatorsForForging() {
        List<Validator> selectedValidators = new ArrayList<>();

        // Calculate total stake across all validators
        float totalStake = 0;
        for (Validator validator : validators) {
            totalStake += validator.getStake();
        }

        // Randomly select validators weighted by their stakes
        Random random = new Random();
        while (selectedValidators.size() < 2) {
            float randomNumber = random.nextFloat() * totalStake;
            float cumulativeWeight = 0;
            for (Validator validator : validators) {
                cumulativeWeight += validator.getStake();
                if (cumulativeWeight >= randomNumber) {
                    selectedValidators.add(validator);
                    break;
                }
            }
        }

        return selectedValidators;
    }
}

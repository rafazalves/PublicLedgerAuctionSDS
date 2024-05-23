package org.blockchain.consensus;

import org.blockchain.Block;
import org.blockchain.Blockchain;
import org.blockchain.Wallet;
import org.blockchain.utils.Utils;
import org.blockchain.validators.BlockValidator;

import java.util.List;
import java.util.Random;

import static org.blockchain.utils.Utils.applySha256;

public class ProofOfStake {
    public static void mineBlock(Block block, Blockchain blockchain) {
        // Select validators for forging the block
        List<Wallet> selectedValidators = blockchain.selectValidatorsForForging();

        // Implement PoS mining algorithm to select a validator from the list
        Wallet chosenValidator = selectValidatorForForging(selectedValidators);

        while (!BlockValidator.isValidPoS(block, chosenValidator, blockchain)) {
            block.setHash(calculateHashPoS(block, chosenValidator)); // Recalculate the hash based on the chosen validator
        }

        System.out.println("The validator chosen has a stake of " + chosenValidator.getStake());

        block.addValidator(chosenValidator);
    }

    // Method to select a validator from the list of selected validators for forging
    private static Wallet selectValidatorForForging(List<Wallet> selectedValidators) {
        // Check if the list of selected validators is not empty
        if (selectedValidators.isEmpty()) {
            throw new IllegalArgumentException("No validators selected for forging.");
        }

        // Generate a random index within the range of the list size
        Random random = new Random();
        int randomIndex = random.nextInt(selectedValidators.size());

        // Return the validator at the randomly chosen index
        return selectedValidators.get(randomIndex);
    }

    // Method to calculate the hash of the block based on the chosen validator (PoS)
    private static String calculateHashPoS(Block block, Wallet validator) {
        // Construct the data to be hashed based on the chosen validator
        String dataToHash = Integer.toString(block.getId()) + Long.toString(block.getblockTimestamp()) +
                block.getPreviousHash() + Integer.toString(block.getNonce()) +
                block.getMerkleRoot() + Utils.getStringFromKey(validator.getPublicKey());

        // Calculate and return the hash
        return applySha256(dataToHash);
    }

}
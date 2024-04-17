package org.blockchain.validators;

import java.util.List;

import org.blockchain.Block;
import org.blockchain.Blockchain;
import org.blockchain.consensus.ProofOfWork;

public class BlockChainValidator {
       public static boolean isChainValid(Blockchain blockchain, int difficulty) {
        Block currentBlock;
        Block previousBlock;
        List<Block> blockchainBlocks = blockchain.getblockchainBlocks();

        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for (int i = 1; i < blockchainBlocks.size(); i++) {
            currentBlock = blockchainBlocks.get(i);
            previousBlock = blockchainBlocks.get(i - 1);
            if (!currentBlock.getHash().equals(ProofOfWork.calculateHashPoW(currentBlock))) { //VER ISTO, NAO PODE SER currentBlock.calculateHash() PORQUE SENAO VAI SER DIFERENTE
                System.out.println("HASH É DIFERENTE");
                return false;
            }
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                System.out.println("HASH ANTERIOR É DIFERENTE");
                return false;
            }
            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                System.out.println("DIFICULDADE É DIFERENTE");
                return false;
            }
        }
        return true;
    }
}

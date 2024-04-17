package org.blockchain.validators;

import java.util.Date;

import org.blockchain.transaction.Transaction;
import org.blockchain.utils.Utils;

public class TransactionValidator {

    public static boolean isValidTransaction(Transaction transaction) {

        if(!verifyTransactionFormat(transaction)) return false;
        if(!verifyTransactionSignature(transaction)) return false;

        return true;
    }

    public static boolean verifyTransactionFormat(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
    
        // Check if sender, recipient, and hash are not null
        if (transaction.getSender() == null || transaction.getRecipient() == null || transaction.getHash() == null || transaction.getHash().isEmpty()) {
            return false;
        }

        // Check if amount and timestamp are positive, and if timestamp is before the current time
        if (transaction.getAmount() < 0 || transaction.getTransactionTimestamp() < 0  || transaction.getTransactionTimestamp() > new Date().getTime()) {
            return false;
        }
        
        return true;
    }

    public static boolean verifyTransactionSignature(Transaction transaction) {
        //Verifies signature to ensure that data wasn't violated
        String data = Utils.getStringFromKey(transaction.getSender()) + Utils.getStringFromKey(transaction.getRecipient()) + transaction.getAmount();
        if(!Utils.verifyString(transaction.getSender(), data, transaction.getSignature())){
            return false;
        }

        return true;
    }
}
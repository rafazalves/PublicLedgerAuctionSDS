package org.Kademlia.proof;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ProofOfWork {

    // requirements for the challeger
    // larger numbers means harder
    private static final int PoW_Difficulty = 5;


    // para proteger do ataque sybils -> usar id do node
    public static int mineChallenge(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest algorithm = new SHA3.Digest256();

        byte[] hash;
        int nonce = 0;

        do {
            // Concatena o nonce com os dados
            byte[] combinedData = concatenateByteArrays(data, intToBytes(nonce));

            // Calcula o hash SHA3-256 dos dados combinados
            hash = algorithm.digest(combinedData);

            // Verifica se o hash satisfaz os requisitos do desafio
            if (startsWithNZeroBytes(hash, PoW_Difficulty)) {
                return nonce;
            }

            // Incrementa o nonce para tentar novamente
            nonce++;

        } while (true);
    }

    // First Array a and next Array b
    private static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    // Converte um inteiro para um array de bytes (big endian)
    public static byte[] intToBytes(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    // Verifica se os primeiros n bytes de um array são zeros
    private static boolean startsWithNZeroBytes(byte[] array, int n) {
        int tempN = 0;
        for (byte Byte : array){


           if (Byte==0) tempN += 8; //se o byte for 0 quer dizer que todos os bits são tb 0;     1 byte = 8 bits
           else {
               int countZerosInTheLeft = 0; // até encontrar um bit 1
               for (int i=7; i>=0; i--){
                   if((Byte & (1 << i)) == 0) // vai bit a bit, desde o bit mais significativo a ver se são 0 ou não.
                       countZerosInTheLeft ++;
                   else break; // quando encontra o primeiro bit 1 para de procurar
               }

               tempN += countZerosInTheLeft; // Adiciona os bit que encontramos a contagem
               break; // porque foi encontrado um bit 1, e os bits 0 mais significativos (a esquerda) ja foram adicionados
           }
        }

        return tempN == n;
    }
}

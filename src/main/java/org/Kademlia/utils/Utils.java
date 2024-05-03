package org.Kademlia.utils;

import java.math.BigInteger;

public class Utils {

    // Função usada para converter os NodeIDs que são do tipo Byte[] (para melhor aramzenamento)
    // converter para bigInteger (para realizar operaçoes aritmetricas ou comparaçoes com o valor)
    public static BigInteger byteToBigInteger(byte[] bytetoConvert) {
        return new BigInteger(1, bytetoConvert);
    }
}

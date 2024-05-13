package org.Kademlia.utils;

import java.math.BigInteger;

public class Utils {
    public static final int K = 20;
    public static final int RPC_CALL_TIMEOUT = 10000;
    public static final int ID_LENGTH = 256;
    // Função usada para converter os NodeIDs que são do tipo Byte[] (para melhor aramzenamento)
    // converter para bigInteger (para realizar operaçoes aritmetricas ou comparaçoes com o valor)
    public static BigInteger byteToBigInteger(byte[] bytetoConvert) {
        return new BigInteger(1, bytetoConvert);
    }
}

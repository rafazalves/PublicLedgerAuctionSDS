package org.kademlia.proof;

import org.junit.jupiter.api.Test;

import static org.Kademlia.proof.ProofOfWork.intToBytes;
import static org.junit.jupiter.api.Assertions.*;

class ProofOfWorkTest {

    @Test
    public void testIntToBytes() {
        // Valor de entrada
        int value = 1;

        // Resultado esperado
        byte[] expectedBytes = {0, 0, 0, 1};

        // Chama a função intToBytes
        byte[] resultBytes = intToBytes(value);

        // Verifica se o resultado da função é o esperado
        assertArrayEquals(expectedBytes, resultBytes);
    }

}
package org.example;

import org.Kademlia.Node;

import java.math.BigInteger;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        // long timestamp = Instant.now().getEpochSecond();
        Node node3 = new Node(80,12345);
        System.out.println(node3.toString());
        BigInteger A, B;
        A = new BigInteger("2");
        B = new BigInteger("30");
        System.out.println("1:"+A.toString(2));
        System.out.println("2:"+B.toString(2));
        System.out.println("3:"+A.xor(B).toString(2));
        System.out.println("4:"+A.xor(B));

    }
}
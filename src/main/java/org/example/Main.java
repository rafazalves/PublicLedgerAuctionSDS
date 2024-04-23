package org.example;

import org.Kademlia.Node;

import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        long timestamp = Instant.now().getEpochSecond();
        Node node3 = new Node(2, "value");
    }
}
package org.example;

import org.Kademlia.Node;

import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        long timestamp = Instant.now().getEpochSecond();
        Node node = new Node(1, 2, timestamp, "value");
        Node node2 = new Node(2, 2, "value");

    }
}
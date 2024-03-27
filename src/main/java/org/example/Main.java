package org.example;

import org.Kademlia.Node;

import java.sql.SQLOutput;
import java.sql.Timestamp;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Node node = new Node(1, 2, String.valueOf(timestamp), "value");
    }
}
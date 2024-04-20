package org.Kademlia;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Node {
    private final int nodeId;
    private final int nodePublicPort;

    private long nodeTimestamp;

    private final String nodeValue;

    private PrivateKey privKey;
    private PublicKey pubKey;

    public Node(int nodeId, int nodePublicPort, long nodeTimestamp, String nodeValue) {
        this.nodeId = nodeId;
        this.nodePublicPort = nodePublicPort;
        this.nodeTimestamp = nodeTimestamp;
        this.nodeValue = nodeValue;
        generateKeys();
        getTimestamp();
    }

    public Node(int nodeId, int nodePublicPort, String nodeValue) {
        this.nodeId = nodeId;
        this.nodePublicPort = nodePublicPort;
        this.nodeValue = nodeValue;
        generateKeys();
        generateTimestamp();
        getTimestamp();
    }

    public void generateKeys() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair pair = keyGen.generateKeyPair();
            this.privKey = pair.getPrivate();
            this.pubKey = pair.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void generateTimestamp(){
        long Unixtimestamp = Instant.now().getEpochSecond();
        this.nodeTimestamp = Unixtimestamp;
    }

    public void getTimestamp() { // TimeStamp is Unix Time

        System.out.println("Current Unix Timestamp in node: " + nodeTimestamp);

        // Convertendo Unix timestamp para um objeto Instant
        Instant instant = Instant.ofEpochSecond(nodeTimestamp);

        // Convertendo Instant para LocalDateTime para obter detalhes de dia, hora, minuto e segundo
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // Formatando a data e hora conforme necessário
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        // Imprimindo o timestamp formatado
        System.out.println("Timestamp convertido: " + formattedDateTime);

    }

    public int getNodeId() {
        return nodeId;
    }

    public int getNodePublicPort() {
        return nodePublicPort;
    }

    public long getNodeTimestamp() {
        return nodeTimestamp;
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public PrivateKey getPrivKey() {
        return privKey;
    }

    public PublicKey getPubKey() {
        return pubKey;
    }
}

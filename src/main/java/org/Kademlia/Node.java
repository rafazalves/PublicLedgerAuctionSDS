package org.Kademlia;

import java.sql.Timestamp;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;

public class Node {
    private final int nodeId;
    private final int nodePublicPort;

    private String nodeTimestamp;

    private final String nodeValue;

    private PrivateKey privKey;
    private PublicKey pubKey;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public Node(int nodeId, int nodePublicPort, String nodeTimestamp, String nodeValue) {
        this.nodeId = nodeId;
        this.nodePublicPort = nodePublicPort;
        this.nodeTimestamp = nodeTimestamp;
        this.nodeValue = nodeValue;
        generateKeys();
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

    public void getTimestamp() { // converter o timestamp que vem da outra classe para só hora:minuto:segundo
        Timestamp timestamp = Timestamp.valueOf(nodeTimestamp);
        System.out.println("2:"+sdf.format(timestamp));
        this.nodeTimestamp = sdf.format(timestamp);
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getNodePublicPort() {
        return nodePublicPort;
    }

    public String getNodeTimestamp() {
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

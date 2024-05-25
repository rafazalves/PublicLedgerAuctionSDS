package org.Kademlia;

import java.math.BigInteger;
import java.security.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.Kademlia.RoutingTable.Bucket;
import org.bouncycastle.*;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.Kademlia.utils.Utils;
import org.gRPC.clientManager;

import static org.Kademlia.proof.ProofOfWork.mineChallenge;
import org.Kademlia.RoutingTable.RoutingTable;


public class Node {
        private final byte[] nodeId; //KEY
        private final int nodePublicPort;
        private final int nodeIP;

        private long nodeTimestamp;

        private PrivateKey privKey;
        private PublicKey pubKey;
        private int nonce;

    public Node(int nodePublicPort, int nodeIP) {
        this.nodePublicPort = nodePublicPort;
        this.nodeIP = nodeIP;
        generateKeys();
        generateTimestamp();
        this.nodeId = generateId(this.pubKey);
        try {
            nonce = mineChallenge(nodeId);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public void printNodeID_Hash(){
        StringBuilder texto = new StringBuilder();
        System.out.print("NodeID: ");
        for (byte b : this.nodeId) {
            texto.append(String.format("%02X", 0xFF & b));
        }
        System.out.println(texto.toString());
    }


    public String printNodeId_Hash(){
        StringBuilder texto = new StringBuilder();
        for (byte b : this.nodeId) {
            texto.append(String.format("%02X", 0xFF & b));
        }
        return  texto.toString();
    }

    public byte[] generateId(PublicKey pk) {
        assert pk != null : "Public key is null in GenerateID";

        try {
            MessageDigest algorithm = new SHA3.Digest256();
            byte[] hash = algorithm.digest(pk.getEncoded());

            return hash;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public String printTimeStamp() { // TimeStamp is Unix Time

        // Convertendo Unix timestamp para um objeto Instant
        Instant instant = Instant.ofEpochSecond(nodeTimestamp);

        // Convertendo Instant para LocalDateTime para obter detalhes de dia, hora, minuto e segundo
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // Formatando a data e hora conforme necessário
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        return nodeTimestamp + "(unix)" + " = " + formattedDateTime + "(standard)";

    }

    public int nodeDistance(byte[] node1, byte[] node2) {
        BigInteger xorResult = Utils.byteToBigInteger(node1).xor(Utils.byteToBigInteger(node2));
        return xorResult.intValue();
    }

    public int getNodePublicPort() {
        return nodePublicPort;
    }

    public long getNodeTimestamp() {
        return nodeTimestamp;
    }

    public byte[] getNodeId() {
        return nodeId;
    }
    public String getNodeIdString() {
        return printNodeId_Hash();
    }

    public PrivateKey getPrivKey() {
        return privKey;
    }

    public PublicKey getPubKey() {
        return pubKey;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public int getNodeIP() {
        return nodeIP;
    }


    @Override
    public String toString() {
        String nodeid = printNodeId_Hash();
        String printTimeStamp = printTimeStamp();

        return "Node{" + "\n" +
                "   NodeId = " + nodeid + "\n" +
                "   nodeIP = " + nodeIP + "\n" +
                "   nonce = " + nonce + "\n" +
                "   nodePublicPort = " + nodePublicPort + "\n" +
                "   nodeTimestamp = " + printTimeStamp + '\'' + "\n" +
                '}';
    }


}

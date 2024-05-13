package org.Kademlia;

import java.math.BigInteger;
import java.security.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.bouncycastle.*;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.Kademlia.utils.Utils;

import static org.Kademlia.proof.ProofOfWork.mineChallenge;


public class Node {
    private final byte[] nodeId;
    private final int nodePublicPort;

    private long nodeTimestamp;

    private final String nodeValue;

    private PrivateKey privKey;
    private PublicKey pubKey;

    private final int nonce;

    public Node(int nodePublicPort, String nodeValue) {
        this.nodePublicPort = nodePublicPort;
        this.nodeValue = nodeValue;
        generateKeys();
        generateTimestamp();
        printTimeStamp();
        this.nodeId = generateId(this.pubKey);
        printNodeID_Hash();
        try {
            nonce = mineChallenge(nodeId);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

//        BigInteger resBigInteger = Utils.byteToBigInteger(nodeId);
//        System.out.println(resBigInteger);

    }

    public void printNodeID_Hash(){
        StringBuilder texto = new StringBuilder();
        System.out.print("NodeID: ");
        for (byte b : this.nodeId) {
            texto.append(String.format("%02X", 0xFF & b));
        }
        System.out.println(texto.toString());
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

    public void printTimeStamp() { // TimeStamp is Unix Time

        System.out.print("Current Timestamp in node: " + nodeTimestamp + "(unix)");

        // Convertendo Unix timestamp para um objeto Instant
        Instant instant = Instant.ofEpochSecond(nodeTimestamp);

        // Convertendo Instant para LocalDateTime para obter detalhes de dia, hora, minuto e segundo
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // Formatando a data e hora conforme necessário
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        // Imprimindo o timestamp formatado
        System.out.println(" -> " + formattedDateTime + "(standard)");

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

    public String getNodeValue() {
        return nodeValue;
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
}

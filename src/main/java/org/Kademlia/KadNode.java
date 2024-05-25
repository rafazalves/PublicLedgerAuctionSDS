package org.Kademlia;

import org.Kademlia.RoutingTable.Bucket;
import org.Kademlia.RoutingTable.Contactos;
import org.Kademlia.RoutingTable.RoutingTable;
import org.Kademlia.Storage.StorageManager;
import org.gRPC.clientManager;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.Kademlia.proof.ProofOfWork.mineChallenge;

public class KadNode {
    private Node node;
    private StorageManager storageManager;
    private RoutingTable routingTable;

    private org.gRPC.clientManager clientManager;

    public org.gRPC.clientManager getClientManager() {
        return clientManager;
    }

    public void setClientManager(org.gRPC.clientManager clientManager) {
        this.clientManager = clientManager;
    }

    public KadNode(Node node) {
        this.node = node;
        routingTable = new RoutingTable(node);
        routingTable.add(node);
        storageManager = new StorageManager();
    }


    public KadNode merge(Node node){
        KadNode aux;
        if (node.getNodeId().equals(this.node.getNodeId())){
            return this;
        }
        return null;
    }

    private void pingHeadOfKBucket(int kBucket) {
        List<Node> nodes = this.routingTable.ListNodesIndex(kBucket);

        Node node = nodes.get(0);
        if (nodes != null) {
            this.clientManager.doPing(this , merge(node));
        }
    }

    public void handleSeenNode(Node seen) throws NoSuchAlgorithmException {
        // procurar  bucket para o node seen V
        // listar todos os contactos desse bucket e ver se o seen já está lá V
        // se estiver retira do bucket e volta a adicionar no fim
        // se nao estiver e bucket n estiver cheio adiciona no final (bucket com espaço)
        // se nao estiver e o bucket estiver cheio fazer ping à head do bucket

        int bucket_index = routingTable.getBucketIndex(seen.getNodeId());

        List<Node> nodes = routingTable.ListNodesIndex(bucket_index);
        boolean present = false;
        for (Node n : nodes){
            if (seen.getNodeId().equals(n.getNodeId())){
                present = true;
                break;
            }
        }

        if (present){
            //node is present
            // colocar node no final da bucket e atualizar o lastseen do contacto

            Bucket bucket = routingTable.getSpecificBucket(bucket_index);
            bucket.removeDoContactos(seen);
            bucket.add(seen);

        }else {
            // node is not in the bucket

            //verificar que o node fez o challeger - prevent sybil
            // se nao fez refazer
            if(seen.getNonce() == 0){
                int nonce = mineChallenge(seen.getNodeId());
                seen.setNonce(nonce);
            }

            Bucket bucket = routingTable.getSpecificBucket(bucket_index);
            if(bucket.isFull()){
                pingHeadOfKBucket(bucket_index);
            }else {
                bucket.add(seen);
            }
        }
    }

    public void printErrorHandle(Node seen) throws NoSuchAlgorithmException{
        int bucket_index = routingTable.getBucketIndex(seen.getNodeId());
        Bucket bucket = routingTable.getSpecificBucket(bucket_index);
        bucket.penaltyContacto(seen);

    }

    public Node getNode() {
        return node;
    }

    public StorageManager getStorageManager(){
        return storageManager;
    }
}

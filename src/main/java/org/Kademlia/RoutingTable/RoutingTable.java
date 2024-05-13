package org.Kademlia.RoutingTable;
import org.Kademlia.*;
import org.Kademlia.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class RoutingTable {
    private final Node Node;
    private transient Bucket[] buckets;;

    public RoutingTable(Node node) {
        this.Node = node;
        initBuckets();
    }

    private void initBuckets() {
        this.buckets = new Bucket[Utils.ID_LENGTH];
        for (int i = 0; i < buckets.length; i++) {
            this.buckets[i] = new Bucket(Utils.K);
        }
    }

    private synchronized final void addContactos(Contactos c) {
        int index = getBucketIndex(c.getN().getNodeId());
        this.buckets[index].addContactos(c);
    }

    private final int getBucketIndex(byte[] nid) {
        int Index = this.Node.nodeDistance(this.Node.getNodeId(), nid) - 1;
        // If i use my own nodeID the index will return -1, this if handles that case.
        if (Index < 0)
            Index = 0;
        else if (Index > Utils.ID_LENGTH - 1) {
            Index = Utils.ID_LENGTH;
        }
        return Index;
    }

    public synchronized final void addNode(Node n) { // Adds a node to the routing table based on how far it is from the LocalNode.
        int index = getBucketIndex(n.getNodeId());
        this.buckets[index].addNode(n);
    }

    public synchronized final List<Node> findClosest (Node n, int num_nodes){
        TreeSet<Node> sorted ;
        return null;
    }

    public synchronized final List<Node> ListNodes() {
        List<Node> nodes = new ArrayList<>();

        for (Bucket b : this.buckets) {
            for (Contactos c : b.getContactos()) {
                nodes.add(c.getN());
            }
        }
        return nodes;
    }

    @Override
    public String toString() {
        return "RoutingTable{" +
                "Node=" + Node +
                ", buckets=" + Arrays.toString(buckets) +
                '}';
    }
}

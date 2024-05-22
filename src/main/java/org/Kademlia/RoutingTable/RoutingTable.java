package org.Kademlia.RoutingTable;
import jdk.jshell.execution.Util;
import org.Kademlia.*;
import org.Kademlia.utils.Utils;

import java.math.BigInteger;
import java.util.*;

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

    private synchronized final void add(Contactos c) {
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

    public synchronized final void add(Node n) { // Adds a node to the routing table based on how far it is from the LocalNode.
        int index = getBucketIndex(n.getNodeId());
        this.buckets[index].addNode(n);
    }

    public synchronized final List<Node> findClosest(byte[] nid, int num_nodes){
        PriorityQueue<Node> sortedNodes = new PriorityQueue<>(num_nodes, new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) { // compare nodes based on distance to n
                BigInteger nID0 = Utils.byteToBigInteger(n1.getNodeId());
                BigInteger nID1 = Utils.byteToBigInteger(n2.getNodeId());
                BigInteger dist0 = Utils.byteToBigInteger(nid).xor(nID0);
                BigInteger dist1 = Utils.byteToBigInteger(nid).xor(nID1);
                int d0 = dist0.intValue();
                int d1 = dist1.intValue();
                return Integer.compare(d0, d1);
            }
        });
        for (Bucket b : this.buckets) {
            for (Contactos c : b.getContactos()) {
                sortedNodes.add(c.getN());
                if (sortedNodes.size() > num_nodes) {
                    sortedNodes.poll(); // Remove the farthest node if we have more than num_nodes nodes
                }
            }
        }

        List<Node> closest = new ArrayList<>(num_nodes);
        while (!sortedNodes.isEmpty()) {
            closest.add(sortedNodes.poll());
        }
        // The closest list is in reverse order (farthest to closest), reverse it to return closest to farthest
        Collections.reverse(closest);
        return closest;
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

    //Adds a penalty to a node. This is used when a node fails to respond to a RPC.
    public synchronized void penaltyContacto(Node n) {
        Bucket bucket = this.buckets[this.getBucketIndex(n.getNodeId())];
        bucket.penaltyContacto(n);
    }

    @Override
    public String toString() {
        return "RoutingTable{" +
                "Node=" + Node +
                ", buckets=" + Arrays.toString(buckets) +
                '}';
    }
}

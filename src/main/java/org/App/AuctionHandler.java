package org.App;
import org.Kademlia.KadNode;
import org.Kademlia.Storage.StorageManager;
import org.Kademlia.utils.Utils;
import org.blockchain.Blockchain;

import java.math.BigInteger;
import java.util.Map;

public class AuctionHandler {
    private Auction auction;
    public KadNode node;
    private StorageManager storageManager;
    private Map<KadNode, Auction> bids;

    public AuctionHandler(Auction auction, KadNode node){
        this.auction = auction;
        this.node = node;
        long timestamp = System.currentTimeMillis() / 1000L;
        storageManager = node.getStorageManager();
        storageManager.put(Utils.byteToBigInteger(node.getNode().getNodeId()), BigInteger.valueOf(auction.getAuctionID()), timestamp);
    }

    public AuctionHandler(){
    }

    public void storeBid(long timestamp){
        storageManager.put(Utils.byteToBigInteger(node.getNode().getNodeId()), BigInteger.valueOf(auction.getAuctionID()), timestamp);
    }

    public Auction getAuction(){
        return this.auction;
    }

    public StorageManager getStorageManager(){
        return this.storageManager;
    }
    // fazer funçoes de prpagação
    // quando cria leilao ou faz uma licitação mandar isso para os outros nodes da rede
}

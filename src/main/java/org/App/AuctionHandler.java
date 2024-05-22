package org.App;
import org.Kademlia.Node;
import org.Kademlia.Storage.StorageManager;
import org.blockchain.Blockchain;

import java.math.BigInteger;
import java.util.Map;

public class AuctionHandler {
    private Auction auction;
    public Node node;
    private StorageManager storageManager;
    private Map<Node, Auction> bids;

    public AuctionHandler(Auction auction, Node node){
        this.auction = auction;
        this.node = node;
        long timestamp = System.currentTimeMillis() / 1000L;
        storageManager = new StorageManager();
        storageManager.put(BigInteger.valueOf(auction.getAuctionID()), BigInteger.valueOf(auction.getAuctionID()), timestamp);
    }

    public void storeBid(long timestamp){
        storageManager.put(BigInteger.valueOf(auction.getAuctionID()), BigInteger.valueOf(auction.getAuctionID()), timestamp);
    }
}

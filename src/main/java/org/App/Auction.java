package org.App;

import javax.swing.*;
import java.security.PublicKey;
import java.security.Timestamp;
import java.util.Date;

public class Auction {
private final int auctionID;
    private final String auctionName;
    private final PublicKey auctionOwner;
    private PublicKey auctionWinner;
    private int auctionStatus;
    private final long auctionStartDate;
    private final long auctionEndDate;
    private final int auctionStartPrice;
    private float auctionCurrentPrice;
    private float auctionWinnerPrice;
    private final float auctionMaxPrice;

    public Auction(int auctionID, String auctionName, PublicKey auctionOwner, PublicKey auctionWinner, int auctionStatus, long auctionStartDate,
                   long auctionEndDate, int auctionStartPrice, int auctionCurrentPrice, int auctionWinnerPrice, float auctionMaxPrice) {
        this.auctionID = auctionID;
        this.auctionName = auctionName;
        this.auctionOwner = auctionOwner;
        this.auctionWinner = auctionWinner;
        this.auctionStatus = auctionStatus;
        this.auctionStartDate = auctionStartDate;
        this.auctionEndDate = auctionEndDate;
        this.auctionStartPrice = auctionStartPrice;
        this.auctionCurrentPrice = auctionCurrentPrice;
        this.auctionWinnerPrice = auctionWinnerPrice;
        this.auctionMaxPrice = auctionMaxPrice;
    }

    public Auction(int auctionID, String auctionName, PublicKey auctionOwner, int auctionStartPrice ,float auctionMaxPrice){
        this.auctionID = 0;
        this.auctionName = auctionName;
        this.auctionOwner = auctionOwner;
        this.auctionMaxPrice = auctionMaxPrice;
        this.auctionStartDate = new Date().getTime();
        this.auctionStartPrice = auctionStartPrice;
        this.auctionCurrentPrice = 0;
        this.auctionStatus = 0;
        this.auctionWinnerPrice = 0;
        this.auctionWinner = null;
        Date currentDate = new Date(auctionStartDate);
        currentDate.setMonth(currentDate.getMonth() + 1);
        this.auctionEndDate = currentDate.getTime();

    }

    public int getAuctionID() {
        return auctionID;
    }

    public String getAuctionName() {
        return auctionName;
    }

    public PublicKey getAuctionOwner() {
        return auctionOwner;
    }

    public PublicKey getAuctionWinner() {
        return auctionWinner;
    }

    public int getAuctionStatus() {
        return auctionStatus;
    }

    public long getAuctionStartDate() {
        return auctionStartDate;
    }

    public long getAuctionEndDate() {
        return auctionEndDate;
    }

    public float getAuctionStartPrice() {
        return auctionStartPrice;
    }

    public float getAuctionCurrentPrice() {
        return auctionCurrentPrice;
    }

    public float getAuctionWinnerPrice() {
        return auctionWinnerPrice;
    }

    public float getAuctionMaxPrice() {
        return auctionMaxPrice;
    }

    public void setAuctionWinner(PublicKey auctionWinner) {
        this.auctionWinner = auctionWinner;
    }

    public void setAuctionStatus(int auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public void setAuctionCurrentPrice(float auctionCurrentPrice) {
        this.auctionCurrentPrice = auctionCurrentPrice;
    }

    public void setAuctionWinnerPrice(float auctionWinnerPrice) {
        this.auctionWinnerPrice = auctionWinnerPrice;
    }
}

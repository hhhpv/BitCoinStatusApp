package com.example.ilovezappos;

public class RowHelper {
    private String bids,asks;
    public RowHelper(String bids,String asks){
        this.asks=asks;
        this.bids=bids;
    }
    public String getBids() {
        return bids;
    }

    public void setBids(String bids) {
        this.bids = bids;
    }

    public String getAsks() {
        return asks;
    }

    public void setAsks(String asks) {
        this.asks = asks;
    }
}

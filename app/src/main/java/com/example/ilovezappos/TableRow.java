package com.example.ilovezappos;

import android.inputmethodservice.Keyboard;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TableRow {
    private String bids;
    private String asks;
    public TableRow(String bids,String asks) {
        this.bids=bids;
        this.asks=asks;
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

    public void setAsks(String timestamp) {
        this.asks = asks;
    }
}


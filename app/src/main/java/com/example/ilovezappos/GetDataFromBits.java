package com.example.ilovezappos;

import com.google.gson.annotations.SerializedName;

public class GetDataFromBits {
    @SerializedName("date")
    private String date;
    @SerializedName("tid")
    private String tid;
    @SerializedName("price")
    private String price;
    @SerializedName("type")
    private String type;
    @SerializedName("amount")
    private String amount;

    public GetDataFromBits(String date, String tid, String price, String type, String amount) {
        this.date = date;
        this.tid = tid;
        this.price = price;
        this.type = type;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price= price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}


package com.example.ilovezappos;

import com.google.gson.JsonObject;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.util.List;

public interface GetDataService {
    @Headers("Content-Type: application/json")
    @GET("btcusd/")
    Call<List<GetDataFromBits>> getLineGraph();

    @Headers("Content-Type: application/json")
    @GET("btcusd/")
    Call<JsonObject> getOrderBook();

    @Headers("Content-Type: application/json")
    @GET("btcusd/")
    Call<JsonObject> getAlert();
}

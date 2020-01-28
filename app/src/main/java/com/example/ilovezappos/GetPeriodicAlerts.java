package com.example.ilovezappos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetPeriodicAlerts {
    private static Retrofit retrofit=null;
    private static final String BASE_URL = "https://www.bitstamp.net/api/v2/ticker_hour/";
    public static Retrofit getPeriodicAlerts() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

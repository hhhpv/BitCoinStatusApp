package com.example.ilovezappos;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;
    public session(Context ctx)
    {
        this.ctx=ctx;
        prefs=ctx.getSharedPreferences("myapp",Context.MODE_PRIVATE);
        editor=prefs.edit();
    }
    public void setPrice(float price){
        editor.putFloat("price",price);
        editor.commit();
    }
    public float getUserDetails(){
        float savedPrice=prefs.getFloat("price",0.0f);
        return savedPrice;
    }
}

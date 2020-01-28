package com.example.ilovezappos;

import android.content.Intent;
import android.os.Build;
import android.se.omapi.Session;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class EnterPrice extends AppCompatActivity {
    public Button btn;
    public EditText price;
    public session s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_price);
        btn=(Button)findViewById(R.id.save);
        price=(EditText)findViewById(R.id.price_field);
        s=new session(this);
        float present=s.getUserDetails();
        price.setText(String.valueOf(present));
        btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                s.setPrice(Float.parseFloat(price.getText().toString()));
                MyService.flag[0]=0;
                Intent in=new Intent(EnterPrice.this,MainActivity.class);
                startActivity(in);
            }
        });
    }
}

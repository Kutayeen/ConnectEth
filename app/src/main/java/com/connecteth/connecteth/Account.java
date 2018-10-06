package com.connecteth.connecteth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Account  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.account);
    }
}

package com.github.catvod.demo;

import android.app.Activity;
import android.os.Bundle;

import org.json.JSONObject;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {

            @Override
            public void run() {



            }
        }).
                start();
    }
}



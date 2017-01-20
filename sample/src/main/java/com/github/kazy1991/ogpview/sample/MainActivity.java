package com.github.kazy1991.ogpview.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.kazy1991.ogpview.OgpView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((OgpView) findViewById(R.id.ogp_view)).loadUrl("https://github.com/vanniktech/OnActivityResult");
    }
}

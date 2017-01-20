package com.github.kazy1991.ogpview.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.kazy1991.ogpview.OgpView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((OgpView) findViewById(R.id.ogp_view)).loadUrl("https://github.com/square/okhttp");
        ((OgpView) findViewById(R.id.ogp_view)).setOnClickListener((view, url) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }
}

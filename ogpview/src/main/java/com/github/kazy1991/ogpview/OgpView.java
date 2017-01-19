package com.github.kazy1991.ogpview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OgpView extends FrameLayout {
    public OgpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.ogp_view, this);
        ((SimpleDraweeView) findViewById(R.id.favicon)).setImageURI("http://ogp.me/logo.png");
        ((SimpleDraweeView) findViewById(R.id.og_image)).setImageURI("http://ogp.me/logo.png");
    }

    public void loadUrl(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    String html = response.body().string();
                    Document document = Jsoup.parse(html);
                    final String title = document.title();
                    final String ogTitle = document.select("meta[property=og:title]").attr("content");
                    final String ogImage = document.select("meta[property=og:image]").attr("content");
                    final String ogDescription = document.select("meta[property=og:description]").attr("content");
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) findViewById(R.id.site_title)).setText(title);
                            ((TextView) findViewById(R.id.og_title)).setText(ogTitle);
                            ((TextView) findViewById(R.id.og_description)).setText(ogDescription);
                            ((SimpleDraweeView) findViewById(R.id.favicon)).setImageURI(ogImage);
                            ((SimpleDraweeView) findViewById(R.id.og_image)).setImageURI(ogImage);
                        }
                    });
                } catch (IOException e) {
                    // todo: error handling
                }
            }
        });

    }
}

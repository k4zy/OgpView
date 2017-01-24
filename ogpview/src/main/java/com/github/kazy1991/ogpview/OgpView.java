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
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OgpView extends FrameLayout {

    private Call call;

    private static OkHttpClient client = new OkHttpClient().newBuilder()
            .readTimeout(15 * 1000, TimeUnit.MILLISECONDS)
            .writeTimeout(20 * 1000, TimeUnit.MILLISECONDS)
            .connectTimeout(20 * 1000, TimeUnit.MILLISECONDS)
            .build();

    public interface OnClickListener {
        void onClick(View view, String url);
    }

    private String url;

    public OgpView(Context context) {
        this(context, null);
    }

    public OgpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnClickListener(final OnClickListener onClickListener) {
        if (onClickListener == null) {
            return;
        }
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v, url);
            }
        });
    }

    private void init() {
        setVisibility(GONE);
        View.inflate(getContext(), R.layout.ogp_view, this);
    }

    public void loadUrl(final String url) {
        this.url = url;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        setVisibility(GONE);
                    }
                });
                // todo: error handling
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    String html = response.body().string();
                    Document document = Jsoup.parse(html);

                    // todo: fix condition
                    if (document.select("meta[property=og:site_name]") != null && document.select("meta[property=og:site_name]").attr("content").equals("")) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                setVisibility(GONE);
                            }
                        });
                        return;
                    }

                    final String title = document.select("meta[property=og:site_name]").attr("content");
                    final String ogTitle = document.select("meta[property=og:title]").attr("content");
                    final String ogImage = document.select("meta[property=og:image]").attr("content");
                    final String ogDescription = document.select("meta[property=og:description]").attr("content");
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) findViewById(R.id.site_title)).setText(title);
                            ((TextView) findViewById(R.id.og_title)).setText(ogTitle);
                            ((TextView) findViewById(R.id.og_description)).setText(ogDescription);
                            ((SimpleDraweeView) findViewById(R.id.favicon)).setImageURI("https://www.google.com/s2/favicons?domain=" + url);
                            ((SimpleDraweeView) findViewById(R.id.og_image)).setImageURI(ogImage);
                            setVisibility(VISIBLE);
                        }
                    });
                } catch (UnsupportedCharsetException | IOException e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            setVisibility(GONE);
                        }
                    });
                    // todo: error handling
                }
            }
        });

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (call != null) {
            call.cancel();
        }
    }
}

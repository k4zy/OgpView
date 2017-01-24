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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
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

    private static Map<String, OgpContent> cache = new HashMap<>();

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

        if (cache.containsKey(url)) {
            OgpContent content = cache.get(url);
            if (content == null) {
                setVisibility(GONE);
                return;
            }
            setupWithContent(content);
            return;
        }

        this.url = url;
        Request request = new Request.Builder()
                .cacheControl(new CacheControl.Builder()
                        .maxStale(365, TimeUnit.DAYS)
                        .build()
                )
                .url(url)
                .get()
                .build();

        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFail();
                // todo: error handling
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    String html = response.body().string();
                    OgpContent content = parseHtml(html);
                    cache.put(url, content);
                    if (content == null) {
                        onFail();
                        return;
                    }
                    setupWithContent(content);
                } catch (UnsupportedCharsetException | IOException e) {
                    cache.put(url, null);
                    onFail();
                    // todo: error handling
                }
            }
        });
    }

    private OgpContent parseHtml(String html) {
        Document document = Jsoup.parse(html);
        // todo: fix condition
        if (document.select("meta[property=og:site_name]") != null && document.select("meta[property=og:site_name]").attr("content").equals("")) {
            return null;
        }
        final String title = document.select("meta[property=og:site_name]").attr("content");
        final String ogTitle = document.select("meta[property=og:title]").attr("content");
        final String ogImage = document.select("meta[property=og:image]").attr("content");
        final String ogDescription = document.select("meta[property=og:description]").attr("content");
        final String faviconUrl = "https://www.google.com/s2/favicons?domain=" + url;
        return new OgpContent(title, ogTitle, ogImage, ogDescription, faviconUrl);
    }

    private void onFail() {
        post(new Runnable() {
            @Override
            public void run() {
                setVisibility(GONE);
            }
        });
    }

    private void setupWithContent(final OgpContent content) {
        post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.site_title)).setText(content.getTitle());
                ((TextView) findViewById(R.id.og_title)).setText(content.getOgTitle());
                ((TextView) findViewById(R.id.og_description)).setText(content.getOgDescription());
                ((SimpleDraweeView) findViewById(R.id.favicon)).setImageURI(content.getFaviconUrl());
                ((SimpleDraweeView) findViewById(R.id.og_image)).setImageURI(content.getOgImageUrl());
                setVisibility(VISIBLE);
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

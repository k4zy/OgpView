package com.github.kazy1991.ogpview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.drawee.view.SimpleDraweeView;

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
}

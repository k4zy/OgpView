package com.github.kazy1991.ogpview;


public class OgpContent {

    String title;
    String ogTitle;
    String ogImageUrl;
    String ogDescription;
    String faviconUrl;

    public OgpContent(String title, String ogTitle, String ogImageUrl, String ogDescription, String faviconUrl) {
        this.title = title;
        this.ogTitle = ogTitle;
        this.ogImageUrl = ogImageUrl;
        this.ogDescription = ogDescription;
        this.faviconUrl = faviconUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getOgTitle() {
        return ogTitle;
    }

    public String getOgImageUrl() {
        return ogImageUrl;
    }

    public String getOgDescription() {
        return ogDescription;
    }

    public String getFaviconUrl() {
        return faviconUrl;
    }
}

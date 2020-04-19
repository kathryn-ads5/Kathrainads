package com.kathryn.kathryn.Shop;

public class shopMenuModel {
    String title, url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "shopMenuModel{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

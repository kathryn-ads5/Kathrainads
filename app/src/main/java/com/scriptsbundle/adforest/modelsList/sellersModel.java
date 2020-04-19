package com.kathryn.kathryn.modelsList;

import org.json.JSONObject;

public class sellersModel {

    private String authour_name, author_img, author_rating, author_location;
    private int author_id;
    private JSONObject author_social;

    public String getAuthour_name() {
        return authour_name;
    }

    public void setAuthour_name(String authour_name) {
        this.authour_name = authour_name;
    }

    public String getAuthor_img() {
        return author_img;
    }

    public void setAuthor_img(String author_img) {
        this.author_img = author_img;
    }

    public String getAuthor_rating() {
        return author_rating;
    }

    public void setAuthor_rating(String author_rating) {
        this.author_rating = author_rating;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public JSONObject getAuthor_social() {
        return author_social;
    }

    public void setAuthor_social(JSONObject author_social) {
        this.author_social = author_social;
    }

    public String getAuthor_location() {
        return author_location;
    }

    public void setAuthor_location(String author_location) {
        this.author_location = author_location;
    }
}

package com.kathryn.kathryn.modelsList;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class myAdsModel {

    private String name;
    private String adId;
    private String image;
    private String price;

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    private String views;
    private ArrayList<String> spinerData;
    private ArrayList<String> spinerValue;
    private String editAd;
    private String rejectedAds;
    private String delAd;

    public String getRejectedAds() {
        return rejectedAds;
    }

    public void setRejectedAds(String rejectedAds) {
        this.rejectedAds = rejectedAds;
    }

    private String adType;
    private String adTypeText;
    private String adStatus;
    private String adStatusValue;

    public String getAdTypeText() {
        return adTypeText;
    }

    public void setAdTypeText(String adTypeText) {
        this.adTypeText = adTypeText;
    }

    public String getAdStatusValue() {
        return adStatusValue;
    }

    public void setAdStatusValue(String adStatusValue) {
        this.adStatusValue = adStatusValue;
    }


    public String getAdStatus() {
        return adStatus;
    }

    public void setAdStatus(String adStatus) {
        this.adStatus = adStatus;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getEditAd() {
        return editAd;
    }

    public void setEditAd(String editAd) {
        this.editAd = editAd;
    }

    public String getDelAd() {
        return delAd;
    }

    public void setDelAd(String delAd) {
        this.delAd = delAd;
    }

    public ArrayList<String> getSpinerData() {
        return spinerData;
    }

    public void setSpinerData(JSONArray spinerData) {
        ArrayList<String> listdata = new ArrayList<>();
        if (spinerData != null) {
            for (int i = 0; i < spinerData.length(); i++) {
                try {
                    listdata.add(spinerData.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        this.spinerData = listdata;
    }

    public ArrayList<String> getSpinerValue() {
        return spinerValue;
    }

    public void setSpinerValue(JSONArray spinerData) {

        ArrayList<String> listdata = new ArrayList<>();
        if (spinerData != null) {
            for (int i = 0; i < spinerData.length(); i++) {
                try {
                    listdata.add(spinerData.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        this.spinerValue = listdata;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.kathryn.kathryn.modelsList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PackagesModel {

    private String validaty;
    private String freeAds;
    private String allowBidding;
    private String numOfImages;
    private String videoUrl;
    private String allowTags;
    private String allowCats;
    private String featureAds;
    private String btnText;
    private String btnTag;
    private String price;
    private String regularPrice;
    private String saleText;
    private String planType;
    private String readMoreText;
    private String ListTitleText;
    private ArrayList<String> spinnerData;
    private ArrayList<String> spinnerValue;
    private JSONArray allowCatsValue;
    private String packagesPrice;
    private String bumupAds;
    private JSONObject jsonObject;


    public String getReadMoreText() {
        return readMoreText;
    }

    public void setReadMoreText(String readMoreText) {
        this.readMoreText = readMoreText;
    }

    public void setSpinnerData(ArrayList<String> spinnerData) {
        this.spinnerData = spinnerData;
    }

    public void setSpinnerValue(ArrayList<String> spinnerValue) {
        this.spinnerValue = spinnerValue;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getBumupAds() {
        return bumupAds;
    }

    public void setBumupAds(String bumupAds) {
        this.bumupAds = bumupAds;
    }

    public String getFeatureAds() {
        return featureAds;
    }

    public void setFeatureAds(String featureAds) {
        this.featureAds = featureAds;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getBtnTag() {
        return btnTag;
    }

    public void setBtnTag(String btnTag) {
        this.btnTag = btnTag;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }


    public String getValidaty() {
        return validaty;
    }

    public void setValidaty(String validaty) {
        this.validaty = validaty;
    }

    public String getFreeAds() {
        return freeAds;
    }

    public void setFreeAds(String freeAds) {
        this.freeAds = freeAds;
    }

    public String getAllowBidding() {
        return allowBidding;
    }

    public void setAllowBidding(String allowBidding) {
        this.allowBidding = allowBidding;
    }

    public ArrayList<String> getSpinnerData() {
        return this.spinnerData;
    }

    public void setSpinnerData(JSONArray spinnerData) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (spinnerData != null) {
            for (int i = 0; i < spinnerData.length(); i++) {
                try {
                    arrayList.add(spinnerData.getJSONObject(i).getString("value"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        this.spinnerData = arrayList;
    }

    public ArrayList<String> getSpinnerValue() {
        return this.spinnerValue;
    }

    public void setSpinnerValue(JSONArray spinnerData) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (spinnerData != null) {
            for (int i = 0; i < spinnerData.length(); i++) {
                try {
                    arrayList.add(spinnerData.getJSONObject(i).getString("key"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        this.spinnerValue = arrayList;
    }

    public String getPackagesPrice() {
        return this.packagesPrice;
    }
    public void setPackagesPrice(String packagePrice) {

        this.packagesPrice = packagePrice;
    }

    public String getNumOfImages() {
        return numOfImages;
    }

    public void setNumOfImages(String numOfImages) {
        this.numOfImages = numOfImages;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getAllowTags() {
        return allowTags;
    }

    public void setAllowTags(String allowTags) {
        this.allowTags = allowTags;
    }

    public String getListTitleText() {
        return ListTitleText;
    }

    public void setListTitleText(String listTitleText) {
       this.ListTitleText = listTitleText;
    }

    public String getAllowCats() {
        return allowCats;
    }

    public String setAllowCats(String allowCats) {
        this.allowCats = allowCats;
        return allowCats;
    }
    public JSONArray getAllowCatsValue() {
        return allowCatsValue;
    }

    public String setAllowCatsValue(JSONArray allowCatsValue) {
        this.allowCatsValue = allowCatsValue;
        return null;
    }
    public String getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(String regularPrice) {
        this.regularPrice = regularPrice;
    }
    public String getSaleText() {
        return saleText;
    }

    public void setSaleText(String saleText) {
        this.saleText = saleText;
    }
}

package com.kathryn.kathryn.modelsList;

import java.util.ArrayList;

public class homeCatRelatedList {

    private String title;
    private String catId;
    private String viewAllBtnText;
    private ArrayList<catSubCatlistModel> arrayList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViewAllBtnText() {
        return viewAllBtnText;
    }

    public void setViewAllBtnText(String viewAllBtnText) {
        this.viewAllBtnText = viewAllBtnText;
    }

    public ArrayList<catSubCatlistModel> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<catSubCatlistModel> arrayList) {
        this.arrayList = arrayList;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }
}

package com.kathryn.kathryn.modelsList;

public class subcatDiloglist {

    public String id;
    public String title;
    public String name;
    private boolean hasSub;
    private boolean hasCustom;
    private boolean isShow;
    private boolean isBidding;

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    private boolean isPaid;

    public boolean isBidding() {
        return isBidding;
    }

    public void setBidding(boolean bidding) {
        isBidding = bidding;
    }


    public boolean isHasCustom() {
        return hasCustom;
    }

    public void setHasCustom(boolean hasCustom) {
        this.hasCustom = hasCustom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isHasSub() {
        return hasSub;
    }

    public void setHasSub(boolean hasSub) {
        this.hasSub = hasSub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

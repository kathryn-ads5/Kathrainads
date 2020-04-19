package com.kathryn.kathryn.modelsList;

public class ChatTyping {
    public String adId;
    public String senderId;
    public String recieverId;
    public String type;
    public String text;
    public boolean typing;

    public ChatTyping() {
    }

    public ChatTyping(String adId, String senderId, String recieverId, String type, String text, boolean typing) {
        this.adId = adId;
        this.senderId = senderId;
        this.recieverId = recieverId;
        this.type = type;
        this.text = text;
        this.typing = typing;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

}

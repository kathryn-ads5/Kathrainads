package com.kathryn.kathryn.home.helper;

public class ProgressModel {
    private String title;
    private String successTitle;
    private String failTitles;
    private String successMessage;
    private String failMessage;
    private String buttonText;


    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSuccessTitle() {
        return successTitle;
    }

    public void setSuccessTitle(String successTitle) {
        this.successTitle = successTitle;
    }

    public String getFailTitles() {
        return failTitles;
    }

    public void setFailTitles(String failTitles) {
        this.failTitles = failTitles;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }
}
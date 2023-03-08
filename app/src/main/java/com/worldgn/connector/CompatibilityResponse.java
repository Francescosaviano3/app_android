package com.worldgn.connector;

public class CompatibilityResponse {

    private long statusCode;
    private String statusMessage;
    private String uiMessage;

    public long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(long statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getUiMessage() {
        return uiMessage;
    }

    public void setUiMessage(String uiMessage) {
        this.uiMessage = uiMessage;
    }
}

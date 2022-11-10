package com.example.attendenceroll.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CheckInResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private LoggedEmployee data;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public LoggedEmployee getData() {
        return data;
    }

    public void setData(LoggedEmployee data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

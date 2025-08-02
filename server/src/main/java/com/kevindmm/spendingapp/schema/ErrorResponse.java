package com.kevindmm.spendingapp.schema;

public class ErrorResponse {
    public String message;

    public String getMessage() {
        return message;
    }

    public ErrorResponse(){
        
    }

    public ErrorResponse(String message, String param, String location) {
        this.message = message;
        this.param = param;
        this.location = location;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String param;

    public String location; 
}

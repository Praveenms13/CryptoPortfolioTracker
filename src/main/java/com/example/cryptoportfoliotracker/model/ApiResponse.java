package com.example.cryptoportfoliotracker.model;

public class ApiResponse {
    private boolean result;
    private String message;
    private Object data;

    public ApiResponse(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public ApiResponse(boolean result, String message, Object data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

package com.example.CryptoPortfolioTracker.exceptions.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ValidationErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, List<String>> fieldErrors;

    // Default constructor
    public ValidationErrorResponse() {
    }

    // Constructor with all parameters
    public ValidationErrorResponse(int status, String error, String message, String path,
                                   LocalDateTime timestamp, Map<String, List<String>> fieldErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.fieldErrors = fieldErrors;
    }

    // Builder pattern implementation
    public static ValidationErrorResponseBuilder builder() {
        return new ValidationErrorResponseBuilder();
    }

    // Getter and Setter methods
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, List<String>> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    // toString method
    @Override
    public String toString() {
        return "ValidationErrorResponse{" +
                "status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", timestamp=" + timestamp +
                ", fieldErrors=" + fieldErrors +
                '}';
    }

    // Builder class
    public static class ValidationErrorResponseBuilder {
        private int status;
        private String error;
        private String message;
        private String path;
        private LocalDateTime timestamp;
        private Map<String, List<String>> fieldErrors;

        public ValidationErrorResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ValidationErrorResponseBuilder error(String error) {
            this.error = error;
            return this;
        }

        public ValidationErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ValidationErrorResponseBuilder path(String path) {
            this.path = path;
            return this;
        }

        public ValidationErrorResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ValidationErrorResponseBuilder fieldErrors(Map<String, List<String>> fieldErrors) {
            this.fieldErrors = fieldErrors;
            return this;
        }

        public ValidationErrorResponse build() {
            return new ValidationErrorResponse(status, error, message, path, timestamp, fieldErrors);
        }
    }
}
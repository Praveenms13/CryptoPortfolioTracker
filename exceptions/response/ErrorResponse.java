package com.example.CryptoPortfolioTracker.exceptions.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String traceId; // For debugging purposes

    // Default constructor
    public ErrorResponse() {
    }

    // Constructor with all parameters
    public ErrorResponse(int status, String error, String message, String path, LocalDateTime timestamp, String traceId) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }

    // Builder pattern implementation
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
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

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    // toString method
    @Override
    public String toString() {
        return "ErrorResponse{" +
                "status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", timestamp=" + timestamp +
                ", traceId='" + traceId + '\'' +
                '}';
    }

    // Builder class
    public static class ErrorResponseBuilder {
        private int status;
        private String error;
        private String message;
        private String path;
        private LocalDateTime timestamp;
        private String traceId;

        public ErrorResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ErrorResponseBuilder error(String error) {
            this.error = error;
            return this;
        }

        public ErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorResponseBuilder path(String path) {
            this.path = path;
            return this;
        }

        public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ErrorResponseBuilder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(status, error, message, path, timestamp, traceId);
        }
    }
}
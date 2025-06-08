package com.example.greenlens.model.response;

import com.google.gson.annotations.SerializedName;

public class AnalyzeResponse {
    @SerializedName("analysis_id")
    private Long analysisId;

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("created_at")
    private String createdAt;

    public Long getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Long analysisId) {
        this.analysisId = analysisId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
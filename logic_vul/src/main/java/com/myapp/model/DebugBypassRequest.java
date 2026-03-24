package com.myapp.model;

public class DebugBypassRequest {
    private Long actingUserId;
    private Boolean debugMode;
    private Boolean skipAudit;
    private String reason;

    public Long getActingUserId() {
        return actingUserId;
    }

    public void setActingUserId(Long actingUserId) {
        this.actingUserId = actingUserId;
    }

    public Boolean getDebugMode() {
        return debugMode;
    }

    public void setDebugMode(Boolean debugMode) {
        this.debugMode = debugMode;
    }

    public Boolean getSkipAudit() {
        return skipAudit;
    }

    public void setSkipAudit(Boolean skipAudit) {
        this.skipAudit = skipAudit;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

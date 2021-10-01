package org.sunbird.cert.actor;

import java.util.List;

public class VerificationResponse {

    private Boolean valid;

    private List<String> messages;

    private int errorCount;

    public VerificationResponse() {
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
}

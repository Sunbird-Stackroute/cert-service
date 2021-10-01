package org.sunbird.response;

import java.util.Map;

public class CertificateResponse {
    private String id;
    private String accessCode;
    private Map<String, Object> jsonData;
    private String recipientId;
    private String jsonUrl;
    private String svgUrl;

    public CertificateResponse(String id, String accessCode, String recipientId, Map<String, Object> jsonData) {
        this.id = id;
        this.accessCode = accessCode;
        this.recipientId = recipientId;
        this.jsonData = jsonData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public Map<String, Object> getJsonData() {
        return jsonData;
    }

    public void setJsonData(Map<String, Object> jsonData) {
        this.jsonData = jsonData;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getJsonUrl() {
        return jsonUrl;
    }

    public void setJsonUrl(String jsonUrl) {
        this.jsonUrl = jsonUrl;
    }

    public String getSvgUrl() {
        return svgUrl;
    }

    public void setSvgUrl(String svgUrl) {
        this.svgUrl = svgUrl;
    }

}

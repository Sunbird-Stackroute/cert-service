package org.sunbird.response;


import java.util.Map;

public class CertificateResponseV1 extends CertificateResponse {
    private String pdfUrl;

    public CertificateResponseV1(String id, String accessCode, String recipientId, Map<String, Object> jsonData, String pdfUrl) {
        super(id, accessCode, recipientId, jsonData);
        setPdfUrl(pdfUrl);
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    @Override
    public String toString() {
        return "CertificateResponseV1{" +
                "id='" + getId() + '\'' +
                ", accessCode='" + getAccessCode() + '\'' +
                ", jsonData=" + getJsonData() +
                ", recipientId='" + getRecipientId() + '\'' +
                ", jsonUrl='" + getJsonUrl() + '\'' +
                ", svgUrl='" + getSvgUrl() + '\'' +
                ", pdfUrl='" + pdfUrl + '\'' +
                '}';
    }
}

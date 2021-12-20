package controllers.Model;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Data {
    private String recipientName;
    private String recipientEmail;
    private String recipientPhone;

    private static ObjectMapper mapper = new ObjectMapper();

    public Data(){

    }


    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    @Override
    public String toString() {
        return "Data{" +
                "recipientName='" + recipientName + '\'' +
                ", recipientEmail='" + recipientEmail + '\'' +
                ", recipientPhone='" + recipientPhone + '\'' +
                '}';
    }
}

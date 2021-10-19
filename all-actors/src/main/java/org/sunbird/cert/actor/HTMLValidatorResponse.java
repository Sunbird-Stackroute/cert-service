package org.sunbird.cert.actor;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HTMLValidatorResponse {


    private Boolean valid;

    private Message message;


    public HTMLValidatorResponse() {
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Message getMessage() {
        return message;
    }


    public void setMessage(Set<String> invalidVars) {
        this.message = new Message(invalidVars);
    }

    class Message {

        private Set<String> invalidVars;

        public Message(Set<String> invalidVars) {
            this.setInvalidVars(invalidVars);
        }

        public Set<String> getInvalidVars() {
            return invalidVars;
        }

        public void setInvalidVars(Set<String> invalidVars) {
            this.invalidVars = invalidVars;
        }

    }
}

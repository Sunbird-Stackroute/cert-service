package controllers.Model;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Azure {

    private String account;

    private String key;

    private String containerName;

    public Azure(){
    }

    private static ObjectMapper mapper = new ObjectMapper();



    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    @Override
    public String toString() {
        return "Azure{" +
                "account='" + account + '\'' +
                ", key='" + key + '\'' +
                ", containerName='" + containerName + '\'' +
                '}';
    }
}

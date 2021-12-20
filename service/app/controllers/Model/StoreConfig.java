package controllers.Model;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StoreConfig {

    private String type;

    private Azure azure;

    private static ObjectMapper mapper = new ObjectMapper();

    public StoreConfig(){

    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Azure getAzure() {
        return azure;
    }

    public void setAzure(Azure azure) {
        this.azure = azure;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static void setMapper(ObjectMapper mapper) {
        StoreConfig.mapper = mapper;
    }

    @Override
    public String toString() {
        return "StoreConfig{" +
                "type='" + type + '\'' +
                ", azure=" + azure +
                '}';
    }
}

package controllers.Model;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SignatoryList {

    private String name;
    private String designation;
    private String id;
    private String image;

    public SignatoryList(){

    }

    private static ObjectMapper mapper = new ObjectMapper();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "SignatoryList{" +
                "name='" + name + '\'' +
                ", designation='" + designation + '\'' +
                ", id='" + id + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

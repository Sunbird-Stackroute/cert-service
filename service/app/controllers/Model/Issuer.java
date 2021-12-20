package controllers.Model;

public class Issuer {

    private String name;

    private String url;

    public Issuer() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Issuer{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

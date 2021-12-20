package controllers.Model;

public class Bulk {

    private Certificate Bulk;

    public Certificate getBulk() {
        return Bulk;
    }

    public void setBulk(Certificate bulk) {
        Bulk = bulk;
    }

    @Override
    public String toString() {
        return "Bulk{" +
                "Bulk=" + Bulk +
                '}';
    }
}

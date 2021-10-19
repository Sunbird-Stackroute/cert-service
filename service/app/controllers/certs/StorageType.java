package controllers.certs;

import java.util.ArrayList;
import java.util.List;

public class StorageType {


    private static List<String> storageType = new ArrayList<>();

    static {
        for (CloudStorageType mode : CloudStorageType.values()) {
            storageType.add(mode.toString());
        }
    }

    public static List<String> get() {
        return storageType;
    }

    public enum CloudStorageType {
        aws,
        azure
    }

}

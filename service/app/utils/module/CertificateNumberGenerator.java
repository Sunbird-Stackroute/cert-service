package utils.module;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class CertificateNumberGenerator {
    private static AtomicInteger atomicInteger = new AtomicInteger();

    public static String getUniqueIdFromTimestamp(int environmentId) {

        Random random = new Random();
        long env = (environmentId + random.nextInt(99999)) / 10000000;
        long uid = System.currentTimeMillis() + random.nextInt(999999);
        uid = uid << 13;
        return env + "" + uid + "" + atomicInteger.getAndIncrement();
    }
}

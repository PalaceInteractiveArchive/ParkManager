package network.palace.parkmanager.utils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("NO MD5?");
        }
    }

    /**
     * Generate hash for inventory JSON
     *
     * @param inventory the JSON
     * @return MD5 hash of inventory
     */
    public static String generateHash(String inventory) {
        digest.update(inventory.getBytes());
        return DatatypeConverter.printHexBinary(digest.digest()).toLowerCase();
    }
}

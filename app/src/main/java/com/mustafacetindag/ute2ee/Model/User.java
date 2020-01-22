package com.mustafacetindag.ute2ee.Model;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String search;
    private String publicKey;
    private String privateKey;
    private SecretKeySpec aesKey;
    private byte[] sharedKey;
    private byte[] encodedParam;
    KeyAgreement userKeyAgree;

    public User(String id, String username, String imageURL, String status, String search, String publicKey) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.search = search;
        this.publicKey = publicKey;

    }

    public User() {
    }


    public byte[] getSharedKey() {
        return sharedKey;
    }

    public SecretKeySpec getAesKey() {
        return aesKey;
    }

    public void setAesKey(SecretKeySpec aesKey) {
        this.aesKey = aesKey;
    }

    public void setSharedKey(byte[] sharedKey) {
        this.sharedKey = sharedKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }


    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public byte[] getEncodedParam() {
        return encodedParam;
    }

    public void setEncodedParam(byte[] encodedParam) {
        this.encodedParam = encodedParam;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String[] generateKey() throws NoSuchAlgorithmException {

        // Key Pair Generator prepared
        KeyPairGenerator ecKeyGenerator = KeyPairGenerator.getInstance("EC");
        ecKeyGenerator.initialize(256);

        // Key pair generate , KP object has two key public , private
        KeyPair kp = ecKeyGenerator.generateKeyPair();

        // PrivateKey and PublicKey object to encode String
        String privateStr = byteToStr(kp.getPrivate().getEncoded());
        String publicStr = byteToStr(kp.getPublic().getEncoded());

        String[] keys = new String[2];
        keys[0] = publicStr;
        keys[1] = privateStr;

        return keys;
    }


    public void generateCommonSecretKey(String receivedPublicKey) {

        // PrivateKey and PublicKey to decode byte[]
        byte[] privateKeyBytes = strToByte(this.privateKey);
        byte[] publicKeyBytes = strToByte(receivedPublicKey);
        try {
            KeyFactory publicKf = KeyFactory.getInstance("EC");
            X509EncodedKeySpec pkSpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = publicKf.generatePublic(pkSpec);

            KeyFactory privateKf = KeyFactory.getInstance("EC");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = privateKf.generatePrivate(privateKeySpec);

            KeyAgreement keyAgree = KeyAgreement.getInstance("ECDH");
            keyAgree.init(privateKey);
            keyAgree.doPhase(publicKey, true);

            this.sharedKey = keyAgree.generateSecret();
            this.aesKey = new SecretKeySpec(this.sharedKey, 0, 16, "AES");

            System.out.println("ECT- DH Basarili  " + Arrays.toString(this.sharedKey));
        } catch (Exception e) {
            System.out.println("ECT- Message  " + e.getMessage());
            System.out.println("ECT- Public  " + this.privateKey);
            System.out.println("ECT- Private  " + receivedPublicKey);
        }
    }

    /*
     * Converts a byte array to hex string
     */

    public static String byteToStr(final byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public static byte[] strToByte(final String s) {
        if (s == null) {
            return (new byte[]{});
        }

        if (s.length() % 2 != 0 || s.length() == 0) {
            return (new byte[]{});
        }

        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            try {
                data[i / 2] = (Integer.decode("0x" + s.charAt(i) + s.charAt(i + 1))).byteValue();
            } catch (NumberFormatException e) {
                return (new byte[]{});
            }
        }
        return data;
    }


}

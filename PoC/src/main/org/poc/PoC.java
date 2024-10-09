package main.org.poc;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PoC {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String json = "{\"appType\":\"TP-Link_Tapo_Android\",\"appVersion\":\"3.7.113\",\"cloudPassword\":\"test\",\"cloudUserName\":\"ad1s0n@test.com\",\"platform\":\"Android 12\",\"refreshTokenNeeded\":false,\"terminalMeta\":\"1\",\"terminalName\":\"Google sdk_gphone64_x86_64\",\"terminalUUID\":\"2F5AC7AD94F77F91BC2419DE55CA3C0A\"}";
        String presignature = createPresignature(json);
        System.out.println("Presignature: " + presignature);
        String key = "6ed7d97f3e73467f8a5bab90b577ba4c";
        String nonce = "55bcd878-5c44-45c2-9d5a-e011c24a7ac8";
        String apiUrl = "/api/v2/account/login";
        long timestamp = 9999999999L;
        System.out.println("Signature: " + createSignature(presignature, timestamp, nonce, apiUrl, key));
    }

    private static byte[] requestToBytes(String json) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readByteArray();
    }

    private static byte[] calculateMD5(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(bytes);
        return md5.digest();
    }

    private static String b64encode(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String createPresignature(String json) throws IOException, NoSuchAlgorithmException {
        return b64encode(calculateMD5(requestToBytes(json)));
    }

    private static String bytesToHexstring(byte[] bytes){
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes){
            String hex = Integer.toHexString(b & 255);
            if (hex.length() == 1){
                builder.append(0);
                builder.append(hex);
            } else{
                builder.append(hex);
            }
        }
        return builder.toString();
    }

    private static String createSignature(String presignature, long timestamp, String nonce, String apiUrl, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder builder = new StringBuilder();
        if (presignature != null){
            builder.append(presignature);
            builder.append("\n");
        }
        builder.append(timestamp);
        builder.append("\n");
        if (nonce != null){
            builder.append(nonce);
            builder.append("\n");
        }
        builder.append(apiUrl);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(secretKeySpec);
        byte[] bytes = mac.doFinal(builder.toString().getBytes());
        return bytesToHexstring(bytes);
    }


}
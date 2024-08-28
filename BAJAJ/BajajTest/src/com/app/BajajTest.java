package com.app;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class BajajTest {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar test.jar <PRN Number> <path/to/file.json>");
            System.exit(1);
        }

        String prnNumber = args[0].toLowerCase().replaceAll("\\s", "");
        String filePath = args[1];

        try {
            String destinationValue = extractDestinationValue(filePath);
            if (destinationValue == null) {
                System.err.println("No 'destination' key found in the JSON file.");
                System.exit(1);
            }
            
            String randomString = generateRandomString(8);
            String concatenatedString = prnNumber + destinationValue + randomString;

            String md5Hash = generateMD5Hash(concatenatedString);
            String output = md5Hash + ";" + randomString;

            System.out.println(output);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String extractDestinationValue(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);
            return findKey(jsonObject, "destination");
        }
    }

    private static String findKey(JSONObject jsonObject, String key) {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String currentKey = keys.next();
            Object value = jsonObject.get(currentKey);
            if (currentKey.equals(key)) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String foundValue = findKey((JSONObject) value, key);
                if (foundValue != null) {
                    return foundValue;
                }
            } else if (value instanceof JSONArray) {
                for (int i = 0; i < ((JSONArray) value).length(); i++) {
                    Object item = ((JSONArray) value).get(i);
                    if (item instanceof JSONObject) {
                        String foundValue = findKey((JSONObject) item, key);
                        if (foundValue != null) {
                            return foundValue;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }

        return randomString.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}

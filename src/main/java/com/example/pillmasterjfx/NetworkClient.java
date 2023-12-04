package com.example.pillmasterjfx;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

public class NetworkClient {
    private static final String FILENAME = ".json";
    private static final String DATABASE = "pillmaster-9e7a7-default-rtdb";
    private static final String ADDRESS = "firebaseio.com";

    public String requestMedicationFile() throws IOException {
        String line;
        StringBuilder responseContent = new StringBuilder();
        BufferedReader reader;

        HttpURLConnection con = initHTTPConnection();
        con.setRequestMethod("GET");

        reader = new BufferedReader(
                        new InputStreamReader(con.getInputStream())
                );
        while((line = reader.readLine()) != null) {
            responseContent.append(line);
        }
        return responseContent.toString();
    }

    public boolean putMedicationFile(JSONObject json) {
        try {
            HttpURLConnection con = initHTTPConnection("medication.json");
            con.setRequestProperty("Content-Type", "application/json; " +
                    "charset=UTF-8");
            con.setDoOutput(true);
            con.setRequestMethod("PUT");

            OutputStream os = con.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();
            int code = con.getResponseCode();
            System.out.println("Response Code: " + code);
            if(code < 200 || code >= 300) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean postMedicationFail(String name, LocalDateTime now) {
        try{
            HttpURLConnection con = initHTTPConnection(
                    "failures.json"
            );
            con.setRequestProperty("Content-Type", "application/json; " +
                    "charset=UTF-8");
            con.setDoOutput(true);
            con.setRequestMethod("POST");

            OutputStream os = con.getOutputStream();
            JSONObject json = new JSONObject();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            json.put(name, now.format(formatter));
            os.write(json.toString().getBytes("UTF-8"));
            os.close();
            int code = con.getResponseCode();
            System.out.println("Response Code: " + code);
            if(code < 200 || code >= 300) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean postMedicationEmpty(JSONObject json, LocalDateTime time) {
        try{
            HttpURLConnection con = initHTTPConnection(
                    "empty.json"
            );
            con.setRequestProperty("Content-Type", "application/json; " +
                    "charset=UTF-8");
            con.setDoOutput(true);
            con.setRequestMethod("POST");

            OutputStream os = con.getOutputStream();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            json.put("timestamp", time.format(formatter));
            os.write(json.toString().getBytes("UTF-8"));
            os.close();
            int code = con.getResponseCode();
            System.out.println("Response Code: " + code);
            if(code < 200 || code >= 300) {
                return false;
        }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
            return true;
    }

    public HttpURLConnection initHTTPConnection() throws IOException {
        URL url = new URL(
                "https://" + DATABASE + "."
                        + ADDRESS
                        + "/" + FILENAME);
        return (HttpURLConnection) url.openConnection();
    }

    public HttpURLConnection initHTTPConnection(String path) throws IOException {
        URL url = new URL(
                "https://" + DATABASE + "."
                        + ADDRESS
                        + "/" + path);
        return (HttpURLConnection) url.openConnection();
    }
}

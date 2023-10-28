package com.example.pillmasterjfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkClient {
    private static final int SERVER_PORT = 7455;
    private static final String FILENAME = "sample-meds.json";
    private static final String ADDRESS = "localhost";

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

    public HttpURLConnection initHTTPConnection() throws IOException {
        URL url = new URL(
                "http://" + ADDRESS
                        + ":" + SERVER_PORT
                        + "/" + FILENAME);
        return (HttpURLConnection) url.openConnection();
    }
}

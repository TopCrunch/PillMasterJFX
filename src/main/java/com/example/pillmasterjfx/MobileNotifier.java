package com.example.pillmasterjfx;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class MobileNotifier {

    private static final String PATH = "accessToken.txt";
    public void alert(Medication medication) throws FirebaseMessagingException {
        // This registration token comes from the client FCM SDKs.
        String registrationToken = "YOUR_REGISTRATION_TOKEN";

        // See documentation on defining a message payload.
        com.google.firebase.messaging.Message message =
                com.google.firebase.messaging.Message.builder()
                .putData("ALERT", medication.toString())
                .setToken(registrationToken)
                .build();

        // Send a message to the device corresponding to the provided
        // registration token.
        String response = FirebaseMessaging.getInstance().send(message);
        // Response is a message ID string.
        System.out.println("Successfully sent message: " + response);
    }

    public String getToken() {
        return "";
    }

    public void sendEmail() {
        ArrayList<String> values;
        try {
            values = getAccessKey(PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final String username = values.get(0);
        final String password = values.get(1);
        final String recipient = values.get(2);

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username,password);
                    }
                });

        try {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipient)
            );
            message.setSubject("Testing Gmail TLS");
            message.setText("Test succeeded!");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getAccessKey(String path) throws IOException {
        ArrayList<String> values = new ArrayList<>();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            while((line = reader.readLine()) != null) {
                values.add(line);
            }
        }
        return values;
    }

    public void authenticate() throws IOException {
        /*
        FileInputStream serviceAccount;
        try {
            serviceAccount = new FileInputStream("path/to/serviceAccountKey.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

         */
        /*
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setDatabaseUrl("https://pillmaster-9e7a7-default-rtdb.firebaseio.com/")
                .build();

        FirebaseApp.initializeApp(options);

         */
    }
}

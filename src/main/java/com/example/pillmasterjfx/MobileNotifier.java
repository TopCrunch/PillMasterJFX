package com.example.pillmasterjfx;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class MobileNotifier {

    private static final String PATH = "accessToken.txt";

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
}

package services;

import utils.DatabaseConnection;

import javax.mail.*;
import javax.mail.internet.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmailService {

    // Configurer les paramètres SMTP
    private static final String SMTP_HOST = "smtp.gmail.com";  // Utilise le SMTP de ton fournisseur d'email
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USER = "donghol2023@gmail.com";  // Remplace par ton adresse email
    private static final String SMTP_PASSWORD ="enjf udvm akcs inyu";  // Ton mot de passe d'application (pas ton mot de passe Gmail normal)

    private Connection cnx;

    public EmailService() {
        cnx = DatabaseConnection.getInstance().getConnection();
    }
    // Envoi d'un email
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", "465"); // port SSL
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true"); // active SSL directement
        properties.put("mail.smtp.ssl.trust", SMTP_HOST); // faire confiance à Gmail

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_USER));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }

    public List<String> rechercherEmailsParMotCle(String keyword) throws SQLException {
        List<String> emails = new ArrayList<>();
        String query = "SELECT email FROM users WHERE email LIKE ?";
        try (
             PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                emails.add(rs.getString("email"));
            }
        }
        return emails;
    }

}

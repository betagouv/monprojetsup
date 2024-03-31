package fr.gouv.monprojetsup.web.mail;

import fr.gouv.monprojetsup.web.server.WebServerConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.*;

public class MailSender {

    public static void send(EmailConfig config, String recipient, String subject, String content) {
        send(config, List.of(recipient), subject, content);
    }

    public static Thread send(EmailConfig config, Collection<String> recipients, String subject, String content) {
        return send(config, recipients, subject, content, List.of(), true, null);
    }

    public static Thread send(EmailConfig config, Collection<String> recipients, String subject, String content, boolean ccSender, Thread.UncaughtExceptionHandler handler) {
        return send(config, recipients, subject, content, List.of(), ccSender, handler);
    }


    public static Thread send(EmailConfig config,
                              Collection<String> recipients,
                              String subject,
                              String body,
                              List<Path> attachments,
                              boolean ccSender,
                              Thread.UncaughtExceptionHandler handler) {


        Thread thread = new Thread(
                () -> {

                    if(config.password() == null || config.password().isEmpty()) return;

                    //workaround bug https://github.com/jakartaee/mail-api/issues/665
                    Thread t = Thread.currentThread();
                    t.setContextClassLoader(MailSender.class.getClassLoader());
                    //end workaround

                    try {
                        doSend( config,  recipients,  subject,  body, attachments, ccSender);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        thread.setUncaughtExceptionHandler(handler);
        thread.start();
        return thread;
    }


    static synchronized void doSend(
            EmailConfig config,
            Collection<String> recipients,
            String subject, String content,
            List<Path> attachments, boolean ccSender) throws MessagingException, UnsupportedEncodingException {

        if(config == null || Objects.equals(config.user(), "user")) return;

        Properties prop = config.getProperties();
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.user(), config.password());
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom( new InternetAddress(config.sender(), "MonProjetSup"));
        String adresses = String.join(",", recipients);
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(adresses)
        );
        if(ccSender && !adresses.endsWith("monprojetsup.fr")) {
            message.setRecipients(
                    Message.RecipientType.BCC, InternetAddress.parse("support@monprojetsup.fr"));
        }
        message.setSubject(subject);

        Multipart multipart = new MimeMultipart();

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content, "text/html; charset=utf-8");

        multipart.addBodyPart(mimeBodyPart);

        attachments.forEach(path -> {
            try {
                MimeBodyPart part = new MimeBodyPart();
                File f = new File(path.toString());
                part.attachFile(f);
                multipart.addBodyPart(part);
            } catch (IOException | MessagingException e) {
                throw new RuntimeException(e);
            }
        });

        message.setContent(multipart);

        Transport.send(message, config.user(), config.password());
    }

    public static void main(String[] args) throws IOException {
        WebServerConfig config = WebServerConfig.load();
        send(config.getEmailConfig(), "info@monprojetsup.fr", "test", "test");
    }


}

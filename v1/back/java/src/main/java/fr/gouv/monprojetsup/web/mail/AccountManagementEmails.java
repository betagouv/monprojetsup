package fr.gouv.monprojetsup.web.mail;

import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.server.WebServer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountManagementEmails {

    public static Map<String, LocalDateTime> lastEmailSent = new ConcurrentHashMap<>();

    public static void checkLastEmailWasSentReasonableLungTimeAgo(String emailAdress) throws DBExceptions.UserInputException.TooManyEmails {
        LocalDateTime lastEmail = lastEmailSent.get(emailAdress);
        if (lastEmail != null && lastEmail.plusMinutes(1).isAfter(LocalDateTime.now())) {
            throw new DBExceptions.UserInputException.TooManyEmails();
        }
        lastEmailSent.put(emailAdress, LocalDateTime.now());
    }

    public static void sendConfirmationLink(String emailAdress, String confirmationToken) throws DBExceptions.UserInputException.TooManyEmails {

        checkLastEmailWasSentReasonableLungTimeAgo(emailAdress);

        String confirmationLink
                = String.format("%s/validate.html?email=", WebServer.config().getUrl())
                + URLEncoder.encode(emailAdress, StandardCharsets.UTF_8)
                + "&token="
                + URLEncoder.encode(confirmationToken, StandardCharsets.UTF_8);
        MailSender.send(
                WebServer.config().getEmailConfig(),
                emailAdress,
                "MonProjetSup: validation de la création de votre compte",
                String.format(
                        """
                                <p>Afin de valider la création de votre compte MonProjetSup,
                                veuillez cliquer sur le lien suivant,
                                ou bien le copier-coller dans la barre d'adresse
                                de votre navigateur web.</p>
                                <br/>
                                <a href="%s">%s</a>
                                <br/>
                                <p>Si vous n'avez pas sollicité la création d'un compte, merci d'ignorer ce message.</p>
                                <br/>
                                Cordialement,<br/>
                                l'équipe de MonProjetSup<br/>
                                support@monprojetsup.fr<br/>
                                https://monprojetsup.fr
                                """, confirmationLink, confirmationLink)
        );
    }

    public static void informSupport(String login, String activationMessage) {

        String subject = "Action requise pour le compte " + login;
        MailSender.send(
                WebServer.config().getEmailConfig(),
                WebServer.config().getSupportEmailAddress(),
                subject,
                String.format(
                        """
                                 <p>Message envoyé à '%s':</p>
                                    <br/>                              
                                    <br/>                              
                                <p>%s</p>
                                """, login, activationMessage)
        );
    }

    public static void sendEmailAccountCreationRejected(String user) throws DBExceptions.UserInputException.TooManyEmails {

        checkLastEmailWasSentReasonableLungTimeAgo(user);

        WebServer.LOGGER.info(" Sending rejection email to " + user);
        MailSender.send(
                WebServer.config().getEmailConfig(),
                user,
                "MonProjetSup: échec de votre création de compte",
                String.format(
                        """
                                <p>Votre demande de création de compte a été rejetée par un modérateur.</p>
                                <br/>                              
                                <br/>                              
                                Cordialement,<br/>
                                l'équipe de MonProjetSup<br/>
                                support@monprojetsup.fr<br/>
                                https://monprojetsup.fr
                                """)
        );
    }

    public static void sendEmailAccountCreationConfirmed(String user) throws DBExceptions.UserInputException.TooManyEmails {

        checkLastEmailWasSentReasonableLungTimeAgo(user);

        WebServer.LOGGER.info("sendEmailAccountCreationConfirmed Sending confirmation email to " + user);
        MailSender.send(
                WebServer.config().getEmailConfig(),
                user,
                "MonProjetSup: validation de votre création de compte",
                String.format(
                        """
                                <p>Votre demande de création de compte a été validée.</p>
                                <p>Vous pouvez dès maintenant vous connecter à l'adresse 
                                <a href="%s">%s</a>.
                                <br/>                              
                                <br/>                              
                                Cordialement,<br/>
                                l'équipe de MonProjetSup<br/>
                                support@monprojetsup.fr<br/>
                                https://monprojetsup.fr
                                """, WebServer.config().getUrl(), WebServer.config().getUrl())
        );
    }

    public static void sendEmailAddressConfirmedEmail(String user, String msg) throws DBExceptions.UserInputException.TooManyEmails {

        //checkLastEmailWasSentReasonableLungTimeAgo(user);

        WebServer.LOGGER.info("sendEmailAddressConfirmedEmail Sending confirmation email to " + user);
        MailSender.send(
                WebServer.config().getEmailConfig(),
                user,
                "MonProjetSup: validation de votre création de compte",
                String.format(msg +
                        """
                                <br/>                              
                                <br/>                              
                                Cordialement,<br/>
                                l'équipe de MonProjetSup<br/>
                                support@monprojetsup.fr<br/>
                                https://monprojetsup.fr
                                """)
        );
    }

    public static void sendAcceptUserInGroupEmail(String user, String groupName) throws DBExceptions.UserInputException.TooManyEmails {

        checkLastEmailWasSentReasonableLungTimeAgo(user);

        WebServer.LOGGER.info("sendAcceptUserInGroupEmail Sending confirmation email to '"
                + user
                + "' in group '"
                + groupName + "'"
        );
        MailSender.send(
                WebServer.config().getEmailConfig(),
                user,
                "MonProjetSup: validation de votre inscription dans le groupe",
                String.format(
                        """
                                <p>Vous avez rejoint le groupe %s.</p>
                                <br/>                              
                                <br/>                              
                                Cordialement,<br/>
                                l'équipe de MonProjetSup<br/>
                                support@monprojetsup.fr<br/>
                                https://monprojetsup.fr
                                """, groupName)
        );

    }


    public static void sendResetPasswordEmail(String emailAdress, String confirmationToken) throws DBExceptions.UserInputException.TooManyEmails {

        checkLastEmailWasSentReasonableLungTimeAgo(emailAdress);

        WebServer.LOGGER.info("sendResetPasswordEmail Sending confirmation email to " + emailAdress);
        String confirmationLink
                = String.format("%s/reset_password.html?email=", WebServer.config().getUrl())
                + URLEncoder.encode(emailAdress, StandardCharsets.UTF_8)
                + "&token="
                + URLEncoder.encode(confirmationToken, StandardCharsets.UTF_8);
        MailSender.send(
                WebServer.config().getEmailConfig(),
                emailAdress,
                "MonProjetSup: réinitialisation du mot de passe",
                String.format(
                        """
                                <p>Une réinitialisation de mot de passe MonProjetSup a été demandée pour cette adresse email.
                                <p>Si vous n'avez pas sollicité cette opération, merci d'ignorer ce message.</p>
                                <br/>
                                <p>Pour procéder à la réinitisalisation, veuillez cliquer sur le lien suivant,
                                    ou bien le copier-coller dans la barre d'adresse
                                        de votre navigateur web.
                                 </p>
                                 <p>
                                <a href="%s">%s</a>
                                </p>
                                <br/>
                                Cordialement,<br/>
                                l'équipe de MonProjetSup<br/>
                                support@monprojetsup.fr<br/>
                                https://monprojetsup.fr
                                """, confirmationLink, confirmationLink)
        );

    }
}

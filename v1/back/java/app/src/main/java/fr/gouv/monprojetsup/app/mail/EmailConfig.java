package fr.gouv.monprojetsup.app.mail;

import java.util.Properties;

/*        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.mailtrap.io");
        prop.put("mail.smtp.port", "25");
        prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
*/
public record EmailConfig(
        boolean smtpAuth,
        boolean smtpStarttlsEnable,
        String smtpHost,
        int smtpPort,
        String smtpSslTrust,
        String user,
        String password,

        String sender
) {

    public EmailConfig() {
        this(true,true,
                "smtp.mailtrap.io",
                25,
                "smtp.mailtrap.io",
                "user",
                "password",
                "exemple@mailtrap.io");
    }

    public Properties getProperties() {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", smtpAuth);
        prop.put("mail.smtp.starttls.enable", smtpStarttlsEnable);
        prop.put("mail.smtp.ssl.enable", true);
        prop.put("mail.smtp.host", smtpHost);
        prop.put("mail.smtp.port", smtpPort);
        prop.put("mail.smtp.ssl.trust", smtpSslTrust);
        prop.put("mail.debug", true);
        return prop;
    }
}

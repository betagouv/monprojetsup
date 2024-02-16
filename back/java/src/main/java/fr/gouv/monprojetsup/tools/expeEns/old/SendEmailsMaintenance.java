package fr.gouv.monprojetsup.tools.expeEns.old;

import fr.gouv.monprojetsup.tools.Serialisation;
import fr.gouv.monprojetsup.tools.csv.CsvTools;
import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.db.model.Group;
import fr.gouv.monprojetsup.web.db.model.Groups;
import fr.gouv.monprojetsup.web.mail.MailSender;
import fr.gouv.monprojetsup.web.server.WebServerConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SendEmailsMaintenance {


    private static final Logger LOGGER = Logger.getLogger(SendEmailsMaintenance.class.getName());

    static String templateEmail =
            """ 
            Madame, Monsieur,<br/>\n<br/>\n
            Vous recevez cet e-mail car vous êtes référent(e) d'un groupe sur MonProjetSup.<br/>\n
                    <br/>\n
            Suite à un incident technique, nous avons été contraints de fermer temporairement le site aujourd'hui.<br/>\n
                    <br/>\n
            Nous vous présentons nos excuses pour la gêne occasionnée. Nous vous recontacterons dès la réouverture du site.<br/>\n
                    <br/>\n
            Nous restons à votre disposition pour toute question ou demande,<br/>\n
                    <br/>\n
            Bien cordialement,<br/>\n
                    <br/>\n
            Bien cordialement,<br/>\n
            l’équipe support MonProjetSup - Avenir(s)<br/>\n
            support@monprojetsup.fr<br/>\n
                    \n
                    """;
    static String templateEmail2 =
            """ 
            Madame, Monsieur,<br/>\n<br/>\n
            Vous recevez cet e-mail car vous êtes référent(e) d'un groupe sur MonProjetSup.<br/>\n
                    <br/>\n
            Le service est revenu à la normale.<br/>\n Merci pour votre patience.<br/>\n
                                <br/>\n
            Bien cordialement,<br/>\n
            l’équipe support MonProjetSup - Avenir(s)<br/>\n
            support@monprojetsup.fr<br/>\n
                    \n
                    """;
    /* loads a list of emails from the file filePath.
     * The file must be a csv file with the following format: nom;uai;email1;email2;email3
     * The first line is ignored.
     * The first column is the name of the establishment.
     * The second column is the UAI of the establishment.
     * The third column is the email of the establishment.
     * The fourth column is the email of the establishment.
     * The fifth column is the email of the establishment.
     * The other columns are ignored.
     * The separator is the semicolon.
     * The file must be encoded in UTF-8.
     *
     */

    public static void main(String[] args) throws IOException, DBExceptions.ModelException, InterruptedException {


        WebServerConfig config = WebServerConfig.load();


        Groups groups = Serialisation.fromJsonFile("groups.json", Groups.class);

        groups.init();
        final Set<String> lyceesExpeENS = groups.getLyceesExpeENS();

        List<String> emails
                = new ArrayList<>(groups.getGroups().stream()
                .filter(g -> lyceesExpeENS.contains(g.getLycee()))
                .map(Group::getAdmins).flatMap(Collection::stream).collect(Collectors.toSet())
                .stream().sorted()
                .collect(Collectors.toList()));


        try ( CsvTools tools = new CsvTools("mailing_list.csv", ';') ) {
            for (String email : emails) {
                tools.append(email);
                tools.newLine();
            }
        }

        while(true) {
            if(emails.isEmpty()) break;
            String email = emails.get(0);
            if(email.equals("patrick-jean.forgue@ac-besancon.fr")) break;
            emails.remove(0);
        }

        System.out.println(emails.stream().collect(Collectors.joining(";")));

        //emails.clear();
        emails.add("louis.gleyo@gmail.com");
        emails.add("hugo.gimbert@labri.fr");



        int i = 0;
        emails.clear();
        for (String email : emails) {

            LOGGER.info("sending email to " + email + "...");
            String subject = "MonProjetSup: rétablissement du service";
            Thread thread = MailSender.send(
                    config.getEmailConfig(),
                    List.of(email),
                    subject,
                    templateEmail2
            );
            thread.join();
            LOGGER.info("sent email to " + email + "...");
            i++;
            Thread.sleep(500);
        }


        System.out.println("done");



    }
}

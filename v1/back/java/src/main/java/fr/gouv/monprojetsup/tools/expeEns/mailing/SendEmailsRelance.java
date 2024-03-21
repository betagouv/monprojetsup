package fr.gouv.monprojetsup.tools.expeEns.mailing;

import fr.gouv.monprojetsup.tools.Serialisation;
import fr.gouv.monprojetsup.tools.csv.CsvTools;
import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.db.model.Group;
import fr.gouv.monprojetsup.web.db.model.Groups;
import fr.gouv.monprojetsup.web.mail.MailSender;
import fr.gouv.monprojetsup.web.server.WebServerConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SendEmailsRelance {


    private static final Logger LOGGER = Logger.getLogger(SendEmailsRelance.class.getName());
    private static final String STATE_FILENAME = "relance.json";

    static String subjectEmail = "Avance sur tes voeux Parcoursup avec MonProjetSup!";
    static String templateEmail1 =
            """
            Tu es élève en terminale et tu t’es connecté(e) à MonProjetSup en classe
            pour explorer des propositions de formations Parcoursup calculées pour toi.
            Beaucoup d’élèves n'ont pas visualisé ces suggestions,
            qui étaient peu mises en avant sur le site.<br/>
            Ce problème a été réglé.<br/>
            <br/>            
            C’est la dernière ligne droite avant la clôture des voeux Parcoursup le 14 mars,
            et toute l’équipe MonProjetSup est avec toi!<br/>
            <br/>            
            <b>Tu peux dès à présent te reconnecter sur https://monprojetsup.fr chez toi </b>
            pour y retrouver des suggestions de formations personnalisées,<br/>
            dont tu pourras discuter avec l’équipe éducative de ton lycée.<br/>
            <b>Tu pourras retrouver tes formations favorites directement sur la carte de Parcoursup 
            et les ajouter à tes vœux, grâce à l'onglet "Ma sélection"</b>.<br/>
            <br/>            
            N'hésite pas à partager ce mail avec tes camarades de classe 
            pour qu'ils puissent aussi découvrir leurs suggestions de formations.<br/>
            <br/>          
            A bientôt,
            <br/>
            
            L’équipe MonProjetSup - Avenir(s) 
            """;
    static String templateEmail2 =
            """
            Tu es élève en terminale et tu t’es déjà connecté(e) à MonProjetSup en classe avec ton enseignant.
            <br/>         
            <br/>
            C’est la dernière ligne droite avant la clôture des voeux Parcoursup le 14 mars, 
            et toute l’équipe MonProjetSup est avec toi!
            <br/>
            <br/>
            <b>Tu peux dès à présent te reconnecter sur https://monprojetsup.fr chez toi 
            pour y retrouver des suggestions de formations personnalisées, </b>
            dont tu pourras discuter avec l’équipe éducative de ton lycée. 
            <b>Tu pourras retrouver tes formations favorites directement sur la carte de Parcoursup 
            et les ajouter à tes vœux, grâce à l'onglet "Ma sélection"</b>.
            <br/>
            <br/>
            N'hésite pas à partager ce mail avec tes camarades de classe 
            pour qu'ils puissent aussi découvrir leurs suggestions de formations.
            <br/>
            <br/>
            A bientôt,
            <br/>
            L’équipe MonProjetSup - Avenir(s) 
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

    static class SendEmailsRelanceState {
        public final Set<String> todo1 =  new TreeSet<>();
        public final Set<String> done1 =  new TreeSet<>();
        public final Set<String> failed1 =  new TreeSet<>();
        public final Set<String> todo2 =  new TreeSet<>();
        public final Set<String> done2 =  new TreeSet<>();

        public final Set<String> failed2 =  new TreeSet<>();

        public Pair<String, String> next() {
            if(todo1.isEmpty() && todo2.isEmpty()) return null;
            if(!todo1.isEmpty()) return Pair.of(todo1.iterator().next(), templateEmail1);
            return Pair.of(todo2.iterator().next(), templateEmail2);
        }

        public void setDone(String email) throws IOException {
            if(todo1.contains(email)) {
                todo1.remove(email);
                done1.add(email);
            } else {
                todo2.remove(email);
                done2.add(email);
            }
        }

        public void setFailed(String email) {
            if(todo1.contains(email)) {
                todo1.remove(email);
                failed1.add(email);
            } else {
                todo2.remove(email);
                failed2.add(email);
            }
        }
    }

    public static void initState() throws IOException {

        SendEmailsRelanceState state = new SendEmailsRelanceState();


        Groups groups = Serialisation.fromJsonFile("groups.json", Groups.class);

        groups.init();

        final Set<String> lyceesExpeENS = groups.getLyceesExpeENS();

        List<String> emails
                = new ArrayList<>(groups.getGroups().stream()
                .filter(g -> lyceesExpeENS.contains(g.getLycee()))
                .filter(g -> g.isExpeENSGroupeTest())
                .map(Group::getMembers)
                .flatMap(Collection::stream).collect(Collectors.toSet())
                .stream()
                .filter(m -> m != null && m.contains("@"))
                .sorted()
                .collect(Collectors.toList()));

        state.todo2.addAll(emails);
        List<Map<String, String>> mails2 = CsvTools.readCSV("liste_eleves_test.csv", ';');
        mails2.forEach( m -> state.todo1.add(m.get("email")));
        state.todo2.removeAll(state.todo1);

        Serialisation.toJsonFile(STATE_FILENAME, state, true);

    }

    public static void main(String[] args) throws IOException, DBExceptions.ModelException, InterruptedException {


        WebServerConfig config = WebServerConfig.load();


        if(!Files.exists(Path.of(STATE_FILENAME))) initState();

        SendEmailsRelanceState state = Serialisation.fromJsonFile(STATE_FILENAME, SendEmailsRelanceState.class);

        Serialisation.toJsonFile(STATE_FILENAME + LocalDateTime.now() + ".json", state, true);

        int i = 0;
        while(true) {
            Pair<String,String> p = state.next();
            if(p == null) break;

            String email = p.getLeft();
            String body = p.getRight();


            LOGGER.info("sending email to " + email + "...");
            String subject = subjectEmail;
            final boolean[] failed = {false};
            Thread thread = MailSender.send(
                    config.getEmailConfig(),
                    List.of(email),
                    subject,
                    body,
                    i++ % 30 == 0,
                    new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(Thread t, Throwable e) {
                            LOGGER.warning("failed to send email to " + email + "...");
                            failed[0] = true;
                        }
                    }
            );
            thread.join();
            LOGGER.info("sent email to " + email + "...");
            Thread.sleep(4000);
            if(!failed[0]) state.setDone(email);
            else state.setFailed(email);
            Serialisation.toJsonFile(STATE_FILENAME, state, true);

        }


        System.out.println("done");



    }
}

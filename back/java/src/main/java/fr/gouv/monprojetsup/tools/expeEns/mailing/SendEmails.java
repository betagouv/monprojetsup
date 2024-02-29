package fr.gouv.monprojetsup.tools.expeEns.mailing;

import fr.gouv.monprojetsup.tools.csv.CsvTools;
import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.db.dbimpl.DBMongo;
import fr.gouv.monprojetsup.web.db.model.Lycee;
import fr.gouv.monprojetsup.web.mail.MailSender;
import fr.gouv.monprojetsup.web.server.WebServer;
import fr.gouv.monprojetsup.web.server.WebServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableMongoRepositories
@ComponentScan(basePackages = {"fr.gouv.monprojetsup"})
public class SendEmails {

    private static final Logger LOGGER = Logger.getLogger(SendEmails.class.getName());

    static String filePath = "emails_20_12_23.csv";

    static String templateEmail =
            """ 
                    Madame, Monsieur,
                    
                    <p>Je vous recontacte à propos de l’expérimentation du module MonProjetSup - Avenir(s), 
                    pour laquelle votre lycée s’est porté volontaire. 
                    La plateforme est désormais disponible pour les enseignants à l’adresse suivante :
                     <a href="https://monprojetsup.fr">https://monprojetsup.fr</a>. 
                    Elle sera utilisable par les élèves à la rentrée de janvier.
                    </p>
                    <p>
                    La semaine passée, nous avons pu présenter à des enseignants et PsyEN volontaires la plateforme MonProjetSup - Avenir(s) 
                    ainsi que l’expérimentation. Nous remercions les personnes présentes pour leur participation. 
                    Cette présentation a été enregistrée et mise en ligne pour les enseignants et PsyEN qui n’ont pas pu venir (voir ci-dessous). 
                    <b>Aussi, nous vous rappelons que l’expérimentation est ouverte à toutes les classes de terminale générale et technologique, 
                    et que tous les enseignants et PsyEN peuvent rejoindre l’expérimentation en tant que référent, 
                    même s’ils n’étaient pas présents à une des présentations. 
                    Il est important pour la validité de l’expérimentation que le maximum de classes y participe.
                    </b>
                    </p>
                    <p>
                    Afin de faciliter la prise en main de la plateforme par les volontaires, 
                    <b>nous vous transférons 
                    le lien vers le document récapitulatif qui contient toutes les ressources utiles à l’expérimentation, 
                    y compris la rediffusion de la présentation aux enseignants et PsyEN.</b> 
                    Les référents volontaires trouveront sur ce document le fascicule de présentation de l’expérimentation, 
                    le support pédagogique de déroulé des séances, mais aussi une vidéo-tutoriel qui montre comment utiliser 
                    la plateforme et une Foire aux Questions. Pour accéder au document récapitulatif,
                    <a href="https://docs.google.com/document/d/16pAqEHUkKRRyqIopk2uGSrYhjz9vfOBPSDyZcTPlK_U/edit?usp=sharing">cliquer ici</a>.
                    Aussi, en fin de mail, vous trouverez <b>pour chaque classe de terminale</b>
                     de votre établissement <b>2 codes d’accès</b> :</p>
                    
                    <ul>
                    <li>
                    <b>Code d’accès “Référent”</b> : ce code permet au référent (enseignant ou PsyEN) de créer un compte 
                    sur la plateforme dès aujourd’hui. Lors de son inscription, 
                    il doit saisir le code associé à la classe dont il est référent. 
                    Par exemple, si Monsieur A. est référent de la Terminale 1, 
                    il devra saisir le code de la Terminale 1 pour s’inscrire sur la plateforme. 
                    S’il y a plusieurs référents pour la même classe, ils peuvent saisir le même code.
                     <b>Le code “Référent” est réservé aux référents, et ne doit pas être transmis aux élèves</b>. 
                     Les référents volontaires peuvent s’inscrire sur MonProjetSup dès aujourd’hui, 
                     pour en découvrir les fonctionnalités (voir la 
                     <a href="https://youtu.be/91mKLRFovRM">vidéo-tutoriel</a>).</li>
                    <li>
                    <b>Code d’accès “Lycéen”</b> : ce code permet aux lycéens, dès le 8 janvier, de s’inscrire sur la plateforme et réaliser la première séance. Le référent devra transmettre aux élèves le code “Lycéen” (correspondant à leur classe) au début de la première séance (par exemple en l’écrivant au tableau). Les élèves saisiront ce code d’accès, ce qui leur permettra d’être inscrits directement dans le bon groupe sur la plateforme. Le code est commun à chaque classe, donc si les élèves passent en demi-groupe, ils devront quand même saisir le même code.
                    </li>
                    </ul>
                    
                    <p>
                    Idéalement, les séances MonProjetSup devraient être organisées par classe (en demi-groupe ou classe entière). 
                    Néanmoins, nous avons noté que certains lycées organisent les séances d’orientation en terminale 
                    générale par groupe d’enseignement de spécialité, et non par classe. 
                    Si votre établissement est concerné, nous vous invitons à nous communiquer 
                    par email le nom de chacun des groupes existants. 
                    Nous générerons alors de nouveaux codes d’accès correspondant à ces groupes.
                    </p>
                    <p>
                    Pour récapituler, vous pouvez dès à présent :\s
                    <ul>
                    <li>
                    Transmettre aux référents (PsyEN ou enseignants volontaires) les différentes ressources de présentation de la plateforme, 
                    et notamment le document récapitulatif 
                    (<a href="https://docs.google.com/document/d/16pAqEHUkKRRyqIopk2uGSrYhjz9vfOBPSDyZcTPlK_U/edit?usp=sharing">cliquer ici</a>),
                     ainsi que le lien vers la plateforme (<a href="https://monprojetsup.fr">https://monprojetsup.fr</a>).
                    </li>
                    <li>
                    Transmettre aux référents volontaires les codes d’accès “Référent” correspondant à leurs classes, afin qu’ils puissent s’inscrire et découvrir la plateforme. Vous pouvez également transmettre aux référents les codes “Lycéens” qui permettent aux lycéens de s’inscrire, mais ce code ne devra être diffusé aux élèves qu’après la rentrée de janvier.
                    </li>
                    <li>
                    Rappeler aux enseignants (notamment PP) ou aux PsyEN hésitants qu’ils peuvent toujours rejoindre l’expérimentation, et que les ressources mises à disposition permettent une prise en main facile et rapide de la plateforme.
                    </li>
                    <li>
                    Nous contacter en réponse à cet e-mail si votre établissement a une organisation spécifique qui nécessite des aménagements. Notamment, si vous organisez les séances d’orientation par groupes de spécialités et non par classe, vous pouvez nous transmettre la liste des groupes existants, pour que nous puissions générer de nouveaux codes d’accès correspondant à ces groupes.
                    </li>
                    </ul>
                    <p>
                    Vous trouverez la liste des codes d’accès ci-dessous. Si votre lycée est un LPO, 
                    merci de ne pas prendre en compte les codes générés pour des classes de terminale professionnelle.
                    (non incluses dans l’expérimentation)  :
                                        </p>
                    %s
                    <p>
                    En vous remerciant encore pour votre implication dans l’expérimentation. Nous restons à votre disposition pour toute question.
                    </p>
                    <br/>
                    <br/>
                    Bien cordialement,\s
                    <br/>
                    <br/>
                    Marc Gurgand pour l’équipe de recherche
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

        ConfigurableApplicationContext context = SpringApplication.run(SendEmails.class, args);
        DBMongo db = context.getBean(DBMongo.class);

        db.load(config);

        Map<String, Lycee> lycees = db.getLycees().stream()
                .collect(Collectors.toMap(
                                Lycee::getId,
                                lycee -> lycee
                        )
                );

        List<Map<String, String>> lines = CsvTools.readCSV(filePath, ';');

        Map<String, Set<String>> emails = new HashMap<>();
        lines.forEach(line -> {
            String uai = line.get("uai").replaceAll("\"","").trim();
            Lycee lycee = lycees.get(uai);
            if (lycee == null) {
                throw new RuntimeException("lycee not found: " + uai);
            }
        });

        lines.forEach(line -> {
            String uai = line.get("uai").trim();
            Lycee lycee = lycees.get(uai);
            if (lycee != null) {
                Set<String> emailsLycee = emails.computeIfAbsent(uai, k -> new HashSet<>());
                if (lycee.getProviseurs() != null) {
                    emailsLycee.addAll(lycee.getProviseurs());
                }
                for (int i = 1; i < 3; i++) {
                    emailsLycee.add(line.get("email" + i));
                }
                emailsLycee.remove(null);
                emailsLycee.removeIf(l -> l == null || !l.contains("@") || l.contains("/"));
                if (emailsLycee.isEmpty()) {
                    throw new RuntimeException("no email for lycee: " + uai);
                }
            }
        });

        int i = 0;
        for (Map.Entry<String, Set<String>> entry : emails.entrySet()) {
            String uai = entry.getKey();
            Set<String> emailsLycee = entry.getValue();


            //comment to reactivate
            emailsLycee.clear();
            //emailsLycee.add("louis.gleyo@gmail.com");

            Lycee lycee = lycees.get(uai);
            if (lycee == null) {
                throw new RuntimeException("lycee not found: " + uai);
            }

            StringBuilder sb = new StringBuilder();

            sb.append("Lycée: '" + lycee.getName() + "' (UAI " + uai + ")" + "\n\n");
            WebServer.db().getGroupsOfLycee(uai).forEach(group -> {
                sb.append("Classe: " + group.getClasse() + "\n");
                sb.append("Code d'accès référents: " + group.getAdminToken() + "\n");
                sb.append("Code d'accès lycéens: " + group.getRegistrationToken() + "\n\n");
            });

            LOGGER.info("sending email to " + emailsLycee + "...");
            String subject = "Expérimentation MonProjetSup - Transmission des codes d’accès - lycée '" + lycee.getName() + "'";
            Thread thread = MailSender.send(
                    config.getEmailConfig(),
                    emailsLycee,
                    subject,
                    String.format(templateEmail,sb.toString().replace("\n","<br/>"))
            );
            thread.join();
            i++;
        }

        System.out.println("done");
        System.exit(0);


    }
}

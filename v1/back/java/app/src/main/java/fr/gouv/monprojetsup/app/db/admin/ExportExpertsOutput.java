package fr.gouv.monprojetsup.app.db.admin;


import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.db.dbimpl.DBMongo;
import fr.gouv.monprojetsup.app.server.WebServerConfig;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.config.DataServerConfig;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.dto.SuggestionDTO;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import lombok.val;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.app.db.DB.LYCEE_EXPERTS;
import static fr.gouv.monprojetsup.data.DataSources.PROFILS_EXPERTS_MPS_PATH;
import static fr.gouv.monprojetsup.data.ServerData.getDebugLabel;

@SpringBootApplication
@EnableMongoRepositories
@ComponentScan(basePackages = {"fr.gouv.monprojetsup"})
public class ExportExpertsOutput {
    public static void main(String[] args) throws IOException, DBExceptions.ModelException {
        // Replace the placeholder with your Atlas connection string

        WebServerConfig config = WebServerConfig.load();
        DataServerConfig.load();

        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(ExportExpertsOutput.class)
                .web(WebApplicationType.NONE)
                .run(args);

        DBMongo db = context.getBean(DBMongo.class);

        db.load(config);

        db.exportExpertProfiles("profilsExperts.json", LYCEE_EXPERTS, false);
        db.exportExpertProfiles(DataSources.getSourceDataFilePath(PROFILS_EXPERTS_MPS_PATH), LYCEE_EXPERTS, false);

        List<Pair<String, ProfileDTO>> profiles = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(PROFILS_EXPERTS_MPS_PATH),
                new TypeToken<List<ImmutablePair<String, ProfileDTO>>>(){}.getType()

        );

        ServerData.load();
        toResumeCsv("profilesExperts.csv", profiles);


        db.stop();

        SpringApplication.exit(context);
    }

    private static void toResumeCsv(String filename, List<Pair<String, ProfileDTO>> profiles) throws IOException {


        try (CsvTools output = new CsvTools(filename, ',')) {
            output.appendHeaders(List.of(
                    "Nom",
                    "Profil",
                    "Goûts et compétences",//Intérets
                    "Centres intérêts",//thematiques
                    "Idées et Favoris Métiers",//isMetier
                    "Idées et Favoris Formatione",//isFormation
                    "Corbeille (refus / pas intéressé)",
                    "Suggestions définies par experts",
                    "Sujets de discussion avec le lycéen",
                    "Notes et Remarques")
            );
            int i = 1;

            for (val pp : profiles) {
                String name = pp.getLeft();
                val pf = pp.getRight();
                i++;
                output.append(name);
                output.append(toExplanationStringShort(pf, ""));

                output.append(toExplanations(pf.interests().stream()
                        .filter(Helpers::isInteret
                        )
                        .toList(), ""));

                output.append(toExplanations(pf.interests().stream()
                        .filter(Helpers::isTheme
                        )
                        .toList(), ""));


                output.append(toExplanationString(pf.suggApproved().stream().filter(s -> Helpers.isMetier(s.fl())).toList(), ""));
                output.append(toExplanationString(pf.suggApproved().stream().filter(s -> Helpers.isFiliere(s.fl())).toList(), ""));
                output.append(toExplanationString(pf.suggRejected(), ""));
                output.append(toString(pf.suggApproved()));
                output.append("");
                output.append("");
                output.newLine();
            }
        }
    }

    private static String toString(List<SuggestionDTO> suggestionDTOS) {
        return suggestionDTOS.stream().map(s -> s.fl() + " (" + s.score() + ")").collect(Collectors.joining(","));
    }

    public static String toExplanationString(ProfileDTO pf) {
        return "Profil: \n" + toExplanationStringShort(pf, "\t") +
                "\n\tcentres d'intérêts: " + toExplanationString2(pf.interests(), "\t") + "\n" +
                "\n\tformations et métiers d'intérêt: " + toExplanationString(pf.suggApproved(), "\t") + "\n" +
                "\n\tcorbeille (refus / rejet): " + toExplanationString(pf.suggRejected(), "\t") + "\n" +
                '}';
    }

    public static String toExplanationStringShort(ProfileDTO pf, String sep) {
        return sep + "niveau: '" + pf.niveau() + "'\n" +
                sep + "bac: '" + pf.bac() + "'\n" +
                sep + "duree: '" + pf.duree() + "'\n" +
                sep + "apprentissage: '" + toApprentissageExplanationString(pf.apprentissage()) + "'\n" +
                sep + "geo_pref: " + pf.geo_pref() + "'\n" +
                sep + "spe_classes: " + pf.spe_classes() + "'\n" +
                sep + "moyenne générale auto-évaluée: '" + pf.moygen() + "'\n";
    }

    public static String toExplanationString(List<SuggestionDTO> suggestions, String sep) {
        if (suggestions == null) return sep;
        return suggestions.stream()
                .map(s -> getDebugLabel(s.fl()))
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
    }

    public static String toExplanationString2(List<String> interests, String sep) {
        return interests == null ? "" : interests.stream()
                .map(ServerData::getDebugLabel)
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
    }

    public static String toExplanations(List<String> list, String sep) {
        return list == null ? "" : list.stream()
                .map(ServerData::getDebugLabel)
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
    }

    static String toApprentissageExplanationString(String apprentissage) {
        if (apprentissage == null) return "Non-renseigné";
        if (apprentissage.equals("A")) return "Indifférent";
        if (apprentissage.equals("B")) return "Indifférent";
        if (apprentissage.equals("C")) return "Peu intéressé";
        return apprentissage;
    }


    public static void copyFile(Path src, Path dest) throws IOException {
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
    }
}

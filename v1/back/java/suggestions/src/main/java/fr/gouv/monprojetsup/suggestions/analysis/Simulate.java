package fr.gouv.monprojetsup.suggestions.analysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.algos.Suggestion;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static fr.gouv.monprojetsup.data.Helpers.isFiliere;
import static fr.gouv.monprojetsup.suggestions.analysis.ReferenceCases.ReferenceCase.useRemoteUrl;

public class Simulate {

    public static final Logger LOGGER = Logger.getLogger(Simulate.class.getName());
    public static final String REF_CASES_WITH_SUGGESTIONS = "refCasesWithSuggestions.json";

    private static final Integer RESTRICT_TO_INDEX = null;

    private static final boolean ONLY_FORMATIONS = true;

    public static void main(String[] args) throws Exception {


        //we want the server in debug mode, with full explanations
        useRemoteUrl(false);

        LOGGER.info("Loading config...");
        try {
            ServerData.statistiques = new PsupStatistiques();
            ServerData.statistiques.labels = Serialisation.fromJsonFile(
                    "labelsDebug.json",
                    Map.class
            );
        } catch (Exception e) {
            SuggestionServer server = new SuggestionServer();
            server.init();
        }

        ReferenceCases cases;
        LOGGER.info("Loading reference cases...");
        try {
            cases = ReferenceCases.loadFromFile("referenceCases.json");
        } catch (Exception e) {
            LOGGER.warning("Could not load reference cases, trying old format...");
            ReferenceCasesOld casesOld = Serialisation.fromJsonFile("referenceCases.json", ReferenceCasesOld.class);
            cases = casesOld.toReferencesCases();
            Serialisation.toJsonFile("referenceCases.json", cases, true);
        }


        LOGGER.info("Retrieving details and explanations...");
        ReferenceCases results = cases.getSuggestionsAndExplanations(RESTRICT_TO_INDEX);

        if(ONLY_FORMATIONS) {
            results.cases().forEach(referenceCase -> referenceCase.suggestions().removeIf(
                    suggestion -> !isFiliere(suggestion.fl())
            ));
        }

        LOGGER.info("Saving results...");
        results.toFile(REF_CASES_WITH_SUGGESTIONS);

    }

    public record ReferenceCasesOld(
            List<ReferenceCaseOld> cases
    ) {

        public ReferenceCasesOld() {
            this(new ArrayList<>());
        }

        public ReferenceCases toReferencesCases() {
            return new ReferenceCases(
                    cases.stream().map(ReferenceCaseOld::toReferenceCase).toList()
            );
        }

        public record ReferenceCaseOld(
                String name,
                ProfileDTOOld pf,
                List<String> expectations,

                //stores label to explanations
                List<Suggestion> suggestions
        ) {
            public ReferenceCases.ReferenceCase toReferenceCase() {
                return new ReferenceCases.ReferenceCase(
                        name,
                        pf.toProfileDTO(),
                        expectations,
                        suggestions
                );
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record ProfileDTOOld(

                @Schema(name = "niveau", description = "classe actuelle", example = "term", allowableValues = {"", "sec", "secSTHR", "secTMD", "prem", "term"})
                String niveau,
                @Schema(name = "bac", description = "type de Bac choisi ou envisagé", example = "Générale", allowableValues = {"", "Générale", "P", "PA", "S2TMD", "ST2S", "STAV", "STD2A", "STHR", "STI2D", "STL", "STMG"})
                String bac,
                @Schema(name = "duree", description = "durée envisagée des études", example = "long", allowableValues = {"", "court", "long", "indiff"})
                String duree,
                @Schema(name = "apprentissage", description = "intérêt pour les formations en apprentissage", example = "C", allowableValues = {"", "A", "B", "C", "D"})
                String apprentissage,
                @ArraySchema(arraySchema = @Schema(name = "geo_pref", description = "villes préférées pour étudier", example = "[\"Soulac-sur-Mer\",\"Nantes\"]"))
                Set<String> geo_pref,
                @ArraySchema(arraySchema = @Schema(name = "spe_classes", description = "enseignements de spécialité de terminale choisis ou envisagés", example = "[\"Sciences de la vie et de la Terre\",\"Mathématiques\"]"))
                Set<String> spe_classes,
                @ArraySchema(arraySchema = @Schema(name = "interests", description = "centres d'intérêt", example = "[\"T_ITM_1054\",\"T_ITM_1534\",\"T_ITM_1248\",\"T_ITM_1351\", \"T_ROME_2092381917\", \"T_IDEO2_4812\"]"))
                List<String> interests,
                @Schema(description = "moyenne générale scolaire estimée en terminale", example = "14")
                String moygen,
                @ArraySchema(arraySchema = @Schema(name = "choices", description = "sélection de formations, métiers et secteurs d'activité"))
                Map<String, SuggestionDTO> choices,

                @Schema(description = "statut de réflexion 0/1/2", example = "0")
                String statut

        ) {

            public ProfileDTO toProfileDTO() {
                return new ProfileDTO(
                        niveau,
                        bac,
                        duree,
                        apprentissage,
                        geo_pref,
                        spe_classes,
                        interests,
                        moygen,
                        choices.values().stream().toList(),
                        statut
                );
            }
        }
    }

}

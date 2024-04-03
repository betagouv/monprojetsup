package fr.gouv.monprojetsup.data.analysis.typesFormation;

import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.update.BackEndData;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.IOException;
import java.util.*;

public record MatchingTypesFormations(
        Map<String,String> gfrcodToSlug
) {

    public MatchingTypesFormations() {
        this(new HashMap<>());
    }

    private static final LevenshteinDistance levenAlgo = new LevenshteinDistance(10);

    public static void main(String[] args) throws IOException {
        MatchingTypesFormations matching;
        try {
            matching = Serialisation.fromJsonFile("matchingTypesformations.json", MatchingTypesFormations.class);
        } catch(Exception e) {
            matching = new MatchingTypesFormations();
        }

        Map<Integer, String> gfrCodes
                = new TreeMap<>(Serialisation
                .fromZippedJson(DataSources.getBackDataFilePath(), BackEndData.class)
                .psupData().formations().typesMacros);

        Serialisation.toJsonFile("typesFormationsPsup.json",
                gfrCodes,
                true
        );
        List<TypeFormationOni> typesformations =
                Serialisation.fromJsonFile(
                                DataSources.getSourceDataFilePath(DataSources.TYPES_FORMATIONS_ONISEP_PATH),
                                TypeFormationOni.Typesformations.class
                        )
                        .extraction().content();

        MatchingTypesFormations finalMatching = matching;
        gfrCodes.forEach((gFrCod, s) -> {
            Pair<String,Integer> best = typesformations.stream()
                    .filter(t -> t.descriptif_type_formation() != null)
                    .filter(t -> t.libelle() != null)
                    .map(
                            typeFormationOni -> Pair.of(typeFormationOni.slug(), levenAlgo.apply(s, typeFormationOni.libelle()))
                    )
                    .filter(p -> p.getRight() != null && p.getRight() >= 0)
                    .sorted(Comparator.comparing(t -> t.getRight()))
                    .findFirst()
                    .orElse(null);
            if(best != null) {
                finalMatching.gfrcodToSlug.put("fr" + gFrCod, best.getKey());
            }
        });

        TypeFormationOni.Typesformations typesMatched =  new TypeFormationOni.Typesformations();

        MatchingTypesFormations finalMatching1 = matching;
        matching.gfrcodToSlug.forEach((gFrCod, slug) -> {
            typesformations.stream()
                    .filter(t -> Objects.equals(t.slug(), slug))
                    .forEach(t -> {
                                typesMatched.extraction().content().add(
                                        new TypeFormationOni(
                                                t, gFrCod, gfrCodes.get(Integer.parseInt(gFrCod.substring(2)))
                                        )
                                );
                                finalMatching1.gfrcodToSlug.put(gFrCod, slug);
                            }
                    );
        });

        Serialisation.toJsonFile("typesMatched.json", typesMatched, true);
        Serialisation.toJsonFile("matchingTypesformations2.json", matching, true);

    }
}

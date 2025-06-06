package fr.gouv.monprojetsup.data.model.attendus;

import fr.gouv.monprojetsup.data.model.psup.PsupData;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record GrilleAnalyse(
        //same key than labels below
        Map<String,Integer> pcts
) {

    public static final String RES_ACA_FIELD = "RES_ACA";
    public static final String COM_ACA_FIELD = "COM_ACA";
    public static final String SAV_ETR_FIELD = "SAV_ETR";
    public static final String MOT_CON_FIELD = "MOT_CON";
    public static final String CEN_INT_FIELD = "ACT_CEN_INT";
    @NotNull
    public List<Integer> getCriteresFront() {
     return GrilleAnalyse.labelsFront.stream().map(label -> pcts.getOrDefault(label.getLeft(), 0)).toList();
    }


    public static Map<String, GrilleAnalyse> getGrilles(PsupData psupData) {
        return psupData.getGrillesAnalyseCandidatures();
    }

    public static Map<String, String> getLabelsMap() {
        return Map.of(
                RES_ACA_FIELD, "Résultats académiques",
                COM_ACA_FIELD, "Compétences académiques",
                SAV_ETR_FIELD, "Savoir-être",
                MOT_CON_FIELD, "Motivation, connaissance",
                CEN_INT_FIELD, "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires"
        );
    }
    public static final List<Pair<String, String>> labelsFront = List.of(
            Pair.of(RES_ACA_FIELD,  "Résultats académiques"),
            Pair.of(COM_ACA_FIELD,  "Compétences académiques"),
            Pair.of(SAV_ETR_FIELD,  "Savoir-être"),
            Pair.of(MOT_CON_FIELD,  "Motivation, connaissance"),
            Pair.of(CEN_INT_FIELD,  "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires")
    );

}

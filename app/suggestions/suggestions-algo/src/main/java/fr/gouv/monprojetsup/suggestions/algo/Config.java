package fr.gouv.monprojetsup.suggestions.algo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.TOUS_BACS_CODE_MPS;
import static java.util.Map.entry;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Config {
    public static final double SEUIL_TYPE_BAC_NO_MATCH = 0.01;
    public static final double SEUIL_TYPE_BAC_FULL_MATCH = 0.2;
    public static final double SEUIL_TYPE_BAC_FITTED = 0.5;
    public static final int DUREE_MAX = 5;
    public static final int DUREE_LONGUE = 4;
    public static final int DUREE_COURTE = 1;
    public static final String DUREE_COURTE_PROFILE_VALUE = "court";
    public static final String DUREE_LONGUE_PROFILE_VALUE = "long";
    public static final double MIN_SPEC_PCT_FOR_EXP = 0.1;
    public static final int ADMISSIBILITY_LOWEST_GRADE = 8;
    public static final int MAX_DISTANCE = 3;
    public static final int MIN_NB_TAGS_MATCH_FOR_PERFECT_FIT = 6;
    public static final double DISTANCE_KM_FOR_MAX_SCORE = 10.0;
    public static final double METIER_BONUS_TAG_MULTIPLIER = 2.0;
    static final double LASS_TO_PASS_INHERITANCE_PENALTY = 0.25;
    static final double EDGES_FORMATIONS_DOMAINES_WEIGHT = 0.01;//sachant que je m'intéresse à la formation, je m'intéresse un peu au domaine
    static final double EDGES_DOMAINES_FORMATIONS_WEIGHT = 1.0;//sachant que je m'intéresse au domaine, je m'intéresse à la formation
    static final double EDGES_METIERS_FORMATIONS_WEIGHT = 1.0;//sachant que je m'intéresse au métier, je m'intéresse à la formation
    static final double EDGES_FORMATIONS_METIERS_WEIGHT = 0.01;//sachant que je m'intéresse à la formation, je m'intéresse peut-être au métier
    static final double EDGES_DOMAINES_METIERS_WEIGHT = 0.01;
    static final double EDGES_INTERETS_METIERS_WEIGHT = 0.001;
    static final double EDGES_METIERS_ASSOCIES_WEIGHT = 0.10;
    static final String NOTHING_PERSONAL = "Nothing personal in the profile, serving nothing.";
    static final double ZERO_ADMISSIBILITY = 0.001;
    static final double ADMISSIBILITY_10 = 0.1;//at the first décile
    static final double ADMISSIBILITY_25 = 0.3;//at the 25 and 75 quartile
    static final double ADMISSIBILITY_50 = 0.9;//at the 25 and 75 quartile
    static final double ADMISSIBILITY_75 = 1.0;//at the 25 and 75 quartile
    static final double ADMISSIBILITY_90 = 1.0;//above the last decile
    static final double MAX_SCORE_PATH_LENGTH_2 = 1.0;
    static final double MAX_SCORE_PATH_LENGTH_3 = 0.25;

    @Getter
    @Setter
    public long DiversityShortListLength = 10L;

    @Getter
    @Setter
    public double diversityMultiplicativeMalusBacGen = 0.2;

    @Getter
    @Setter
    public double diversityMultiplicativeMalusBacPro = 0.9;

    @Getter
    @Setter
    public double diversityMultiplicativeMalusBacTechno = 0.6;

    @Getter
    @Setter
    private int verbosityLevel = 0;

    @Getter
    @Setter
    private boolean useAutoEvalMoyGen = false;

    public static final String BONUS_APPRENTISSAGE = "app";
    public static final String BONUS_TAGS = "tags";
    public static final String BONUS_SIM = "sim";
    public static final String BONUS_VOEU_FAVORI = "fav";
    public static final String BONUS_DURATION = "duration";
    public static final String BONUS_GEO = "geo";
    public static final String BONUS_TYPE_BAC = "typebac";
    public static final String BONUS_SPECIALITE = "spec";
    public static final String BONUS_SPECIALITE_BAC_PRO = "spec_bac_pro";
    public static final String BONUS_MOY_GEN = "moygen";
    public static final double NO_MATCH_SCORE = 0.0;

    public static final double FULL_MATCH_MULTIPLIER = 1.0;

    public static final Map<String,String> BONUS_LABELS = Map.of(
            BONUS_TAGS,"proximité intérêts et favoris",
            BONUS_SPECIALITE,"EDS",
            BONUS_SPECIALITE_BAC_PRO, "spécialité",
            BONUS_DURATION,"durée",
            BONUS_SIM,"similarité avec autres favoris",
            BONUS_VOEU_FAVORI,"voeu favori",
            BONUS_MOY_GEN,"moyenne générale",
            BONUS_TYPE_BAC,"type de bac",
            BONUS_APPRENTISSAGE,"preférences apprentissage",
            BONUS_GEO,"préférences géographiques"
            );

    static final double MULTIPLIER_FOR_NOSTATS_BAC = 0.01;
    static final double MULTIPLIER_FOR_UNFITTED_BAC = 1.0E-08;
    static final double MULTIPLIER_FOR_UNFITTED_TAGS = 1.0E-08;
    static final double MULTIPLIER_FOR_UNFITTED_APP = 1.0E-05;
    static final double MULTIPLIER_FOR_UNFITTED_GEO = 1.0E-04;
    static final double MULTIPLIER_FOR_UNFITTED_DURATION = 1.0E-04;
    static final double MULTIPLIER_FOR_UNFITTED_SIM = 1.0E-03;
    static final double MULTIPLIER_FOR_UNFITTED_SPEC = 1.0E-03;
    static final double MULTIPLIER_FOR_UNFITTED_SPEC_BAC_PRO = 1.0E-08;
    static final double MULTIPLIER_FOR_UNFITTED_NOTES = 1.0E-01;
    static final double MULTIPLIER_FOR_UNFITTED_VOEU_FAVORI = 1.0E-09;

    @JsonIgnore
    private final transient Map<String, Double> minMultipliers = new HashMap<>(Map.ofEntries(
            entry(BONUS_TYPE_BAC, MULTIPLIER_FOR_UNFITTED_BAC),
            entry(BONUS_TAGS, MULTIPLIER_FOR_UNFITTED_TAGS),
            entry(BONUS_APPRENTISSAGE, MULTIPLIER_FOR_UNFITTED_APP),
            entry(BONUS_DURATION, MULTIPLIER_FOR_UNFITTED_DURATION),
            entry(BONUS_GEO, MULTIPLIER_FOR_UNFITTED_GEO),
            entry(BONUS_SIM, MULTIPLIER_FOR_UNFITTED_SIM),
            entry(BONUS_SPECIALITE, MULTIPLIER_FOR_UNFITTED_SPEC),
            entry(BONUS_SPECIALITE_BAC_PRO, MULTIPLIER_FOR_UNFITTED_SPEC_BAC_PRO),
            entry(BONUS_MOY_GEN, MULTIPLIER_FOR_UNFITTED_NOTES),
            entry(BONUS_VOEU_FAVORI, MULTIPLIER_FOR_UNFITTED_VOEU_FAVORI)
    ));
    @JsonIgnore
    private final transient List<String> personalCriteria = new ArrayList<>(
            List.of(BONUS_TAGS, BONUS_SIM, BONUS_VOEU_FAVORI)
    );


    @JsonIgnore
    public Map<String, Double> minMultipliers() {
        return minMultipliers;
    }

    @JsonIgnore
    public List<String> personalCriteria() {
        return personalCriteria;
    }

    @JsonIgnore
    public boolean isVerbose() {
        return verbosityLevel >= 1;
    }

    @JsonIgnore
    public boolean isVeryVerbose() {
        return verbosityLevel >= 2;
    }

    public double getDiversityMultiplicativeMalusBacGen(String bac) {
        if(bac == null || bac.isEmpty() || bac.startsWith("G") || bac.equals(TOUS_BACS_CODE_MPS)) {
            return diversityMultiplicativeMalusBacGen;
        }
        if(bac.startsWith("P")) {
            return diversityMultiplicativeMalusBacPro;
        }
        return diversityMultiplicativeMalusBacTechno;
    }
}

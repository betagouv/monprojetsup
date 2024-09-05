package fr.gouv.monprojetsup.suggestions.algo;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static java.util.Map.entry;

public final class Config {
    public static final double SEUIL_TYPE_BAC_NO_MATCH = 0.001;
    public static final double SEUIL_TYPE_BAC_FULL_MATCH = 0.2;
    public static final double SEUIL_TYPE_BAC_FITTED = 0.5;
    public static final int MAX_NG_SUGGESTIONS = 20;
    static final int MAX_LENGTH_FOR_SUGGESTIONS = 3;
    //because LAS informatique is a plus but not the canonical path to working as a surgeon for example
    static final double LASS_TO_PASS_INHERITANCE_PENALTY = 0.25;
    static final double EDGES_INTERETS_METIERS_WEIGHT = 0.001;
    static final double EDGES_SECTEUR_METIERS_WEIGHT = 0.01;
    static final double EDGES_METIERS_ASSOCIES_WEIGHT = 0.75;
    static final String NOTHING_PERSONAL = "Nothing personal in the profile, serving nothing.";
    static final double MAX_AFFINITY_PERCENT = 0.90;
    static final int PATHES_CACHE_SIZE = 1000;  //1000 is enough ?

    @Getter
    @Setter
    private boolean verboseMode = false;

    public Config() {
    }

    public static final String BONUS_APPRENTISSAGE = "app";
    public static final String BONUS_TAGS = "tags";
    public static final String BONUS_SIM = "sim";
    public static final String BONUS_DURATION = "duration";
    public static final String BONUS_GEO = "geo";
    public static final String BONUS_MENTION_BAC = "bac";

    public static final String BONUS_TYPE_BAC = "typebac";
    public static final String BONUS_SPECIALITE = "spec";
    public static final String BONUS_MOY_GEN = "moygen";
    public static final double NO_MATCH_SCORE = 0.0;

    public static final Map<String,String> BONUS_LABELS = Map.of(
            BONUS_TAGS,"mots-clés",
            BONUS_SPECIALITE,"EDS",
            BONUS_DURATION,"durée",
            BONUS_SIM,"similarité avec autres favoris",
            BONUS_MOY_GEN,"moyenne générale",
            BONUS_TYPE_BAC,"type de bac",
            BONUS_APPRENTISSAGE,"preférences apprentissage",
            BONUS_GEO,"préférences géographiques",
            BONUS_MENTION_BAC,"moyenne au bac"
            );

    /* sspecial wiehg tused to indicate that we use the score has a multiplier rather than addition */
    public static final double SPECIAL_WEIGHT_MULTIPLIER = 0.0000007777777;
    public static final double NEUTRAL_MULTIPLIER = SPECIAL_WEIGHT_MULTIPLIER;

    private final Map<String, Double> weights = new HashMap<>(Map.ofEntries(
            entry(BONUS_APPRENTISSAGE, 500.0),
            entry(BONUS_TAGS, 1000.0),
            entry(BONUS_SIM, 25.0),
            entry(BONUS_DURATION, 500.0),
            entry(BONUS_GEO, 50.0),
            entry(BONUS_MENTION_BAC, 10.0),
            entry(BONUS_TYPE_BAC, SPECIAL_WEIGHT_MULTIPLIER),//PRIS EN COMPTE MULTIPLICATIVEMENT
            entry(BONUS_MOY_GEN, SPECIAL_WEIGHT_MULTIPLIER),
            entry(BONUS_SPECIALITE, 20.0)
    ));
    private final List<String> personalCriteria = new ArrayList<>(
            List.of(BONUS_TAGS, BONUS_SIM)
    );


    public int maxNbSuggestions() {
        return MAX_NG_SUGGESTIONS;//we force the value
    }

    public Map<String, Double> weights() {
        return weights;
    }

    public List<String> personalCriteria() {
        return personalCriteria;
    }

}

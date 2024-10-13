package fr.gouv.monprojetsup.suggestions.algo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.suggestions.algo.Config.FULL_MATCH_MULTIPLIER;
import static fr.gouv.monprojetsup.suggestions.algo.Config.NO_MATCH_SCORE;

@SuppressWarnings("unused")
public record Affinite(
        double affinite,
        Map<String,Double> scoresAdequationProfil,

        EnumMap<SuggestionQuota, Double> scoresDiversiteResultats

) {

    public static final String CHAMP_SCORE_AGGREGE = "aggrege";

    public boolean satisfiesAllOf(Map<SuggestionQuota, Double> minScoreForQuota) {
        return affinite > NO_MATCH_SCORE && scoresDiversiteResultats.entrySet().stream().allMatch(e -> e.getValue() >= minScoreForQuota.get(e.getKey()));
    }

    public boolean satisfiesOneOf(Map<SuggestionQuota, Double> minScoreForQuota) {
        return affinite > NO_MATCH_SCORE && scoresDiversiteResultats.entrySet().stream().anyMatch(e -> e.getValue() >= minScoreForQuota.get(e.getKey()));
    }

    public int getNbQuotasSatisfied(Map<SuggestionQuota, Double> minScoreForQuotaSatisfaction) {
        return (int) scoresDiversiteResultats.entrySet().stream().filter(e -> e.getValue() >= minScoreForQuotaSatisfaction.get(e.getKey())).count();
    }

    public Map<String, Double> toScoreDetailsMap() {
        HashMap<String,Double> result = new HashMap<>(scoresAdequationProfil);
        quotas.forEach((suggestionQuota, aDouble)
                -> result.put(suggestionQuota.name(), scoresDiversiteResultats.get(suggestionQuota))
        );
        result.put(CHAMP_SCORE_AGGREGE, affinite);
        return result;
    }

    public enum SuggestionQuota {
        OFFRE_FORMATION,
        PEU_CONNUE
    }

    public static final EnumMap<SuggestionQuota, Double> quotas;

    static {
        quotas = new EnumMap<>(SuggestionQuota.class);
        quotas.put(SuggestionQuota.OFFRE_FORMATION, 0.75);
        quotas.put(SuggestionQuota.PEU_CONNUE, 0.5);
    }

    public static Affinite getNoMatch() {
        return new Affinite(NO_MATCH_SCORE, Map.of(), new EnumMap<>(SuggestionQuota.class));
    }

    public static Affinite round(Affinite aff, double finalMaxScore) {
        double newAffinite = roundScore(aff.affinite, finalMaxScore);
        Map<String,Double> newScoresAdequationProfil = aff.scoresAdequationProfil.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> roundScore(e.getValue(), FULL_MATCH_MULTIPLIER)
                ));
        return new Affinite(newAffinite, newScoresAdequationProfil, aff.scoresDiversiteResultats);
    }

    private static double roundScore(double affinite, double finalMaxScore) {
        return Math.max(0.0, Math.min(1.0, Math.round((affinite / finalMaxScore) * 10e6) / 10e6));
    }

    public @NotNull Affinite max(@Nullable Affinite affinite) {
        if (affinite == null) return this;
        if (affinite.affinite() > this.affinite) return affinite;
        return this;
    }


}

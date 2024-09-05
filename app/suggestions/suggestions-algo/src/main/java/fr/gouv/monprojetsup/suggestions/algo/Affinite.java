package fr.gouv.monprojetsup.suggestions.algo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

import static fr.gouv.monprojetsup.suggestions.algo.Config.NO_MATCH_SCORE;

@SuppressWarnings("unused")
public record Affinite(
        double affinite,
        EnumMap<SuggestionDiversityQuota, Double> scores
) {

    public boolean satisfiesAllOf(Map<SuggestionDiversityQuota, Double> minScoreForQuota) {
        return affinite > NO_MATCH_SCORE && scores.entrySet().stream().allMatch(e -> e.getValue() >= minScoreForQuota.get(e.getKey()));
    }

    public boolean satisfiesOneOf(Map<SuggestionDiversityQuota, Double> minScoreForQuota) {
        return affinite > NO_MATCH_SCORE && scores.entrySet().stream().anyMatch(e -> e.getValue() >= minScoreForQuota.get(e.getKey()));
    }

    public int getNbQuotasSatisfied(Map<SuggestionDiversityQuota, Double> minScoreForQuota) {
        if (affinite <= NO_MATCH_SCORE) return 0;
        return (int) scores.entrySet().stream().filter(e -> e.getValue() >= minScoreForQuota.get(e.getKey())).count();
    }

    public enum SuggestionDiversityQuota {
        NOT_SMALL,
        NOT_BIG,
        BAC,
        MOYGEN
    }

    public static final EnumMap<SuggestionDiversityQuota, Double> quotas;

    static {
        quotas = new EnumMap<>(SuggestionDiversityQuota.class);
        quotas.put(SuggestionDiversityQuota.BAC, 0.9);
        quotas.put(SuggestionDiversityQuota.NOT_SMALL, 0.75);
        quotas.put(SuggestionDiversityQuota.NOT_BIG, 0.5);
        quotas.put(SuggestionDiversityQuota.MOYGEN, 0.5);
    }

    public static Affinite getNoMatch() {
        return new Affinite(NO_MATCH_SCORE, new EnumMap<>(SuggestionDiversityQuota.class));
    }

    public static Affinite round(Affinite aff, double finalMaxScore) {
        double newAffinite = Math.max(0.0, Math.min(1.0, Math.round((aff.affinite / finalMaxScore) * 10e6) / 10e6));
        return new Affinite(newAffinite, aff.scores);
    }

    public @NotNull Affinite max(@Nullable Affinite affinite) {
        if (affinite == null) return this;
        if (affinite.affinite() > this.affinite) return affinite;
        return this;
    }


}

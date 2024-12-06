package fr.gouv.monprojetsup.suggestions.algo;

import fr.gouv.monprojetsup.data.model.Ville;
import fr.gouv.monprojetsup.data.model.stats.Middle50;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.Constants;
import fr.gouv.monprojetsup.suggestions.data.model.Path;
import fr.gouv.monprojetsup.suggestions.dto.ChoiceDTO;
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.dto.explanations.Explanation;
import fr.gouv.monprojetsup.suggestions.dto.explanations.ExplanationGeo;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.isFiliere;
import static fr.gouv.monprojetsup.data.Constants.isMetier;
import static fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.TOUS_BACS_CODE_MPS;
import static fr.gouv.monprojetsup.suggestions.algo.Config.ADMISSIBILITY_10;
import static fr.gouv.monprojetsup.suggestions.algo.Config.ADMISSIBILITY_25;
import static fr.gouv.monprojetsup.suggestions.algo.Config.ADMISSIBILITY_50;
import static fr.gouv.monprojetsup.suggestions.algo.Config.ADMISSIBILITY_75;
import static fr.gouv.monprojetsup.suggestions.algo.Config.ADMISSIBILITY_90;
import static fr.gouv.monprojetsup.suggestions.algo.Config.ADMISSIBILITY_LOWEST_GRADE;
import static fr.gouv.monprojetsup.suggestions.algo.Config.BONUS_LABELS;
import static fr.gouv.monprojetsup.suggestions.algo.Config.BONUS_MOY_GEN;
import static fr.gouv.monprojetsup.suggestions.algo.Config.BONUS_TAGS;
import static fr.gouv.monprojetsup.suggestions.algo.Config.DUREE_COURTE;
import static fr.gouv.monprojetsup.suggestions.algo.Config.DUREE_LONGUE_PROFILE_VALUE;
import static fr.gouv.monprojetsup.suggestions.algo.Config.FULL_MATCH_MULTIPLIER;
import static fr.gouv.monprojetsup.suggestions.algo.Config.MAX_DISTANCE;
import static fr.gouv.monprojetsup.suggestions.algo.Config.MAX_SCORE_PATH_LENGTH_2;
import static fr.gouv.monprojetsup.suggestions.algo.Config.MAX_SCORE_PATH_LENGTH_3;
import static fr.gouv.monprojetsup.suggestions.algo.Config.MIN_SPEC_PCT_FOR_EXP;
import static fr.gouv.monprojetsup.suggestions.algo.Config.MULTIPLIER_FOR_NOSTATS_BAC;
import static fr.gouv.monprojetsup.suggestions.algo.Config.NO_MATCH_SCORE;
import static fr.gouv.monprojetsup.suggestions.algo.Config.ZERO_ADMISSIBILITY;
import static java.util.Map.entry;

/**
 * Evaluator of affinities between a profile and a formation
 * The profile is fixed.
 * Some data are precomputed to be reused for all formations.
 */
public class AffinityEvaluator {

    /* entry point to the algo and its data sources */
    private final AlgoSuggestions algo;

    /* configuration of recommandations */
    private final Config cfg;

    /* the profile */
    private final ProfileDTO pf;

    /***** data precomputed from the profile ***********/
    /* true iff something in the profile is related to health. If not LAS are deprioritized.  */
    private final boolean isInterestedinHealth;

    /* bac of the profile.  */
    private final @NotNull String bac;

    private final boolean isBacPro;

    /* estimate of moygen.  */
    private Double moyGenEstimee;

    /* list of formations selected.  */
    private final Set<String> flApproved;

    /* list of formations connected to a voeux favoris.  */
    private final Map<String,List<String>> flConnectedToVoeuxFavori;

    /* list of formations in the bin.  */
    private final Set<String> rejected =  new HashSet<>();

    /* precomputed candidates for  formations similar to selected formations */
    private final Set<String> candidatsSimilaires;

    /* precomputed pathes to interests */
    private final Map<String, List<Path>> pathesFromTagsIndexedByTarget;



    public AffinityEvaluator(ProfileDTO pf, Config cfg, AlgoSuggestions algo, boolean excludeRejected) {
        this.cfg = cfg;
        this.pf = pf;
        this.algo = algo;

        String pfBac = pf.bac();
        if(pfBac == null || pfBac.isBlank()) pfBac = TOUS_BACS_CODE_MPS;
        this.bac = pfBac;
        this.isBacPro = pfBac.equals("P") || pfBac.equals("PA");

        try {
            this.moyGenEstimee
                    = (pf.moygen() == null || pf.moygen().isBlank())
                    ? null
                    : Double.parseDouble(pf.moygen())
            ;
        } catch (NumberFormatException ignored) {
            this.moyGenEstimee = null;
        }

        //computing filieres we do not want to give advice about
        //because they are already in the profile
        List<ChoiceDTO> approved = pf.suggApproved();
        this.flApproved = approved.stream()
                .filter(s -> s.score() == null || s.score() >= 3)
                .map(ChoiceDTO::id).filter(Constants::isMpsFormation).collect(Collectors.toSet());

        val voeux = pf.choix().stream()
                .filter(ChoiceDTO::isApproved)
                .map(ChoiceDTO::id)
                .filter(fr.gouv.monprojetsup.data.Constants::isVoeu)
                .toList();
        this.flConnectedToVoeuxFavori = new HashMap<>(algo.getFormationsConnectedToVoeux(voeux));

        List<ChoiceDTO> rejectedSuggestions = pf.suggRejected();

        if(excludeRejected) {
            rejected.addAll(rejectedSuggestions.stream().map(ChoiceDTO::id).toList());
        }

        //precomputing candidats for filieres similaires
        candidatsSimilaires = flApproved.stream().flatMap(
                fl -> algo.getFormationsSimilaires(fl, pf.bacIndex()).keySet().stream()
        ).collect(Collectors.toSet());

        //precomputing distance to tags

        //centres d'intérêts
        Set<String> nonZeroScores = new HashSet<>();

        if(pf.interests() != null) nonZeroScores.addAll(pf.interests());

        //autres formations
        nonZeroScores.addAll(approved.stream().map(ChoiceDTO::id).toList());

        isInterestedinHealth = algo.isRelatedToHealth(nonZeroScores);

        //tag --> node --> distance
        //noinspection DataFlowIssue
        pathesFromTagsIndexedByTarget =
                nonZeroScores.stream()
                        .flatMap(
                                n -> algo
                                        .computePathesFrom(n, MAX_DISTANCE)
                                        .stream()
                        )
                        .collect(
                                Collectors.groupingBy(
                                        Path::last
                                ));
    }


    /**
     * computes affinity
     * @param fl key
     * @param inclureDetailsScores include scores details in result
     * @return affinity
     */
    public Affinite getAffinityEvaluation(String fl, boolean inclureDetailsScores) {
        if (rejected.contains(fl)) return Affinite.getNoMatch();
        return getAffinityAndExplanations(fl, null, null, inclureDetailsScores);
    }


    public record Explanations(
            List<Explanation> explanations
    ) {
        public Explanations() {
            this(new ArrayList<>());
        }

        public void add(Explanation expl) {
            explanations.add(expl);
        }

    }



    private static final DecimalFormat df = new DecimalFormat("#.#####");
    private static final DecimalFormat df2 = new DecimalFormat("#.#E0");

    /**
     * get explanations, including debug explanations if needed
     * @param fl key
     * @return list of explanations
     */
    public Pair<List<Explanation>, Double> getExplanations(String fl) {

        //en verbose mode, on récupère également les interests
        TreeMap<String, Double> subScores = cfg.isVerbose() ? new TreeMap<>() : null;

        var sortedExpl = new Explanations();

        //the computation
        Affinite affinite = getAffinityAndExplanations(fl, sortedExpl, subScores, true);

        if (cfg.isVerbose() && subScores != null) {
            List<Explanation> expl2 = new ArrayList<>(sortedExpl.explanations);

            //expl2.add(Explanation.getDebugExplanation("Score Total: " + df2.format(affinite.affinite())));

            StringBuilder calculScoreDetails = new StringBuilder();
            calculScoreDetails.append("Score Total pour ").append(fl).append(" :");
            calculScoreDetails.append(df2.format(affinite.affinite()));
            calculScoreDetails.append(" obtenu comme le produit de [ ");

            List<Map.Entry<String, Double>> entries = new ArrayList<>(subScores.entrySet());
            entries.sort(Comparator.comparing(e -> -e.getValue()));
            entries.forEach(e -> {
                val key = e.getKey();
                if(!key.equals(BONUS_MOY_GEN) || cfg.isUseAutoEvalMoyGen()) {
                    double weight = cfg.minMultipliers().get(key);
                    val label = BONUS_LABELS.getOrDefault(e.getKey(), e.getKey());
                    expl2.add(Explanation.getDebugExplanation(
                            label
                                    + " " + df.format(e.getValue()) + " * (1 - " + df2.format(weight) + ") + " + df2.format(weight)
                    ));
                    calculScoreDetails.append(" ");
                    calculScoreDetails.append(df.format(getMultiplier(e.getKey(), e.getValue())));
                    calculScoreDetails.append(" (");
                    calculScoreDetails.append(label);
                    calculScoreDetails.append(") , ");
                }
            });
            calculScoreDetails.append(" ]");

            expl2.add(Explanation.getDebugExplanation("Scores de diversité: " + affinite.scoresDiversiteResultats()));
            expl2.add(Explanation.getDebugExplanation(calculScoreDetails.toString()));

            sortedExpl = new Explanations(expl2);
        }
        return Pair.of(sortedExpl.explanations, affinite.affinite());
    }



    /**
     * Calcule les interests affinités entre un profil et une filière
     *
     * @param fl      la filière considérée
     * @param expl    les explications, à compléter si expl != null
     * @param subScores used to get debug info about what matched and how
     * @return the score
     */
    private Affinite getAffinityAndExplanations(
            String fl,
            Explanations expl,
            @Nullable Map<String, Double> subScores,
            boolean includeScores
            ) {

         if(rejected.contains(fl)) return Affinite.getNoMatch();

        /* LAS filter: is the formation is a LAS and santé was not checked, it is not proposed */
        if (algo.isLas(fl) && !isInterestedinHealth) {
            return Affinite.getNoMatch();
        }

        /*
         * map des critères vers des doubles
         */
        Map<String, Double> scores = new HashMap<>(
                Map.ofEntries(
                        entry(Config.BONUS_SIM, getBonusSimilaires(fl, pf.bacIndex(), expl)),
                        entry(Config.BONUS_VOEU_FAVORI, getBonusVoeuxFavori(fl, expl)),
                        entry(BONUS_TAGS, getBonusTags(fl, expl))
                )
        );

        //règle: si pas d'accroche sur les critères personnalisés, et pas de demande d'explications
        // alors on ne traite pas, pour économiser du CPU.
        boolean process = expl != null
                || scores.entrySet().stream()
                .anyMatch(e -> cfg.personalCriteria().contains(e.getKey()) && e.getValue() > Config.NO_MATCH_SCORE);
        if (!process) return Affinite.getNoMatch();

        if(isFiliere(fl)) {
            scores.putAll(Map.ofEntries(
                    entry(Config.BONUS_GEO, getBonusGeographicAffinity(fl, expl)),
                    entry(Config.BONUS_DURATION, getBonusDuree(fl, expl)),
                    entry(Config.BONUS_APPRENTISSAGE, getBonusApprentissage(fl, expl)),
                    entry(Config.BONUS_SPECIALITE, getBonusSpecialites(fl, expl)),
                    entry(Config.BONUS_TYPE_BAC, getBonusTypeBac(fl, expl)),
                    entry(Config.BONUS_MOY_GEN, getBonusMoyGen2(fl, expl))
                )
            );
        }
        if(isBacPro) {
            //plus de poids sur ce critère de la spécialité en bac pro
            scores.put(Config.BONUS_SPECIALITE_BAC_PRO, getBonusSpecialites(fl, expl));
        } else {
            scores.put(Config.BONUS_SPECIALITE, getBonusSpecialites(fl, expl));
        }

        double score = aggregateScores(scores);

        //put interests in expl, if required
        if (subScores != null && cfg.isVerbose()) {
            subScores.putAll(scores.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        EnumMap<Affinite.SuggestionQuota, Double> quotas = new EnumMap<>(Affinite.SuggestionQuota.class);
        double notSmallDiversity = getNotSmallDiversityScore(fl);
        quotas.put(Affinite.SuggestionQuota.OFFRE_FORMATION, notSmallDiversity);

        return new Affinite(score, includeScores ? scores : Map.of(), quotas);
    }

    private double aggregateScores(Map<String, Double> scores) {
        double score = FULL_MATCH_MULTIPLIER;

        //on fait la somme pondérée des interests additifs
        for(Map.Entry<String, Double> e : scores.entrySet()) {
            String key = e.getKey();
            Double value = e.getValue();
            score *= getMultiplier(key, value);
        }

        return score;
    }


    private double getMultiplier(String key, Double value) {
        val minMultiplier = cfg.minMultipliers().get(key);
        if (minMultiplier == null) throw new RuntimeException("Unknown key:" + key);
        value = Math.max(NO_MATCH_SCORE, Math.min(FULL_MATCH_MULTIPLIER, value));
        return minMultiplier + (1.0 - minMultiplier) * value;
    }



    private double getBonusTypeBac(String grp, Explanations expl) {
        if (bac.equals(TOUS_BACS_CODE_MPS)) return MULTIPLIER_FOR_NOSTATS_BAC;
        Integer nbAdmisTousBac = algo.getNbAdmis(grp, TOUS_BACS_CODE_MPS);
        Integer nbAdmisBac = algo.getNbAdmis(grp, bac);
        if(nbAdmisTousBac != null && nbAdmisBac == null) return NO_MATCH_SCORE;
        if (nbAdmisBac == null || nbAdmisTousBac == null) return MULTIPLIER_FOR_NOSTATS_BAC;
        double percentage = FULL_MATCH_MULTIPLIER * nbAdmisBac / nbAdmisTousBac;
        if (percentage <= Config.SEUIL_TYPE_BAC_NO_MATCH) return Config.NO_MATCH_SCORE;
        final double bonus;
        if (percentage >= Config.SEUIL_TYPE_BAC_FULL_MATCH)
            bonus = FULL_MATCH_MULTIPLIER;
        else
            bonus = (percentage - Config.SEUIL_TYPE_BAC_NO_MATCH) / (Config.SEUIL_TYPE_BAC_FULL_MATCH - Config.SEUIL_TYPE_BAC_NO_MATCH);
        if (expl != null && percentage >= Config.SEUIL_TYPE_BAC_FITTED) {
            expl.add(Explanation.getTypeBacExplanation((int) (100 * percentage), bac));
        }
        return bonus;
    }


    private double getBonusMoyGen2(String fl, Explanations expl) {
        if (moyGenEstimee == null) return FULL_MATCH_MULTIPLIER;
        val stats = algo.getStatsBac(fl, this.bac);
        return getBonusNotes(expl, stats, moyGenEstimee);
    }

    private double getBonusNotes(
            @Nullable Explanations expl,
            @Nullable Pair<String, Middle50> stats,
            double autoEval) {
        //on va chercher les stats pour ce type de bac et cette filiere
        if (stats == null || stats.getRight() == null) {
            if (expl != null && cfg.isUseAutoEvalMoyGen() &&cfg.isVerbose())
                expl.add(Explanation.getDebugExplanation("Pas de stats pour cette filiere"));
            return FULL_MATCH_MULTIPLIER;
        }
        String bacUtilise = stats.getLeft();
        Middle50 middle50 = stats.getRight();
        if (middle50 == null) {
            if (expl != null && cfg.isUseAutoEvalMoyGen() && cfg.isVerbose())
                expl.add(Explanation.getDebugExplanation("Pas de middle50 pour cette filiere"));
            return FULL_MATCH_MULTIPLIER;
        }
        double bonus = computeBonusNotes(stats, autoEval);
        //affiché systématiquement
        if (expl != null) {
            expl.add(Explanation.getNotesExplanation(autoEval, middle50, bacUtilise));
        }
        return bonus;
    }

    private double computeBonusNotes(Pair<String, Middle50> stats, double autoEval) {
        final Middle50 middle50 = stats.getRight();
        int noteMaxInt = middle50.rangMax();
        int note = (int) (noteMaxInt * autoEval / 40);
        //avantage aux details plus exigeantes
        return (0.7 * computeAdmissibiliteNotes(middle50, note, noteMaxInt)
                + 0.3 * computeProximiteNotes(middle50, note));
    }

    private double computeAdmissibiliteNotes(Middle50 middle50, int note, int noteMaxInt) {
        int lowestGrade = ADMISSIBILITY_LOWEST_GRADE * noteMaxInt / 20;
        if(note <= lowestGrade) {
            return ZERO_ADMISSIBILITY;
        } else if(note <= middle50.rangEch10()) {
            return ADMISSIBILITY_10 * coef(note, lowestGrade, middle50.rangEch10());
        } else if(note <= middle50.rangEch25()) {
            return ADMISSIBILITY_10 + (ADMISSIBILITY_25 - ADMISSIBILITY_10) * coef(note, middle50.rangEch10(), middle50.rangEch25());
        } else if(note <= middle50.rangEch50()) {
            return ADMISSIBILITY_25 + (ADMISSIBILITY_50 - ADMISSIBILITY_25) * coef(note, middle50.rangEch25(), middle50.rangEch75());
        } else if(note <= middle50.rangEch75()) {
            return ADMISSIBILITY_50 + (ADMISSIBILITY_75 - ADMISSIBILITY_50) * coef(note, middle50.rangEch25(), middle50.rangEch75());
        } else if(note <= middle50.rangEch90()) {
            return ADMISSIBILITY_75 + (ADMISSIBILITY_90 - ADMISSIBILITY_75) * coef(note, middle50.rangEch75(), middle50.rangEch90());
        } else {
            return ADMISSIBILITY_90;
        }
    }

    private double computeProximiteNotes(Middle50 middle50, int note) {
        if (note >= middle50.rangEch90()) {
            return 0.0;
        } else if (note < middle50.rangEch10()) {
            return 0.0;
        } else if(note >= middle50.rangEch25() && note <= middle50.rangEch75()) {
            return 1.0;
        } else if(note <= middle50.rangEch25()) {
            return coef(note, middle50.rangEch10(), middle50.rangEch25());
        } else {
            return coef(note, middle50.rangEch75(), middle50.rangEch25());
        }
    }

    private double coef(int note, int zero, int one) {
        if(one == zero) return 0.5;
        double result =  ((double) (note - zero) ) / ((double) (one - zero));
        return Math.max(0.0, Math.min(1.0, result));
    }

    protected double getBonusGeographicAffinity(String fl, Explanations expl) {
        //pour chaque filiere, on scanne toutes les details et on calcule la distance min ç chaque ville
        double bonus = Config.NO_MATCH_SCORE;

        /* geo_pref is not null in theory but not in practice, because of what I consider a broken feature of GSon deserialization
        on the kotlin / spring side */
        if (pf.geo_pref() == null || pf.geo_pref().isEmpty()) return bonus;

        for (String cityName : pf.geo_pref()) {
            val result = getGeoExplanations(fl, cityName);
            int distanceKm = result.stream().mapToInt(ExplanationGeo::distance).min().orElse(-1);
            if (distanceKm >= 0) {
                double distanceKmIncludingAdmissibleTransport = Math.max(0.0, distanceKm - Config.DISTANCE_KM_FOR_MAX_SCORE);
                bonus += 1.0 / (1.0 + distanceKmIncludingAdmissibleTransport);
                if (expl != null && distanceKm < 50) {
                    expl.add(Explanation.getGeoExplanation(result));
                }
            }
        }
        return bonus;
    }

    private List<ExplanationGeo> getGeoExplanations(String fl, String nomVille) {
        Ville ville = algo.getVille(nomVille);
        if(ville == null) return List.of();
        return ExplanationGeo.getGeoExplanations(
                ville,
                algo.getVoeuxCoords(fl)
        );
    }

    private double getBonusDuree(String fl, Explanations expl) {
        if (pf.duree() == null) return Config.NO_MATCH_SCORE;
        int duree = algo.getDuree(fl);
        duree = Math.min(Config.DUREE_MAX, duree);
        duree = Math.max(DUREE_COURTE, duree);

        final double result;
        switch (pf.duree().toLowerCase()) {
            case Config.DUREE_COURTE_PROFILE_VALUE -> {
                if(duree >= Config.DUREE_LONGUE) {
                    result = NO_MATCH_SCORE;
                } else {
                    result =
                            FULL_MATCH_MULTIPLIER
                            * (Config.DUREE_LONGUE - duree)
                            / (Config.DUREE_LONGUE - DUREE_COURTE);
                    if (expl != null)
                        expl.add(Explanation.getDurationExplanation(pf.duree()));
                }
            }
            case DUREE_LONGUE_PROFILE_VALUE -> {
                if(duree < Config.DUREE_LONGUE) {
                    result = Config.NO_MATCH_SCORE;
                } else {
                    result =
                            FULL_MATCH_MULTIPLIER
                            * (duree - (Config.DUREE_LONGUE - 1) )
                            / (Config.DUREE_MAX - (Config.DUREE_LONGUE - 1) );
                    if (expl != null)
                        expl.add(Explanation.getDurationExplanation(pf.duree()));
                }
            }
            default -> result = FULL_MATCH_MULTIPLIER;
        }
        return result;
    }

    private double getBonusSimilaires(String fl, int bacIndex, Explanations expl) {
        return getBonusSimilaires(fl, bacIndex, expl, flApproved, candidatsSimilaires);
    }

    private double getBonusSimilaires(
            String fl,
            int bacIndex,
            Explanations expl,
            Set<String> ok,
            Set<String> okCodes
    ) {
        if (!okCodes.contains(fl)) return Config.NO_MATCH_SCORE;

        Map<String, Long> sim = algo.getFormationsSimilaires(fl, bacIndex);
        if (sim.isEmpty()) return Config.NO_MATCH_SCORE;

        double bonus = Config.NO_MATCH_SCORE;
        for (String approved : ok) {
            long simScore = sim.getOrDefault(approved, 0L);
            if (simScore > 0) {
                double simi = 1.0 * simScore / PsupStatistiques.SIM_FIL_MAX_WEIGHT;
                bonus += simi;
                if (expl != null && !fl.equals(approved)) {
                    int percentage = Math.max(1, (int) simi * 100);
                    expl.add(Explanation.getSimilarityExplanation(approved, percentage));
                }
            }
        }
        return bonus;
    }

    private double getSubScoreOfPathList(List<Path> pathes) {
        double maxValue = pathes.stream().anyMatch(p -> p.size() <= 2) ? MAX_SCORE_PATH_LENGTH_2 : MAX_SCORE_PATH_LENGTH_3;
        double score = pathes.stream().mapToDouble(Path::score).sum();
        return Math.min(maxValue, score);
    }

    private double getBonusTags(String node, Explanations expl) {

        List<Path> pathes =
                pathesFromTagsIndexedByTarget.getOrDefault(node, Collections.emptyList())
                        .stream()
                        .filter(p -> p.size() > 1 && p.size() <= MAX_DISTANCE)
                        .toList()
                ;
        if (pathes.isEmpty()) return NO_MATCH_SCORE;

        //noinspection DataFlowIssue non empty path cannot have first equal to null
        Map<String, Double> subscores = pathes
                .stream()
                .collect(Collectors.groupingBy(
                        Path::first
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> getSubScoreOfPathList(e.getValue())
                ));
        double score
                = subscores.entrySet().stream()
                .mapToDouble(e -> applySourceTypeBonusTagMultipliers(e.getKey(), e.getValue())).sum();

        score  = score / Config.MIN_NB_TAGS_MATCH_FOR_PERFECT_FIT;

        score = Math.max(NO_MATCH_SCORE, Math.min(FULL_MATCH_MULTIPLIER, score));

        if (expl != null) {
            /* on regroupe les chemins en gardant juste la première node
            qui est l'élément d'accroche du profil
             */
            if(cfg.isVerbose()) {
                String msg = getTagSubScoreExplanation(score, subscores);
                expl.add(Explanation.getDebugExplanation(msg));
                if(cfg.isVeryVerbose()) {
                    //on inclut tous les chemins
                    expl.add(Explanation.getDebugExplanation(pathes.stream().map(Path::toString).collect(Collectors.joining(" , "))));
                }
            }
            expl.add(Explanation.getTagExplanationShort(pathes));
            pathes.stream()
                    .filter(p -> p.size() <= 2)
                    .map(Path::first)
                    .filter(Objects::nonNull)
                    .filter(fr.gouv.monprojetsup.data.Constants::isFiliere)
                    .distinct().forEach(fl -> expl.add(Explanation.getSimilarityExplanation(fl, 50)));
        }
        return score;
    }

    private double applySourceTypeBonusTagMultipliers(String key, Double value) {
        if(isMetier(key)) return Config.METIER_BONUS_TAG_MULTIPLIER * value;
        else return value;
    }

    private String getTagSubScoreExplanation(double score, Map<String, Double> subscores) {

        return BONUS_LABELS.get(BONUS_TAGS) + ": " + score + " = plafonnement à 1.0 de 1 / " + Config.MIN_NB_TAGS_MATCH_FOR_PERFECT_FIT
        + " de la somme de "
                + subscores.entrySet().stream()
                .sorted(Comparator.comparing(e -> -e.getValue()))
                .map(e -> df.format( e.getValue()) + " " + algo.getDebugLabel(e.getKey()))
                .collect(Collectors.joining(
                        " , ", "[ ", " ]"
                )) + " .";
    }

    private double getBonusApprentissage(String grp, Explanations expl) {
        if (pf.apprentissage() == null) return 0.0;
        boolean isApp = algo.existsInApprentissage(grp);
        double resultat = switch (pf.apprentissage()) {
            case "A" -> isApp ? FULL_MATCH_MULTIPLIER : 0.5 * FULL_MATCH_MULTIPLIER;//très intéressé
            case "B" -> isApp ? FULL_MATCH_MULTIPLIER : 0.8 * FULL_MATCH_MULTIPLIER;//intéressé
            case "C" -> FULL_MATCH_MULTIPLIER;//indifférent
            case "D" -> FULL_MATCH_MULTIPLIER;//pas du tout intéressé
            default -> FULL_MATCH_MULTIPLIER;
        };
        if (expl != null && (pf.apprentissage().equals("A") || pf.apprentissage().equals("B")) && isApp) {
            expl.add(Explanation.getAppExplanation(pf.apprentissage()));
        }
        return resultat;
    }

    private double getBonusSpecialites(String fl, Explanations expl) {
        if (pf.spe_classes() == null || pf.spe_classes().isEmpty())
            return FULL_MATCH_MULTIPLIER;
        Map<String, Double> stats = new HashMap<>();
        pf.spe_classes().forEach(s -> {
            Double stat = algo.getStatsSpecialite(fl, s);
            if (stat != null) {
                stats.put(s, stat);
            }
        });
        if (stats.isEmpty()) {
            if (expl != null && cfg.isVerbose())
                expl.add(Explanation.getDebugExplanation("Aucune des spécialités ne correspond"));
            return NO_MATCH_SCORE;
        }
        double score = stats.values().stream().mapToDouble(x -> x).sum();
        if (expl != null && stats.values().stream().anyMatch(x -> x >= Config.MIN_SPEC_PCT_FOR_EXP)) {
            stats.values().removeIf(x -> x < MIN_SPEC_PCT_FOR_EXP);
            expl.add(Explanation.getSpecialitesExplanation(stats));
        }
        return Math.min(FULL_MATCH_MULTIPLIER, score);
    }

    /**
     * Sort some candidates by pertinence
     *
     * @param candidates the candidates
     * @return  the candidates ordered by pertinence, the most pertinent first
     */
    public @NotNull List<String> getCandidatesOrderedByPertinence(@NotNull Collection<String> candidates) {

        Map<String,Double> scores = new HashMap<>();
        candidates.forEach(met -> scores.put(met, 0.0));

        pathesFromTagsIndexedByTarget
                .entrySet().stream()
                .filter(e -> candidates.contains(e.getKey()))
                .forEach(e -> scores.put(e.getKey(), scores.getOrDefault(e.getKey(), 0.0) + getSubScoreOfPathList(e.getValue())));
        return scores
                .keySet()
                .stream()
                .sorted(Comparator.comparing(o -> -scores.getOrDefault(o, 0.0)))
                .toList();
    }

    /**
     * computes diversity score with respect to non small formations
     * @param fl the key
     * @return the score
     */
    public double getNotSmallDiversityScore(String fl) {

        int nbFormations = algo.getNbVoeux(fl);
        int capacity = algo.getCapacity(fl);

        double capacityScore = (capacity >= algo.p75Capacity) ? 1.0 : (double) capacity / algo.p75Capacity;
        double nbFormationsScore = (nbFormations >= algo.p50NbFormations) ? 1.0 : (double) nbFormations / algo.p50NbFormations;
        return capacityScore * nbFormationsScore;

    }

    public GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples getExplanationsAndExamples(String key) {
        final Set<String> candidates = new HashSet<>(algo.getAllCandidatesMetiers(key));

        List<String> examples = getCandidatesOrderedByPertinence(candidates);

        List<Explanation> explanations;
        if (isFiliere(key)) {
            explanations = getExplanations(key).getLeft();
        } else {
            explanations = List.of();
        }
        return new GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples(
                key,
                explanations,
                examples
        );
    }

    private double getBonusVoeuxFavori(String fl, Explanations expl) {
        if(flApproved.contains(fl)) return NO_MATCH_SCORE;

        val voeux = flConnectedToVoeuxFavori.getOrDefault(fl, List.of());
        val result = voeux.isEmpty() ? NO_MATCH_SCORE : FULL_MATCH_MULTIPLIER;
        if(result > 0 && expl != null) {
            expl.explanations.add(Explanation.getVoeuxFavoriExplanation(voeux));
        }
        return result;
    }


}

package fr.gouv.monprojetsup.suggestions.algo;

import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.model.Path;
import fr.gouv.monprojetsup.data.model.stats.Middle50;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.dto.SuggestionDTO;
import fr.gouv.monprojetsup.suggestions.dto.explanations.CachedGeoExplanations;
import fr.gouv.monprojetsup.suggestions.dto.explanations.Explanation;
import fr.gouv.monprojetsup.suggestions.dto.explanations.ExplanationGeo;
import fr.parcoursup.carte.algos.tools.Paire;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.*;
import static fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions.*;
import static fr.gouv.monprojetsup.suggestions.algo.Config.*;
import static java.util.Map.entry;

public class AffinityEvaluator {


    public static final int MAX_DISTANCE = 3;
    private static final double MULTIPLIER_FOR_UNFITTED_NOTES = 0.1;
    private static final double MULTIPLIER_FOR_UNFITTED_BAC = 0.000;
    private static final double MULTIPLIER_FOR_NOSTATS_BAC = 0.01;

    private static final double MAX_SCORE_PATH_LENGTH_2 = 1.0;

    private static final double MAX_SCORE_PATH_LENGTH_3 = 0.25;

    public static final int ADMISSIBILITY_LOWEST_GRADE = 8;
    private static final double ZERO_ADMISSIBILITY = 0.001;

    private static final double ADMISSIBILITY_10 = 0.1;//at the first decile

    private static final double ADMISSIBILITY_25 = 0.3;//at the 25 and 75 quartile

    private static final double ADMISSIBILITY_50 = 0.9;//at the 25 and 75 quartile
    private static final double ADMISSIBILITY_75 = 1.0;//at the 25 and 75 quartile

    private static final double ADMISSIBILITY_90 = 1.0;//above the last decile
    public static final boolean USE_BIN = false;

    private final boolean isInterestedinHealth;
    private final Set<String> rejected =  new HashSet<>();
    private final SuggestionsData data;
    private final AlgoSuggestions algo;

    public AffinityEvaluator(ProfileDTO pf, Config cfg, SuggestionsData data, AlgoSuggestions algo) {
        this.cfg = cfg;
        this.pf = pf;
        this.data = data;
        this.algo = algo;

        //computing filieres we do not want to give advice about
        //because they are already in the profile
        List<SuggestionDTO> approved = pf.suggApproved();
        this.flApproved = approved.stream()
                .filter(s -> s.score() == null || s.score() >= 3)
                .map(SuggestionDTO::fl).filter(s -> s.startsWith(FILIERE_PREFIX)).toList();

        List<SuggestionDTO> rejectedSuggestions = pf.suggRejected();

        alreadyKnown.addAll(
                pf.choices().stream()
                        .filter(SuggestionDTO::isKnown)
                        .map(SuggestionDTO::fl)
                        .toList()
        );
        rejected.addAll(rejectedSuggestions.stream().map(SuggestionDTO::fl).toList());

        //precomputing candidats for filieres similaires
        candidatsSimilaires = flApproved.stream().flatMap(
                fl -> data.getFormationsSimilaires(fl, pf.bacIndex()).keySet().stream()
        ).collect(Collectors.toSet());

        //precomputing distance to tags

        //centres d'intérêts
        Set<String> nonZeroScores = new HashSet<>();
        if(pf.interests() != null) {
            nonZeroScores.addAll(pf.interests());
            nonZeroScores.addAll(
                    data.getAllRelatedInterests(
                        pf.interests()
                    )
            );
        }

        //autres formations
        nonZeroScores.addAll(approved.stream().map(SuggestionDTO::fl).toList());

        isInterestedinHealth = algo.isRelatedToHealth(nonZeroScores);

        //tag --> node --> distance
        pathesFromTagsIndexedByTarget =
                nonZeroScores.stream()
                        .flatMap(
                                n -> algo
                                        .computePathesFrom(n, MAX_DISTANCE)
                                        .entrySet().stream()
                        )
                        .collect(Collectors.groupingBy(Map.Entry::getKey))
                        .entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().stream().flatMap(f -> f.getValue().stream()).toList()
                        )
                        );
    }

    public double getBigCapacityScore(String fl) {

        int nbFormations = data.getNbVoeux(fl);
        int capacity = data.getCapacity(fl);

        double capacityScore = (capacity >= p75Capacity) ? 1.0 : (double) capacity / p75Capacity;
        double nbFormationsScore = (nbFormations >= p90NbFormations) ? 1.0 : (double) nbFormations / p90NbFormations;
        return capacityScore * nbFormationsScore;

    }

    /* the public entry points */
    public Affinite getAffinityEvaluation(String fl) {
        if (USE_BIN && rejected.contains(fl)) return Affinite.getNoMatch();
        return getScoreAndExplanation(fl, null, null);
    }

    /**
     * get explanations, including debug explanations if needed
     * @param fl key
     * @return list of explanations
     */
    public Pair<List<Explanation>, Double> getExplanations(String fl) {

        //en verbose mode, on récupère également les interests
        TreeMap<String, Double> nonZeroScores = cfg.isVerboseMode() ? new TreeMap<>() : null;

        List<Paire<Double, Explanation>> unsortedExpl = new ArrayList<>();

        //the computation
        Affinite totalScoreforFiliere = getScoreAndExplanation(fl, unsortedExpl, nonZeroScores);

        unsortedExpl.sort(Comparator.comparing(e -> -e.cg1));

        List<Explanation> sortedExpl = unsortedExpl.stream().map(e -> e.cg2).toList();
        if (cfg.isVerboseMode() && nonZeroScores != null) {
            List<Explanation> expl2 = new ArrayList<>(sortedExpl);
            expl2.add(Explanation.getDebugExplanation("Score Total: " + totalScoreforFiliere + BR));

            List<Map.Entry<String, Double>> entries = new ArrayList<>(nonZeroScores.entrySet());
            entries.sort(Comparator.comparing(e -> -e.getValue()));
            entries.forEach(e -> {
                double weight = cfg.weights().get(e.getKey());
                if(weight == SPECIAL_WEIGHT_MULTIPLIER) {
                    expl2.add(Explanation.getDebugExplanation(
                            e.getKey()
                                    + " multiplicateur global "
                                    + getMultiplier(e.getKey(),e.getValue())
                    ));
                } else {
                    expl2.add(Explanation.getDebugExplanation(
                            BONUS_LABELS.getOrDefault(e.getKey(), e.getKey())
                                    + " "
                                    + e.getValue()
                                    + " * "
                                    + weight
                                    + BR
                    ));
                }
            });
            sortedExpl = expl2;
        }
        return Pair.of(sortedExpl, totalScoreforFiliere.affinite());
    }


    private final Config cfg;
    private final ProfileDTO pf;

    private final Set<String> candidatsSimilaires;
    public final Set<String> alreadyKnown = new HashSet<>();

    private final Map<String, List<Path>> pathesFromTagsIndexedByTarget;

    private final List<String> flApproved;

    /**
     * Calcule les interests affinités entre un profil et une filière
     *
     * @param fl      la filière considérée
     * @param expl    les explications, à compléter si expl != null
     * @param matches used to get debug info about what matched and how
     * @return the score
     */
    private Affinite getScoreAndExplanation(
            String fl,
            List<Paire<Double, Explanation>> expl,
            Map<String, Double> matches
            ) {

         if(rejected.contains(fl)) return Affinite.getNoMatch();

        final int bacIndex = pf.bacIndex();

        /* LAS filter: is the formation is a LAS and santé was not checked, it is not proposed */
        if (algo.isLas(fl) && !isInterestedinHealth) {
            return Affinite.getNoMatch();
        }

        /*
         * map des critères vers des doubles
         */

        Map<String, Double> scores = new HashMap<>(
                Map.ofEntries(
                        entry(Config.BONUS_SIM, getBonusSimilaires(fl, bacIndex, expl, cfg.weights().getOrDefault(Config.BONUS_SIM, 0.0))),
                        entry(Config.BONUS_TAGS, getBonusTags(fl, expl, cfg.weights().getOrDefault(Config.BONUS_TAGS, 0.0)))
                )
        );

        //règle: si pas d'accroche sur les critères personnalisés, et pas de demande d'explications
        // alors on ne traite pas, pour économiser du CPU.
        boolean process = expl != null
                || scores.entrySet().stream()
                .anyMatch(e -> cfg.personalCriteria().contains(e.getKey()) && e.getValue() > Config.NO_MATCH_SCORE);
        if (!process) return Affinite.getNoMatch();

        if(Helpers.isFiliere(fl)) {
            scores.putAll(Map.ofEntries(
                    entry(Config.BONUS_GEO, getBonusCities(fl, expl, cfg.weights().getOrDefault(Config.BONUS_GEO, 0.0))),
                    entry(Config.BONUS_DURATION, getBonusDuree(fl, expl, cfg.weights().getOrDefault(Config.BONUS_DURATION, 0.0))),
                    entry(Config.BONUS_APPRENTISSAGE, getBonusApprentissage(fl, expl, cfg.weights().getOrDefault(Config.BONUS_APPRENTISSAGE, 0.0))),
                    entry(Config.BONUS_SPECIALITE, getBonusSpecialites(fl, expl, cfg.weights().getOrDefault(Config.BONUS_SPECIALITE, 0.0))),
                    entry(Config.BONUS_TYPE_BAC, getBonusTypeBac(fl, expl, cfg.weights().getOrDefault(Config.BONUS_TYPE_BAC, 0.0))),
                    entry(Config.BONUS_MOY_GEN, getBonusMoyGen2(fl, expl, cfg.weights().getOrDefault(Config.BONUS_MOY_GEN, 0.0)))
                )
            );
        }

        double score = 0;

        //on fait la somme pondérée des interests additifs
        for(Map.Entry<String, Double> e : scores.entrySet()) {
            String key = e.getKey();
            Double value = e.getValue();
            double weight = cfg.weights().getOrDefault(key, 0.0);
            if(weight != SPECIAL_WEIGHT_MULTIPLIER) {
                score += value * weight;
            }
        }

        //on applique les multiplicateurs
        for(Map.Entry<String, Double> e : scores.entrySet()) {
            String key = e.getKey();
            Double value = e.getValue();
            double weight = cfg.weights().getOrDefault(key, 0.0);
            if(weight == SPECIAL_WEIGHT_MULTIPLIER) {
                score *= getMultiplier(key, value);
            }
        }

        //put interests in expl, if required
        if (matches != null && cfg.isVerboseMode()) {
            matches.putAll(scores.entrySet().stream().filter(
                    e -> e.getValue() > Config.NO_MATCH_SCORE
                            || cfg.weights().getOrDefault(e.getKey(), 0.0) == SPECIAL_WEIGHT_MULTIPLIER
                    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        double moyGenMultiplier = getMultiplier(Config.BONUS_MOY_GEN, scores.getOrDefault(Config.BONUS_MOY_GEN, 1.0));
        double bacMultiplier = getMultiplier(BONUS_TYPE_BAC, scores.getOrDefault(Config.BONUS_TYPE_BAC, 0.0));
        double bigCapacityScore = getBigCapacityScore(fl);
        double smallCapacityScore = AlgoSuggestions.getSmallCapacityScore(fl);

        EnumMap<Affinite.SuggestionDiversityQuota, Double> quotas = new EnumMap<>(Affinite.SuggestionDiversityQuota.class);
        quotas.put(Affinite.SuggestionDiversityQuota.NOT_SMALL, bigCapacityScore);
        quotas.put(Affinite.SuggestionDiversityQuota.NOT_BIG, smallCapacityScore);
        quotas.put(Affinite.SuggestionDiversityQuota.BAC, bacMultiplier);
        quotas.put(Affinite.SuggestionDiversityQuota.MOYGEN, moyGenMultiplier);

        return new Affinite(score, quotas);
    }


    private double getMultiplier(String key, Double value) {
        if(Objects.equals(key,BONUS_MOY_GEN)) return MULTIPLIER_FOR_UNFITTED_NOTES + value;
        if(Objects.equals(key,BONUS_TYPE_BAC)) return MULTIPLIER_FOR_UNFITTED_BAC + value;
        return value;
    }


    private double getBonusTypeBac(String grp, List<Paire<Double, Explanation>> expl, double weight) {
        if (pf.bac() == null || pf.bac().equals(PsupStatistiques.TOUS_BACS_CODE)) return Config.NO_MATCH_SCORE;
        Integer nbAdmisTousBac = data.getNbAdmis(grp, PsupStatistiques.TOUS_BACS_CODE);
        Integer nbAdmisBac = data.getNbAdmis(grp, pf.bac());
        if(nbAdmisTousBac != null && nbAdmisBac == null) return MULTIPLIER_FOR_UNFITTED_BAC;
        if (nbAdmisBac == null || nbAdmisTousBac == null) return MULTIPLIER_FOR_NOSTATS_BAC;
        double percentage = 1.0 * nbAdmisBac / nbAdmisTousBac;
        if (percentage <= Config.SEUIL_TYPE_BAC_NO_MATCH) return Config.NO_MATCH_SCORE;
        final double bonus;
        if (percentage >= Config.SEUIL_TYPE_BAC_FULL_MATCH) bonus = 1.0;
        else
            bonus = (percentage - Config.SEUIL_TYPE_BAC_NO_MATCH) / (Config.SEUIL_TYPE_BAC_FULL_MATCH - Config.SEUIL_TYPE_BAC_NO_MATCH);
        if (expl != null && percentage >= Config.SEUIL_TYPE_BAC_FITTED) {
            expl.add(
                    new Paire<>(bonus * weight, Explanation.getTypeBacExplanation((int) (100 * percentage), pf.bac()))
            );
        }
        return bonus;
    }


    private double getBonusMoyGen2(String fl, List<Paire<Double, Explanation>> expl, double weight) {
        if (pf.moygen() == null || pf.moygen().isEmpty()) return Config.NO_MATCH_SCORE;
        val stats = data.getStatsBac(fl, pf.bac());
        String moyBacEstimee = pf.moygen();
        return getBonusNotes(expl, weight, stats, moyBacEstimee);
    }

    /**
     * Computes the bonus.
     *
     * @param expl   if null, no explanation is generated. If non-null, expl is populated with explanations and interests.
     * @param weight weight, for explanations
     * @return the bonus
     */
    private double getBonusNotes(
            @Nullable List<Paire<Double, Explanation>> expl,
            double weight,
            @Nullable Pair<String, Middle50> stats,
            @Nullable String moy) {
        if (moy == null) return Config.NO_MATCH_SCORE;
        final double autoEval;
        try {
            autoEval = Double.parseDouble(moy);
        } catch (NumberFormatException ignored) {
            return 1.0 - MULTIPLIER_FOR_UNFITTED_NOTES;
        }
        //on va chercher les stats pour ce type de bac et cette filiere
        if (stats == null || stats.getRight() == null) {
            if (expl != null && cfg.isVerboseMode())
                expl.add(new Paire<>(Config.NO_MATCH_SCORE, Explanation.getDebugExplanation("Pas de stats pour cette filiere")));
            return 1.0 - MULTIPLIER_FOR_UNFITTED_NOTES;
        }
        String bacUtilise = stats.getLeft();
        Middle50 middle50 = stats.getRight();
        if (middle50 == null) {
            if (expl != null && cfg.isVerboseMode())
                expl.add(new Paire<>(Config.NO_MATCH_SCORE, Explanation.getDebugExplanation("Pas de middle50 pour cette filiere")));
            return 1.0 - MULTIPLIER_FOR_UNFITTED_NOTES;
        }
        double bonus = computeBonusNotes(stats, autoEval);
        //affiché systématiquement
        if (expl != null) {
            expl.add(
                    new Paire<>(weight == SPECIAL_WEIGHT_MULTIPLIER ? Double.MAX_VALUE : bonus * weight,
                            Explanation.getNotesExplanation(autoEval, middle50, bacUtilise)
                    )
            );
        }
        return bonus;
    }

    private double computeBonusNotes(Pair<String, Middle50> stats, double autoEval) {
        final Middle50 middle50 = stats.getRight();
        int noteMaxInt = middle50.rangMax();
        int note = (int) (noteMaxInt * autoEval / 20);
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

    private double getBonusCities(String fl, List<Paire<Double, Explanation>> expl, double weight) {
        //pour chaque filiere, on scanne toutes les details et on calcule la distance min ç chaque ville
        double bonus = Config.NO_MATCH_SCORE;

        /* geo_pref is not null in theory but not in practice, because of what I consider a broken feature of GSon deserialization
        on the kotlin / spring side */
        if (pf.geo_pref() == null || pf.geo_pref().isEmpty()) return bonus;

        for (String cityName : pf.geo_pref()) {
            @NotNull val result = ExplanationGeo.getGeoExplanations(
                    fl,
                    cityName,
                    data.getVoeuxCoords(fl),
                    CachedGeoExplanations.distanceCaches
            );
            int distanceKm = result.stream().mapToInt(ExplanationGeo::distance).min().orElse(-1);
            if (distanceKm >= 0) {
                bonus += 1.0 / (1.0 + Math.max(10.0, distanceKm));
                if (expl != null && distanceKm < 50) {
                    expl.add(new Paire<>(weight / distanceKm,
                                    Explanation.getGeoExplanation(result)
                            )
                    );
                }
            }
        }
        return bonus;
    }


    private double getBonusDuree(String fl, List<Paire<Double, Explanation>> expl, double weight) {
        if (pf.duree() == null) return Config.NO_MATCH_SCORE;
        int duree = data.getDuree(fl);
        final double result;
        switch (pf.duree().toLowerCase()) {
            case "court" -> {
                result = duree <= 3 ? (5 - duree) : Config.NO_MATCH_SCORE;
                if (result > 2 && expl != null)
                    expl.add(new Paire<>(result * weight, Explanation.getDurationExplanation(pf.duree())));
            }
            case "long" -> {
                result = duree >= 4 ? duree : Config.NO_MATCH_SCORE;
                if (result > 3 && expl != null)
                    expl.add(new Paire<>(result * weight, Explanation.getDurationExplanation(pf.duree())));
            }
            default -> result = Config.NO_MATCH_SCORE;
        }
        return result;
    }

    private double getBonusSimilaires(String fl, int bacIndex, List<Paire<Double, Explanation>> expl, double weight) {
        return getBonusSimilaires(fl, bacIndex, expl, flApproved, candidatsSimilaires, weight);
    }

    private double getBonusSimilaires(
            String fl,
            int bacIndex,
            List<Paire<Double, Explanation>> expl,
            List<String> ok,
            Set<String> okCodes,
            double weight
    ) {
        if (!okCodes.contains(fl)) return Config.NO_MATCH_SCORE;

        Map<String, Integer> sim = data.getFormationsSimilaires(fl, bacIndex);
        if (sim == null) return Config.NO_MATCH_SCORE;

        double bonus = Config.NO_MATCH_SCORE;
        for (String approved : ok) {
            int simScore = sim.getOrDefault(approved, 0);
            if (simScore > 0) {
                double simi = 1.0 * simScore / PsupStatistiques.SIM_FIL_MAX_WEIGHT;
                bonus += simi;
                if (expl != null && !fl.equals(approved)) {
                    int percentage = Math.max(1, (int) simi * 100);
                    expl.add(new Paire<>(simi * weight, Explanation.getSimilarityExplanation(approved, percentage)));
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

    private double getBonusTags(String node, List<Paire<Double, Explanation>> expl, double weight) {

        List<Path> pathes =
                pathesFromTagsIndexedByTarget.getOrDefault(node, Collections.emptyList())
                        .stream()
                        .filter(p -> p.size() > 1 && p.size() <= MAX_DISTANCE)
                        .toList()
                ;
        if (pathes.isEmpty()) return Config.NO_MATCH_SCORE;

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
        double score = subscores.values().stream().mapToDouble(x -> x).sum();

        if (expl != null) {
            /* on regroupe les chemins en gardant juste la première node
            qui est l'élément d'accroche du profil
             */
            if(cfg.isVerboseMode()) {
                String msg = getTagSubScoreExplanation(score, subscores);
                expl.add(new Paire<>(score * weight, Explanation.getDebugExplanation(msg)));
            }
            expl.add(new Paire<>(score * weight, Explanation.getTagExplanationShort(pathes)));
        }
        return score;
    }

    private String getTagSubScoreExplanation(double score, Map<String, Double> subscores) {

        return "Mots-clés interests total: " + score + "=Sum( "
                + subscores.entrySet().stream()
                .sorted(Comparator.comparing(e -> -e.getValue()))
                .map(e -> e.getValue() + "\t    : "
                        + data.getLabel(e.getKey()))
                .collect(Collectors.joining(
                        "\n\t", "\n\t", "\n"
                )) + ").";
    }

    public void restrictPathesToTarget(String key) {
        pathesFromTagsIndexedByTarget.keySet().retainAll(List.of(key));
    }

    public List<Suggestion> getCloseTagsSuggestionsOrderedByIncreasingDistance(
            int maxLength,
            boolean includeExplanations,
            int maxNbSuggestions
    ) {

        //stocke les chemins de chaque match
        Map<String, Set<Path>> matches = new HashMap<>();

        //stocke les interests de chaque match, obtenus en sommant les interests des chemins individuels
        Map<String, Double> scores = new HashMap<>();

        //on itère sur les chemins depuis les noeuds activés, source par source
        pathesFromTagsIndexedByTarget.forEach((s1, paths) -> {
            if (!alreadyKnown.contains(s1)) {
                paths.forEach(path -> {
                    if (path.size() > 1 && path.size() <= maxLength) {
                        Set<Path> pathesSet = matches.computeIfAbsent(s1, z -> new HashSet<>());
                        if(includeExplanations) pathesSet.add(path);
                        double subscore = path.score();
                        scores.put(s1, subscore + scores.getOrDefault(s1, 0.0));
                    }
                });
            }
        });

        List<String> nodes = new ArrayList<>(matches.keySet());
        nodes.sort(Comparator.comparingDouble(node -> -scores.get(node)));

        return nodes.stream()
                .filter(s -> s.startsWith(SEC_ACT_PREFIX_IN_GRAPH))
                .limit(Math.min(maxNbSuggestions, cfg.maxNbSuggestions()))
                .map(s -> {
                            Set<Path> pathes = matches.getOrDefault(s, Collections.emptySet());
                            List<Explanation> explanations;
                            if(includeExplanations) {
                                explanations = new ArrayList<>();
                                if (cfg.isVerboseMode()) {
                                    //explanations.add(Explanation.getTagExplanation(pathes));
                                    double score = scores.getOrDefault(s, 0.0);
                                    explanations.add(Explanation.getDebugExplanation("Score : " + score + BR));
                                }
                                explanations.add(Explanation.getTagExplanationShort(pathes));
                            } else {
                                explanations = null;
                            }
                            return Suggestion.getPendingSuggestion(
                                    s,
                                    explanations,
                                    null
                            );
                        }
                )
                .toList();
    }

    private double getBonusApprentissage(String grp, List<Paire<Double, Explanation>> expl, double weight) {
        if (pf.apprentissage() == null) return 0.0;
        boolean isApp = algo.existsInApprentissage(grp);
        double resultat = switch (pf.apprentissage()) {
            case "A" -> isApp ? 2.0 : -1.0;
            case "B" -> isApp ? 1.0 : 0.0;
            case "D" -> isApp ? -10000.0 : 1.0;
            default -> 0.0;
        };
        if (expl != null && resultat > 0.5 && isApp) {
            expl.add(new Paire<>(resultat * weight, Explanation.getAppExplanation(pf.apprentissage())));
        }
        return resultat;
    }

    private double getBonusSpecialites(String fl, List<Paire<Double, Explanation>> expl, double weight) {
        if (pf.spe_classes() == null || pf.spe_classes().isEmpty() || !algo.hasSpecialitesInTerminale(pf.bac()))
            return Config.NO_MATCH_SCORE;
        Map<String, Double> stats = new HashMap<>();
        pf.spe_classes().forEach(s -> {
            //Décodage soit par nom spécialité soit par code
            Integer iMtCod = algo.codesSpecialites.get(s);
            if (iMtCod != null) {
                Double stat = data.getStatsSpecialite(fl, iMtCod);
                if (stat != null) {
                    stats.put(s, stat);
                }
            }
        });
        if (stats.isEmpty()) {
            if (expl != null && cfg.isVerboseMode())
                expl.add(new Paire<>(Config.NO_MATCH_SCORE, Explanation.getDebugExplanation("Pas de stats spécialités pour cette filiere")));
            return Config.NO_MATCH_SCORE;
        }
        double score = stats.values().stream().mapToDouble(x -> x).sum();
        stats.values().removeIf(x -> x < 0.2);
        if (expl != null && !stats.isEmpty()) {
            expl.add(new Paire<>(score * weight, Explanation.getSpecialitesExplanation(stats)));
        }
        return score;
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


    public GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples getExplanationsAndExamples(String key) {
        final Set<String> candidates = new HashSet<>();
        candidates.addAll(data.getAllCandidatesMetiers(key));

        List<String> examples = getCandidatesOrderedByPertinence(candidates);

        List<Explanation> explanations;
        if (Helpers.isFiliere(key)) {
            explanations = getExplanations(key).getLeft();
        } else {
            restrictPathesToTarget(key);
            explanations =
                    getCloseTagsSuggestionsOrderedByIncreasingDistance(
                            MAX_LENGTH_FOR_SUGGESTIONS,
                            true,
                            cfg.maxNbSuggestions()
                    )
                            .stream()
                            .filter(e -> e.expl() != null)
                            .flatMap(e -> e.expl().stream())
                            .toList();

        }
        return new GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples(
                key,
                explanations,
                examples
        );
    }

}

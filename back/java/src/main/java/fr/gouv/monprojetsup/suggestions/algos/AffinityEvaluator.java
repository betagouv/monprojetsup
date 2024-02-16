package fr.gouv.monprojetsup.suggestions.algos;

import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.Path;
import fr.gouv.monprojetsup.data.model.stats.Middle50;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.model.stats.Statistique;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.parcoursup.carte.algos.tools.Paire;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.*;
import static fr.gouv.monprojetsup.data.ServerData.flGroups;
import static fr.gouv.monprojetsup.data.ServerData.isFiliere;
import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.isLas;
import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.isRelatedToHealth;
import static fr.gouv.monprojetsup.suggestions.algos.Config.*;
import static java.util.Map.entry;

public class AffinityEvaluator {


    public static final int MAX_DISTANCE = 3;
    private static final double MULTIPLIER_FOR_UNFITTED_NOTES = 0.1;
    private static final double MULTIPLIER_FOR_UNFITTED_BAC = 0.3;

    private static final double MAX_SCORE_PATH_LENGTH_2 = 1.0;

    private static final double MAX_SCORE_PATH_LENGTH_3 = 0.25;

    public static final int ADMISSIBILITY_LOWEST_GRADE = 8;
    private static final double ZERO_ADMISSIBILITY = 0.001;

    private static final double ADMISSIBILITY_10 = 0.1;//at the first decile

    private static final double ADMISSIBILITY_25 = 0.3;//at the 25 and 75 quartile

    private static final double ADMISSIBILITY_50 = 0.9;//at the 25 and 75 quartile
    private static final double ADMISSIBILITY_75 = 1.0;//at the 25 and 75 quartile

    private static final double ADMISSIBILITY_90 = 1.0;//above the last decile

    private final boolean isInterestedinHealth;

    /**
     * Adds an item to already known items
     *
     * @param key the key of the item to add
     */
    private void addAlreadyKnown(String key) {
        alreadyKnown.add(key);
        String grp = flGroups.get(key);
        if (grp != null) {
            alreadyKnown.add(grp);
        }
        Collection<String> grps = ServerData.reverseFlGroups.get(key);
        if (grps != null) {
            alreadyKnown.addAll(grps);
        }

    }

    public AffinityEvaluator(ProfileDTO pf, Config cfg) {
        this.cfg = cfg;
        this.pf = pf;

        //computing filieres we do not want to give advice about
        //because they are already in the profile
        List<Suggestion> approved = pf.suggApproved();
        this.flApproved = approved.stream().filter(s -> s.fl().startsWith(FILIERE_PREFIX)).toList();

        approved.forEach(sugg -> addAlreadyKnown(sugg.fl()));
        pf.suggRejected().forEach(sugg -> addAlreadyKnown(sugg.fl()));

        //precomputing candidats for filieres similaires
        for (Suggestion sugg : flApproved) {
            Map<String, Integer> sim = ServerData.backPsupData.getFilieresSimilaires(sugg.fl(), pf.bacIndex());
            if (sim != null) {
                candidatsSimilaires.addAll(sim.keySet());
            }
        }

        //precomputing distance to tags

        //centres d'intérêts
        Set<String> nonZeroScores = new HashSet<>();
        nonZeroScores.addAll(pf.scores().entrySet()
                .stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .toList());

        //autres formations
        nonZeroScores.addAll(approved.stream().map(Suggestion::fl).toList());

        isInterestedinHealth = isRelatedToHealth(nonZeroScores);

        //tag --> node --> distance
        pathesFromTagsIndexedByTarget =
                nonZeroScores.stream()
                        .flatMap(
                                n -> AlgoSuggestions
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

    /* the public entry points */
    public Double getAffinityEvaluation(String fl) {
        if (alreadyKnown.contains(fl)) return Config.NO_MATCH_SCORE;
        return getScoreAndExplanation(fl, null, null);
    }

    /**
     * get explanations, including debug explanations if needed
     * @param fl key
     * @return list of explanations
     */
    public Pair<List<Explanation>, Double> getExplanations(String fl) {

        //en verbose mode, on récupère également les scores
        TreeMap<String, Double> nonZeroScores = cfg.isVerboseMode() ? new TreeMap<>() : null;

        List<Paire<Double, Explanation>> unsortedExpl = new ArrayList<>();

        //the computation
        double totalScoreforFiliere = getScoreAndExplanation(fl, unsortedExpl, nonZeroScores);

        unsortedExpl.sort(Comparator.comparing(e -> -e.cg1));

        List<Explanation> sortedExpl = unsortedExpl.stream().map(e -> e.cg2).toList();
        if (cfg.isVerboseMode() && nonZeroScores != null) {
            List<Explanation> expl2 = new ArrayList<>(sortedExpl);
            expl2.add(Explanation.getDebugExplanation("Score Total: " + totalScoreforFiliere + ".<br/>"));

            List<Map.Entry<String, Double>> entries = new ArrayList<>(nonZeroScores.entrySet());
            entries.sort(Comparator.comparing(e -> -e.getValue()));
            entries.forEach(e -> {
                double weight = cfg.weights().get(e.getKey());
                if(weight == SPECIAL_WEIGHT_MULTIPLIER) {
                    expl2.add(Explanation.getDebugExplanation(
                            e.getKey()
                                    + " global multiplier "
                                    + getMultiplier(e.getKey(),e.getValue())
                    ));
                } else {
                    expl2.add(Explanation.getDebugExplanation(
                            e.getKey()
                                    + " "
                                    + e.getValue()
                                    + " * "
                                    + weight
                                    + BR
                    ));
                }
            });
            expl2.addAll(sortedExpl);
            sortedExpl = expl2;
        }
        return Pair.of(sortedExpl, totalScoreforFiliere);
    }


    private final Config cfg;
    private final ProfileDTO pf;

    private final Set<String> candidatsSimilaires = new HashSet<>();
    public final Set<String> alreadyKnown = new HashSet<>();

    private final Map<String, List<Path>> pathesFromTagsIndexedByTarget;

    private final List<Suggestion> flApproved;

    /**
     * Calcule les scores affinités entre un profil et une filière
     *
     * @param fl      la filière considérée
     * @param expl    les explications, à compléter si expl != null
     * @param matches used to get debug info about what matched and how
     * @return the score
     */
    private double getScoreAndExplanation(
            String fl,
            List<Paire<Double, Explanation>> expl,
            Map<String, Double> matches
            ) {

        final int bacIndex = pf.bacIndex();

        /* LAS filter: is the formation is a LAS and santé was not checked, it is not proposed */
        if (isLas(fl) && !isInterestedinHealth) {
            return Config.NO_MATCH_SCORE;
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
        if (!process) return Config.NO_MATCH_SCORE;

        if(isFiliere(fl)) {
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
        for(Map.Entry<String, Double> e : scores.entrySet()) {
            String key = e.getKey();
            Double value = e.getValue();
            double weight = cfg.weights().getOrDefault(key, 0.0);
            if(weight != SPECIAL_WEIGHT_MULTIPLIER) {
                score += value * weight;
            }
        }
        for(Map.Entry<String, Double> e : scores.entrySet()) {
            String key = e.getKey();
            Double value = e.getValue();
            double weight = cfg.weights().getOrDefault(key, 0.0);
            if(weight == SPECIAL_WEIGHT_MULTIPLIER) {
                score *= getMultiplier(key, value);
            }
        }

        //put scores in expl, if required
        if (matches != null && cfg.isVerboseMode()) {
            matches.putAll(scores.entrySet().stream().filter(
                    e -> e.getValue() > Config.NO_MATCH_SCORE
                            || cfg.weights().getOrDefault(e.getKey(), 0.0) == SPECIAL_WEIGHT_MULTIPLIER
                    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        return score;
    }

    private double getMultiplier(String key, Double value) {
        if(Objects.equals(key,BONUS_MOY_GEN)) return MULTIPLIER_FOR_UNFITTED_NOTES + value;
        if(Objects.equals(key,BONUS_TYPE_BAC)) return MULTIPLIER_FOR_UNFITTED_BAC + value;
        return value;
    }


    private double getBonusTypeBac(String grp, List<Paire<Double, Explanation>> expl, double weight) {
        if (pf.bac() == null || pf.bac().equals(PsupStatistiques.TOUS_BACS_CODE)) return Config.NO_MATCH_SCORE;
        Integer nbAdmisTousBac = ServerData.statistiques.getNbAdmis(grp, PsupStatistiques.TOUS_BACS_CODE);
        Integer nbAdmisBac = ServerData.statistiques.getNbAdmis(grp, pf.bac());
        if(nbAdmisTousBac != null && nbAdmisBac == null) return Config.NO_MATCH_SCORE;
        if (nbAdmisBac == null || nbAdmisTousBac == null) return 1.0 - MULTIPLIER_FOR_UNFITTED_BAC;
        double percentage = 1.0 * nbAdmisBac / nbAdmisTousBac;
        if (percentage <= Config.SEUIL_TYPE_BAC_NO_MATCH) return Config.NO_MATCH_SCORE;
        final double bonus;
        if (percentage >= Config.SEUIL_TYPE_BAC_FULL_MATCH) bonus = 1.0;
        else
            bonus = (percentage - Config.SEUIL_TYPE_BAC_NO_MATCH) / (Config.SEUIL_TYPE_BAC_FULL_MATCH - Config.SEUIL_TYPE_BAC_NO_MATCH);
        if (expl != null) {
            expl.add(
                    new Paire<>(bonus * weight, Explanation.getTypeBacExplanation((int) (100 * percentage), pf.bac()))
            );
        }
        return bonus;
    }


    private double getBonusMoyGen2(String fl, List<Paire<Double, Explanation>> expl, double weight) {
        if (pf.moygen() == null || pf.moygen().isEmpty()) return Config.NO_MATCH_SCORE;
        Pair<String, Statistique> stats = ServerData.statistiques.getStatsBac(fl, pf.bac());
        String moyBacEstimee = pf.moygen();
        return getBonusNotes(expl, weight, stats, moyBacEstimee);
    }

    /**
     * Computes the bonus.
     *
     * @param expl   if null, no explanation is generated. If non-null, expl is populated with explanations and scores.
     * @param weight weight, for explanations
     * @return the bonus
     */
    private double getBonusNotes(
            @Nullable List<Paire<Double, Explanation>> expl,
            double weight,
            @Nullable Pair<String, Statistique> stats,
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
        Middle50 middle50 = stats.getRight().middle50();
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

    private double computeBonusNotes(Pair<String, Statistique> stats, double autoEval) {
        final Middle50 middle50 = stats.getRight().middle50();
        int noteMaxInt = stats.getRight().frequencesCumulees().length;
        int note = (int) (noteMaxInt * autoEval / 20);
        //avantage aux suggestions plus exigeantes
        return (0.7 * computeAdmissibiliteNotes(middle50, note, noteMaxInt)
                + 0.3 * computeProximiteNotes(middle50, note, noteMaxInt));
                //* middle50.rangEch75() / noteMaxInt;
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

    private double computeProximiteNotes(Middle50 middle50, int note, int noteMaxInt) {
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
        //pour chaque filiere, on scanne toutes les suggestions et on calcule la distance min ç chaque ville
        double bonus = Config.NO_MATCH_SCORE;

        for (String cityName : pf.geo_pref()) {
            @NotNull val result = AlgoSuggestions.getDistanceKm(fl, cityName);
            int distanceKm = result.stream().mapToInt(Explanation.ExplanationGeo::distance).min().orElse(-1);
            if (distanceKm >= 0) {
                bonus += 1.0 / (1.0 + Math.max(10.0, distanceKm));
                if (expl != null && distanceKm < 150) {
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
        if (pf.duree() == null) return 0.0;
        int duree = ServerData.backPsupData.getDuree(fl);
        final double result;
        switch (pf.duree().toLowerCase()) {
            case "court" -> {
                result = (5 - duree);
                if (result > 2 && expl != null)
                    expl.add(new Paire<>(result * weight, Explanation.getDurationExplanation(pf.duree())));
            }
            case "long" -> {
                result = duree;
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
            List<Suggestion> ok,
            Set<String> okCodes,
            double weight
    ) {
        if (!okCodes.contains(fl)) return Config.NO_MATCH_SCORE;

        Map<String, Integer> sim = ServerData.backPsupData.getFilieresSimilaires(fl, bacIndex);
        if (sim == null) return Config.NO_MATCH_SCORE;

        double bonus = Config.NO_MATCH_SCORE;
        for (Suggestion sugg : ok) {
            int simScore = sim.getOrDefault(sugg.fl(), 0);
            if (simScore > 0) {
                double simi = 1.0 * simScore / PsupStatistiques.SIM_FIL_MAX_WEIGHT;
                bonus += simi;
                if (expl != null) {
                    int percentage = Math.max(1, (int) simi * 100);
                    expl.add(new Paire<>(simi * weight, Explanation.getSimilarityExplanation(sugg.fl(), percentage)));
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
                expl.add(new Paire<>(score * weight, Explanation.getTagExplanation(pathes)));
            } else {
                expl.add(new Paire<>(score * weight, Explanation.getTagExplanationShort(pathes)));
            }
        }
        /*
        List<Suggestion> suggNodes
                = affinityEvaluator
                .getCloseTagsSuggestionsOrderedByIncreasingDistance(
                        MAX_LENGTH_FOR_SUGGESTIONS,
                        false,
                        SEC_ACT_PREFIX_IN_GRAPH,
                        cfg.maxNbSuggestions()
                );
        */
        return score;
    }

    private String getTagSubScoreExplanation(double score, Map<String, Double> subscores) {

        return "Tags scores total: " + score + "=Sum( "
                + subscores.entrySet().stream()
                .sorted(Comparator.comparing(e -> -e.getValue()))
                .map(e -> e.getValue() + "\t    : "
                        + ServerData.getDebugLabel(e.getKey()))
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
            String prefix,
            int maxNbSuggestions
    ) {

        //stocke les chemins de chaque match
        Map<String, Set<Path>> matches = new HashMap<>();

        //stocke les scores de chaque match, obtenus en sommant les scores des chemins individuels
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
                                if (cfg.isVerboseMode()) {
                                    explanations = new ArrayList<>();
                                    explanations.add(Explanation.getTagExplanation(pathes));
                                    double score = scores.getOrDefault(s, 0.0);
                                    explanations.add(Explanation.getDebugExplanation("Score : " + score + BR));
                                } else {
                                    explanations = List.of(Explanation.getTagExplanationShort(pathes));
                                }
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
        boolean isApp = AlgoSuggestions.existsInApprentissage(grp);
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
        if (pf.spe_classes() == null || pf.spe_classes().isEmpty() || !AlgoSuggestions.hasSpecialitesInTerminale(pf.bac()))
            return Config.NO_MATCH_SCORE;
        Map<String, Double> stats = new HashMap<>();
        pf.spe_classes().forEach(s -> {
            Integer iMtCod = ServerData.codesSpecialites.get(s);
            if (iMtCod != null) {
                Double stat = ServerData.statistiques.getStatsSpecialite(fl, iMtCod);
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

    public @NotNull List<String> getCandidatesOrderedByPertinence(@NotNull Set<String> candidates) {

        Map<String,Double> scores = new HashMap<>();
        candidates.stream().filter(m -> !alreadyKnown.contains(m)).forEach(met -> scores.put(met,0.0));

        pathesFromTagsIndexedByTarget
                .entrySet().stream()
                .filter(e -> candidates.contains(e.getKey()))
                .forEach(e -> {
                    scores.put(e.getKey(), scores.getOrDefault(e.getKey(), 0.0) + getSubScoreOfPathList(e.getValue()));
                });
        return scores
                .keySet()
                .stream()
                .sorted(Comparator.comparing(o -> -scores.getOrDefault(o, 0.0)))
                .toList();
    }




}

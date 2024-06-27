package fr.gouv.monprojetsup.suggestions.algos;

import fr.gouv.monprojetsup.common.tools.ConcurrentBoundedMapQueue;
import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.distances.Distances;
import fr.gouv.monprojetsup.data.dto.GetExplanationsAndExamplesServiceDTO;
import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;
import fr.gouv.monprojetsup.data.model.Edges;
import fr.gouv.monprojetsup.data.model.Path;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.*;
import static fr.gouv.monprojetsup.data.ServerData.*;
import static fr.gouv.monprojetsup.data.update.onisep.OnisepData.EDGES_INTERETS_METIERS_WEIGHT;
import static fr.gouv.monprojetsup.suggestions.algos.AffinityEvaluator.USE_BIN;
import static fr.gouv.monprojetsup.suggestions.algos.Config.NO_MATCH_SCORE;

public class AlgoSuggestions {

    public static final Logger LOGGER = Logger.getLogger(AlgoSuggestions.class.getName());

    /* les relations entre les différents indices dans les nomenclatures */
    public static final Edges edgesKeys = new Edges();

    protected static final Set<String> apprentissage = new HashSet<>();
    static final int MAX_LENGTH_FOR_SUGGESTIONS = 3;

    //because LAS informatique is a plus but not the canonical path to working as a surgeon for example
    private static final double LASS_TO_PASS_METIERS_PENALTY = 0.25;
    private static final String NOTHING_PERSONAL = "Nothing personal in the profile, serving nothing.";
    private static final double MAX_AFFINITY_PERCENT = 0.90;
    private static final int MIN_CAPACITY_FOR_PENALTY = 1000;
    private static final int MIN_NB_FORMATIONS_FOR_PENALTY = 50;

    //utilisé par suggestions
    public static Map<String, Integer> codesSpecialites = new HashMap<>();

    protected static PsupStatistiques.LASCorrespondance lasCorrespondance;

    @Getter
    protected static final Set<String> relatedToHealth = new HashSet<>();

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static double getBigCapacityScore(String fl) {

        int nbFormations = ServerData.getNbFormations(fl);
        int capacity = ServerData.getCapacity(fl);

        double capacityScore = (capacity >= p75Capacity) ? 1.0 : (double) capacity / p75Capacity;
        double nbFormationsScore = (nbFormations >= p90NbFormations) ? 1.0 : (double) nbFormations / p90NbFormations;
        return capacityScore * nbFormationsScore;

    }
    public static double getSmallCapacityScore(String fl) {
        return 1.0;
        /*

        //int nbFormations = ServerData.getNbFormations(fl);
        int capacity = ServerData.getCapacity(fl);

        double capacityScore = (capacity <= p90Capacity) ? 1.0 : (double) p90Capacity / capacity;
        //double nbFormationsScore = (nbFormations >= p90NbFormations) ? 1.0 : (double) nbFormations / p90NbFormations;
        return capacityScore;// * nbFormationsScore;*/

    }


    public record Affinite(
            double affinite,
            EnumMap<SuggestionDiversityQuota, Double> scores
    ) {

        public boolean satisfiesAllOf(EnumMap<SuggestionDiversityQuota, Double> minScoreForQuota) {
            return affinite > NO_MATCH_SCORE &&  scores.entrySet().stream().allMatch(e -> e.getValue() >= minScoreForQuota.get(e.getKey()));
        }

        public boolean satisfiesOneOf(EnumMap<SuggestionDiversityQuota, Double> minScoreForQuota) {
            return affinite > NO_MATCH_SCORE &&scores.entrySet().stream().anyMatch(e -> e.getValue() >= minScoreForQuota.get(e.getKey()));
        }

        public int getNbQuotasSatisfied(EnumMap<SuggestionDiversityQuota, Double> minScoreForQuota) {
            if(affinite <= NO_MATCH_SCORE) return 0;
            /*if(scores.entrySet().stream().anyMatch(e -> e.getValue() == null)) {
                throw new IllegalStateException("Null score");
            }*/
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
            double newAffinite = Math.max(0.0, Math.min(1.0, Math.round( (aff.affinite / finalMaxScore) * 10e6) / 10e6));
            return new Affinite(newAffinite,aff.scores);
        }

        public @NotNull Affinite max(@Nullable Affinite affinite) {
            if(affinite == null) return this;
            if(affinite.affinite() > this.affinite) return affinite;
            return this;
        }


    }

    /**
     * Get affinities associated to a profile.
     *
     * @param pf  the profile
     * @param cfg the config
     * @return the affinities
     */
    public static @NotNull List<Pair<String, Affinite>> getFormationsAffinities(@NotNull ProfileDTO pf, @NotNull Config cfg) {
        counter.getAndIncrement();
        //rien de spécifique --> on ne suggère rien pour éviter les trucs généralistes
        if(containsNothingPersonal(pf)) {
            LOGGER.info(NOTHING_PERSONAL);
            return List.of();
        }
        //computing interests of all alive filieres
        AffinityEvaluator affinityEvaluator = new AffinityEvaluator(pf, cfg);

        Map<String, Affinite> affinites =
                filieresFront.stream()
                .collect(Collectors.toMap(
                        fl -> flGroups.getOrDefault(fl,fl),
                        affinityEvaluator::getAffinityEvaluation,
                        Affinite::max,
                        TreeMap::new
                ));


        //computing maximal score for etalonnage
        double maxScore = affinites.values().stream().mapToDouble(aff -> aff.affinite).max().orElse(1.0) / MAX_AFFINITY_PERCENT;

        if(maxScore <= NO_MATCH_SCORE) maxScore = 1.0;

        if(USE_BIN) {
            pf.suggRejected().forEach(suggestionDTO -> {
                String fl = suggestionDTO.fl();
                if (affinites.containsKey(fl)) {
                    affinites.put(fl, Affinite.getNoMatch());
                }
            });
        }

        //rounding to 6 digits
        double finalMaxScore = maxScore;
        affinites.entrySet().forEach(e -> e.setValue(Affinite.round(e.getValue(), finalMaxScore)));
        val sorted =  affinites.entrySet().stream()
                .map(Pair::of)
                .sorted(Comparator.comparingDouble(p -> -p.getRight().affinite))
                .toList();
        return sorted;
    }


    /**
     * Get suggestions associated to a profile.
     *
     * @param pf  the profile
     * @param cfg the config
     * @return the suggestions, each indexed with a score
     */
    public static @NotNull List<Pair<String, Double>> getFormationsSuggestions(@NotNull ProfileDTO pf, @NotNull Config cfg) {

        LinkedList<Pair<String, Affinite> > affinities = new LinkedList<>(getFormationsAffinities(pf, cfg));

        //reordering with the following diversity rules
        //the concatenation of the favoris and the suggestions shoudl include at most 1/3 rare formation
        //the first should be adequate to bac

        Map<Affinite.SuggestionDiversityQuota, Double> totals = new EnumMap<>(Affinite.SuggestionDiversityQuota.class);

        List<Pair<String, Double>> result = new ArrayList<>();

        while(!affinities.isEmpty()) {
            int nb = result.size() + 1;
            EnumMap<Affinite.SuggestionDiversityQuota, Double> minScoreForQuota = new EnumMap<>(Affinite.SuggestionDiversityQuota.class);
            Affinite.quotas.forEach((k,v) -> minScoreForQuota.put(k,
                    v * nb - totals.getOrDefault(k, 0.0))
            );

            Pair<String, Affinite> choice;

            val candidates = affinities.stream().filter(c -> c.getRight().affinite > 0).toList();

            Map<String, Integer> scores =
                    candidates.stream()
                            .collect(Collectors.toMap(
                    Pair::getLeft,
                    p -> p.getRight().getNbQuotasSatisfied(minScoreForQuota)
            ));

            int maxScore = scores.values().stream().max(Integer::compareTo).orElse(0);

            choice = candidates.stream()
                    .filter(a -> scores.getOrDefault(a.getLeft(), 0) >= maxScore)
                    .findFirst().orElse(null);

            if(choice == null) {
                //can happen when all scores are zer
                choice = affinities.getFirst();
            }

            result.add(Pair.of(choice.getLeft(), choice.getRight().affinite));
            affinities.remove(choice);

            choice.getRight().scores.forEach((k,v) -> totals.merge(k, v, Double::sum));

        }

        return result;

    }


    /**
     * Sort metiers by affinities
     * @param pf the profile
     * @param cles the keys
     * @param cfg the config
     * @return the sorted metiers. Best first in the list, then second best and so on...
     */
    public static List<String> sortMetiersByAffinites(@NotNull ProfileDTO pf, @Nullable Collection<String> cles, @NotNull Config cfg) {
        counter.getAndIncrement();
        //rien de spécifique --> on ne suggère rien pour éviter les trucs généralistes
        if(containsNothingPersonal(pf)) {
            LOGGER.info(NOTHING_PERSONAL);
            return List.of();
        }

        final Set<String> clesFiltrees;
        //noinspection ReplaceNullCheck
        if(cles != null) {
            clesFiltrees = new HashSet<>(cles);
        } else {
            clesFiltrees = new HashSet<>(edgesKeys.nodes().stream().filter(Helpers::isMetier).toList());
        }
        pf.suggRejected().stream().map(SuggestionDTO::fl).toList().forEach(clesFiltrees::remove);

        return  new AffinityEvaluator(pf, cfg).getCandidatesOrderedByPertinence(clesFiltrees);
    }





    /**
     * Get explanations and examples that explain why a list of formations is suited for a profile
     *
     * @param profile the profile
     * @param keys     the keys of the formations
     * @param cfg     the config
     * @return the explanations and examples associated to the node
     */
    public static List<GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples> getExplanationsAndExamples(
            @Nullable ProfileDTO profile,
            @NotNull List<String> keys,
            @NotNull Config cfg
    ) {
        if(profile == null) {
            return List.of();
        }
        AffinityEvaluator affinityEvaluator = new AffinityEvaluator(profile, cfg);

        return keys.stream().map(affinityEvaluator::getExplanationsAndExamples).toList();

    }

    private static boolean containsNothingPersonal(@NotNull ProfileDTO pf) {
        return  (pf.interests() == null || pf.interests().isEmpty())
                && pf.suggApproved().isEmpty()
                && (pf.geo_pref() == null || pf.geo_pref().isEmpty());
    }

    /**
     * Preccmpute some data used to to details details
     */
    public static void initialize() throws IOException {


        createGraph();

        ServerData.specialites.specialites().forEach((iMtCod, s) -> AlgoSuggestions.codesSpecialites.put(s, iMtCod));

        LOGGER.info("Liste des types de bacs ayant au moins 3 spécialités en terminale");
        bacsWithSpecialites.addAll(ServerData.specialites.specialitesParBac().keySet());

        backPsupData.formations().filieres.values().forEach(filiere -> {
            String key = FILIERE_PREFIX + filiere.gFlCod;
            if (filiere.apprentissage) {
                AlgoSuggestions.apprentissage.add(key);
                String origKey = FILIERE_PREFIX + filiere.gFlCodeFi;
                AlgoSuggestions.apprentissage.add(origKey);
                AlgoSuggestions.apprentissage.add(flGroups.getOrDefault(origKey, origKey));
            }
        });


        lasCorrespondance = statistiques.getLASCorrespondance();

        relatedToHealth.addAll(edgesKeys
                .getSuccessors(gFlCodToFrontId(PASS_FL_COD))
                .keySet());
        relatedToHealth.add(gFlCodToFrontId(PASS_FL_COD));
        relatedToHealth.addAll(lasCorrespondance.lasToGeneric().keySet());
    }


    /**
     * @param bac the bac
     * @return true if the bac is specified and it has three or more different specialites in terminale
     */
    public static boolean hasSpecialitesInTerminale(String bac) {
        return bac == null || bac.isEmpty() || bacsWithSpecialites.contains(bac);
    }

    private static final Set<String> bacsWithSpecialites = new HashSet<>();

    public static boolean existsInApprentissage(String grp) {
        return apprentissage.contains(grp);
    }

    private AlgoSuggestions() {
    }

    public static void createGraph() throws IOException {
        LOGGER.info("Creating global graph");
        edgesKeys.clear();

        backPsupData.filActives().forEach(flcod -> edgesKeys.addNode(gFlCodToFrontId(flcod)));




        /* intégration des relations étendues aux graphes */
        Map<String, Set<String>> metiersVersFormations = ServerData.getMetiersVersFormations();
        val metiersPass = ServerData.getMetiersPass(metiersVersFormations);
        val lasCorr = ServerData.statistiques.getLASCorrespondance();

        metiersVersFormations.forEach((metier, strings) -> strings.forEach(fil -> {
            if(lasCorr.isLas(fil) && metiersPass.contains(metier)) {
                edgesKeys.put(metier, fil, true, LASS_TO_PASS_METIERS_PENALTY);
                /*last evolutiion was t extend metiers generation to all metiers of onisep
                        and to use this 0.25 coef. That pushes up PCSI on profile #1*/

            } else {
                edgesKeys.put(metier, fil, true, 1.0);
            }
        }));

        edgesKeys.putAll(onisepData.edgesInteretsMetiers(), false, EDGES_INTERETS_METIERS_WEIGHT);
        edgesKeys.putAll(onisepData.edgesFilieresThematiques());
        edgesKeys.putAll(onisepData.edgesThematiquesMetiers());

        //ajout des secteurs d'activité
        onisepData.fichesMetiers().metiers().metier().forEach(fiche -> {
            String keyMetier = cleanup(fiche.identifiant());
            if(fiche.secteurs_activite() != null && fiche.secteurs_activite().secteur_activite() != null) {
                fiche.secteurs_activite().secteur_activite().forEach(secteur -> {
                    String keySecteur = cleanup(secteur.id());
                    edgesKeys.put(keyMetier, keySecteur, true, 1.0);
                });

                if(fiche.metiers_associes() != null && fiche.metiers_associes().metier_associe() != null) {
                    fiche.metiers_associes().metier_associe().forEach(metierAssocie -> {
                        String keyMetierAssocie = cleanup(metierAssocie.id());
                        edgesKeys.put(keyMetier, keyMetierAssocie, true, 0.75);
                    });
                }
            }
        });

        //ajout taxonomie thèmes (aujourd'hui plate)
        onisepData.thematiques().parents().forEach(edgesKeys::put);

        //ajout des correspondances de groupes
        flGroups.forEach((s, s2) -> edgesKeys.put(s,s2,true,1.0));
        edgesKeys.addEdgesFromMoreGenericItem(flGroups, 1.0);

        LOGGER.info("Restricting graph to the prestar of recos");
        Set<String> before = new HashSet<>(edgesKeys.nodes());
        Set<String> recoNodes = edgesKeys.nodes().stream().filter(
                Helpers::isFiliere
        ).collect(Collectors.toSet());
        Set<String> useful = edgesKeys.preStar(recoNodes);
        edgesKeys.retainAll(useful);
        Set<String> after = new HashSet<>(edgesKeys.nodes());
        LOGGER.info("Removed  " + (before.size() - after.size()) + " elments using prestar computation");
        before.removeAll(after);
        LOGGER.info("Total nb of edges+ " + edgesKeys.size());

        //LAS inheritance, oth from their mother licence and from PASS
        edgesKeys.addEdgesFromMoreGenericItem(lasCorr.lasToGeneric(), 1.0);

        val corr = new HashMap<>(lasCorr.lasToGeneric());
        corr.entrySet().forEach(e -> e.setValue(Constants.gFlCodToFrontId(PASS_FL_COD)));
        edgesKeys.addEdgesFromMoreGenericItem(corr, LASS_TO_PASS_METIERS_PENALTY);

        //suppression des filières inactives, qui peuvent réapparaitre via les correspondances
        Set<String> filActives = backPsupData.filActives().stream().map(Constants::gFlCodToFrontId).collect(Collectors.toSet());
        Set<String> toErase = edgesKeys.keys().stream().filter(
                s -> s.startsWith(FILIERE_PREFIX) && !filActives.contains(s)
        ).collect(Collectors.toSet());
        //suppression des filières en app couvertes par une filière sans app,
        toErase.addAll(
                backPsupData.getFormationsenAppAvecEquivalentSansApp(filActives)
                        .stream().map(Constants::gFlCodToFrontId)
                        .collect(Collectors.toSet())
        );
        //on conserve les groupes en supprimant de la suppression
        toErase.removeAll(flGroups.values());
        toErase.removeAll(lasCorr.lasToGeneric().keySet());
        edgesKeys.eraseNodes(toErase);

    }

    private static final int PATHES_CACHE_SIZE = 1000;  //1000 is enough ?
    private static final ConcurrentBoundedMapQueue<Pair<String,Integer>, Map<String,List<Path>>> pathsFromDistances
            = new ConcurrentBoundedMapQueue<>(PATHES_CACHE_SIZE);

    /**
     * return s the set of pathes indexed by last node
     * @param n the node
     * @param maxDistance the max distance
     * @return a map of pathes indexed by last node
     */
    public static Map<String,List<Path>> computePathesFrom(String n, int maxDistance) {
        Pair<String, Integer> key = Pair.of(n, maxDistance);

        Map<String,List<Path>> result = pathsFromDistances.get(key);
        if(result != null) return result;
        //noinspection DataFlowIssue
        @NotNull Map<String,List<Path>> result2 = edgesKeys
                        .computePathesFrom(n, maxDistance)
                        .stream()
                        .filter(p -> p.last() != null)
                        .collect(
                                Collectors.groupingBy(Path::last)
                        );
        pathsFromDistances.put(key, result2);
        return result2;
    }

    public static boolean isRelatedToHealth(Set<String> nonZeroScores) {
        return relatedToHealth.stream()
                .anyMatch(nonZeroScores::contains);
    }

    public static boolean isLas(String fl) {
        return lasCorrespondance.isLas(fl);
    }

    public static String getStats() {
        return
                "<br>\ndetails served since last boot: " + counter.get()
                        + "<br>\nnodes in graph: " + edgesKeys.nodes().size()
                + "<br>\nedges in graph: " + edgesKeys.size()
                + "<br>\npathes cache size " + pathsFromDistances.size()
                + "<br>\ndistance cache size " + Distances.distanceCaches.size();

    }


}

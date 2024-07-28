package fr.gouv.monprojetsup.suggestions.algo;

import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.domain.port.EdgesPort;
import fr.gouv.monprojetsup.data.model.Edges;
import fr.gouv.monprojetsup.data.model.Path;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.dto.SuggestionDTO;
import fr.gouv.monprojetsup.suggestions.dto.explanations.CachedGeoExplanations;
import fr.gouv.monprojetsup.suggestions.tools.ConcurrentBoundedMapQueue;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.PASS_FL_COD;
import static fr.gouv.monprojetsup.data.Constants.gFlCodToFrontId;
import static fr.gouv.monprojetsup.data.Helpers.isFiliere;
import static fr.gouv.monprojetsup.suggestions.algo.AffinityEvaluator.USE_BIN;
import static fr.gouv.monprojetsup.suggestions.algo.Config.NO_MATCH_SCORE;

@Component
public class AlgoSuggestions {

    public static final Logger LOGGER = Logger.getLogger(AlgoSuggestions.class.getName());
    @Autowired
    public AlgoSuggestions(SuggestionsData data, EdgesPort edgesPort) {
        this.data = data;
        this.edgesPort = edgesPort;
    }

    private final SuggestionsData data;
    private final EdgesPort edgesPort;

    /* les relations entre les différents indices dans les nomenclatures */
    public final Edges edgesKeys = new Edges();

    private Set<String> apprentissage;
    static final int MAX_LENGTH_FOR_SUGGESTIONS = 3;

    //because LAS informatique is a plus but not the canonical path to working as a surgeon for example
    private static final double LASS_TO_PASS_METIERS_PENALTY = 0.25;

    private static final double EDGES_INTERETS_METIERS_WEIGHT = 0.001;
    private static final double EDGES_METIERS_ASSOCIES_WEIGHT = 0.75;
    private static final String NOTHING_PERSONAL = "Nothing personal in the profile, serving nothing.";
    private static final double MAX_AFFINITY_PERCENT = 0.90;
    private static final int MIN_CAPACITY_FOR_PENALTY = 1000;
    private static final int MIN_NB_FORMATIONS_FOR_PENALTY = 50;

    //utilisé par suggestions
    public Map<String, Integer> codesSpecialites = new HashMap<>();
    public static int p90NbFormations;
    public static int p75Capacity;

    protected Set<String> lasFilieres;

    @Getter
    protected final Set<String> relatedToHealth = new HashSet<>();

    private final AtomicInteger counter = new AtomicInteger(0);

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

        public boolean satisfiesAllOf(Map<SuggestionDiversityQuota, Double> minScoreForQuota) {
            return affinite > NO_MATCH_SCORE &&  scores.entrySet().stream().allMatch(e -> e.getValue() >= minScoreForQuota.get(e.getKey()));
        }

        public boolean satisfiesOneOf(Map<SuggestionDiversityQuota, Double> minScoreForQuota) {
            return affinite > NO_MATCH_SCORE &&scores.entrySet().stream().anyMatch(e -> e.getValue() >= minScoreForQuota.get(e.getKey()));
        }

        public int getNbQuotasSatisfied(Map<SuggestionDiversityQuota, Double> minScoreForQuota) {
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
    public @NotNull List<Pair<String, Affinite>> getFormationsAffinities(@NotNull ProfileDTO pf, @NotNull Config cfg) {
        counter.getAndIncrement();
        //rien de spécifique --> on ne suggère rien pour éviter les trucs généralistes
        if(containsNothingPersonal(pf)) {
            LOGGER.info(NOTHING_PERSONAL);
            return List.of();
        }
        //computing interests of all alive filieres
        AffinityEvaluator affinityEvaluator = new AffinityEvaluator(pf, cfg, data, this);

        Map<String, Affinite> affinites =
                edgesPort.getFilieresFront().stream()
                .collect(Collectors.toMap(
                        fl -> fl,
                        affinityEvaluator::getAffinityEvaluation
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
        return affinites.entrySet().stream()
                .map(Pair::of)
                .sorted(Comparator.comparingDouble(p -> -p.getRight().affinite))
                .toList();
    }


    /**
     * Get suggestions associated to a profile.
     *
     * @param pf  the profile
     * @param cfg the config
     * @return the suggestions, each indexed with a score
     */
    public @NotNull List<Pair<String, Double>> getFormationsSuggestions(@NotNull ProfileDTO pf, @NotNull Config cfg) {

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
    public List<String> sortMetiersByAffinites(@NotNull ProfileDTO pf, @Nullable Collection<String> cles, @NotNull Config cfg) {
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

        return  new AffinityEvaluator(pf, cfg, data, this).getCandidatesOrderedByPertinence(clesFiltrees);
    }





    /**
     * Get explanations and examples that explain why a list of formations is suited for a profile
     *
     * @param profile the profile
     * @param keys     the keys of the formations
     * @param cfg     the config
     * @return the explanations and examples associated to the node
     */
    public List<GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples> getExplanationsAndExamples(
            @Nullable ProfileDTO profile,
            @NotNull List<String> keys,
            @NotNull Config cfg
    ) {
        if(profile == null) {
            return List.of();
        }
        AffinityEvaluator affinityEvaluator = new AffinityEvaluator(profile, cfg, data, this);

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
    @PostConstruct
    void initialize() throws IOException {

        createGraph();

        data.getSpecialites().forEach(
                (iMtCod, s) -> {
                    codesSpecialites.put(s, iMtCod);
                    codesSpecialites.put(Integer.toString(iMtCod), iMtCod);
                }
        );

        LOGGER.info("Liste des types de bacs ayant au moins 3 spécialités en terminale");
        bacsWithSpecialites.addAll(data.getBacsWithAtLeastTwoSpecialites());

        apprentissage = data.getFormationIdsWithApprentissage();

        lasFilieres = data.getLASFormations();

        relatedToHealth.addAll(edgesKeys
                .getSuccessors(gFlCodToFrontId(PASS_FL_COD))
                .keySet());
        relatedToHealth.add(gFlCodToFrontId(PASS_FL_COD));
        relatedToHealth.addAll(lasFilieres);
    }

    public void createGraph() throws IOException {
        LOGGER.info("Creating global graph");
        edgesKeys.clear();

        edgesPort.getFilieresFront().forEach(key -> edgesKeys.addNode(key));

        // intégration des relations étendues aux graphes
        Map<String, Set<String>> metiersVersFormations = data.getMetiersVersFormations();
        val metiersPass = data.getMetiersPass();
        val lasCorr = data.getLasToGeneric();

        metiersVersFormations.forEach((metier, strings) -> strings.forEach(fil -> {
            if(lasCorr.containsKey(fil) && metiersPass.contains(metier)) {
                edgesKeys.put(metier, fil, true, LASS_TO_PASS_METIERS_PENALTY);
                //last evolutiion was t extend metiers generation to all metiers of onisep
                //        and to use this 0.25 coef. That pushes up PCSI on profile #1
            } else {
                edgesKeys.put(metier, fil, true, 1.0);
            }
        }));

        edgesKeys.putAll(data.edgesInteretsMetiers(), false, EDGES_INTERETS_METIERS_WEIGHT);
        edgesKeys.putAll(data.edgesFilieresThematiques());
        edgesKeys.putAll(data.edgesThematiquesMetiers());
        edgesKeys.putAll(data.edgesSecteursMetiers());
        edgesKeys.putAll(data.edgesMetiersAssocies(), true, EDGES_METIERS_ASSOCIES_WEIGHT);


        //ajout des correspondances de groupes
        edgesKeys.putAll(data.edgesFilieresGroupes());
        edgesKeys.addEdgesFromMoreGenericItem(data.edgesFilieresGroupes(), 1.0);

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
        edgesKeys.addEdgesFromMoreGenericItem(data.lasToGeneric(), 1.0);
        edgesKeys.addEdgesFromMoreGenericItem(data.lasToPass(), LASS_TO_PASS_METIERS_PENALTY);

        //suppression des filières inactives, qui peuvent réapparaitre via les correspondances
        Set<String> filFront = new HashSet<>(edgesPort.getFilieresFront());
        Set<String> toErase = edgesKeys.keys().stream().filter(
                s -> isFiliere(s) && !filFront.contains(s)
        ).collect(Collectors.toSet());
        edgesKeys.eraseNodes(toErase);

    }



    /**
     * @param bac the bac
     * @return true if the bac is specified and it has three or more different specialites in terminale
     */
    public boolean hasSpecialitesInTerminale(String bac) {
        return bac == null || bac.isEmpty() || bacsWithSpecialites.contains(bac);
    }

    private final Set<String> bacsWithSpecialites = new HashSet<>();

    public boolean existsInApprentissage(String grp) {
        return apprentissage.contains(grp);
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
    public Map<String,List<Path>> computePathesFrom(String n, int maxDistance) {
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

    public boolean isRelatedToHealth(Set<String> nonZeroScores) {
        return relatedToHealth.stream()
                .anyMatch(nonZeroScores::contains);
    }

    public boolean isLas(String fl) {
        return lasFilieres.contains(fl);
    }

    public String getStats() {
        return
                "<br>\ndetails served since last boot: " + counter.get()
                        + "<br>\nnodes in graph: " + edgesKeys.nodes().size()
                + "<br>\nedges in graph: " + edgesKeys.size()
                + "<br>\npathes cache size " + pathsFromDistances.size()
                + "<br>\ndistance cache size " + CachedGeoExplanations.distanceCaches.size();

    }


}

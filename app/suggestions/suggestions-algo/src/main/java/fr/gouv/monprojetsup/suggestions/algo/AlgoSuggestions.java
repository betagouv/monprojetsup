package fr.gouv.monprojetsup.suggestions.algo;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.model.LatLng;
import fr.gouv.monprojetsup.data.model.Ville;
import fr.gouv.monprojetsup.data.model.stats.Middle50;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.data.model.Edges;
import fr.gouv.monprojetsup.suggestions.data.model.Path;
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.dto.ChoiceDTO;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.isFiliere;
import static fr.gouv.monprojetsup.suggestions.Constants.PASS_FL_COD;
import static fr.gouv.monprojetsup.suggestions.Constants.gFlCodToFrontId;
import static fr.gouv.monprojetsup.suggestions.algo.Config.NO_MATCH_SCORE;

@Component
public class AlgoSuggestions {

    public static final Logger LOGGER = Logger.getLogger(AlgoSuggestions.class.getName());

    @Autowired
    public AlgoSuggestions(SuggestionsData data) {
        this.data = data;
        this.las = data.getLASFormations();
        p50NbFormations = data.p50NbFormations();
        p75Capacity = data.p75Capacity();
    }

    /** all accesses to data after init are cached for perf reasons */
    private final SuggestionsData data;

    /**
     * Données en cache utilisées par le service, précalculées au démarrage
     */

    /* le graphe des relations entre les différentes clés dans les nomenclatures */
    @Getter
    private final Edges edgesKeys = new Edges();

    /* map les flcod vers les frcod */
    private @NotNull Map<String, @NotNull String> typesFormations = new HashMap<>();
    /* les formations en apprentissage */
    private Set<String> apprentissage;
    /* les formations qui sont des LAS, elles sont systématiquement supprimées des suggestions su run profil qui n'a pas coché un intérêt "santé" */
    protected Set<String> las;
    /* la médiane du nombre d'action de formation dans une formation, permet de définir beaucoup d'offre ou peu d'offre */
    public final int p50NbFormations;
    /* le 75 percentiel de la capacité d'accueil globale d'une formation, permet de définir gros ou petit */
    public final int p75Capacity;
    /* la liste des éléments (formations, métiers, ntérêts et secteursActivite) qui sont directement liés aux études de santé, i.e. à PASS */
    @Getter
    protected final Set<String> relatedToHealth = new HashSet<>();
    /**
     * end cache data
     */

    /* compteur d'usage du service */
    private final AtomicInteger counter = new AtomicInteger(0);

     /**
     * Précalcul des données en cache avant démarrage du service
     */
    @PostConstruct
    void initialize() {

        createGraph();

        this.typesFormations = data.getTypesFormations();

        apprentissage = data.getFormationIdsWithApprentissage();

        relatedToHealth.addAll(edgesKeys
                .getSuccessors(gFlCodToFrontId(PASS_FL_COD))
                .keySet());
        relatedToHealth.add(gFlCodToFrontId(PASS_FL_COD));
        relatedToHealth.addAll(data.getLASFormations());
    }

    /**
     * Création du graphe global
     */
    public void createGraph() {
        LOGGER.info("Creating global graph");
        edgesKeys.clear();

        getFormationIds().forEach(edgesKeys::addNode);

        data.edgesFormationsPsupDomaines().forEach(edge -> {
            val formation = edge.src();
            val domaine = edge.dst();
            edgesKeys.put(formation, domaine, false, Config.EDGES_FORMATIONS_DOMAINES_WEIGHT);
            edgesKeys.put(domaine, formation, false, Config.EDGES_DOMAINES_FORMATIONS_WEIGHT);
        });

        val metiersPass = data.getMetiersPass();
        val lasCorr = data.getLASFormations();
        data.edgesMetiersFormationsPsup().forEach(edge -> {
            val metier = edge.src();
            val formation = edge.dst();
            val isLasSante = lasCorr.contains(formation) && metiersPass.contains(metier);
            val penalty = isLasSante ? Config.LASS_TO_PASS_INHERITANCE_PENALTY : 1.0;
            edgesKeys.put(metier, formation, false, penalty * Config.EDGES_METIERS_FORMATIONS_WEIGHT);
            edgesKeys.put(formation, metier, false, penalty * Config.EDGES_FORMATIONS_METIERS_WEIGHT);
        });

        edgesKeys.putAll(data.edgesDomainesMetiers(), true, Config.EDGES_DOMAINES_METIERS_WEIGHT);
        edgesKeys.putAll(data.edgesInteretsMetiers(), false, Config.EDGES_INTERETS_METIERS_WEIGHT); //faible poids
        edgesKeys.putAll(data.edgesMetiersAssocies(), true, Config.EDGES_METIERS_ASSOCIES_WEIGHT);


        //ajout des correspondances atome --> groupement atomes
        //edgesKeys.putAll(data.edgesAtometoElement());
        edgesKeys.replaceSpecificByGeneric(data.edgesAtometoElement(), 1.0);

        //passage au référentiel des formations MPS
        val edgesFilieresGroupes = data.edgesFormationPsupFormationMps();
        //edgesKeys.putAll(edgesFilieresGroupes);
        edgesKeys.replaceSpecificByGeneric(edgesFilieresGroupes, 1.0);

        //LAS inheritance, both from their mother licence and from PASS
        edgesKeys.inheritEdgesFromRicherItem(data.lasToGeneric(), 1.0);
        edgesKeys.inheritEdgesFromRicherItem(data.lasToPass(), Config.LASS_TO_PASS_INHERITANCE_PENALTY);

        //suppression des formations hors référentiel MPS
        Set<String> mpsFormationsIds = new HashSet<>(getFormationIds());

        Set<String> toErase = edgesKeys.keys().stream().filter(
                s -> isFiliere(s) && !mpsFormationsIds.contains(s)
        ).collect(Collectors.toSet());
        LOGGER.info("Erasing filieres which are not indexed by MPS " + toErase.size());
        edgesKeys.eraseNodes(toErase);

        LOGGER.info("Restricting graph to the prestar of recos");
        Set<String> before = new HashSet<>(edgesKeys.nodes());
        Set<String> recoNodes = edgesKeys.nodes().stream().filter(mpsFormationsIds::contains).collect(Collectors.toSet());
        Set<String> useful = edgesKeys.preStar(recoNodes);
        edgesKeys.retainAll(useful);
        Set<String> after = new HashSet<>(edgesKeys.nodes());
        LOGGER.info("Removed  " + (before.size() - after.size()) + " elements using prestar computation");
        before.removeAll(after);
        LOGGER.info("Total nb of edges+ " + edgesKeys.size());


    }


    /**
     * Get affinities associated to a profile.
     *
     * @param pf            the profile
     * @param cfg           the config
     * @param inclureScores if true, the scores are included in the result
     * @return the affinities
     */
    public @NotNull List<Pair<String, Affinite>> getFormationsAffinities(
            @NotNull ProfileDTO pf,
            @NotNull Config cfg,
            boolean inclureScores
    ) {
        counter.getAndIncrement();
        //rien de spécifique --> on ne suggère rien pour éviter les trucs généralistes
        if (containsNothingPersonal(pf)) {
            LOGGER.info(Config.NOTHING_PERSONAL);
            return List.of();
        }
        //computing interests of all alive filieres
        AffinityEvaluator affinityEvaluator = new AffinityEvaluator(pf, cfg, this, true);

        Map<String, Affinite> affinites =
                getFormationIds().stream()
                        .collect(Collectors.toMap(
                                fl -> fl,
                                fl -> affinityEvaluator.getAffinityEvaluation(fl, inclureScores)
                        ));

        //computing maximal score for etalonnage
        double maxScore = affinites.values().stream().mapToDouble(Affinite::affinite).max().orElse(1.0);

        if (maxScore <= NO_MATCH_SCORE) maxScore = 1.0;

        pf.suggRejected().forEach(suggestionDTO -> {
            String fl = suggestionDTO.id();
            if (affinites.containsKey(fl)) {
                affinites.put(fl, Affinite.getNoMatch());
            }
        });


        //rounding to 6 digits
        double finalMaxScore = maxScore;
        affinites.entrySet().forEach(e -> e.setValue(Affinite.round(e.getValue(), finalMaxScore)));
        return affinites.entrySet().stream()
                .map(Pair::of)
                .toList();
    }



    /**
     * Get suggestions associated to a profile.
     *
     * @param pf  the profile
     * @return the suggestions, each indexed with a score
     */
    public @NotNull List<Pair<String, @NotNull Map<String, @NotNull Double>>> getFormationsSuggestions(
            @NotNull ProfileDTO pf,
            boolean inclureScores) {

        LinkedList<Pair<String, Affinite> > affinities = new LinkedList<>(
                getFormationsAffinities(pf, data.getConfig(), inclureScores)
        );
        affinities.sort(Comparator.comparingDouble(p -> -p.getRight().affinite()));

        Map<Affinite.SuggestionQuota, Double> totals = new EnumMap<>(Affinite.SuggestionQuota.class);

        @NotNull List<Pair<String, @NotNull Map<String, @NotNull Double>>> result = new ArrayList<>();

        val config = data.getConfig();
        val diversityMultiplicativeMalus = config.getDiversityMultiplicativeMalusBacGen(pf.bac());

        while(!affinities.isEmpty()) {
            int nb = result.size() + 1;
            EnumMap<Affinite.SuggestionQuota, Double> minScoreForQuotaSatisfaction = new EnumMap<>(Affinite.SuggestionQuota.class);
            Affinite.quotas.forEach((k, v) -> minScoreForQuotaSatisfaction.put(k,
                    v * nb - totals.getOrDefault(k, 0.0))
            );

            Pair<String, Affinite> choice;

            val candidates = affinities.stream().filter(c -> c.getRight().affinite() > 0).toList();

            Map<String, Integer> nbQuotasSatisfied =
                    candidates.stream()
                            .filter(p -> p.getRight().affinite() > NO_MATCH_SCORE)
                            .collect(Collectors.toMap(
                                    Pair::getLeft,
                                    p -> p.getRight().getNbQuotasSatisfied(minScoreForQuotaSatisfaction)
                            ));

            int maxNbQuotasSatisfied = nbQuotasSatisfied.values().stream().mapToInt(n -> n).max().orElse(0);


            //on sélectionne les 10 prochains candidats
            val shortListStream = candidates.stream()
                    .filter(a -> nbQuotasSatisfied.getOrDefault(a.getLeft(), 0) >= maxNbQuotasSatisfied)
                    .limit(config.DiversityShortListLength)
                    ;

            //on calcule la fréquence d'occurence de chaque type de formation dans les 10 derniers résultats
            val typeFormationsCounters =
                    result.stream().skip(Math.max(0, result.size() - config.DiversityShortListLength))
                            .map(Pair::getLeft)
                            .collect(Collectors.groupingBy(typesFormations::get, Collectors.counting()));

            //on trie les 10 prochains candidats en fonction de leur afffinité,
            //avec un malus multiplicatif pour celles qui sont déjà beaucoup apparues lors des 10 derniers résultats
            val shortListSortedStream = shortListStream
                    .sorted(Comparator.comparingDouble(
                            a -> {
                                val typeFormation = typesFormations.getOrDefault(a.getLeft(),"");
                                val nbOccurences = typeFormationsCounters.getOrDefault(typeFormation, 0L);
                                val diversityMalus = Math.pow(diversityMultiplicativeMalus, nbOccurences);
                                val affiniteIncluantMalus = diversityMalus * a.getRight().affinite();
                                return - affiniteIncluantMalus;
                            }
                    ));

            choice = shortListSortedStream.findFirst().orElse(null);

            if (choice == null) {
                //can happen when all scoresDiversiteResultats are zero
                choice = affinities.getFirst();
            }

            result.add(
                    Pair.of(
                            choice.getLeft(),
                            choice.getRight().toScoreDetailsMap()
                    )
            );
            affinities.remove(choice);

            choice.getRight().scoresDiversiteResultats().forEach((k, v) -> totals.merge(k, v, Double::sum));

        }

        return result;

    }


    /**
     * Sort metiers by affinities
     * @param pf the profile
     * @param cles the keys
     * @return the sorted metiers. Best first in the list, then second best and so on...
     */
    public List<String> sortMetiersByAffinites(@NotNull ProfileDTO pf, @Nullable Collection<String> cles) {
        counter.getAndIncrement();
        //rien de spécifique --> on ne suggère rien pour éviter les trucs généralistes
        if(containsNothingPersonal(pf)) {
            LOGGER.info(Config.NOTHING_PERSONAL);
            return List.of();
        }

        final Set<String> clesFiltrees;
        //noinspection ReplaceNullCheck
        if(cles != null) {
            clesFiltrees = new HashSet<>(cles);
        } else {
            clesFiltrees = new HashSet<>(edgesKeys.nodes().stream().filter(Constants::isMetier).toList());
        }
        pf.suggRejected().stream().map(ChoiceDTO::id).toList().forEach(clesFiltrees::remove);

        return  new AffinityEvaluator(pf, data.getConfig(), this, false).getCandidatesOrderedByPertinence(clesFiltrees);
    }





    /**
     * Get explanations and examples that explain why a list of formations is suited for a profile
     *
     * @param profile the profile
     * @param keys     the keys of the formations
     * @return the explanations and examples associated to the node
     */
    public List<GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples> getExplanationsAndExamples(
            @Nullable ProfileDTO profile,
            @NotNull List<String> keys
    ) {
        if(profile == null) {
            return List.of();
        }
        AffinityEvaluator affinityEvaluator = new AffinityEvaluator(profile, data.getConfig(), this, false);

        return keys.stream().map(affinityEvaluator::getExplanationsAndExamples).toList();

    }

    private static boolean containsNothingPersonal(@NotNull ProfileDTO pf) {
        return  (pf.interests() == null || pf.interests().isEmpty())
                && pf.suggApproved().isEmpty()
                && (pf.geo_pref() == null || pf.geo_pref().isEmpty());
    }

    public boolean existsInApprentissage(String grp) {
        return apprentissage.contains(grp);
    }

    private final ConcurrentHashMap< Pair<String,Integer>, List<Path> > pathes = new ConcurrentHashMap<>();
    /**
     * return s the set of pathes indexed by last node
     * @param n the node
     * @param maxDistance the max distance
     * @return a list of pathes from nodes n with a distance less than maxDistance
     */
    public List<Path> computePathesFrom(String n, int maxDistance) {
        return pathes.computeIfAbsent(
                Pair.of(n,maxDistance),
                z -> edgesKeys
                .computePathesFrom(n, maxDistance)
                        .stream()
                        .filter(p -> p.size() > 1)
                        .toList()
        );
    }

    public boolean isRelatedToHealth(Set<String> nonZeroScores) {
        return relatedToHealth.stream()
                .anyMatch(nonZeroScores::contains);
    }

    public boolean isLas(String fl) {
        return las.contains(fl);
    }

    public String getStats() {
        return
                "<br>\ndetails served since last boot: " + counter.get()
                        + "<br>\nnodes in graph: " + edgesKeys.nodes().size()
                + "<br>\nedges in graph: " + edgesKeys.size()
                ;

    }


    private final ConcurrentHashMap<String,Ville> villes = new ConcurrentHashMap<>();
    public Ville getVille(String nomVille) {
        return villes.computeIfAbsent(nomVille, z -> data.getVille(nomVille));
    }


    private final ConcurrentHashMap<Pair<String,Integer>, Map<String, Long>> formationsSimilaires = new ConcurrentHashMap<>();
    public Map<String, Long> getFormationsSimilaires(String fl, int i) {
        return formationsSimilaires.computeIfAbsent(Pair.of(fl, i), z -> data.getFormationsSimilaires(fl, i));
    }

    private final ConcurrentHashMap<String, Integer> nbVoeux = new ConcurrentHashMap<>();
    public int getNbVoeux(String fl) {
        return nbVoeux.computeIfAbsent(fl, data::getNbVoeux);
    }

    private final ConcurrentHashMap<String, Integer> capacites = new ConcurrentHashMap<>();
    public int getCapacity(String fl) {
        return capacites.computeIfAbsent(fl, data::getCapacity);
    }

    private final ConcurrentHashMap<Pair<String,String>, @Nullable Integer> nbAdmis = new ConcurrentHashMap<>();
    public @Nullable Integer getNbAdmis(String grp, String bac) {
        return nbAdmis.computeIfAbsent(Pair.of(grp, bac), z -> data.getNbAdmis(grp, bac));
    }

    private final ConcurrentHashMap<Pair<String,String>, Pair<String, Middle50>> statsBac = new ConcurrentHashMap<>();
    public @Nullable Pair<String, Middle50> getStatsBac(String fl, String bac) {
        return statsBac.computeIfAbsent(Pair.of(fl, bac), z -> data.getStatsBac(fl, bac));
    }

    private final ConcurrentHashMap<String, @NotNull List<@NotNull Pair<@NotNull String, @NotNull LatLng>>> voeuxCoords = new ConcurrentHashMap<>();
    public @NotNull List<@NotNull Pair<@NotNull String, @NotNull LatLng>> getVoeuxCoords(String fl) {
        return voeuxCoords.computeIfAbsent(fl, z -> data.getVoeuxCoords(fl));
    }

    private final ConcurrentHashMap<String, @NotNull Integer> durees = new ConcurrentHashMap<>();
    public int getDuree(String fl) {
        return durees.computeIfAbsent(fl, z -> data.getDuree(fl));
    }

    private final ConcurrentHashMap<String, @NotNull String> debugLabels = new ConcurrentHashMap<>();
    public String getDebugLabel(String key) {
        return debugLabels.computeIfAbsent(key, z -> data.getDebugLabel(key));
    }

    private final ConcurrentHashMap<Pair<String,String>, Double> statsSpecialites = new ConcurrentHashMap<>();
    public Double getStatsSpecialite(String fl, String iMtCod) {
        return statsSpecialites.computeIfAbsent(Pair.of(fl, iMtCod), z -> data.getStatsSpecialite(fl, iMtCod));
    }

    private final ConcurrentHashMap<String, Collection<String>> candidatsMetiers = new ConcurrentHashMap<>();
    public Collection<String> getAllCandidatesMetiers(String key) {
        return candidatsMetiers.computeIfAbsent(key, z -> data.getAllCandidatesMetiers(key));
    }


    private final ConcurrentHashMap<String, List<String>> formationIds = new ConcurrentHashMap<>();
    private List<String> getFormationIds() {
        return formationIds.computeIfAbsent( "", z->data.getFormationIds());
    }

    private final ConcurrentHashMap<String, List<String>> voeuToFormationsIds = new ConcurrentHashMap<>();
    public Map<String,List<String>> getFormationsConnectedToVoeux(List<String> voeux) {
        val result = new HashMap<String,List<String>>();
        voeux.forEach(v -> {
            val formations = voeuToFormationsIds.computeIfAbsent(v, z -> data.getFormationsOfVoeu(v));
            formations.forEach(s -> result.computeIfAbsent(s, z -> new ArrayList<>()).add(v));
        });
        return result;
    }
}

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
import fr.gouv.monprojetsup.suggestions.dto.SuggestionDTO;
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
import static fr.gouv.monprojetsup.suggestions.algo.AffinityEvaluator.USE_BIN;
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

    /* les formations en apprentissage */
    private Set<String> apprentissage;
    /* les formations qui sont des LAS, elles sont systématiquement supprimées des suggestions su run profil qui n'a pas coché un intérêt "santé" */
    protected Set<String> las;
    /* le décodage des matières qui sont des spécialités, soit par nom soit par code string */
    protected final Map<String, Integer> codesSpecialites = new HashMap<>();
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

        data.getSpecialites().forEach(
                spe -> {
                    codesSpecialites.put(spe.idMps(), spe.idPsup());//prod ref
                    codesSpecialites.put(spe.label(), spe.idPsup());//retrocompat pour experts métiers
                    codesSpecialites.put(Integer.toString(spe.idPsup()), spe.idPsup());//retrocompat pour experts métiers
                }
        );

        LOGGER.info("Liste des types de bacs ayant au moins 3 spécialités en terminale");
        bacsWithSpecialites.addAll(data.getBacsWithAtLeastTwoSpecialites());

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

        // intégration des relations étendues aux graphes
        Map<String, Set<String>> metiersVersFormations = data.getMetiersVersFormations();
        val metiersPass = data.getMetiersPass();
        val lasCorr = data.getLASFormations();

        metiersVersFormations.forEach((metier, strings) -> strings.forEach(fil -> {
            if(lasCorr.contains(fil) && metiersPass.contains(metier)) {
                edgesKeys.put(metier, fil, true, Config.LASS_TO_PASS_INHERITANCE_PENALTY);
            } else {
                edgesKeys.put(metier, fil, true, 1.0);
            }
        }));

        data.edgesFormationsPsupDomaines().forEach(edge -> {
            val formation = edge.src();
            val domaine = edge.dst();
            edgesKeys.put(formation, domaine, false, Config.EDGES_FORMATIONS_DOMAINES_WEIGHT);
            edgesKeys.put(domaine, formation, false, Config.EDGES_DOMAINES_FORMATIONS_WEIGHT);
        });
        data.edgesMetiersFormationsPsup().forEach(edge -> {
            val metier = edge.src();
            val formation = edge.dst();
            edgesKeys.put(metier, formation, false, Config.EDGES_METIERS_FORMATIONS_WEIGHT);
            edgesKeys.put(formation, metier, false, Config.EDGES_FORMATIONS_METIERS_WEIGHT);
        });
        edgesKeys.putAll(data.edgesDomainesMetiers(), true, Config.EDGES_DOMAINES_METIERS_WEIGHT);
        edgesKeys.putAll(data.edgesInteretsMetiers(), false, Config.EDGES_INTERETS_METIERS_WEIGHT); //faible poids
        //edgesKeys.putAll(data.edgesSecteursMetiers(), true, Config.EDGES_SECTEUR_METIERS_WEIGHT); //faible poids
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
    public @NotNull List<Pair<String, Affinite>> getFormationsAffinities(@NotNull ProfileDTO pf, @NotNull Config cfg, boolean inclureScores) {
        counter.getAndIncrement();
        //rien de spécifique --> on ne suggère rien pour éviter les trucs généralistes
        if(containsNothingPersonal(pf)) {
            LOGGER.info(Config.NOTHING_PERSONAL);
            return List.of();
        }
        //computing interests of all alive filieres
        AffinityEvaluator affinityEvaluator = new AffinityEvaluator(pf, cfg, this);

        Map<String, Affinite> affinites =
                getFormationIds().stream()
                .collect(Collectors.toMap(
                        fl -> fl,
                        fl -> affinityEvaluator.getAffinityEvaluation(fl, inclureScores)
                ));

        //computing maximal score for etalonnage
        double maxScore = affinites.values().stream().mapToDouble(Affinite::affinite).max().orElse(1.0) / Config.MAX_AFFINITY_PERCENT;

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
                .sorted(Comparator.comparingDouble(p -> -p.getRight().affinite()))
                .toList();
    }



    /**
     * Get suggestions associated to a profile.
     *
     * @param pf  the profile
     * @param cfg the config
     * @return the suggestions, each indexed with a score
     */
    public @NotNull List<Pair<String, @NotNull Map<String, @NotNull Double>>> getFormationsSuggestions(
            @NotNull ProfileDTO pf,
            @NotNull Config cfg,
            boolean inclureScores) {

        LinkedList<Pair<String, Affinite> > affinities = new LinkedList<>(getFormationsAffinities(pf, cfg, inclureScores));

        //reordering with the following diversity rules
        //the concatenation of the favoris and the suggestions shoudl include at most 1/3 rare formation
        //the first should be adequate to bac

        Map<Affinite.SuggestionDiversityQuota, Double> totals = new EnumMap<>(Affinite.SuggestionDiversityQuota.class);

        @NotNull List<Pair<String, @NotNull Map<String, @NotNull Double>>> result = new ArrayList<>();

        while(!affinities.isEmpty()) {
            int nb = result.size() + 1;
            EnumMap<Affinite.SuggestionDiversityQuota, Double> minScoreForQuota = new EnumMap<>(Affinite.SuggestionDiversityQuota.class);
            Affinite.quotas.forEach((k, v) -> minScoreForQuota.put(k,
                    v * nb - totals.getOrDefault(k, 0.0))
            );

            Pair<String, Affinite> choice;

            val candidates = affinities.stream().filter(c -> c.getRight().affinite() > 0).toList();

            Map<String, Integer> nbQuotasSatisfied =
                    candidates.stream()
                            .collect(Collectors.toMap(
                                    Pair::getLeft,
                                    p -> p.getRight().getNbQuotasSatisfied(minScoreForQuota)
                            ));

            int maxNbQuotasSatisfied = nbQuotasSatisfied.values().stream().max(Integer::compareTo).orElse(0);

            choice = candidates.stream()
                    .filter(a -> nbQuotasSatisfied.getOrDefault(a.getLeft(), 0) >= maxNbQuotasSatisfied)
                    .findFirst().orElse(null);

            if (choice == null) {
                //can happen when all scoresDiversiteResultats are zer
                choice = affinities.getFirst();
            }

            result.add(
                    Pair.of(
                            choice.getLeft(),
                            choice.getRight().toMap()
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
     * @param cfg the config
     * @return the sorted metiers. Best first in the list, then second best and so on...
     */
    public List<String> sortMetiersByAffinites(@NotNull ProfileDTO pf, @Nullable Collection<String> cles, @NotNull Config cfg) {
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
        pf.suggRejected().stream().map(SuggestionDTO::fl).toList().forEach(clesFiltrees::remove);

        return  new AffinityEvaluator(pf, cfg, this).getCandidatesOrderedByPertinence(clesFiltrees);
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
        AffinityEvaluator affinityEvaluator = new AffinityEvaluator(profile, cfg, this);

        return keys.stream().map(affinityEvaluator::getExplanationsAndExamples).toList();

    }

    private static boolean containsNothingPersonal(@NotNull ProfileDTO pf) {
        return  (pf.interests() == null || pf.interests().isEmpty())
                && pf.suggApproved().isEmpty()
                && (pf.geo_pref() == null || pf.geo_pref().isEmpty());
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

    /**
     * return s the set of pathes indexed by last node
     * @param n the node
     * @param maxDistance the max distance
     * @return a map of pathes indexed by last node
     */
    private ConcurrentHashMap< Pair<String,Integer>, List<Path> > pathes = new ConcurrentHashMap<>();
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


    private ConcurrentHashMap<String,Ville> villes = new ConcurrentHashMap<>();
    public Ville getVille(String nomVille) {
        return villes.computeIfAbsent(nomVille, z -> data.getVille(nomVille));
    }


    private ConcurrentHashMap<Pair<String,Integer>, Map<String, Integer>> formationsSimilaires = new ConcurrentHashMap<>();
    public Map<String, Integer> getFormationsSimilaires(String fl, int i) {
        return formationsSimilaires.computeIfAbsent(Pair.of(fl, i), z -> data.getFormationsSimilaires(fl, i));
    }

    private ConcurrentHashMap<String, Integer> nbVoeux = new ConcurrentHashMap<>();
    public int getNbVoeux(String fl) {
        return nbVoeux.computeIfAbsent(fl, data::getNbVoeux);
    }

    private ConcurrentHashMap<String, Integer> capacitoes = new ConcurrentHashMap<>();
    public int getCapacity(String fl) {
        return capacitoes.computeIfAbsent(fl, data::getCapacity);
    }

    private ConcurrentHashMap<Pair<String,String>, Integer> nbAdmis = new ConcurrentHashMap<>();
    public Integer getNbAdmis(String grp, String tousBacsCodeMps) {
        return nbAdmis.computeIfAbsent(Pair.of(grp, tousBacsCodeMps), z -> data.getNbAdmis(grp, tousBacsCodeMps));
    }

    private ConcurrentHashMap<Pair<String,String>, Pair<String, Middle50>> statsBac = new ConcurrentHashMap<>();
    public @Nullable Pair<String, Middle50> getStatsBac(String fl, String bac) {
        return statsBac.computeIfAbsent(Pair.of(fl, bac), z -> data.getStatsBac(fl, bac));
    }

    private ConcurrentHashMap<String, @NotNull List<@NotNull Pair<@NotNull String, @NotNull LatLng>>> voeuxCoords = new ConcurrentHashMap<>();
    public @NotNull List<@NotNull Pair<@NotNull String, @NotNull LatLng>> getVoeuxCoords(String fl) {
        return voeuxCoords.computeIfAbsent(fl, z -> data.getVoeuxCoords(fl));
    }

    private ConcurrentHashMap<String, @NotNull Integer> durees = new ConcurrentHashMap<>();
    public int getDuree(String fl) {
        return durees.computeIfAbsent(fl, z -> data.getDuree(fl));
    }

    private ConcurrentHashMap<String, @NotNull String> debugLabels = new ConcurrentHashMap<>();
    public String getDebugLabel(String key) {
        return debugLabels.computeIfAbsent(key, z -> data.getDebugLabel(key));
    }

    private ConcurrentHashMap<Pair<String,Integer>, Double> statsSpecialites = new ConcurrentHashMap<>();
    public Double getStatsSpecialite(String fl, Integer iMtCod) {
        return statsSpecialites.computeIfAbsent(Pair.of(fl, iMtCod), z -> data.getStatsSpecialite(fl, iMtCod));
    }

    private ConcurrentHashMap<String, Collection<String>> candidatsMetiers = new ConcurrentHashMap<>();
    public Collection<String> getAllCandidatesMetiers(String key) {
        return candidatsMetiers.computeIfAbsent(key, z -> data.getAllCandidatesMetiers(key));
    }


    private ConcurrentHashMap<String, List<String>> formationIds = new ConcurrentHashMap<>();
    private List<String> getFormationIds() {
        return formationIds.computeIfAbsent( "", z->data.getFormationIds());
    }

}

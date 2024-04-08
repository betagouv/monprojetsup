package fr.gouv.monprojetsup.suggestions.algos;

import com.google.gson.Gson;
import fr.gouv.monprojetsup.common.dto.SuggestionDTO;
import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.Edges;
import fr.gouv.monprojetsup.data.model.Path;
import fr.gouv.monprojetsup.data.model.cities.CitiesBack;
import fr.gouv.monprojetsup.data.model.cities.Coords;
import fr.gouv.monprojetsup.data.model.cities.Distance;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.model.formations.Formation;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.update.UpdateFrontData;
import fr.gouv.monprojetsup.common.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.server.Log;
import fr.parcoursup.carte.algos.tools.Paire;
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
import static fr.gouv.monprojetsup.suggestions.algos.Config.NO_MATCH_SCORE;
import static java.lang.Math.max;
import static java.lang.Math.signum;

public class AlgoSuggestions {

    public static final Logger LOGGER = Logger.getLogger(AlgoSuggestions.class.getName());

    /* les relations entre les différents indices dans les nomenclatures */
    public static final Edges edgesKeys = new Edges();

    /* les relations entre les différents labels dans les nomenclatures */
    public static final Edges edgesLabels = new Edges();
    protected static final Set<String> apprentissage = new HashSet<>();
    static final int MAX_LENGTH_FOR_SUGGESTIONS = 3;

    //because LAS informatique is a plus but not the canonical path to working as a surgeon for example
    private static final double LAX_TO_PASS_METIERS_PENALTY = 0.25;
    private static final String NOTHING_PERSONAL = "Nothing personal in the profile, serving nothing.";

    private static PsupStatistiques.LASCorrespondance lasCorrespondance;

    @Getter
    protected static final Set<String> relatedToHealth = new HashSet<>();

    private static final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Get details associated to a profile.
     * Side effect: inject explanations into personal choices personal
     *
     * @param pf  the profile
     * @param cfg the config used, essentially weights on criteria
     * @return the details
     */
    public static @NotNull Suggestions getSuggestions(
            @Nullable ProfileDTO pf,
            @NotNull Config cfg
    ) {
        counter.getAndIncrement();
        //rien de spécifique --> on ne suggère rien pour éviter les trucs généralistes
        if(pf == null || containsNothingPersonal(pf)) {
            LOGGER.info(NOTHING_PERSONAL);
            return new Suggestions();
        }
        //computing interests of all alive filieres
        AffinityEvaluator affinityEvaluator = new AffinityEvaluator(pf, cfg);
        Map<String, Double> filieresScores = new HashMap<>();

        /* première passe pour calculer les interests */
        backPsupData.filActives().forEach(gFlCod -> {
            String fl = FILIERE_PREFIX + gFlCod;
            // the core computation
            double score = affinityEvaluator.getAffinityEvaluation(fl);
            //
            if (score > NO_MATCH_SCORE) {
                filieresScores.put(fl, score);
            }
        });

        if(filieresScores.isEmpty()) {
            Log.logTrace(AlgoSuggestions.class.getSimpleName(), "Tous les interests des filières à 0 sur un un profil contenant des éléments personnesl"
                    + new Gson().toJson(pf.cleanupDates())
            );
        }

        //LOGGER.info(filieresScores.size() + " filieres have a score > 0");


        /* seconde passe pour calculer des explications */
        List<Suggestion> filiereSuggs = getFilieresSuggestions(filieresScores, cfg);

        if(filiereSuggs.isEmpty()  && !filieresScores.isEmpty()) {
            Log.logBackError("Pas de suggestion de filiere sur un un profil contenant des éléments personnesl et des interests filieres non vide"
                    + new Gson().toJson(pf.cleanupDates())
            );
        }

        /* troisième passe pour compléter avec les thèmes et les métiers */
        List<Suggestion> metiersSuggestions
                = affinityEvaluator
                .getCloseTagsSuggestionsOrderedByIncreasingDistance(
                        MAX_LENGTH_FOR_SUGGESTIONS,
                        false,
                        cfg.maxNbSuggestions()
                );
        if(metiersSuggestions.isEmpty()) {
            Log.logTrace(AlgoSuggestions.class.getSimpleName(), "Tous les interests des metiers à 0 sur le profil " + new Gson().toJson(pf.cleanupDates()));
        }
        //LOGGER.info(metiersSuggestions.size() + " metiers details");

        List<Suggestion> answer = new ArrayList<>(
                filiereSuggs.stream()
                        .limit((cfg.isVerboseMode() ? 5L : 1L) * cfg.maxNbSuggestions())
                        .toList()
        );

        List.of(SEC_ACT_PREFIX_IN_GRAPH).forEach(pref ->
                answer.addAll(
                metiersSuggestions.stream()
                        .filter(s -> s.fl().startsWith(pref))
                        .limit(cfg.maxNbSuggestions())
                        .toList()
                )
        );
        Log.logTrace("anonymous",  "Serving " + filiereSuggs.size()
                + " formations details, total "+ answer.size()
        );

        return new Suggestions(answer);

    }


    /**
     * Get affinities associated to a profile.
     *
     * @param pf  the profile
     * @param cfg the config
     * @return the affinities
     */
    public static @NotNull List<Pair<String, Double>> getFormationsAffinities(@NotNull ProfileDTO pf, @NotNull Config cfg) {
        counter.getAndIncrement();
        //rien de spécifique --> on ne suggère rien pour éviter les trucs généralistes
        if(containsNothingPersonal(pf)) {
            LOGGER.info(NOTHING_PERSONAL);
            return List.of();
        }
        //computing interests of all alive filieres
        AffinityEvaluator affinityEvaluator = new AffinityEvaluator(pf, cfg);

        Map<String, Double> affnites = backPsupData.filActives().stream()
                .map(Constants::gFlCodToFrontId)
                .collect(Collectors.toMap(
                        fl -> flGroups.getOrDefault(fl,fl),
                        affinityEvaluator::getAffinityEvaluation,
                        Math::max,
                        TreeMap::new
                ));


        //computing maximal score for etalonnage
        double maxScore = affnites.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);

        if(maxScore <= NO_MATCH_SCORE) return List.of();

        pf.suggRejected().forEach(suggestionDTO -> {
            String fl = suggestionDTO.fl();
            if(affnites.containsKey(fl)) {
                affnites.put(fl, NO_MATCH_SCORE);
            }
        });

        //rounding to 6 digits
        affnites.entrySet().forEach(e -> e.setValue(max(0.0, Math.min(1.0, Math.round( (e.getValue() / maxScore) * 10e6) / 10e6))));
        return affnites.entrySet().stream().map(Pair::of).sorted(Comparator.comparingDouble(p -> -p.getRight())).toList();
    }


    /**
     * Sort metiers by affinities
     * @param pf the profile
     * @param cles the keys
     * @param cfg the config
     * @return the sorted metiers. Best first in the list, then second best and so on...
     */
    public static List<String> sortMetiersByAffinites(@NotNull ProfileDTO pf, @NotNull List<String> cles, @NotNull Config cfg) {
        counter.getAndIncrement();
        //rien de spécifique --> on ne suggère rien pour éviter les trucs généralistes
        if(containsNothingPersonal(pf)) {
            LOGGER.info(NOTHING_PERSONAL);
            return List.of();
        }

        Set<String> clesFiltrees = new HashSet<>(cles);
        pf.suggRejected().stream().map(SuggestionDTO::fl).toList().forEach(clesFiltrees::remove);

        return  new AffinityEvaluator(pf, cfg).getCandidatesOrderedByPertinence(clesFiltrees);
    }


    /**
     * Get all details about details
     *
     * @param pf the profile
     * @param cfg the config
     * @param keys the list of keys for which details are required
     * @return the details
     */
    public static List<DetailedSuggestion> getDetails(ProfileDTO pf, Config cfg, List<String> keys) {

        AffinityEvaluator affinityEvaluator = new AffinityEvaluator(pf, cfg);


        List<DetailedSuggestion> result = new ArrayList<>();

        keys.forEach(key -> {
            affinityEvaluator.getExplanations(key);
            val expls = affinityEvaluator.getExplanationsAndExamples(key);
            result.add(
                    new DetailedSuggestion(
                            key,
                            expls.getLeft(),
                            expls.getRight()
                    )
            );
        });

        return result;

    }

    /**
     * Get explanations and examples associated to a suggestion
     * @param profile the profile
     * @param key the key of the node
     * @param cfg the config
     * @return the explanations and examples associated to the node
     */
    public static Pair<List<Explanation>,List<String>> getExplanationsAndExamples(
            @Nullable ProfileDTO profile,
            @Nullable String key,
            @NotNull Config cfg
    ) {
        if(profile == null || key == null) {
            return Pair.of(Collections.emptyList(), Collections.emptyList());
        }
        AffinityEvaluator affinityEvaluator = new AffinityEvaluator(profile, cfg);
        return affinityEvaluator.getExplanationsAndExamples(key);

    }


    private static List<Suggestion> getFilieresSuggestions(
            Map<String, Double> filieresScores,
            Config cfg) {

        List<String> bestSuggestionsUngrouped = new ArrayList<>(filieresScores.keySet());

        Map<String, List<String>> groupedIndices = bestSuggestionsUngrouped.stream().collect(
                Collectors.groupingBy(
                        ServerData::getGroupOfFiliere
                )
        );

        groupedIndices.forEach((s, suggestions) ->
                filieresScores.put(
                        s,
                        suggestions.stream().mapToDouble(
                                fl -> filieresScores.getOrDefault(fl, NO_MATCH_SCORE)
                        ).max().orElse(NO_MATCH_SCORE)
                ));
        List<String> bestSuggestionsGrouped = new ArrayList<>(groupedIndices.keySet());
        //les plus gros interests en premier
        bestSuggestionsGrouped.sort((o1, o2) ->
             (int) signum(filieresScores.get(o2) - filieresScores.get(o1))
        );

        int nbSugg = Math.min(cfg.maxNbSuggestions(), bestSuggestionsGrouped.size());

        return bestSuggestionsGrouped.stream()
                        .limit(nbSugg)
                        .map(groupKey -> {
                            List<String> keys = groupedIndices.get(groupKey);
                            return Suggestion.merge(groupKey, keys, null);
                        }).toList();
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

        LOGGER.info("Liste des types de bacs ayant au moins 3 spécialités en terminale");
        bacsWithSpecialites.addAll(ServerData.specialites.specialitesParBac().keySet());

        LOGGER.info("Double indexation des villes");
        CitiesBack cities = ServerData.cities;
        //double indexation par nom et par zip code
        cities.cities().forEach(c -> cityClientKeyToCities.put(c.name(), c.coords()));
        cities.cities().forEach(c -> {
                    if (c.coords() != null) {
                        c.coords().forEach(cc ->
                                cityClientKeyToCities.put(cc.zip_code(), c.coords())
                        );
                        c.coords().forEach(cc ->
                                cityClientKeyToCities.put("i" + cc.insee_code(), c.coords())
                        );
                    }
                }
        );

        backPsupData.formations().filieres.values().forEach(filiere -> {
            String key = FILIERE_PREFIX + filiere.gFlCod;
            if (filiere.apprentissage) {
                AlgoSuggestions.apprentissage.add(key);
                AlgoSuggestions.apprentissage.add(FILIERE_PREFIX + filiere.gFlCodeFi);
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
     * computes minimal distance between a city and a filiere
     *
     * @param node     the node, either "fl123" or "fr1223"
     * @param cityName the city name
     * @return the minimal distance of the city to a etablissement providing this filiere
     */
    public static @NotNull List<Explanation.ExplanationGeo> getDistanceKm(String node, String cityName) {

        /*
                                    distanceKm.intValue(),
                                    cityName,
                                    result.getRight()))
        */
        Paire<String, String> p = new Paire<>(node, cityName);

        val l = distanceCaches.get(p);
        if( l != null) return l;

        List<Coords> cities = cityClientKeyToCities.get(cityName);
        if (cities == null || cities.isEmpty())
            return Collections.emptyList();

        List<Formation> fors = Collections.emptyList();
        ///attention aux groupes
        if (node.startsWith(FILIERE_PREFIX)) {
            fors = getFormationsFromFil(node);
        } else if (node.startsWith((Constants.FORMATION_PREFIX))) {
            int gTaCod = Integer.parseInt(node.substring(2));
            Formation f = backPsupData.formations().formations.get(gTaCod);
            if (f != null) {
                fors = List.of(f);
            }
        }

        List<Pair<String, Coords>> coords = fors.stream()
                .filter(f -> f.lng != null && f.lat != null)
                .map(f -> Pair.of(FORMATION_PREFIX + f.gTaCod, new Coords("", "", f.lat, f.lng))).toList();

        if (coords.isEmpty())
            return Collections.emptyList();
        List<Pair<Integer, String>> results = Distance.getDistanceKm(cities, coords);
        if(results == null)
            return Collections.emptyList();
        List<Explanation.ExplanationGeo> e =
                results.stream().map(result -> new Explanation.ExplanationGeo(
                result.getLeft(),
                cityName,
                result.getRight()
                )
                ).toList();
        distanceCaches.put(p, e);
        return e;
    }
    public static @NotNull List<Explanation.ExplanationGeo> getDistanceKm(Collection<String> node, String cityName) {
        return
                node.stream().flatMap(n -> getDistanceKm(n, cityName).stream())
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(Explanation.ExplanationGeo::distance))
                        .distinct()
                        .limit(2)
                        .toList();
    }


    protected static final Map<String, List<Coords>> cityClientKeyToCities = new HashMap<>();


    private static final int DISTANCE_CACHE_SIZE = 10000;
    /**
     * caches return values of getDistanceKm
     */
    private static final ConcurrentBoundedMapQueue<Paire<String, String>, List<Explanation.ExplanationGeo>>
            distanceCaches = new ConcurrentBoundedMapQueue<>(DISTANCE_CACHE_SIZE);

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


        Descriptifs descriptifs = UpdateFrontData.DataContainer.loadDescriptifs(
                onisepData,
                backPsupData.getCorrespondances(),
                statistiques.getLASCorrespondance().lasToGeneric()
        );


        /* intégration des relations étendues aux graphes */
        Map<String, Set<String>> metiersVersFormations = onisepData.getExtendedMetiersVersFormations(
                backPsupData.getCorrespondances(),
                statistiques.getLASCorrespondance(),
                descriptifs
        );
       String passKey =  Constants.gFlCodToFrontId(PASS_FL_COD);
       Set<String> lasKeys = ServerData.statistiques.getLASCorrespondance().lasToGeneric().keySet();

        Set<String> metiersPass =
                metiersVersFormations.entrySet().stream()
                        .filter(e -> e.getValue().contains( passKey))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());

        metiersVersFormations.forEach((metier, strings) -> strings.forEach(fil -> {
            if(lasKeys.contains(fil) && metiersPass.contains(metier)) {
                edgesKeys.put(metier, fil, true, LAX_TO_PASS_METIERS_PENALTY);
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
        onisepData.thematiques().parent().forEach(edgesKeys::put);

        //ajout des correspondances de groupes
        flGroups.forEach((s, s2) -> edgesKeys.put(s,s2,true,1.0));
        edgesKeys.addEdgesFromMoreGenericItem(flGroups, 1.0);

        LOGGER.info("Restricting graph to the prestar of recos");
        Set<String> before = new HashSet<>(edgesKeys.nodes());
        Set<String> recoNodes = edgesKeys.nodes().stream().filter(
                ServerData::isFiliere
        ).collect(Collectors.toSet());
        Set<String> useful = edgesKeys.preStar(recoNodes);
        edgesKeys.retainAll(useful);
        Set<String> after = new HashSet<>(edgesKeys.nodes());
        LOGGER.info("Removed  " + (before.size() - after.size()) + " elments using prestar computation");
        before.removeAll(after);
        LOGGER.info("Total nb of edges+ " + edgesKeys.size());

        //LAS inheritance, oth from their mother licence and from PASS
        Map<String, String> lasCorr = ServerData.statistiques.getLASCorrespondance().lasToGeneric();
        edgesKeys.addEdgesFromMoreGenericItem(lasCorr, 1.0);

        lasCorr.entrySet().forEach(e -> e.setValue(passKey));
        edgesKeys.addEdgesFromMoreGenericItem(lasCorr, LAX_TO_PASS_METIERS_PENALTY);

        //suppression des filières inactives, qui peuvent réapparaitre via les correspondances
        Set<String> filActives = backPsupData.filActives().stream().map(Constants::gFlCodToFrontId).collect(Collectors.toSet());
        Set<String> toErase = edgesKeys.edges().keySet().stream().filter(
                s -> s.startsWith(FILIERE_PREFIX) && !filActives.contains(s)
        ).collect(Collectors.toSet());
        //suppression des filières en app couvertes par une filière sans app,
        toErase.addAll(
                backPsupData.getFormationsenAppAvecEquivalentSansApp().values()
                        .stream().map(Constants::gFlCodToFrontId)
                        .collect(Collectors.toSet())
        );
        //on conserve les groupes
        toErase.removeAll(flGroups.values());
        toErase.removeAll(lasCorr.keySet());
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
        //noinspection DataFlowIssue
        Pair<String, Integer> key = Pair.of(n, maxDistance);

        Map<String,List<Path>> result = pathsFromDistances.get(key);
        if(result != null) return result;
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
                + "<br>\ndistance cache size " + distanceCaches.size();

    }


    public record DetailedSuggestion(
            String key,
            @NotNull List<Explanation> explanations,
            @NotNull List<String> exemples

    ) {
    }
}

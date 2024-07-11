package fr.gouv.monprojetsup.suggestions.data;

import fr.gouv.monprojetsup.suggestions.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.suggestions.data.model.formations.Formation;
import fr.gouv.monprojetsup.suggestions.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.data.model.stats.Statistique;
import fr.gouv.monprojetsup.suggestions.data.model.stats.StatsContainers;
import fr.gouv.monprojetsup.suggestions.data.model.thematiques.Thematiques;
import jakarta.annotation.PostConstruct;
import lombok.val;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.isLas;

@Component
public class SuggestionsData {

        private final Map<String,String> labels = new HashMap<>();

    @PostConstruct
    private void load() {
        throw new NotImplementedException("Not implemented yet");
    }

    public String getLabel(String key, String key1) {
            return labels.getOrDefault(key, key1);
    }

    public static String getLabel(String key) {
        throw new NotImplementedException("Not implemented yet");
    }

    public static @NotNull Map<String, String> getLabels() {
        /* statistiques.labels*/
        throw new NotImplementedException("Not implemented yet");
    }

    public static Collection<String> getChildrenOfGroup(String key) {
        //).reverseFlGroups.getOrDefault(key, List.of(key)
        throw new NotImplementedException("Not implemented yet");
    }

    public static String getGroup(String key) {
        throw new NotImplementedException("Not implemented yet");
    }

    public static void loadStatistiques() {
        /*
                    this.statistiques = new PsupStatistiques();
        ServerData.statistiques.labels = Serialisation.fromJsonFile(
                "labelsDebug.json",
                Map.class
        );*/
        throw new NotImplementedException("Not implemented yet");
    }

    public static Collection<String> getInterestsExpansion(String key) {
        /* onisepData.interets().expansion().getOrDefault(key, Collections.emptyList())*/
        throw new NotImplementedException("Not implemented yet");
    }

    public static Map<String, Integer> getFilieresSimilaires(String fl, int i) {
        //from backpsupdata
        throw new NotImplementedException("Not implemented yet");
    }

    public static List<Formation> getFormationsFromFil(String flKey) {
        throw new NotImplementedException("Not implemented yet");
    }

    public static void initCities() {
        /*
        log.info("Double indexation des villes");
        CitiesBack cities = ServerData.cities;
        Distances.init(cities);*/
        throw new NotImplementedException("Not implemented yet");
    }

    public static Integer getNbAdmis(String grp, String tousBacsCode) {
        /*Integer nbAdmisTousBac = ServerData.statistiques.getNbAdmis(grp, PsupStatistiques.TOUS_BACS_CODE);
        Integer nbAdmisBac = ServerData.statistiques.getNbAdmis(grp, pf.bac());
        */
        throw new NotImplementedException("Not implemented yet");
    }

    public static int getDuree(String fl) {
        /* int duree = ServerData.backPsupData.getDuree(fl);*/
        throw new NotImplementedException("Not implemented yet");
    }

    public static Pair<String, Statistique> getStatsBac(String fl, String bac) {
        /* Pair<String, Statistique> stats = ServerData.statistiques.getStatsBac(fl, pf.bac());*/
        throw new NotImplementedException("Not implemented yet");
    }

    public static String getDebugLabel(String key) {
        throw new NotImplementedException("Not implemented yet");
    }

    public static Double getStatsSpecialite(String fl, Integer iMtCod) {
        /*Double stat = ServerData.statistiques.getStatsSpecialite(fl, iMtCod);*/
        throw new NotImplementedException("Not implemented yet");
    }


    private final static Map<String,Set<String>> candidatesMetiersCache = new ConcurrentHashMap<>();

    public static Set<String> getAllCandidatesMetiers(String key) {
            val result = candidatesMetiersCache.get(key);
            if(result != null) return result;
            Set<String> candidates = new HashSet<>(
                    SuggestionsData.getCandidatesMetiers(key)
            );
            if (isLas(key)) {
                String key2 = getGenericFromLas(key);
                if (key2 != null) {
                    candidates.addAll(SuggestionsData.getMetiersfromfiliere(key2));
                    candidates.addAll(SuggestionsData.getPassMetiers());
                }
            }
            candidates.addAll(SuggestionsData.getMetiersfromGroup(key));
            candidatesMetiersCache.put(key, candidates);
            return candidates;
    }

    private static String getGenericFromLas(String key) {
        /* lasCorrespondance.lasToGeneric().get(key)*/
        throw new NotImplementedException("Not implemented yet");
    }

    private static int getCandidatesMetiers(String key) {
        /*onisepData.edgesMetiersFilieres().getSuccessors(key).keySet()*/
        throw new NotImplementedException("Not implemented yet");

    }

    private static Collection<String> getMetiersfromfiliere(String key2) {
        /* onisepData.edgesMetiersFilieres().getSuccessors(key2).keySet() */
        throw new NotImplementedException("Not implemented yet");
    }

    private static Collection<String> getPassMetiers() {
        /*(key2) onisepData.edgesMetiersFilieres().getSuccessors(gFlCodToFrontId(PASS_FL_COD)).keySet()*/
        throw new NotImplementedException("Not implemented yet");
    }

    private static Collection<String> getMetiersfromGroup(String key) {
        /*        if (reverseFlGroups.containsKey(key)) {
            candidates.addAll(reverseFlGroups.get(key).stream().flatMap(g -> onisepData.edgesMetiersFilieres().getSuccessors(g).keySet().stream()).toList());
        }*/
        throw new NotImplementedException("Not implemented yet");
    }

    public static Collection<String> getMetiersFromSecteur(String key) {
        /* liensSecteursMetiers.getOrDefault(key, Collections.emptySet())*/
        throw new NotImplementedException("Not implemented yet");
    }

    public static int getNbFormations(String fl) {
        throw new NotImplementedException("Not implemented yet");
    }

    public static int getCapacity(String fl) {
        throw new NotImplementedException("Not implemented yet");
    }

    public static Collection<String> getFilieresFront() {
        /* filieresFront */
        throw new NotImplementedException("Not implemented yet");
    }

    public static PsupStatistiques.LASCorrespondance getLASCorrespondance() {
        throw new NotImplementedException("Not implemented yet");
    }

    public static Map<Integer, String> getSpecialites() {
        /*ServerData.specialites.specialites().forEach((iMtCod, s) -> AlgoSuggestions.codesSpecialites.put(s, iMtCod));*/
        throw new NotImplementedException("Not implemented yet");
    }

    public static List<Pair<String, String>> getApprentissage() {
        /*         backPsupData.formations().filieres.values().forEach(filiere -> {
            String key = FILIERE_PREFIX + filiere.gFlCod;
            if (filiere.apprentissage) {
                AlgoSuggestions.apprentissage.add(key);
                String origKey = FILIERE_PREFIX + filiere.gFlCodeFi;
                AlgoSuggestions.apprentissage.add(origKey);
                AlgoSuggestions.apprentissage.add(flGroups.getOrDefault(origKey, origKey));
            }
        });
        */
        throw new NotImplementedException("Not implemented yet");
    }

    public static Collection<String> getBacsWithSpecialite() {
        /*ServerData.specialites.specialitesParBac().keySet()*/
        throw new NotImplementedException("Not implemented yet");
    }

    public static Descriptifs getDescriptifs() {
        /* UpdateFrontData.DataContainer.loadDescriptifs(
                onisepData,
                backPsupData.getCorrespondances(),
                statistiques.getLASCorrespondance().lasToGeneric()
        );
        */
        throw new NotImplementedException("Not implemented yet");
    }

    public static Thematiques getThematiques() {
        /*Thematiques.load();*/
        throw new NotImplementedException("Not implemented yet");
    }


    public static int getNbMetiersOnisep() {
        /* onisepData.metiers().metiers().size())*/
        throw new NotImplementedException("Not implemented yet");
    }

    public static int getNbThematiquesOnisep() {
        /* onisepData.thematiques().thematiques().size());*/
        throw new NotImplementedException("Not implemented yet");
    }

    public static Comparator<? super Object> getTypesMacros() {
        /*  .backPsupData.formations().typesMacros */
        throw new NotImplementedException("Not implemented yet");
    }

    public static void initStatistiques() {
        /*        try {
            ServerData.statistiques =  new PsupStatistiques();
            ServerData.statistiques.labels = Serialisation.fromJsonFile(
                    "labelsDebug.json",
                    Map.class
            );
        } catch (Exception e) {
            SuggestionServer server = new SuggestionServer();
            server.init();
        }
        */
        throw new NotImplementedException("Not implemented yet");
    }

    public static Formation getFormation(int gTaCod) {
        throw new NotImplementedException("Not implemented yet");
    }

    public static StatsContainers.SimpleStatGroupParBac getSimpleGroupStats(String bac, String key) {
        throw new NotImplementedException("Not implemented yet");
    }

    public static void createGraph() {
        /*
        public static void createGraph() throws IOException {
        LOGGER.info("Creating global graph");
        edgesKeys.clear();

        backPsupData.filActives().forEach(flcod -> edgesKeys.addNode(gFlCodToFrontId(flcod)));

        // intégration des relations étendues aux graphes
        Map<String, Set<String>> metiersVersFormations = ServerData.getMetiersVersFormations();
        val metiersPass = ServerData.getMetiersPass(metiersVersFormations);
        val lasCorr = ServerData.statistiques.getLASCorrespondance();

        metiersVersFormations.forEach((metier, strings) -> strings.forEach(fil -> {
            if(lasCorr.isLas(fil) && metiersPass.contains(metier)) {
                edgesKeys.put(metier, fil, true, LASS_TO_PASS_METIERS_PENALTY);
                //last evolutiion was t extend metiers generation to all metiers of onisep
                //        and to use this 0.25 coef. That pushes up PCSI on profile #1
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

        * */
    }

    public static Map<String,String> getFormationTofilieres() {
        throw new NotImplementedException("Not implemented yet");
    }

}

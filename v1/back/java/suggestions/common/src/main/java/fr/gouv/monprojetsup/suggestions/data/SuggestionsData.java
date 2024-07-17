package fr.gouv.monprojetsup.suggestions.data;

import fr.gouv.monprojetsup.suggestions.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.suggestions.data.model.formations.Formation;
import fr.gouv.monprojetsup.suggestions.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.data.model.stats.Statistique;
import fr.gouv.monprojetsup.suggestions.data.model.stats.StatsContainers;
import fr.gouv.monprojetsup.suggestions.data.update.psup.FormationsSimilaires;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.*;

import static fr.gouv.monprojetsup.suggestions.data.Constants.FORMATION_PREFIX;

@Component
public class SuggestionsData {

    private static Map<String, Descriptifs.Descriptif> descriptifs;
    private List<String> filieresFront;
    private final Map<String,String> labels = new HashMap<>();
    private final Map<String,List<String>> relatedInterests = new HashMap<>();
    private PsupStatistiques statistiques;
    private FormationsSimilaires filieresSimilaires;
    private final Map<String,List<Formation>> formations = new HashMap<>();
    private final Map<String,Integer> capacities = new HashMap<>();
    private final Map<String,Integer> durees = new HashMap<>();
    private final Map<String,Set<String>> candidatesMetiers = new HashMap<>();

    private final Map<String, Set<String>> liensSecteursMetiers = new HashMap<>();

    private Set<String> lasFilieres = new HashSet<>();
    private Map<Integer, String> specialites;
    private Set<String> bacsWithSpecialite;
    private Set<String> apprentissage;

    @PostConstruct
    private void load() {
        //load stats
        this.statistiques = new PsupStatistiques();

        //load filieresFront

                /*
                  this.statistiques = new PsupStatistiques();
        ServerData.statistiques.labels = Serialisation.fromJsonFile(
                "labelsDebug.json",
                Map.class
        );*/

        //load labels

        //load relatedInterests
        /* onisepData.interets().expansion().getOrDefault(key, Collections.emptyList())*/

        //load filieres similaires backpsupdata
        //inherit groups but not too much....

        //init formations
        //includes both fl and fr and gta codes
        //        /*        List<Formation> fors = Collections.emptyList();
        //        ///attention aux groupes
        //        if (flKey.startsWith(FILIERE_PREFIX)) {
        //            fors = SuggestionsData.getFormationsFromFil(flKey);
        //        } else if (flKey.startsWith((Constants.FORMATION_PREFIX))) {
        //            int gTaCod = Integer.parseInt(flKey.substring(2));
        //            Formation f = SuggestionsData.getFormation(gTaCod);
        //            if (f != null) {
        //                fors = List.of(f);
        //            }
        //        }
        //        */
        //        //should include groups

        //init caapcities
        formations.forEach((key, value) -> capacities.put(key, value.size()));

        //        throw new NotImplementedException("Not implemented yet");

        //init cities
                /*
        log.info("Double indexation des villes");
        CitiesBack cities = ServerData.cities;
        Distances.init(cities);*/

        //init dures from psup data

        //init candidatesMetiers
        /*            val result = candidatesMetiers.get(key);
            if(result != null) return result;
            Set<String> candidates = new HashSet<>(
                    getCandidatesMetiers(key) // onisepData.edgesMetiersFilieres().getSuccessors(key).keySet()
            );
            if (isLas(key)) {
                String key2 = getGenericFromLas(key); // lasCorrespondance.lasToGeneric().get(key)
        if (key2 != null) {
                    candidates.addAll(getMetiersfromfiliere(key2)); //onisepData.edgesMetiersFilieres().getSuccessors(key2).keySet()
                    candidates.addAll(getPassMetiers());//(key2) onisepData.edgesMetiersFilieres().getSuccessors(gFlCodToFrontId(PASS_FL_COD)).keySet()
                }
            }
            candidates.addAll(getMetiersfromGroup(key));
                //if (reverseFlGroups.containsKey(key)) {
                //candidates.addAll(reverseFlGroups.get(key).stream().flatMap(g -> onisepData.edgesMetiersFilieres().getSuccessors(g).keySet().stream()).toList());
                //}
            candidatesMetiers.put(key, candidates);
            return candidates;

         //liensSecteursMetiers

        //lasFilieres

        //specialites
        ServerData.specialites.specialites()

        //bacs with spcialites
        ServerData.specialites.specialitesParBac().keySet()

        //apprentissage
          backPsupData.formations().filieres.values().forEach(filiere -> {
            String key = FILIERE_PREFIX + filiere.gFlCod;
            if (filiere.apprentissage) {
                apprentissage.add(key);
                String origKey = FILIERE_PREFIX + filiere.gFlCodeFi;
                apprentissage.add(origKey);
                apprentissage.add(flGroups.getOrDefault(origKey, origKey));
            }
        });

        //descriptifs
                /* UpdateFrontData.DataContainer.loadDescriptifs(
                onisepData,
                backPsupData.getCorrespondances(),
                statistiques.getLASCorrespondance().lasToGeneric()
        );

         */

        //throw new NotImplementedException("Not implemented yet");
    }

    public String getLabel(String key, String key1) {
            return labels.getOrDefault(key, key1);
    }

    public String getLabel(String key) {
        return labels.get(key);
    }
    public String getDebugLabel(String key) {
        return getLabel(key, key);
    }

    public @NotNull Map<String, String> getLabels() {
        //return an immutable map of labels
        return Collections.unmodifiableMap(labels);
    }

    public List<String> getAllRelatedInterests(@NotNull Collection<String> keys) {
        return keys.stream().flatMap(key -> relatedInterests.getOrDefault(key, List.of()).stream()).toList();
    }

    public @NotNull Map<String, Integer> getFilieresSimilaires(String fl, int i) {
        return filieresSimilaires.get(fl,i);
    }

    public @NotNull List<Formation> getFormations(String key) {
        return formations.getOrDefault(key, Collections.emptyList());
    }

    public Integer getNbAdmis(String grp, String code) {
        return statistiques.getNbAdmis(grp, code);
    }

    public int getDuree(String fl) {
        return durees.getOrDefault(fl, 3);
    }

    public Pair<String, Statistique> getStatsBac(String fl, String bac) {
        return statistiques.getStatsBac(fl, bac);
    }

    public Double getStatsSpecialite(String fl, Integer iMtCod) {
        return statistiques.getStatsSpecialite(fl, iMtCod);
    }

    public Set<String> getAllCandidatesMetiers(String key) {
        return candidatesMetiers.getOrDefault(key, Collections.emptySet());
    }

    public Collection<String> getMetiersFromSecteur(String key) {
        return liensSecteursMetiers.getOrDefault(key, Collections.emptySet());
    }

    public int getNbFormations(String fl) {
        return formations.getOrDefault(fl, Collections.emptyList()).size();
    }

    public int getCapacity(String fl) {
        return capacities.getOrDefault(fl, 0);
    }

    public Collection<String> getFilieresFront() {
        return Collections.unmodifiableList(filieresFront);
    }

    public Map<Integer, String> getSpecialites() {
        return Collections.unmodifiableMap(specialites);
    }

    public Set<String> getApprentissage() {
       return Collections.unmodifiableSet(apprentissage);
    }

    public Set<String> getBacsWithSpecialite() {
        return Collections.unmodifiableSet(bacsWithSpecialite);
    }

    public static Map<String, Descriptifs.Descriptif> getDescriptifs() {
        return Collections.unmodifiableMap(descriptifs);
    }

    /**
     * utilisé pour l'envoi des stats aux élèves
     *
     * @param bac le bac
     * @param g le groupe
     * @return les détails
     */
    public @NotNull StatsContainers.SimpleStatGroupParBac getSimpleGroupStats(@Nullable String bac, String g) {
        if(bac == null) bac = PsupStatistiques.TOUS_BACS_CODE;
        return getDetailedGroupStats(bac, g, false).stat();
    }

    /**
     * utilisé pour l'envoi des stats aux profs
     * @param bac le bac
     * @param g le groupe
     * @return les stats
     */
    private StatsContainers.DetailFiliere getDetailedGroupStats(@NotNull String bac, String g) {
        return getDetailedGroupStats(bac, g, true);
    }
    private StatsContainers.DetailFiliere getDetailedGroupStats(@NotNull String bac, String g, boolean includeProfDetails) {
        StatsContainers.SimpleStatGroupParBac statFil
                = new StatsContainers.SimpleStatGroupParBac(
                statistiques.getGroupStats(
                        g,
                        bac,
                        !includeProfDetails
                )
        );

        if(includeProfDetails) {
            Map<String, StatsContainers.DetailFormation> statsFormations = new HashMap<>();
            try {
                List<Formation> fors = formations.getOrDefault(g, List.of());
                fors.forEach(f -> {
                    try {
                        String fr = FORMATION_PREFIX + f.gTaCod;
                        StatsContainers.SimpleStatGroupParBac statFor = new StatsContainers.SimpleStatGroupParBac(
                                statistiques.getGroupStats(
                                        fr,
                                        bac,
                                        !includeProfDetails)
                        );
                        statFor.stats().entrySet().removeIf(e -> e.getValue().statsScol().isEmpty());
                        statsFormations.put(fr, new StatsContainers.DetailFormation(
                                f.libelle,
                                fr,
                                statFor
                        ));
                    } catch (Exception ignored) {
                        //ignored
                    }
                });
            } catch (Exception ignored) {
                //ignore
            }
            return new StatsContainers.DetailFiliere(
                    g,
                    statFil,
                    statsFormations
            );
        } else {
            return new StatsContainers.DetailFiliere(
                    g,
                    statFil,
                    null
            );
        }
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

    public Map<String,String> getFormationToFilieres() {
        Map<String,String> result = new HashMap<>();
        formations.forEach((key, value) -> value.forEach(f -> result.put(FORMATION_PREFIX + f.gTaCod, key)));
        return result;
    }

    public Set<String> getLASFilieres() {
        return lasFilieres;
    }
}

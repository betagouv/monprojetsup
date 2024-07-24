package fr.gouv.monprojetsup.suggestions.data;

import fr.gouv.monprojetsup.suggestions.domain.entity.Edge;
import fr.gouv.monprojetsup.suggestions.data.model.formations.Formation;
import fr.gouv.monprojetsup.suggestions.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.data.model.stats.Statistique;
import fr.gouv.monprojetsup.suggestions.data.model.stats.StatsContainers;
import fr.gouv.monprojetsup.suggestions.data.update.psup.FormationsSimilaires;
import fr.gouv.monprojetsup.suggestions.domain.port.EdgesPort;
import fr.gouv.monprojetsup.suggestions.domain.port.LabelsPort;
import jakarta.annotation.PostConstruct;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.suggestions.data.Constants.FORMATION_PREFIX;
import static fr.gouv.monprojetsup.suggestions.data.Constants.PASS_FL_COD;

@Component
public class SuggestionsData {

    private final EdgesPort edgesPort;
    private final LabelsPort labelsPort;

    @Autowired
    public SuggestionsData(
            EdgesPort edgesPort,
            LabelsPort labelsPort) {
        this.edgesPort = edgesPort;
        this.labelsPort = labelsPort;
    }

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
        formations.forEach((key, value) -> capacities.put(key, value.stream().mapToInt(f -> f.capacite).sum()));

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

    public @NotNull String getLabel(@NotNull String key) {
        return labelsPort.retrieveLabel(key).orElse(key);
    }

    public @NotNull Map<String, String> getLabels() {
        //return an immutable map of labels
        return labelsPort.retrieveLabels();
    }

    public List<String> getAllRelatedInterests(@NotNull Collection<String> keys) {
        return keys.stream().flatMap(key -> edgesPort.getOutgoingEdges(key).stream()).toList();
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

    public int getNbFormations(String fl) {
        return formations.getOrDefault(fl, Collections.emptyList()).size();
    }

    public int getCapacity(String fl) {
        return capacities.getOrDefault(fl, 0);
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


    /**
     * utilisé pour l'envoi des stats aux élèves
     *
     * @param bac le bac
     * @param g le groupe
     * @return les détails
     */
    public @NotNull StatsContainers.SimpleStatGroupParBac getSimpleGroupStats(@Nullable String bac, String g) {
        if(bac == null) bac = PsupStatistiques.TOUS_BACS_CODE;
        return new StatsContainers.SimpleStatGroupParBac(
                statistiques.getGroupStats(
                        g,
                        bac,
                        true
                )
        );
    }




    public Map<String,String> getFormationToFilieres() {
        Map<String,String> result = new HashMap<>();
        formations.forEach((key, value) -> value.forEach(f -> result.put(FORMATION_PREFIX + f.gTaCod, key)));
        return result;
    }

    public Set<String> getLASFilieres() {
        return lasFilieres;
    }

    public Map<String, Set<String>> getMetiersVersFormations() {
        return Map.of();
    }

    public Set<String> getMetiersPass() {
        val metiersVersFormation = getMetiersVersFormations();
        String passKey =  Constants.gFlCodToFrontId(PASS_FL_COD);
        return metiersVersFormation.entrySet().stream()
                .filter(e -> e.getValue().contains( passKey))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Map<String,String> getLasToGeneric() {
        return Map.of();
    }

    public List<Edge> edgesInteretsMetiers() {
        return edgesPort.getEdgesInteretsMetiers();
    }

    public List<Edge> edgesFilieresThematiques() {
        return edgesPort.getEdgesFilieresThematiques();
    }

    public List<Edge> edgesThematiquesMetiers() {
        return edgesPort.getEdgesThematiquesMetiers();
    }

    public List<Edge> edgesSecteursMetiers() {
        return edgesPort.getEdgesSecteursMetiers();
    }

    public List<Edge> edgesMetiersAssocies() {
        return edgesPort.getEdgesMetiersAssocies();
    }

    public List<Edge> edgesFilieresGroupes() {
        return edgesPort.getEdgesFilieresGroupes();
    }

    public List<Edge> lasToGeneric() {
        return edgesPort.getEdgesLasToGeneric();
    }

    public List<Edge> lasToPass() {
        return edgesPort.getEdgesLasToPass();
    }
}

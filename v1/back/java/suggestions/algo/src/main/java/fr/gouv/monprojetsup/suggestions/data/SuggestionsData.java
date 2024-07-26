package fr.gouv.monprojetsup.suggestions.data;

import fr.gouv.monprojetsup.suggestions.data.model.cities.Coords;
import fr.gouv.monprojetsup.suggestions.data.model.stats.Middle50;
import fr.gouv.monprojetsup.suggestions.data.model.stats.StatFront;
import fr.gouv.monprojetsup.suggestions.data.model.stats.StatsContainers;
import fr.gouv.monprojetsup.suggestions.domain.entity.Edge;
import fr.gouv.monprojetsup.suggestions.domain.entity.Formation;
import fr.gouv.monprojetsup.suggestions.domain.entity.Matiere;
import fr.gouv.monprojetsup.suggestions.domain.entity.StatsFormation;
import fr.gouv.monprojetsup.suggestions.domain.port.EdgesPort;
import fr.gouv.monprojetsup.suggestions.domain.port.FormationsPort;
import fr.gouv.monprojetsup.suggestions.domain.port.LabelsPort;
import fr.gouv.monprojetsup.suggestions.domain.port.MatieresPort;
import jakarta.annotation.PostConstruct;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.suggestions.data.Constants.PASS_FL_COD;
import static fr.gouv.monprojetsup.suggestions.data.model.stats.PsupStatistiques.MOYENNE_GENERALE_CODE;
import static fr.gouv.monprojetsup.suggestions.data.model.stats.PsupStatistiques.TOUS_BACS_CODE;
import static fr.gouv.monprojetsup.suggestions.data.model.stats.StatFront.getStatistique;

@Component
public class SuggestionsData {

    private static final int DUREE_DEFAULT_VALUE = 3;
    private final EdgesPort edgesPort;
    private final LabelsPort labelsPort;
    private final FormationsPort formationsPort;

    private final MatieresPort matieresPort;

    @Autowired
    public SuggestionsData(
            EdgesPort edgesPort,
            LabelsPort labelsPort,
            FormationsPort formationsPort,
            MatieresPort matieresPort
            ) {
        this.edgesPort = edgesPort;
        this.labelsPort = labelsPort;
        this.formationsPort = formationsPort;
        this.matieresPort = matieresPort;
    }


    @PostConstruct
    private void load() {

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

    public @NotNull Map<String, Integer> getFormationsSimilaires(String formationId, int typeBac) {
        val f = formationsPort.retrieveFormation(formationId).orElse(null);
        if(f == null) return Map.of();
        return f.stats().formationsSimilaires().getOrDefault(typeBac, Map.of());
    }

    public @NotNull List<String> getVoeuxIds(String formationId) {
        val f = formationsPort.retrieveFormation(formationId).orElse(null);
        if(f == null) return List.of();
        return f.getVoeuxIds();
    }

    public List<Pair<String, Coords>> getVoeuxCoords(String id) {
        val f = formationsPort.retrieveFormation(id).orElse(null);
        if(f == null) return List.of();
        return f.getVoeuxCoords();
    }



    public @Nullable Integer getNbAdmis(String formationId, String bac) {
        return formationsPort.retrieveFormation(formationId)
                .map(f -> f.stats().nbAdmis().getOrDefault(bac, null))
                .orElse(null);
    }

    public int getDuree(String formationId) {
        val f = formationsPort.retrieveFormation(formationId);
        return f.map(Formation::duree).orElse(DUREE_DEFAULT_VALUE);
    }

    public @Nullable Pair<String, Middle50> getStatsBac(String formationId, String bac) {
        val f = formationsPort.retrieveFormation(formationId);
        if(f.isEmpty()) return null;
        val stats = f.get().stats().admissions();
        if(stats.containsKey(bac)) {
            return Pair.of(bac, stats.get(bac).middle50());
        }
        if(stats.containsKey(TOUS_BACS_CODE)) {
            return Pair.of(TOUS_BACS_CODE, stats.get(TOUS_BACS_CODE).middle50());
        }
        return null;
    }

    public @Nullable Double getStatsSpecialite(String formationId, Integer iMtCod) {
        return formationsPort.retrieveFormation(formationId)
                .map(f -> f.stats().specialites().getOrDefault(iMtCod, null))
                .orElse(null);
    }

    public Set<String> getAllCandidatesMetiers(String formationID) {
        val f = formationsPort.retrieveFormation(formationID);
        return f.map(formation -> formation.metiers().stream().collect(Collectors.toSet())).orElse(Collections.emptySet());
    }

    public int getNbVoeux(String formationId) {
        val f = formationsPort.retrieveFormation(formationId);
        return f.map(Formation::nbVoeux).orElse(0);
    }

    public int getCapacity(@NotNull String formationId) {
        val f = formationsPort.retrieveFormation(formationId);
        return f.map(Formation::capacite).orElse(0);
    }

    public Map<Integer, String> getSpecialites() {
        return matieresPort.retrieveSpecialites().stream().collect(Collectors.toMap(Matiere::id, Matiere::label));
    }

    public Set<String> getFormationIdsWithApprentissage() {
        return formationsPort.retrieveFormations().values().stream()
                .filter(Formation::apprentissage)
                .map(Formation::id)
                .collect(Collectors.toSet());
    }

    public Set<String> getBacsWithAtLeastTwoSpecialites() {
        Map<String, Integer> nbSpecialitesParBack = matieresPort.retrieveSpecialites().stream()
                .flatMap(m -> m.bacs().stream())
                .collect(Collectors.groupingBy(b -> b, Collectors.summingInt(b -> 1)));
        return nbSpecialitesParBack.entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toSet());
    }


    /**
     * utilisé pour l'envoi des stats aux élèves
     *
     * @param bac le bac
     * @param formationId le groupe
     * @return les détails
     */
    public @NotNull StatsContainers.SimpleStatGroupParBac getSimpleGroupStats(@Nullable String bac, String formationId) {
        if(bac == null) bac = TOUS_BACS_CODE;
        val f = formationsPort.retrieveFormation(formationId);
        if(f.isEmpty()) return new StatsContainers.SimpleStatGroupParBac(Map.of());
        val stats = f.get().stats();

        val statsTousBacs = getSimpleStatGroup(formationId, stats, TOUS_BACS_CODE);
        val statsBacs = getSimpleStatGroup(formationId, stats, bac);

        Map<String, StatsContainers.SimpleStatGroup> statsMap = new HashMap<>();
        if(statsTousBacs != null) statsMap.put(TOUS_BACS_CODE, statsTousBacs);
        if(statsBacs != null) statsMap.put(bac, statsBacs);
        return new StatsContainers.SimpleStatGroupParBac(statsMap);
    }

    private static @Nullable StatsContainers.SimpleStatGroup getSimpleStatGroup(String formationId, StatsFormation stats, String bac) {
        if(stats.admissions().containsKey(bac)) {
            Map<Integer, StatFront> statsScol = new HashMap<>();

            val nbAdmis = stats.nbAdmis().get(bac);
            if(nbAdmis == null) return null;

            val frequencesCumulees = stats.admissions().get(bac).frequencesCumulees();
            if(frequencesCumulees == null) return null;

            val stat = getStatistique(frequencesCumulees, true);

            Map<Integer, Integer> statsSpecs = stats.specialites().entrySet().stream()
                    .map(e -> Pair.of(e.getKey(), (int)(100 * e.getValue())))
                    .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

            return new StatsContainers.SimpleStatGroup(
                    formationId,
                    nbAdmis,
                    statsSpecs,
                    Map.of(MOYENNE_GENERALE_CODE, stat)
            );
        }
        return null;
    }


    public Map<String,String> getFormationToFilieres() {
        val formations = formationsPort.retrieveFormations();
        Map<String,String> result = new HashMap<>();
        formations.values().forEach(f -> f.voeux().forEach(v -> result.put(v.id(), f.id())));
        return result;
    }

    public Set<String> getLASFormations() {
        return formationsPort.retrieveFormations().entrySet()
                .stream()
                .filter(e -> e.getValue().las() != null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Map<String, Set<String>> getMetiersVersFormations() {
        return formationsPort.retrieveFormations().entrySet().stream()
                .flatMap(e -> e.getValue().metiers().stream().map(m -> Pair.of(m, e.getKey())))
                .collect(Collectors.groupingBy(Pair::getLeft, Collectors.mapping(Pair::getRight, Collectors.toSet())));
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
        return formationsPort.retrieveFormations().values().stream()
                .filter(f -> f.las() != null)
                .collect(Collectors.toMap(Formation::las, Formation::id));
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

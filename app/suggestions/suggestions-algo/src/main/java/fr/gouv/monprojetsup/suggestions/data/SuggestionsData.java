package fr.gouv.monprojetsup.suggestions.data;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.domain.model.*;
import fr.gouv.monprojetsup.data.domain.port.*;
import fr.gouv.monprojetsup.data.infrastructure.model.stats.Middle50;
import fr.gouv.monprojetsup.data.infrastructure.model.stats.StatsContainers;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.PASS_FL_COD;
import static fr.gouv.monprojetsup.data.infrastructure.model.stats.PsupStatistiques.MOYENNE_GENERALE_CODE;
import static fr.gouv.monprojetsup.data.infrastructure.model.stats.PsupStatistiques.TOUS_BACS_CODE;
import static fr.gouv.monprojetsup.data.infrastructure.model.stats.StatFront.getStatistique;

@Component
public class SuggestionsData {

    private static final int DUREE_DEFAULT_VALUE = 3;
    private final EdgesPort edgesPort;
    private final LabelsPort labelsPort;
    private final FormationsPort formationsPort;
    private final MatieresPort matieresPort;
    private final VillesPort villesPort;

    @Autowired
    public SuggestionsData(
            EdgesPort edgesPort,
            LabelsPort labelsPort,
            FormationsPort formationsPort,
            MatieresPort matieresPort,
            VillesPort villesPort
            ) {
        this.edgesPort = edgesPort;
        this.labelsPort = labelsPort;
        this.formationsPort = formationsPort;
        this.matieresPort = matieresPort;
        this.villesPort = villesPort;
    }




    public @NotNull String getLabel(@NotNull String key) {
        return labelsPort.retrieveLabel(key).orElse(key);
    }

    public @NotNull String getDebugLabel(@NotNull String key) {
        return labelsPort.retrieveDebugLabel(key).orElse(key);
    }

    public @NotNull Map<String, String> getLabels() {
        //return an immutable map of labels
        return labelsPort.retrieveLabels();
    }

    public @NotNull Map<String, String> getDebugLabels() {
        //return an immutable map of labels
        return labelsPort.retrieveDebugLabels();
    }

    public List<String> getAllRelatedInterests(@NotNull Collection<String> keys) {
        return keys.stream().flatMap(
                key -> edgesPort.getOutgoingEdges(key, EdgesPort.TYPE_EDGE_INTEREST_TO_INTEREST).stream()
        ).toList();
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

    public List<Pair<String, LatLng>> getVoeuxCoords(String id) {
        val f = formationsPort.retrieveFormation(id).orElse(null);
        if(f == null) return List.of();
        return f.getVoeuxCoords();
    }



    public @Nullable Integer getNbAdmis(String formationId, String bac) {
        return formationsPort.retrieveFormation(formationId)
                .map(f -> f.stats().nbAdmisParBac().get(bac))
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
                .map(f -> 1.0 * f.stats().pctAdmisParSpecialite().get(iMtCod))
                .orElse(null);
    }

    public Set<String> getAllCandidatesMetiers(String formationID) {
        val f = formationsPort.retrieveFormation(formationID);
        return f.map(formation -> new HashSet<>(formation.metiers())).orElse(new HashSet<>());
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

            val nbAdmis = stats.nbAdmisParBac().get(bac);
            if(nbAdmis == null) return null;

            val frequencesCumulees = stats.admissions().get(bac).frequencesCumulees();
            if(frequencesCumulees == null) return null;

            val stat = getStatistique(frequencesCumulees, true);

            Map<Integer, Integer> statsSpecs = stats.nbAdmisParSpecialite().entrySet().stream()
                    .map(e -> Pair.of(e.getKey(), 100 * e.getValue()))
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

    public List<LatLng> getCityCoords(String cityName) { return villesPort.getCoords(cityName); }
}

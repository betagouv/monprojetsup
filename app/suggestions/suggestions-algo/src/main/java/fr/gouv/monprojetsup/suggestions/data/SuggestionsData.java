package fr.gouv.monprojetsup.suggestions.data;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.domain.model.*;
import fr.gouv.monprojetsup.data.domain.model.stats.Middle50;
import fr.gouv.monprojetsup.data.domain.model.stats.StatsContainers;
import fr.gouv.monprojetsup.suggestions.port.*;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.PASS_FL_COD;
import static fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques.MOYENNE_GENERALE_CODE;
import static fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques.TOUS_BACS_CODE_MPS;
import static fr.gouv.monprojetsup.data.domain.model.stats.StatFront.getStatistique;

@Component
public class SuggestionsData {

    private static final int DUREE_DEFAULT_VALUE = 3;
    private final EdgesPort edgesPort;
    private final LabelsPort labelsPort;
    private final FormationsPort formationsPort;
    private final FormationsMetiersPort formationsMetierPort;
    private final MatieresPort matieresPort;
    private final VillesPort villesPort;

    @Autowired
    public SuggestionsData(
            EdgesPort edgesPort,
            LabelsPort labelsPort,
            FormationsPort formationsPort,
            MatieresPort matieresPort,
            VillesPort villesPort,
            FormationsMetiersPort formationsMetierPort
            ) {
        this.edgesPort = edgesPort;
        this.labelsPort = labelsPort;
        this.formationsPort = formationsPort;
        this.matieresPort = matieresPort;
        this.villesPort = villesPort;
        this.formationsMetierPort = formationsMetierPort;
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
        if(stats.containsKey(TOUS_BACS_CODE_MPS)) {
            return Pair.of(TOUS_BACS_CODE_MPS, stats.get(TOUS_BACS_CODE_MPS).middle50());
        }
        return null;
    }

    public @Nullable Double getStatsSpecialite(String formationId, Integer iMtCod) {
        return formationsPort.retrieveFormation(formationId)
                .map(f -> 1.0 * f.stats().pctAdmisParSpecialite().getOrDefault(iMtCod, 0))
                .orElse(null);
    }

    public Collection<String> getAllCandidatesMetiers(String formationID) {
        return formationsMetierPort.getMetiersOfFormation(formationID);
    }

    public int getNbVoeux(String formationId) {
        val f = formationsPort.retrieveFormation(formationId);
        return f.map(Formation::nbVoeux).orElse(0);
    }

    public int getCapacity(@NotNull String formationId) {
        val f = formationsPort.retrieveFormation(formationId);
        return f.map(Formation::capacite).orElse(0);
    }

    public @NotNull List<@NotNull Matiere> getSpecialites() {
        return matieresPort.retrieveSpecialites();
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
        if(bac == null) bac = TOUS_BACS_CODE_MPS;
        val f = formationsPort.retrieveFormation(formationId);
        if(f.isEmpty()) return new StatsContainers.SimpleStatGroupParBac(Map.of());
        val stats = f.get().stats();

        val statsTousBacs = getSimpleStatGroup(formationId, stats, TOUS_BACS_CODE_MPS);
        val statsBacs = getSimpleStatGroup(formationId, stats, bac);

        Map<String, StatsContainers.SimpleStatGroup> statsMap = new HashMap<>();
        if(statsTousBacs != null) statsMap.put(TOUS_BACS_CODE_MPS, statsTousBacs);
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


    public Set<String> getLASFormations() {
        return formationsPort.retrieveFormations().entrySet()
                .stream()
                .filter(e -> e.getValue().las() != null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Map<String, Set<String>> getMetiersVersFormations() {
        return formationsMetierPort.findAll().stream()
                .collect(Collectors.groupingBy( e -> e.idMetier))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().stream().map(t -> t.idFormation).collect(Collectors.toSet())
                ));
    }

    public Set<String> getMetiersPass() {
        val metiersVersFormation = getMetiersVersFormations();
        String passKey =  Constants.gFlCodToFrontId(PASS_FL_COD);
        return metiersVersFormation.entrySet().stream()
                .filter(e -> e.getValue().contains( passKey))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
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

    public List<Edge> edgesItemssGroupeItems() {
        return edgesPort.getEdgesItemssGroupeItems();
    }
}

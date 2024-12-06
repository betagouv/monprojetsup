package fr.gouv.monprojetsup.suggestions.data;

import fr.gouv.monprojetsup.data.model.Edge;
import fr.gouv.monprojetsup.data.model.Formation;
import fr.gouv.monprojetsup.data.model.LatLng;
import fr.gouv.monprojetsup.data.model.Ville;
import fr.gouv.monprojetsup.data.model.stats.Middle50;
import fr.gouv.monprojetsup.suggestions.Constants;
import fr.gouv.monprojetsup.suggestions.algo.Config;
import fr.gouv.monprojetsup.suggestions.port.ConfigPort;
import fr.gouv.monprojetsup.suggestions.port.EdgesPort;
import fr.gouv.monprojetsup.suggestions.port.FormationsMetiersPort;
import fr.gouv.monprojetsup.suggestions.port.FormationsPort;
import fr.gouv.monprojetsup.suggestions.port.FormationsVoeuxPort;
import fr.gouv.monprojetsup.suggestions.port.LabelsPort;
import fr.gouv.monprojetsup.suggestions.port.VillesPort;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.TOUS_BACS_CODE_MPS;
import static fr.gouv.monprojetsup.suggestions.Constants.PASS_FL_COD;
import static fr.gouv.monprojetsup.suggestions.tools.Stats.p50;
import static fr.gouv.monprojetsup.suggestions.tools.Stats.p75;

@Component
@Service
public class SuggestionsData {

    private static final int DUREE_DEFAULT_VALUE = 3;
    private final EdgesPort edgesPort;
    private final LabelsPort labelsPort;
    private final FormationsPort formationsPort;
    private final FormationsMetiersPort formationsMetierPort;
    private final FormationsVoeuxPort formationsVoeuxPort;
    private final VillesPort villesPort;
    private final ConfigPort configPort;

    @Autowired
    public SuggestionsData(
            EdgesPort edgesPort,
            LabelsPort labelsPort,
            FormationsPort formationsPort,
            VillesPort villesPort,
            FormationsMetiersPort formationsMetierPort,
            ConfigPort configPort,
            FormationsVoeuxPort formationsVoeuxPort
            ) {
        this.edgesPort = edgesPort;
        this.labelsPort = labelsPort;
        this.formationsPort = formationsPort;
        this.villesPort = villesPort;
        this.formationsMetierPort = formationsMetierPort;
        this.formationsVoeuxPort = formationsVoeuxPort;
        this.configPort = configPort;
        val activeConfig = configPort.retrieveActiveConfig();
        if(activeConfig == null) {
            this.config = new Config();
            configPort.setActiveConfig(this.config);
        } else {
            this.config = activeConfig;
        }
    }

    @Getter
    private @NotNull Config config;

    @Scheduled(fixedDelay = 1000) // Every second
    private void refreshConfig() {
        // Fetch the latest config from a database, external service, or file
        val activeConfig = configPort.retrieveActiveConfig();
        if(activeConfig != null) {
            this.config = activeConfig;
        }
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

    public @NotNull Map<String, Long> getFormationsSimilaires(String formationId, int typeBac) {
        val f = formationsPort.retrieveFormation(formationId).orElse(null);
        if(f == null) return Map.of();
        return f.stats().formationsSimilaires().getOrDefault(typeBac, Map.of());
    }

    public @NotNull List<@NotNull Pair<@NotNull String, @NotNull LatLng>> getVoeuxCoords(String id) {
        return formationsPort.retrieveFormation(id).map(Formation::getVoeuxCoords).orElse(List.of());
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
            return Pair.of(bac, stats.get(bac));
        } else {
            val d = stats.get(TOUS_BACS_CODE_MPS);
            if(d == null) return null;
            return Pair.of(TOUS_BACS_CODE_MPS, d);
        }
    }


    public @Nullable Double getStatsSpecialite(String formationId, String iMtCod) {
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

    public Set<String> getFormationIdsWithApprentissage() {
        return formationsPort.retrieveFormations().values().stream()
                .filter(Formation::apprentissage)
                .map(Formation::id)
                .collect(Collectors.toSet());
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
                        Map.Entry::getKey,
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

    public List<Edge> edgesFormationsPsupDomaines() {
        return edgesPort.getEdgesFormationsPsupThematiques();
    }
    //getEdgesMetiersFormationsPsup
    public List<Edge> edgesMetiersFormationsPsup() {
        return edgesPort.getEdgesMetiersFormationsPsup();
    }

    public List<Edge> edgesDomainesMetiers() {
        return edgesPort.getEdgesDomainesMetiers();
    }

    public List<Edge> edgesMetiersAssocies() {
        return edgesPort.getEdgesMetiersAssocies();
    }

    public List<Edge> edgesFormationPsupFormationMps() {
        return edgesPort.getEdgesFormationPsupFormationMps();
    }

    public List<Edge> lasToGeneric() {
        return edgesPort.getEdgesLasToGeneric();
    }

    public List<Edge> lasToPass() {
        return edgesPort.getEdgesLasToPass();
    }

    public @Nullable Ville getVille(String id) {
        return villesPort.getVille(id);
    }

    public List<Edge> edgesAtometoElement() {
        return edgesPort.getEdgesAtometoElement();
    }

    public List<String> getFormationIds() {
        return formationsPort.retrieveFormations().values().stream().map(Formation::id).toList();
    }

    public int p75Capacity() {
        return p75(
                formationsPort.retrieveFormations().values().stream().map(Formation::capacite).toList()
        );
    }

    public int p50NbFormations() {
        return p50(
                formationsPort.retrieveFormations().values().stream().map(Formation::nbVoeux).toList()
        );
    }

    public Map<String, String> getTypesFormations() {
        return formationsPort.retrieveFormations().values().stream().collect(Collectors.toMap(
                Formation::id,
                Formation::typeFormation
        ));
    }

    public List<String> getFormationsOfVoeu(String idVoeu) {
        return formationsVoeuxPort.getFormationsOfVoeu(idVoeu);
    }
}

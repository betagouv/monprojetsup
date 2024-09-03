package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.domain.model.Edge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

import static fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.*;

public abstract class EdgesPort {


    @NotNull @Unmodifiable
    public abstract List<Edge> retrieveEdgesOfType(int type);

    @NotNull @Unmodifiable
    public abstract List<@NotNull String> getOutgoingEdges(@NotNull String src, int type);

    public List<Edge> getEdgesInteretsMetiers() {
        return retrieveEdgesOfType(TYPE_EDGE_INTERET_METIER);
    }

    public List<Edge> getEdgesFilieresThematiques() {
        return retrieveEdgesOfType(TYPE_EDGE_FILIERES_THEMATIQUES);
    }

    public List<Edge> getEdgesThematiquesMetiers() {
        return retrieveEdgesOfType(TYPE_EDGE_THEMATIQUES_METIERS);
    }

    public List<Edge> getEdgesSecteursMetiers() {
        return retrieveEdgesOfType(TYPE_EDGE_SECTEURS_METIERS);
    }

    public List<Edge> getEdgesMetiersAssocies() { return retrieveEdgesOfType(TYPE_EDGE_METIERS_ASSOCIES); }

    public List<Edge> getEdgesFilieresGroupes() {
        return retrieveEdgesOfType(TYPE_EDGE_FILIERES_GROUPES);
    }

    public List<Edge> getEdgesLasToGeneric() {
        return retrieveEdgesOfType(TYPE_EDGE_LAS_TO_GENERIC);
    }

    public List<Edge> getEdgesLasToPass() {
        return retrieveEdgesOfType(TYPE_EDGE_LAS_TO_PASS);
    }

    @Cacheable(value = {"myCache"})
    public List<String> getFilieresFront() {
        return retrieveEdgesOfType(TYPE_EDGE_FILIERES_GROUPES).stream().map(Edge::dst).distinct().toList();
    }

}

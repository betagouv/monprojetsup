package fr.gouv.monprojetsup.suggestions.domain.port;

import fr.gouv.monprojetsup.suggestions.domain.entity.Edge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public abstract class EdgesPort {

    private static final int TYPE_EDGE_INTERET_METIER = 0;
    private static final int TYPE_EDGE_FILIERES_THEMATIQUES = 1;
    private static final int TYPE_EDGE_THEMATIQUES_METIERS = 2;
    private static final int TYPE_EDGE_SECTEURS_METIERS = 3;
    private static final int TYPE_EDGE_METIERS_ASSOCIES = 4;
    private static final int TYPE_EDGE_FILIERES_GROUPES = 5;
    private static final int TYPE_EDGE_LAS_TO_GENERIC = 6;
    private static final int TYPE_EDGE_LAS_TO_PASS = 7;
    private static final int TYPE_EDGE_INTEREST_TO_INTEREST = 8;

    abstract public @NotNull @Unmodifiable List<Edge> retrieveEdgesOfType(int type);

    abstract public @NotNull @Unmodifiable List<String> getOutgoingEdges(String src);

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

    public List<Edge> getEdgesMetiersAssocies() {
        return retrieveEdgesOfType(TYPE_EDGE_METIERS_ASSOCIES);

    }

    public List<Edge> getEdgesFilieresGroupes() {
        return retrieveEdgesOfType(TYPE_EDGE_FILIERES_GROUPES);
    }

    public List<Edge> getEdgesLasToGeneric() {
        return retrieveEdgesOfType(TYPE_EDGE_LAS_TO_GENERIC);
    }

    public List<Edge> getEdgesLasToPass() {
        return retrieveEdgesOfType(TYPE_EDGE_LAS_TO_PASS);
    }

    public List<String> getFilieresFront() {
        return getEdgesFilieresGroupes().stream().map(Edge::dst).toList();
    }
}

package fr.gouv.monprojetsup.data.domain.port;

import fr.gouv.monprojetsup.data.domain.model.Edge;
import fr.gouv.monprojetsup.data.domain.model.graph.Edges;
import kotlin.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

public abstract class EdgesPort {

    public static final int TYPE_EDGE_INTERET_METIER = 0;
    public static final int TYPE_EDGE_FILIERES_THEMATIQUES = 1;
    public static final int TYPE_EDGE_THEMATIQUES_METIERS = 2;
    public static final int TYPE_EDGE_SECTEURS_METIERS = 3;
    public static final int TYPE_EDGE_METIERS_ASSOCIES = 4;
    public static final int TYPE_EDGE_FILIERES_GROUPES = 5;
    public static final int TYPE_EDGE_LAS_TO_GENERIC = 6;
    public static final int TYPE_EDGE_LAS_TO_PASS = 7;
    public static final int TYPE_EDGE_INTEREST_TO_INTEREST = 8;

    @NotNull @Unmodifiable
    public abstract List<Edge> retrieveEdgesOfType(int type);

    @NotNull @Unmodifiable
    public abstract List<@NotNull String> getOutgoingEdges(@NotNull String src);

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

    public List<String> getFilieresFront() {
        return getEdgesFilieresGroupes().stream().map(Edge::dst).toList();
    }

    public abstract void saveAll(@NotNull Edges edges, int type);

    public abstract void saveAll(@NotNull Map<String, String> edges, int type);

    public abstract void saveAll(@NotNull List<Triple<String, String, Integer>> edges);
}

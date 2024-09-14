package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.model.Edge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.*;

public abstract class EdgesPort {


    @NotNull @Unmodifiable
    public abstract List<Edge> retrieveEdgesOfType(int type);

    public List<Edge> getEdgesInteretsMetiers() {
        return retrieveEdgesOfType(TYPE_EDGE_INTERET_METIER);
    }

    public List<Edge> getEdgesFormationsPsupThematiques() {
        return retrieveEdgesOfType(TYPE_EDGE_FORMATIONS_PSUP_DOMAINES);
    }
    public List<Edge> getEdgesMetiersFormationsPsup() {
        return retrieveEdgesOfType(TYPE_EDGE_METIERS_FORMATIONS_PSUP);
    }
    public List<Edge> getEdgesDomainesMetiers() {
        return retrieveEdgesOfType(TYPE_EDGE_DOMAINES_METIERS);
    }

    public List<Edge> getEdgesSecteursMetiers() {return retrieveEdgesOfType(TYPE_EDGE_SECTEURS_METIERS); }

    public List<Edge> getEdgesMetiersAssocies() { return retrieveEdgesOfType(TYPE_EDGE_METIERS_ASSOCIES); }

    public List<Edge> getEdgesFormationPsupFormationMps() {
        return retrieveEdgesOfType(TYPE_EDGE_FORMATION_PSUP_TO_FORMATION_MPS);
    }

    public List<Edge> getEdgesLasToGeneric() {
        return retrieveEdgesOfType(TYPE_EDGE_LAS_TO_GENERIC);
    }

    public List<Edge> getEdgesLasToPass() {
        return retrieveEdgesOfType(TYPE_EDGE_LAS_TO_PASS);
    }

    public List<Edge> getEdgesItemssGroupeItems() {
        return retrieveEdgesOfType(TYPE_EDGE_ATOME_ELEMENT);
    }
}

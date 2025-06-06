package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.model.PanierVoeux;

import java.util.List;

public interface CandidatsPort {

    //will be used to export data for Guillaume
    @SuppressWarnings("unused")
    List<PanierVoeux> findAll();

}

package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.model.Candidat;

import java.util.List;

public interface CandidatsPort {

    //will be used to export data for Guillaume
    @SuppressWarnings("unused")
    List<Candidat> findAll();

}

package fr.gouv.monprojetsup.data.model.stats;

import java.util.ArrayList;
import java.util.List;

public record AdmisMatiereBacAnneeStats(List<AdmisMatiereBacAnnee> stats) {

    public AdmisMatiereBacAnneeStats() {
        this(new ArrayList<>());
    }
}

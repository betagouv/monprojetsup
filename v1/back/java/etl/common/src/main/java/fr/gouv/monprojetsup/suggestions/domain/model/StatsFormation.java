package fr.gouv.monprojetsup.suggestions.domain.model;

import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.Statistique;

import java.util.Map;

public record StatsFormation(

        //typeBac --> mid50
        Map<String, Statistique> admissions,

        //typeBac --> nbAdmis et pct Admis
        Map<String, Integer> nbAdmisParBac,
        Map<String, Integer> pctAdmisParBac,

        //specialitesId --> nbAdmis et pct Admis
        Map<Integer, Integer> nbAdmisParSpecialite,
        Map<Integer, Integer> pctAdmisParSpecialite,

        //coarseTypeBac --> formationId --> score
        Map<Integer, Map<String, Integer>> formationsSimilaires

) {

}

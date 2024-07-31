package fr.gouv.monprojetsup.suggestions.domain.model;

import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.Statistique;

import java.util.Map;

public record StatsFormation(

        //typeBac --> mid50
        Map<String, Statistique> admissions,

        //typeBac --> nbAdmisParBac
        Map<String, Integer> nbAdmisParBac,

        //specialitesId --> nbAdmisParBac
        Map<Integer, Integer> nbAdmisParSpecialite,

        //coarseTypeBac --> formationId --> score
        Map<Integer, Map<String, Integer>> formationsSimilaires

) {

}

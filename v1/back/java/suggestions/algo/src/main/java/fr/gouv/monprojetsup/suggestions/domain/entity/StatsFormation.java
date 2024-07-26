package fr.gouv.monprojetsup.suggestions.domain.entity;

import fr.gouv.monprojetsup.suggestions.data.model.stats.Statistique;

import java.util.Map;

public record StatsFormation(

        //specialitesId --> % nbAdmis
        Map<Integer, Double> specialites,

        //typeBac --> mid50
        Map<String, Statistique> admissions,

        //typeBac --> nbAdmis
        Map<String, Integer> nbAdmis,

        //coarseTypeBac --> formationId --> score
        Map<Integer, Map<String, Integer>> formationsSimilaires

) {

}

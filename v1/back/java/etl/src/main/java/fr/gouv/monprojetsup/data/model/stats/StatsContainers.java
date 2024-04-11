package fr.gouv.monprojetsup.data.model.stats;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class StatsContainers {


    public  record DetailFiliere(
            String fil,
            SimpleStatGroupParBac stat,
            Map<String, DetailFormation> statsFormations
    ) {
    }

    public record DetailFormation(
            String nom,
            String code,
            SimpleStatGroupParBac stat) {
    }

    public record StatGroup(
            String grp,
            //nombre de lycéens admis l'année précédente
            Integer nbAdmis,
            Integer tauxAcces,
            Map<Integer, Integer> statsSpecs,
            Map<Integer, Statistique> statsScol
    ) {

    }

    public record SimpleStatGroupParBac(
            Map<String, SimpleStatGroup> stats
    ) {
    }

    public record SimpleStatGroup(
            String grp,
            //nombre de lycéens admis l'année précédente
            Integer nbAdmis,
            Integer tauxAcces,
            Map<Integer, Integer> statsSpecs,
            Map<Integer, StatFront> statsScol
    ) {

    }


}

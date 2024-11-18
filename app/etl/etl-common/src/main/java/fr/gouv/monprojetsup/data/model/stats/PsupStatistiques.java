package fr.gouv.monprojetsup.data.model.stats;

import fr.gouv.monprojetsup.data.tools.Serialisation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;


public class PsupStatistiques implements Serializable {

    public static final int MIN_POPULATION_SIZE_FOR_STATS = 10;
    public static final int SIM_FIL_MAX_WEIGHT = 100000;

    public static final String TOUS_GROUPES_CODE = "";

    public static final String TOUS_BACS_CODE_MPS = "NC";

    public static final int PRECISION_PERCENTILES = 40;
    public static final String MATIERE_MOYENNE_GENERALE_CODE = "moygen";
    public static final String MATIERE_MOYENNE_BAC_CODE = "moybac";
    public static final String MATIERE_ADMIS_CODE = "admis";


    /* année de référence */
    private @Nullable Integer annee;

    /* fréquences cumulées et middle 50 par voeu (ta***) par bac par matière */
    private final StatistiquesAdmisParGroupe statsAdmis = new StatistiquesAdmisParGroupe();

    public void setStatistiquesAdmisFromPercentileCounters(
            Map<String, Map<String, Map<String, int[]>>> compteurs) {
        statsAdmis.clear();
        compteurs.forEach((gr, stringMapMap) ->
                statsAdmis.parGroupe()
                        .computeIfAbsent(gr, z -> new StatistiquesAdmisParBac())
                        .set(stringMapMap)
        );
    }

    public void minimize() {
        statsAdmis.minimize();
    }

    public int getAnnee() {
        if(annee == null) throw new IllegalStateException("annee non initialisée");
        return this.annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }


    //trading cpu for memory

    public Set<String> getBacsWithAtLeastNdAdmis(int minNbAdmis) {
        Map<String, @NotNull Integer> admisParBac =
                statsAdmis.parGroupe().get(TOUS_GROUPES_CODE).getAdmisParBacs();
        try {
            Serialisation.toJsonFile("statsBacs.json", admisParBac, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return admisParBac
                .entrySet()
                .stream().filter(e -> e.getValue() >= minNbAdmis)
                .map(Entry::getKey)
                .collect(Collectors.toSet());
    }

    public void restrictToBacs(Set<String> bacsActifs) {
        statsAdmis.parGroupe().values().forEach(s -> s.parBac().keySet().retainAll(bacsActifs));
        statsAdmis.parGroupe().values().removeIf(s -> s.parBac().isEmpty());
    }

    public StatistiquesAdmisParGroupe createGroupAdmisStatistique(Map<String, Collection<String>> groups, Set<String> bacsKeys) {
        StatistiquesAdmisParGroupe result = statsAdmis.createGroupAdmisStatistique(groups, bacsKeys);
        result.rebuilMiddle50();
        result.removeEmptyGroups();
        return result;
    }

}

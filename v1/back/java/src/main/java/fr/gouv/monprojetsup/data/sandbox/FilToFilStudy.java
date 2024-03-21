package fr.gouv.monprojetsup.data.sandbox;

import fr.gouv.monprojetsup.data.model.formations.Formations;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.Edges;
import fr.gouv.monprojetsup.tools.Serialisation;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.FORMATION_PREFIX;
import static fr.gouv.monprojetsup.data.Constants.gFlCodToFrontId;

public class FilToFilStudy {

    public static void main(String[] args) throws Exception {
        ServerData.load();

        Edges filToFil = new Edges();
        filToFil.setCleanupKeys(false);

        Map<String, Integer> stats = ServerData.backPsupData.formations().formations.values().stream()
                .collect(Collectors.groupingBy(
                        formation -> formation.gFlCod
                        )
                )
                .entrySet().stream()
                .filter(e -> ServerData.backPsupData.filActives().contains(e.getKey()))
                .collect(Collectors.toMap(
                        e -> gFlCodToFrontId(e.getKey()),
                        e -> e.getValue().stream().mapToInt(f -> f.capacite).sum()
                ));

        List<ImmutablePair<String, Integer>> statsName =
                stats.entrySet().stream()
                        .map(e -> ImmutablePair.of(
                                ServerData.getLabel(e.getKey(), e.getKey()),
                                e.getValue()
                        ))
                        .sorted(Comparator.comparingInt(ImmutablePair::getRight))
                                .toList();

        Serialisation.toJsonFile("filCapas.json", statsName, true);

        Formations fors = ServerData.backPsupData.formations();

        List<Map<String, String>> filDoublesCapas =
                fors.formations.values().stream()
                        .filter(p -> p.libelle.toLowerCase().contains("double"))
                        .map(e -> Map.of(
                                "lib", e.libelle,
                                        "gta", FORMATION_PREFIX + e.gTaCod,
                                        "fl", ServerData.getLabel(gFlCodToFrontId(e.gFlCod),gFlCodToFrontId(e.gFlCod))
                                ))
                        .toList();

        Serialisation.toJsonFile("formationsDoubles.json", filDoublesCapas, true);

        List<Map<String, Object>> statsTypesformations
                = ServerData.backPsupData.formations().formations.values().stream()
                .collect(Collectors.groupingBy(
                                formation -> fors.typesMacros.get(fors.filieres.get(formation.gFlCod).gFrCod)
                        )
                )
                .entrySet().stream()
                .map(e -> Map.of(
                        "type", e.getKey(),
                        "capacite", e.getValue().stream().mapToInt(f -> f.capacite).sum(),
                        "liste", e.getValue().stream().map(f -> fors.filieres.get(f.gFlCod)).distinct().toList()
                        )

                )
                .sorted(Comparator.comparingInt(m -> (Integer) m.get("capacite")))
                .toList();


        Serialisation.toJsonFile("statsTypesFors.json", statsTypesformations, true);

    }
}

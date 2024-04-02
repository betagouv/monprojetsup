package fr.gouv.monprojetsup.data.analysis.eds;

import fr.gouv.monprojetsup.data.model.eds.Attendus;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public record EDSAggAnalysis(
        Map<String, EDSAnalysis> analyses
) {
    public EDSAggAnalysis() {
        this(new TreeMap<>());
    }

    public Map<String, Attendus> getFrontData() {
        return analyses.entrySet()
                .stream()
                .map(e -> Pair.of(e.getKey(),e.getValue().frontReco()))
                .filter(p -> p.getRight() != null)
                .collect(Collectors.toMap(
                        p -> p.getLeft(),
                        p -> p.getRight()
                ));
    }
}

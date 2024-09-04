package fr.gouv.monprojetsup.data.domain.model.rome;

import java.util.Map;
import java.util.stream.Collectors;

public record RomeData(
        InteretsRome centresInterest,
        Themes themes
) {

    public Map<String,String> getLabels() {
        return centresInterest.arbo_centre_interet().stream()
                .collect(Collectors.toMap(
                        InteretsRome.Item::getKey,
                        InteretsRome.Item::libelle_centre_interet
                        )
                );
    }
}

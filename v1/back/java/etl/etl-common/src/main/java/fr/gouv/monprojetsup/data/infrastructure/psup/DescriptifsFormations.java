package fr.gouv.monprojetsup.data.infrastructure.psup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record DescriptifsFormations(
        List<DescriptifVoeu> descriptions
) {
    public DescriptifsFormations() {
        this(new ArrayList<>());
    }

    /* returns the list of descriptifs, indexed by gtaCod */
    public Map<Integer, DescriptifVoeu> indexed() {
        return descriptions.stream().collect(
                java.util.stream.Collectors.toMap(DescriptifVoeu::code, d -> d));
    }
}

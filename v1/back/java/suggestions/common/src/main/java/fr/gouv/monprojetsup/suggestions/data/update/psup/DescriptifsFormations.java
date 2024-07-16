package fr.gouv.monprojetsup.suggestions.data.update.psup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record DescriptifsFormations(
        List<DescriptifFormation> descriptions
) {
    public DescriptifsFormations() {
        this(new ArrayList<>());
    }

    /* returns the mlist of descriptifs, indexed by gtaCod */
    public Map<Integer,DescriptifFormation> indexed() {
        return descriptions.stream().collect(
                java.util.stream.Collectors.toMap(DescriptifFormation::code, d -> d));
    }
}

package fr.gouv.monprojetsup.data.update.psup;

import java.util.ArrayList;
import java.util.List;

public record DescriptifsFormations(
        List<DescriptifFormation> descriptions
) {
    public DescriptifsFormations() {
        this(new ArrayList<>());
    }

}

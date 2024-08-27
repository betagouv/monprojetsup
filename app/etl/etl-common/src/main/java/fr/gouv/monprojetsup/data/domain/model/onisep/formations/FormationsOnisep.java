package fr.gouv.monprojetsup.data.domain.model.onisep.formations;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public record FormationsOnisep(
        List<FormationOnisep>  formations
) {

    public FormationOnisep get(String key) {
        return formations.stream()
                .filter(f -> f.identifiant().equals(key))
                .findAny()
                .orElse(null);
    }

    public Collection<String> getFormationsDuSup() {
        if(formations == null) return Collections.emptySet();
        return formations.stream()
                .filter(FormationOnisep::isFormationDuSup)
                .map(FormationOnisep::identifiant)
                .collect(Collectors.toSet());
    }

}

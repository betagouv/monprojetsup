package fr.gouv.monprojetsup.data.onisep.formations;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public record Formations(
        List<fr.gouv.monprojetsup.data.onisep.formations.Formation>  formations
) {

    public fr.gouv.monprojetsup.data.onisep.formations.Formation get(String key) {
        return formations.stream()
                .filter(f -> f.identifiant().equals(key))
                .findAny()
                .orElse(null);
    }

    public Collection<String> getFormationsDuSup() {
        if(formations == null) return Collections.emptySet();
        return formations.stream()
                .filter(Formation::isFormationDuSup)
                .map(Formation::identifiant)
                .collect(Collectors.toSet());
    }

}

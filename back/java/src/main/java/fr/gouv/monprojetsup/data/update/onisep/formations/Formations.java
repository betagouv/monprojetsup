package fr.gouv.monprojetsup.data.update.onisep.formations;

import java.util.List;



public record Formations(
        List<Formation>  formations
) {

    public Formation get(String key) {
        return formations.stream()
                .filter(f -> f.identifiant().equals(key))
                .findAny()
                .orElse(null);
    }

}

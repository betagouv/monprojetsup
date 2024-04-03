package fr.gouv.monprojetsup.data.update.onisep.formations;

import java.util.List;


public record FormationsAvecMetiers(
        FormationsList formations
) {
    public record FormationsList(
            List<FormationAvecMetier> formation
    ) {

    }

    public FormationAvecMetier get(String key) {
        return formations.formation().stream()
                .filter(f -> f.identifiant().equals(key))
                .findAny()
                .orElse(null);
    }

}

package fr.gouv.monprojetsup.data.update.onisep.formations;

<<<<<<< HEAD
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
=======
import java.util.List;

>>>>>>> origin/prod


public record Formations(
        List<Formation>  formations
) {

    public Formation get(String key) {
        return formations.stream()
                .filter(f -> f.identifiant().equals(key))
                .findAny()
                .orElse(null);
    }

<<<<<<< HEAD
    public Collection<String> getFormationsDuSup() {
        if(formations == null) return Collections.emptySet();
        return formations.stream()
                .filter(Formation::isFormationDuSup)
                .map(Formation::identifiant)
                .collect(Collectors.toSet());
    }
=======
>>>>>>> origin/prod
}

package fr.gouv.monprojetsup.data.domain.model.onisep;

import fr.gouv.monprojetsup.data.domain.Constants;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ThematiquesOnisep(
        Map<String,ThematiqueOnisep> thematiques,
        List<Pair<String,String>> redirections,
        Map<String, Regroupement> regroupements
) {
    public ThematiquesOnisep() {
        this(
                new HashMap<>(), new ArrayList<>(), new HashMap<>()
        );
    }

    public record Regroupement(
            String label,
            String groupe,

            String emojiGroupe,
            String emoji
    ) {
    }

    public void add(ThematiqueOnisep item) {
        if(item.nom != null && !item.nom.isEmpty()) {
            String kid = Constants.cleanup(item.id);
            if(!thematiques.containsKey(kid)) {
                thematiques.put(kid, item);
            } else {
                val them = thematiques.get(kid);
                thematiques.put(kid, new ThematiqueOnisep(
                        kid,
                        them.nom,
                        item.parent,
                        item.redirection
                ));
            }
        }
        if(item.redirection != null && !item.redirection.isEmpty() && !item.redirection.equals("X")) {
            redirections.add(Pair.of(Constants.cleanup(item.id), Constants.cleanup(item.redirection)));
        }
    }

    public void add(String id, String label, String groupe, String emojiGroupe, String emoji) {
        regroupements.put(id, new Regroupement(label, groupe, emojiGroupe, emoji));
    }

    public record ThematiqueOnisep(
            @NotNull String id,
            String nom,
            String parent,

            String redirection
    ) {

    }
}

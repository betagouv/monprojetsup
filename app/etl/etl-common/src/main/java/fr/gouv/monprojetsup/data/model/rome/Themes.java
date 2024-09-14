package fr.gouv.monprojetsup.data.model.rome;

import java.util.ArrayList;
import java.util.List;

public record Themes(List<Item> arbo_thematique) {

    public record Item(
            int code_theme,
            String libelle_theme,
            List<InteretsRome.Metier> liste_metier
    ) {
        public Item() {
            this(-1,"",new ArrayList<>());
        }
    }

    public Themes() {
        this(new ArrayList<>());
    }
}

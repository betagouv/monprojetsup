package fr.gouv.monprojetsup.suggestions.algos;

import java.util.ArrayList;
import java.util.List;

public record Suggestions(
        List<Suggestion> suggestions

) {

    public Suggestions() {
        this(new ArrayList<>());
    }

    /* on veut des regroupements
    * par métier: des formations
    * par domaine: des formations et des métiers
     */
}

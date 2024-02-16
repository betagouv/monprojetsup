package fr.gouv.monprojetsup.data.model.specialites;

import java.util.*;

public record Specialites(
        String source,
        Map<Integer,String> specialites,
        Map<String, Set<Integer>> specialitesParBac,
        Set<String> specialitesEpreuvesBac )
{
    public Specialites() {
        this("", new HashMap<>(), new HashMap<>(), new HashSet<>());
    }
}

package fr.gouv.parcoursup.carte.modele.modele;

import java.util.Map;
import java.util.TreeMap;

public class Lexique {

    public Lexique() {

    }

    public Integer getIndex(String w) {
        return index.get(w);
    }

    /* maps a string to its stem number */
    private final Map<String,Integer> index = new TreeMap<>();

}

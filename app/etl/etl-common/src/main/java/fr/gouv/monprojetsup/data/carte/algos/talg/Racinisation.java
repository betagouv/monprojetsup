package fr.gouv.monprojetsup.data.carte.algos.talg;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

import java.util.*;

public class Racinisation {

    private final Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);

    private final TreeMap<String, List<String>> correspondance = new TreeMap<>();

    public void ajouterLexique(Collection<String> lexique) {
        if(!lexique.isEmpty()) {
            index = null;
        }
        lexique.forEach(w -> {
            final String stem;
            if(w.contains(" ")) {
                stem = w;
            } else {
                stem = stemmer.stem(w).toString();
            }
            List<String> corrs = correspondance.computeIfAbsent(stem, key -> new ArrayList<>());
            if(!corrs.contains(w)) {
                corrs.add(w);
            }
        });
    }

    public Map<String, Integer> getIndex() {
        if(index == null) {
            index = new HashMap<>();
            indexRep = new HashMap<>();
            int i = 0;
            for (Collection<String> liste : correspondance.values()) {
                for (String w : liste) {
                    index.put(w, i);
                    indexRep.put(i, w);
                }
                i++;
            }
        }
        return index;
    }

    private Map<String, Integer> index;
    private Map<Integer, String> indexRep;


    public String getStem(Integer key) {
        if(index == null) getIndex();
        return indexRep.get(key);
    }
}

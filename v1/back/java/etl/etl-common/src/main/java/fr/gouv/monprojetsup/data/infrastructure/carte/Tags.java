package fr.gouv.monprojetsup.data.infrastructure.carte;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Tags implements Serializable {
    /* Mots clés pour la recherche, avec leur pertinence */
    public final transient Map<String,Integer> tags = new HashMap<>();
    public final transient Map<String,Set<DetailTag>> sources = new HashMap<>();

    //encodes as first the total score, then a sequence of interleaved sources and subscores
    public final Map<String,List<Integer>> scoreSourceSubScoreStar = new HashMap<>();

    public void encode() {
        scoreSourceSubScoreStar.clear();
        tags.forEach((s, score) -> {
            List<Integer> l = scoreSourceSubScoreStar.computeIfAbsent(s, z -> new ArrayList<>());
            l.add(score);
            sources.getOrDefault(s, new HashSet<>()).forEach(detailTag -> {
                        l.add(detailTag.source);
                        l.add(detailTag.poids);
                    }
                    );
        });
    }

    public void decode() {
        tags.clear();
        scoreSourceSubScoreStar.forEach((s, l) -> {
            if(!l.isEmpty()) {
                tags.put(s, l.get(0));
                Set<DetailTag> details = new HashSet<>();
                for(int i = 1; i < l.size(); i+= 2) {
                    details.add(new DetailTag(l.get(i), l.get(i+1)));
                }
                sources.put(s, details);
            }
        });
    }



    private static final ConcurrentHashMap<String,Integer> sourceToIndex = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer,String> indextoSource = new ConcurrentHashMap<>();

    public static Map<Integer,String> getSourceToIndex() { return Collections.unmodifiableMap(indextoSource); }
    private static final AtomicInteger largestSourceIndex = new AtomicInteger(0);


    public static int weightToScore(int weight) {
        return 2 << weight;
    }

    public void addWeight(String mot, int weight, String sourceType) {
        int curScore = tags.getOrDefault(mot, 0);
        int score = weightToScore(weight);
        tags.put(mot, score | curScore);
        int index = getSourceIndex(sourceType);
        sources.computeIfAbsent(mot, z -> new HashSet<>()).add(new DetailTag(index,score));
    }

    public void addScore(String mot, int score, String sourceType) {
        tags.put(mot, score + tags.getOrDefault(mot, 0));
        int index = getSourceIndex(sourceType);
        sources.computeIfAbsent(mot, z -> new HashSet<>()).add(new DetailTag(index,score));
    }

    private int getSourceIndex(String source) {
        synchronized (sourceToIndex) {
            if (sourceToIndex.containsKey(source)) {
                return sourceToIndex.get(source);
            } else {
                sourceToIndex.put(source, largestSourceIndex.incrementAndGet());
                int result = sourceToIndex.get(source);
                indextoSource.put(result, source);
                return result;
            }
        }
    }


    public Set<String> getWords() {
        return tags.keySet();
    }

    public Map<String,Integer> getTags() {
        return Collections.unmodifiableMap(tags);
    }

}
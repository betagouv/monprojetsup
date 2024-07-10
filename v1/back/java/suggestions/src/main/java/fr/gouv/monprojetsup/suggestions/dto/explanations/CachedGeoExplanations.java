package fr.gouv.monprojetsup.suggestions.dto.explanations;

import fr.gouv.monprojetsup.suggestions.tools.ConcurrentBoundedMapQueue;
import fr.parcoursup.carte.algos.tools.Paire;

import java.util.List;

public class CachedGeoExplanations {

    private static final int DISTANCE_CACHE_SIZE = 10000;

    /**
     * caches return values of getDistanceKm
     */
    public static final ConcurrentBoundedMapQueue<Paire<String, String>, List<ExplanationGeo>>
            distanceCaches = new ConcurrentBoundedMapQueue<>(DISTANCE_CACHE_SIZE);

}

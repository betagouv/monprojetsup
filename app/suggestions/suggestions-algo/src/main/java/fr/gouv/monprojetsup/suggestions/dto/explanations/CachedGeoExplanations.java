package fr.gouv.monprojetsup.suggestions.dto.explanations;

import fr.gouv.monprojetsup.suggestions.tools.ConcurrentBoundedMapQueue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CachedGeoExplanations {

    private static final int DISTANCE_CACHE_SIZE = 10000;

    /**
     * caches return values of getDistanceKm
     */
    public static final ConcurrentBoundedMapQueue<Pair<String, String>, List<ExplanationGeo>>
            distanceCaches = new ConcurrentBoundedMapQueue<>(DISTANCE_CACHE_SIZE);

}

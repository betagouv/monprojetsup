package fr.gouv.monprojetsup.suggestions.algos;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConcurrentBoundedMapQueue<K, V> {
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private final int capacity;
    private final LinkedHashMap<K, V> map;

    public ConcurrentBoundedMapQueue(int capacity) {
        this.capacity = capacity;
        this.map = new LinkedHashMap<K, V>(
                capacity, DEFAULT_LOAD_FACTOR, true
        ) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > ConcurrentBoundedMapQueue.this.capacity;
            }
        };
    }

    public synchronized void put(K key, V value) {
        map.put(key, value);
    }

    public synchronized V get(K key) {
        return map.get(key);
    }


    public int size() {
        return map.size();
    }
}

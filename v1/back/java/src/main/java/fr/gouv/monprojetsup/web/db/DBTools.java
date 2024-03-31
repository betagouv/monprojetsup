package fr.gouv.monprojetsup.web.db;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DBTools {
    public static <T> void removeDotsFromKeys(Map<String, T> m) {
        Set<Map.Entry<String, T>> keysWithDots = m.entrySet().stream().filter(k -> k.getKey().contains(".")).collect(Collectors.toSet());
        keysWithDots.forEach(e -> m.put(e.getKey().replace(".", "_"), e.getValue()));
        m.entrySet().removeAll(keysWithDots);
    }
}

package fr.gouv.monprojetsup.suggestions.tools;

import java.util.Collection;

public class Stats {

    public static Integer p50(Collection<Integer> values) {
        return values.stream().sorted().skip(values.size() / 2).findFirst().orElse(0);
    }

    public static Integer p75(Collection<Integer> values) {
        return values.stream().sorted().skip(75L * values.size() / 100).findFirst().orElse(0);
    }

    private Stats() {
    }

}

package fr.gouv.monprojetsup.app.analysis;

import fr.gouv.monprojetsup.app.log.ServerTrace;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordSearch {
    public static void analyseWordSearch(List<ServerTrace> allTraces) throws IOException {
        Map<String, Long> searches = new HashMap<>();

        String beginOldStr = "\"str\":\"";
        for (ServerTrace st : allTraces) {
            if (st.event() != null && st.event().contains("searching ")) {
                String searching = st.event().substring(st.event().indexOf("searching ") + "searching ".length());
                int i = searching.indexOf(beginOldStr);
                if(i > 0) {
                    searching = searching.substring(i + beginOldStr.length());
                    int j = searching.indexOf("\"");
                    if(j > 0) {
                        searching = searching.substring(0, j);
                        searches.merge(searching, 1L, Long::sum);
                    }
                }
            }
            if (st.event() != null && st.event().contains("search ")) {
                String w = st.event().substring(st.event().indexOf("search ") + "search ".length());
                searches.merge(w, 1L, Long::sum);
            }
        }

        List<Pair<String,Long>> sorted = searches.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> Pair.of(e.getKey(), e.getValue()))
                .toList();
        Serialisation.toJsonFile("searches.json", sorted, true);
        try(CsvTools csv = new CsvTools("searches.csv", ',')) {
            csv.appendHeaders(List.of("search", "count"));
            for (Pair<String, Long> pair : sorted) {
                csv.append(pair.getLeft());
                csv.append(pair.getRight());
                csv.newLine();
            }
        }
    }
}

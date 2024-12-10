package fr.gouv.monprojetsup.suggestions.dto.explanations;


import fr.gouv.monprojetsup.data.model.stats.Middle50;
import fr.gouv.monprojetsup.suggestions.data.model.Path;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;


record ExplanationApprentissage (String option) {}

record ExplanationTag(List<Path> pathes) {

}


record ExplanationTagShort(List<String> ns) {

    public static ExplanationTagShort fromPathes(@Nullable Collection<Path> pathes) {
        if(pathes == null) {
            return new ExplanationTagShort(List.of());
        } else {
            return new ExplanationTagShort(
                    pathes.stream()
                            .filter(p -> !p.isEmpty() && p.size() <= 2)
                            .map(Path::first)
                            .distinct()
                            .toList()
            );
        }
    }

    public String toExplanation(String sep, Map<String,String> labels) {

        return this.ns().stream()
                .sorted().map(
                        z -> labels.getOrDefault(z,z)
                ).collect(Collectors.joining(sep, "\t", "\n"));
    }
}

record ExplanationDuration(String option) {}

record ExplanationSimilarity(String id, double p) {}

record ExplanationTypeBac (int percentage, String bac) {}

record ExplanationNotes(double moy, Middle50 middle50, String bacUtilise) {}

record ExplanationDebug (String expl) {}

record ExplanationSpecialite (String spe, int pct) {}

record ExplanationSpecialites (List<ExplanationSpecialite> stats) {}

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Explanation {
/* Centralizes all explanations given to candidate. */

    @Nullable List<ExplanationGeo> geo;
    @Nullable ExplanationApprentissage app;
    @Nullable ExplanationTag tag;
    @Nullable ExplanationTagShort tags;
    @Nullable ExplanationDuration dur;
    @Nullable ExplanationSimilarity simi;
    @Nullable ExplanationTypeBac tbac;
    @Nullable ExplanationNotes moygen;
    @Nullable ExplanationDebug debug;
    @Nullable ExplanationSpecialites spec;

    public static @NotNull Explanation getGeoExplanation(@Nullable List<ExplanationGeo> result) {
        Explanation e = new Explanation();
        e.geo = result;
        return e;
    }

    public static @NotNull Explanation getAppExplanation(String option) {
        Explanation e = new Explanation();
        e.app = new ExplanationApprentissage(option);
        return e;
    }

    public static @NotNull Explanation getTagExplanationShort(Collection<Path> pathes) {
        Explanation e = new Explanation();
        e.tags = ExplanationTagShort.fromPathes(pathes);
        return e;
    }


    public static @NotNull Explanation getDurationExplanation(String option) {
        Explanation e = new Explanation();
        e.dur = new ExplanationDuration(option);
        return e;
    }

    public static @NotNull Explanation getSimilarityExplanation(String flKey, int percentage /*percentage*/) {
        Explanation e = new Explanation();
        e.simi = new ExplanationSimilarity(flKey, percentage);
        return e;
    }

    public static @NotNull Explanation getTypeBacExplanation(int percentage, String bac) {
        Explanation e = new Explanation();
        e.tbac = new ExplanationTypeBac(percentage, bac);
        return e;
    }


    public static @NotNull Explanation getNotesExplanation(double moy, Middle50 middle50, String bac) {
        Explanation e = new Explanation();
        e.moygen = new ExplanationNotes(moy, middle50, bac);
        return e;
    }

    public static @NotNull Explanation getDebugExplanation(String debug) {
        Explanation e = new Explanation();
        e.debug = new ExplanationDebug(debug);
        return e;
    }

    public static @NotNull Explanation getSpecialitesExplanation(Map<String, Double> stats) {
        Explanation e = new Explanation();
        e.spec = new ExplanationSpecialites(stats.entrySet().stream().map(ee -> new ExplanationSpecialite(ee.getKey(), (int) (ee.getValue() * 100))).toList());
        return e;
    }

    public String toHumanReadable(Map<String,String> labels) {
        StringBuilder sb = new StringBuilder();
        if(geo != null) {
            geo.forEach(explanationGeo -> sb.append("préférence géographique:")
                    .append(" ville=")
                    .append(explanationGeo.city())
                    .append(" distance=")
                    .append(explanationGeo.distance()).append("\n"));
        }
        if(app != null) sb.append("Apprentissage: ").append(app.option()).append("\n");
        if(tags != null) {
            sb.append("Lien avec:\n\n").append(tags.toExplanation("\n\t", labels)).append("\n");
        }
        if(dur != null) sb.append("Durée: ").append(dur.option()).append("\n");
        if(simi != null) sb.append("Similarité: ").append(simi.id()).append("\n");
        if(tbac != null) sb.append("Type de bac: ").append(tbac).append("\n");
        if(moygen != null) sb.append("Moyenne générale: ").append(moygen).append("\n");
        if(spec != null) sb.append("Spécialités: ").append(spec).append("\n");
        if(debug != null) sb.append(debug.expl()).append("\n");
        return sb.toString();
    }


    public String toExplanation(Map<String,String> labels) {
        StringBuilder sb = new StringBuilder();
        if(geo != null) sb.append("geo=").append(geo).append("\n");
        if(app != null) sb.append("app=").append(app).append("\n");
        if(tags != null) sb.append("tags=").append(tags.toExplanation("\n\t", labels)).append("\n");
        if(dur != null) sb.append("dur=").append(dur).append("\n");
        if(simi != null) sb.append("simi=").append(simi).append("\n");
        if(tbac != null) sb.append("tbac=").append(tbac).append("\n");
        if(moygen != null) sb.append("moygen=").append(moygen).append("\n");
        if(debug != null && !debug.expl().startsWith("Pas de")) sb.append(debug.expl()).append("\n");
        if(spec != null) sb.append("spec=").append(spec).append("\n");
        return sb.toString();
    }


}



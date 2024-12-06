package fr.gouv.monprojetsup.suggestions.dto.explanations;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
            return new ExplanationTagShort(Collections.emptyList());
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

    public static ExplanationTagShort fromNodes(Set<String> nodes) {
        return new ExplanationTagShort(nodes.stream().toList());
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

    public static @NotNull List<Explanation> merge(@Nullable List<Explanation> explanations) {
        if(explanations == null) return Collections.emptyList();

        //we use a set to avoid duplicates
        Set<Explanation> result = new HashSet<>();
        ExplanationDuration dur = null;

        ExplanationSpecialites spec = null;
        boolean seenSpec = false;
        ExplanationTypeBac tbac = null;
        boolean seenTbac = false;
        ExplanationNotes moygen = null;
        boolean seenMoyGen = false;

        ExplanationApprentissage app = null;

        Set<Path> pathes = new HashSet<>();
        Set<String> nodes = new HashSet<>();
        for (Explanation e : explanations) {
            if(e == null) {
                continue;
            }
            if(e.app != null) {
                app = e.app;
            }
            if(e.dur != null) {
                dur = e.dur;
            }

            if (e.geo != null || e.simi != null || e.debug != null) {
                result.add(e);
            }
            if (e.spec != null) {
                if(!seenSpec) spec = e.spec;
                else spec = null;
                seenSpec = true;
            }
            if (e.tbac != null) {
                if(!seenTbac) tbac = e.tbac;
                else tbac = null;
                seenTbac = true;
            }
            if (e.moygen != null) {
                if(!seenMoyGen) moygen = e.moygen;
                else moygen = null;
                seenMoyGen = true;
            }
            if(e.tag != null) {
                pathes.addAll(e.tag.pathes());
            }
            if(e.tags != null) {
                nodes.addAll(e.tags.ns());
            }
        }

        Explanation e = new Explanation();
        e.app = app;
        e.dur = dur;
        e.spec = spec;
        e.tbac = tbac;
        e.moygen = moygen;
        if(e.isEmpty()) {
            result.add(e);
        }
        if(!pathes.isEmpty()) {
            result.add(getTagExplanation(pathes));
        }
        if(!nodes.isEmpty()) {
            result.add(getTagExplanationShort(nodes));
        }
        return result.stream().toList();
    }

    private boolean isEmpty() {
        return geo == null && app == null && tag == null && tags == null && dur == null && simi == null && tbac == null && moygen == null && debug == null && spec == null;
    }

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

    public static @NotNull Explanation getTagExplanation(
            List<Path> pathes)
    {
        Explanation e = new Explanation();
        e.tag = new ExplanationTag(pathes);
        return e;
    }

    public static @NotNull Explanation getTagExplanation(Set<Path> pathes) {
        return getTagExplanation(pathes.stream().toList());
    }

    public static @NotNull Explanation getTagExplanationShort(Collection<Path> pathes) {
        Explanation e = new Explanation();
        e.tags = ExplanationTagShort.fromPathes(pathes);
        return e;
    }

    private static @NotNull Explanation getTagExplanationShort(Set<String> nodes) {
        Explanation e = new Explanation();
        e.tags = ExplanationTagShort.fromNodes(nodes);
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
        //if(tag != null) sb.append("tag=").append(tag.toExplanation(labels)).append("\n");
        if(tags != null) sb.append("tags=").append(tags.toExplanation("\n\t", labels)).append("\n");
        if(dur != null) sb.append("dur=").append(dur).append("\n");
        if(simi != null) sb.append("simi=").append(simi).append("\n");
        if(tbac != null) sb.append("tbac=").append(tbac).append("\n");
        if(moygen != null) sb.append("moygen=").append(moygen).append("\n");
        if(debug != null && !debug.expl().startsWith("Pas de")) sb.append(debug.expl()).append("\n");
        if(spec != null) sb.append("spec=").append(spec).append("\n");
        return sb.toString();
    }


    @JsonIgnore
    public boolean isInheritableFromSingleFormationToItsGroup() {
        return hasNoStats();
    }

    private boolean hasNoStats() {
        return moygen == null && tbac == null && spec == null;
    }
}



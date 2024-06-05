package fr.gouv.monprojetsup.data.model;


import fr.gouv.monprojetsup.data.dto.ExplanationGeo;
import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.stats.Middle50;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;


record ExplanationApprentissage (String option) {}

record ExplanationTag(List<Path> pathes) {
    static DecimalFormat df = new DecimalFormat("#.#####");
    public String toExplanation() {
        return pathes.stream()
                .filter(p -> p.nodes() != null && !p.nodes().isEmpty())
                .sorted(Comparator.comparingDouble(Path::score).reversed())
                .map(p -> df.format(p.score()) +  p.nodes().stream().map(ServerData::getDebugLabel).collect(Collectors.joining(" -> ")))
                .collect(Collectors.joining("\n\t", "\t", "\n"));
    }

}


record ExplanationTagShort(List<String> ns) {

    public static ExplanationTagShort fromPathes(@Nullable Collection<Path> pathes) {
        if(pathes == null) {
            return new ExplanationTagShort(Collections.emptyList());
        } else {
            return new ExplanationTagShort(
                    pathes.stream()
                            .map(Path::nodes)
                            .filter(l -> l != null && !l.isEmpty())
                            .map(l -> l.get(0))
                            .toList()
            );
        }
    }

    public static ExplanationTagShort fromNodes(Set<String> nodes) {
        return new ExplanationTagShort(nodes.stream().toList());
    }

    public String toExplanation() {
        return toExplanation(",");
    }
    public String toExplanation(String sep) {
        StringBuilder ssb = new StringBuilder();

        ssb.append(ns().stream()
                        .filter(s -> Helpers.isFiliere(s))
                .sorted().map(
                ServerData::getDebugLabel
        ).collect(Collectors.joining(sep,"\t","\n")))
        ;

        ssb.append(ns().stream()
                .filter(s -> Helpers.isMetier(s))
                .sorted().map(
                        ServerData::getDebugLabel
                ).collect(Collectors.joining(sep,"\n\t","\n")))
        ;

        ssb.append(ns().stream()
                .filter(s -> Helpers.isTheme(s))
                .sorted().map(
                        ServerData::getDebugLabel
                ).collect(Collectors.joining(sep,"\n\t","\n")))
        ;

        ssb.append(ns().stream()
                .filter(s -> Helpers.isInteret(s))
                .sorted().map(
                        ServerData::getDebugLabel
                ).collect(Collectors.joining(sep,"\n\t","\n")))
        ;

        return ssb.toString();
    }
}

record ExplanationDuration(String option) {}

record ExplanationSimilarity(String fl, double p) {}

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
        List<Explanation> result = new ArrayList<>();
        ExplanationDuration dur = null;

        ExplanationNotes moygen = null;
        boolean seenMoyGen = false;

        ExplanationTypeBac tbac = null;
        boolean seenTypeBac = false;

        Set<Path> pathes = new HashSet<>();
        Set<String> nodes = new HashSet<>();
        for (Explanation e : explanations) {
            if(e == null) {
                continue;
            }

            if (e.geo != null || e.simi != null || e.debug != null || e.spec != null) {
                result.add(e);
            }
            if (e.dur != null && dur == null) {
                result.add(e);
                dur = e.dur;
            }
            if (e.moygen != null) {
                if(seenMoyGen) {
                    moygen = null;
                } else {
                    moygen = e.moygen;
                    seenMoyGen = true;
                }
            }
            if (e.tbac != null) {
                if(seenTypeBac) {
                    tbac = null;
                } else {
                    tbac = e.tbac;
                    seenTypeBac = true;
                }
            }
            if(e.tag != null) {
                pathes.addAll(e.tag.pathes());
            }
            if(e.tags != null) {
                nodes.addAll(e.tags.ns());
            }
        }
        if(moygen != null) {
            Explanation e = new Explanation();
            e.moygen = moygen;
            result.add(e);
        }
        if(tbac != null) {
            Explanation e = new Explanation();
            e.tbac = tbac;
            result.add(e);
        }
        if(!pathes.isEmpty()) {
            result.add(getTagExplanation(pathes));
        }
        if(!nodes.isEmpty()) {
            result.add(getTagExplanationShort(nodes));
        }
        return result;
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

    public String toHumanReadable() {
        StringBuilder sb = new StringBuilder();
        if(geo != null) {
            geo.forEach(explanationGeo -> {
                sb.append("préférence géographique:")
                        .append(" ville=")
                        .append(explanationGeo.city())
                        .append(" distance=")
                        .append(explanationGeo.distance()).append("\n");
            });
        }
        if(app != null) sb.append("Apprentissage: ").append(app.option()).append("\n");
        if(tags != null) {
            sb.append("Lien avec:\n\n").append(tags.toExplanation("\n\t")).append("\n");
        }
        if(dur != null) sb.append("Durée: ").append(dur.option()).append("\n");
        if(simi != null) sb.append("Similarité: ").append(simi.fl()).append("\n");
        if(tbac != null) sb.append("Type de bac: ").append(tbac).append("\n");
        if(moygen != null) sb.append("Moyenne générale: ").append(moygen).append("\n");
        if(spec != null) sb.append("Spécialités: ").append(spec).append("\n");
        if(debug != null) sb.append(debug.expl()).append("\n");
        return sb.toString();
    }

    public String toExplanation() {
        StringBuilder sb = new StringBuilder();
        if(geo != null) sb.append("geo=").append(geo).append("\n");
        if(app != null) sb.append("app=").append(app).append("\n");
        if(tag != null) sb.append("tag=").append(tag.toExplanation()).append("\n");
        if(tags != null) sb.append("tags=").append(tags.toExplanation()).append("\n");
        if(dur != null) sb.append("dur=").append(dur).append("\n");
        if(simi != null) sb.append("simi=").append(simi).append("\n");
        if(tbac != null) sb.append("tbac=").append(tbac).append("\n");
        if(moygen != null) sb.append("moygen=").append(moygen).append("\n");
        if(debug != null && !debug.expl().startsWith("Pas de")) sb.append(debug.expl()).append("\n");
        if(spec != null) sb.append("spec=").append(spec).append("\n");
        return sb.toString();
    }


}



package fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs;


import fr.gouv.monprojetsup.suggestions.infrastructure.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.suggestions.domain.Helpers;
import lombok.Data;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.suggestions.domain.Constants.cleanup;
import static fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs.DescriptifsFormations.DescriptifFormation.mergeDescriptifs;

public record DescriptifsFormations(

        //indexed by formation
    Map<String, DescriptifFormation> keyToDescriptifs
) {

    private static final String EXPLORER_AVENIRS_URL = "https://explorer-avenirs.onisep.fr";
    private static final String ONISEP_URL1 = "http://www.onisep.fr";
    private static final String ONISEP_URL2 = "https://www.onisep.fr";
    public static final String RESUME_FORMATION_MPS_HEADER = "résumé formation VLauriane";
    public static final String RESUME_FORMATION_V1 = "résumé formation V1";
    public DescriptifsFormations() {
        this(new HashMap<>());
    }

    public static Link toAvenirs(String uri, String label) {
        if(uri == null) return null;
        uri =  uri
                .replace(
                "www.terminales2022-2023.fr","www.onisep.fr")
                .replace(ONISEP_URL1,EXPLORER_AVENIRS_URL)
                .replace(ONISEP_URL2,EXPLORER_AVENIRS_URL)
                ;
        return new Link(label, uri);
    }



    public void injectGroups(Map<String, String> groups) {
        Map<String, List<String>> groupsToItems = groups.keySet().stream().collect(Collectors.groupingBy(groups::get));
        Map<String, DescriptifFormation> urlToDesc = new HashMap<>();


        Map<String, Set<String>> noDataGroupsToUrls = new HashMap<>();
        groupsToItems.forEach((s, strings) -> {
            DescriptifFormation desc = keyToDescriptifs.get(s);
            if(desc == null || !desc.isViable()) {
                strings.forEach(s1 -> {
                    DescriptifFormation desc1 = keyToDescriptifs.get(s1);
                    if(desc1 != null && desc1.isViable()) {
                        noDataGroupsToUrls.computeIfAbsent(s, z -> new HashSet<>()).add(desc1.url);
                    }
                });
            }
        });

        keyToDescriptifs.values().forEach(desc -> {
            if(desc.isViable()) {
                urlToDesc.put(desc.url, desc);
            }
        });

        noDataGroupsToUrls.forEach((s, urls) -> {
            List<DescriptifFormation> descriptifs = urls.stream()
                    .map(urlToDesc::get)
                    .filter(Objects::nonNull)
                    .toList();
            DescriptifFormation desc = mergeDescriptifs(descriptifs);
            keyToDescriptifs.put(s, desc);
        });

    }

    public void injectLas(Map<String, String> lasCorrespondance) {
        lasCorrespondance.forEach((lasKey,genKey) -> keyToDescriptifs.computeIfAbsent(
                lasKey,
                z-> keyToDescriptifs.get(genKey)
        ));
    }

    public void inject(MetiersScrapped metiersScrapped) {
        metiersScrapped.metiers().values().forEach(m -> {
            String cs = cleanup(m.key());
            if(!keyToDescriptifs.containsKey(cs)) {
                String text = null;
                if(m.accroche() != null) {
                    text = m.accroche();
                } else if(m.metier() != null) {
                    text = m.metier();
                }
                if(text != null) {
                    if(m.etudes() != null) {
                        text += "<br/><br/>" + m.etudes();
                    }
                    keyToDescriptifs.put(
                            cs,
                            new DescriptifFormation(
                                    text,
                                    m.url(),
                                    "metier scrapped"
                            )
                    );
                }
            }
        });
    }

    public void inject(Map<String, String> summaries) {
        for (Map.Entry<String, String> entry : summaries.entrySet()) {
            String key = entry.getKey();
            String summary = entry.getValue();
            DescriptifFormation descriptif = keyToDescriptifs.get(key);
            keyToDescriptifs.put(key, DescriptifFormation.setSummary(descriptif, summary));
        }
    }

    public DescriptifFormation get(String key) {
        return keyToDescriptifs.get(key);
    }

    public void push(DescriptifsFormations descriptifs) {
        this.keyToDescriptifs.putAll(descriptifs.keyToDescriptifs);
    }

    @Nullable
    public String getDescriptifGeneralFront(@NotNull String flCod) {
        val desc = keyToDescriptifs.get(flCod);
        if(desc == null) return null;
        return Helpers.removeHtml(desc.getDescriptifGeneralFront());
    }

    @Nullable
    public String getDescriptifDiplomeFront(@NotNull String flCod) {
        val desc = keyToDescriptifs.get(flCod);
        if(desc == null) return null;
        return Helpers.removeHtml(desc.getDescriptifDiplomeFront());
    }

    public void inject(String cleanup, DescriptifFormation domainePro) {
        keyToDescriptifs.put(cleanup, domainePro);
    }


    @Data
    public static final class DescriptifFormation {
        private String presentation;
        private String poursuite;
        private String metiers;
        private @Nullable String url;
        private String error;
        private @NotNull String type;
        private Set<String> multiUrls = new HashSet<>();
        private String summary;
        private String summaryFormation;
        private Map<String, String> mpsData = new HashMap<>();
        private boolean correctedUrl = false;

        public DescriptifFormation(
                String presentation,
                String poursuite,
                String metiers,
                @Nullable String url,
                String error,
                @NotNull String type,

                Collection<String> multiUrls,

                String summary,
                String summaryFormation,

                Map<String, String> mpsData
        ) {
            this.presentation = presentation;
            this.poursuite = poursuite;
            this.metiers = metiers;
            this.url = url;
            this.error = error;
            this.type = type;
            if (multiUrls != null) this.multiUrls.addAll(multiUrls);
            this.summary = summary;
            this.summaryFormation = summaryFormation;
            this.mpsData.putAll(mpsData);
        }


        public DescriptifFormation(String presentation, String url, String type) {
            this(presentation, null, null, url, null, type, null, null, null, new HashMap<>());
        }

        public DescriptifFormation(String summary, String summaryFormation, String url, String type) {
            this(null, null, null, url, null, type, null, summary, summaryFormation, new HashMap<>());
        }

        public DescriptifFormation(Map<String, String> mpsData) {
            this(null, null, null, null, null, "summary", null, null, null, mpsData);
        }


        public static DescriptifFormation addToDescriptif(String addendum, DescriptifFormation o) {
            if (o == null) {
                return new DescriptifFormation(addendum, null, "addendum from null");
            }
            if (o.type.equals("error")) {
                return new DescriptifFormation(addendum, o.url, "addedndum from error");
            }
            return new DescriptifFormation(
                    addendum + o.presentation,
                    o.poursuite,
                    o.metiers,
                    o.url,
                    o.error,
                    "addendum + " + o.type,
                    o.multiUrls,
                    o.summary,
                    o.summaryFormation,
                    new HashMap<>()
            );
        }

        public static DescriptifFormation mergeDescriptifs(List<DescriptifFormation> descriptifs) {
            if (descriptifs.isEmpty()) {
                return new DescriptifFormation("", null, null, null, "empty merge", "error", null, null, null, new HashMap<>());
            } else if (descriptifs.size() == 1) {
                return descriptifs.get(0);
            }
            String presentation = descriptifs.stream().map(d -> d.presentation)
                    .filter(d -> d != null && !d.isEmpty())
                    .reduce((s, s2) -> s + "<br/>" + s2
                    ).orElse("");
            String poursuite = descriptifs.stream().map(d -> d.poursuite)
                    .filter(d -> d != null && !d.isEmpty())
                    .reduce((s, s2) -> s + "<br/>" + s2
                    ).orElse(null);
            String metiers = descriptifs.stream().map(d -> d.metiers)
                    .filter(d -> d != null && !d.isEmpty())
                    .reduce((s, s2) -> s + "<br/>" + s2
                    ).orElse(null);
            String summaries = descriptifs.stream().map(d -> d.summary)
                    .filter(d -> d != null && !d.isEmpty())
                    .reduce((s, s2) -> s + "<br/>" + s2
                    ).orElse(null);
            String summariesFormations = descriptifs.stream().map(d -> d.summaryFormation)
                    .filter(d -> d != null && !d.isEmpty())
                    .findAny().orElse(null);
            List<String> urls = descriptifs.stream().map(d -> d.url).filter(u -> u != null && !u.isEmpty()).distinct().toList();
            return new DescriptifFormation(presentation, poursuite, metiers, urls.isEmpty() ? null : urls.get(0), null, "merge", urls, summaries, summariesFormations, new HashMap<>());
        }

        public static DescriptifFormation setSummary(DescriptifFormation o, String summary) {
            if (o == null) {
                return new DescriptifFormation(summary, null, "summary");
            } else {
                return new DescriptifFormation(
                        o.presentation,
                        o.poursuite,
                        o.metiers,
                        o.url,
                        o.error,
                        o.type,
                        o.multiUrls,
                        summary,
                        null,
                        new HashMap<>()
                );
            }
        }

        public static DescriptifFormation setSummaries(
                DescriptifFormation o,
                String summaryFormation,
                String summaryFiliere
        ) {
            if (o == null) {
                return new DescriptifFormation(summaryFormation, summaryFiliere, null, "summary");
            } else {
                return new DescriptifFormation(
                        o.presentation,
                        o.poursuite,
                        o.metiers,
                        o.url,
                        o.error,
                        o.type,
                        o.multiUrls,
                        summaryFiliere,
                        summaryFormation,
                        new HashMap<>()
                );
            }
        }

        public boolean hasError() {
            return error != null;
        }

        public boolean isRedirectedToDefaultPage() {
            return error != null && error.startsWith("redirected to");
        }

        public boolean isOutDated() {
            return error != null && error.startsWith("www.terminales2021");
        }

        public boolean isRecherche() {
            return error != null && error.startsWith("recherche");
        }

        public String getFrontRendering() {
            if (summary != null) return summary;
            StringBuilder b = new StringBuilder();
            if (presentation != null) {
                b.append("<p>").append(presentation).append("</p>");
            }
            if (poursuite != null) {
                b.append("<br/><br/>").append("<p>").append(poursuite).append("</p>");
            }
            if (metiers != null) {
                b.append("<br/><br/>").append("<p>").append(metiers).append("</p>");
            }
            String result = b.toString();
            result = result.replace("<h3>", "<br>");
            result = result.replace("</h3>", "<br/>");
            return result;
        }

        public String presentation() {
            return presentation;
        }

        public String metiers() {
            return metiers;
        }

        public @Nullable String url() {
            return url;
        }

        public String error() {
            return error;
        }

        public @NotNull String type() {
            return type;
        }

        public Set<String> multiUrls() {
            return multiUrls;
        }

        public String summary() {
            return summary;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (DescriptifFormation) obj;
            return Objects.equals(this.presentation, that.presentation) &&
                    Objects.equals(this.poursuite, that.poursuite) &&
                    Objects.equals(this.metiers, that.metiers) &&
                    Objects.equals(this.url, that.url) &&
                    Objects.equals(this.error, that.error) &&
                    Objects.equals(this.type, that.type) &&
                    Objects.equals(this.multiUrls, that.multiUrls) &&
                    Objects.equals(this.summary, that.summary) &&
                    Objects.equals(this.summaryFormation, that.summaryFormation) &&
                    Objects.equals(this.mpsData, that.mpsData);
        }

        @Override
        public int hashCode() {
            return Objects.hash(presentation, poursuite, metiers, url, error, type, multiUrls, summary, summaryFormation, mpsData);
        }

        @Override
        public String toString() {
            return "Descriptif[" +
                    "presentation=" + presentation + ", " +
                    "poursuite=" + poursuite + ", " +
                    "metiers=" + metiers + ", " +
                    "url=" + url + ", " +
                    "error=" + error + ", " +
                    "type=" + type + ", " +
                    "multiUrls=" + multiUrls + ", " +
                    "summary=" + summary + ", " +
                    "summaryFormation=" + summaryFormation + ", " +
                    "mpsData=" + mpsData + ']';
        }

        public boolean isViable() {
            return (mpsData != null && !mpsData.isEmpty()) || (error == null || error.isEmpty());
        }

        public @Nullable String getDescriptifGeneralFront() {
            if (summary != null && !summary.isEmpty()) return summary;
            if (presentation != null && !presentation.isEmpty()) return presentation;
            return null;
        }

        public String getDescriptifDiplomeFront() {
            if (summaryFormation != null && !summaryFormation.isEmpty()) return summaryFormation;
            return null;
        }

    }

    public static DescriptifFormation getError(String error, @NotNull String url) {
        return new DescriptifFormation(null, null, null, url, error, "error", null, null, null, new HashMap<>());
    }

    public static DescriptifFormation getDescriptif(List<String> psTextxs, @NotNull String url, @NotNull String type) {
        final StringBuilder presentation = new StringBuilder();
        String poursuite = null;
        String metiers = null;
        for (String s : psTextxs) {
            if (presentation.isEmpty()) {
                if (s.isEmpty()) {
                    /* mise en page bizarre comme sur https://www.terminales2022-2023.fr/ressources/univers-formation/formations/Post-bac/bts-pilotage-de-procedes */
                    presentation.append(String.join("", psTextxs));
                    break;
                } else {
                    presentation.append(s);
                }
            } else if (s.contains("poursuivent")) {
                poursuite = s;
            } else if (s.contains("Exemples de métiers") || s.contains("professionnel") || s.contains("travail") || s.contains("secteur")) {
                metiers = s;
            } else {
                presentation.append(s);
            }
        }
        return new DescriptifFormation(presentation.toString(), poursuite, metiers, url, null, type, null, null, null, new HashMap<>());
    }


    public record Link(String label, String uri) {

    }
}
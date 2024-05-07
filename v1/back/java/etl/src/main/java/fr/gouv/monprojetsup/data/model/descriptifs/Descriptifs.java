package fr.gouv.monprojetsup.data.model.descriptifs;


import fr.gouv.monprojetsup.data.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.update.onisep.FichesMetierOnisep;
import fr.gouv.monprojetsup.data.update.onisep.SecteursPro;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.cleanup;
import static fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs.Descriptif.mergeDescriptifs;

public record Descriptifs(
    Map<String, Descriptif> keyToDescriptifs
) {

    private static final String EXPLORER_AVENIRS_URL = "https://explorer-avenirs.onisep.fr";
    private static final String ONISEP_URL1 = "http://www.onisep.fr";
    private static final String ONISEP_URL2 = "https://www.onisep.fr";
    public static final String RESUME_FORMATION_MPS_HEADER = "résumé formation VLauriane";
    public static final String RESUME_FORMATION_V1 = "résumé formation V1";
    public Descriptifs() {
        this(new HashMap<>());
    }

    public static String toAvenirs(String s) {
        if(s == null) return null;
        return s
                .replace(
                "www.terminales2022-2023.fr","www.onisep.fr")
                .replace(ONISEP_URL1,EXPLORER_AVENIRS_URL)
                .replace(ONISEP_URL2,EXPLORER_AVENIRS_URL)
                ;
    }

    public void injectFichesMetiers(FichesMetierOnisep fiches) {
        //inject descriptifs
        //inject urls
        fiches.metiers().metier().forEach(fiche -> {
            String key = cleanup(fiche.identifiant());
            String descriptif = fiche.accroche_metier();
            if (descriptif == null) {
                Map<String, List<FichesMetierOnisep.FormatCourt>> textes
                        = fiche.formats_courts().format_court().stream().collect(Collectors.groupingBy(FichesMetierOnisep.FormatCourt::type));
                List<FichesMetierOnisep.FormatCourt> accroches = textes.get("accroche_metier");
                if (accroches != null && !accroches.isEmpty()) {
                    FichesMetierOnisep.FormatCourt accroche = accroches.get(0);
                    if (accroche.descriptif() != null) {
                        descriptif = accroche.descriptif();
                    }
                }
            }
            keyToDescriptifs().put(
                    key,
                    new Descriptifs.Descriptif(
                            descriptif,
                            "",
                            "accroche_metier"
                    ));
        });

    }

    public void injectDomainesPro(SecteursPro domainesWeb) {
        domainesWeb.domaines().forEach(domainePro -> keyToDescriptifs.put(
                cleanup(domainePro.id()),
                new Descriptif(
                        domainePro.description(),
                        domainePro.url(),
                        "domainePro")
        ));

    }

    public void injectGroups(Map<String, String> groups) {
        Map<String, List<String>> groupsToItems = groups.keySet().stream().collect(Collectors.groupingBy(groups::get));
        Map<String, Descriptif> urlToDesc = new HashMap<>();


        Map<String, Set<String>> noDataGroupsToUrls = new HashMap<>();
        groupsToItems.forEach((s, strings) -> {
            Descriptif desc = keyToDescriptifs.get(s);
            if(desc == null || !desc.isViable()) {
                strings.forEach(s1 -> {
                    Descriptif desc1 = keyToDescriptifs.get(s1);
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
            List<Descriptif> descriptifs = urls.stream()
                    .map(urlToDesc::get)
                    .filter(Objects::nonNull)
                    .toList();
            Descriptif desc = mergeDescriptifs(descriptifs);
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
                            new Descriptif(
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
            Descriptif descriptif = keyToDescriptifs.get(key);
            keyToDescriptifs.put(key, Descriptif.setSummary(descriptif, summary));
        }
    }


    @Data
    public static final class Descriptif {
        private  String presentation;
        private  String poursuite;
        private  String metiers;
        private  @Nullable String url;
        private  String error;
        private  @NotNull String type;
        private  Set<String> multiUrls = new HashSet<>();
        private  String summary;
        private  String summaryFormation;
        private Map<String, String> mpsData = new HashMap<>();
        private boolean correctedUrl = false;

        public Descriptif(
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
            if(multiUrls != null) this.multiUrls.addAll(multiUrls);
            this.summary = summary;
            this.summaryFormation = summaryFormation;
            this.mpsData.putAll(mpsData);
        }


            public Descriptif(String presentation, String url, String type) {
                this(presentation, null, null, url, null, type, null, null, null, new HashMap<>());
            }

            public Descriptif(String summary, String summaryFormation, String url, String type) {
                this(null, null, null, url, null, type, null, summary, summaryFormation, new HashMap<>());
            }

            public Descriptif(Map<String, String> mpsData) {
                this(null, null, null, null, null, "summary", null, null, null, mpsData);
            }


            public static Descriptif addToDescriptif(String addendum, Descriptif o) {
                if (o == null) {
                    return new Descriptif(addendum, null, "addendum from null");
                }
                if (o.type.equals("error")) {
                    return new Descriptif(addendum, o.url, "addedndum from error");
                }
                return new Descriptif(
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

            public static Descriptif mergeDescriptifs(List<Descriptif> descriptifs) {
                if (descriptifs.isEmpty()) {
                    return new Descriptif("", null, null, null, "empty merge", "error", null, null, null, new HashMap<>());
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
                return new Descriptif(presentation, poursuite, metiers, urls.isEmpty() ? null : urls.get(0), null, "merge", urls, summaries, summariesFormations, new HashMap<>());
            }

            public static Descriptif setSummary(Descriptif o, String summary) {
                if (o == null) {
                    return new Descriptif(summary, null, "summary");
                } else {
                    return new Descriptif(
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

        public static Descriptif setSummaries(
                    Descriptif o,
                    String summaryFormation,
                    String summaryFiliere
            ) {
                if (o == null) {
                    return new Descriptif(summaryFormation, summaryFiliere, null, "summary");
                } else {
                    return new Descriptif(
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
                    b.append("<p>" + presentation + "</p>");
                }
                if (poursuite != null) {
                    b.append("<br/><br/>").append("<p>" + poursuite + "</p>");
                }
                if (metiers != null) {
                    b.append("<br/><br/>").append("<p>" + metiers + "</p>");
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
            var that = (Descriptif) obj;
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
    }

    public static Descriptif getError(String error, @NotNull String url) {
        return new Descriptif(null, null, null, url, error, "error", null, null, null, new HashMap<>());
    }

    public static Descriptif getDescriptif(List<String> psTextxs, @NotNull String url, @NotNull String type) {
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
        return new Descriptif(presentation.toString(), poursuite, metiers, url, null, type, null, null, null, new HashMap<>());
    }


}
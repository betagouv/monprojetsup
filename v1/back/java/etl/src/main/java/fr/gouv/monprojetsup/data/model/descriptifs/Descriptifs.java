package fr.gouv.monprojetsup.data.model.descriptifs;


import fr.gouv.monprojetsup.data.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.update.onisep.FichesMetierOnisep;
import fr.gouv.monprojetsup.data.update.onisep.SecteursPro;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.cleanup;
import static fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs.Descriptif.mergeDescriptifs;

public record Descriptifs(
    Map<String, Descriptif> keyToDescriptifs
) {

    public Descriptifs() {
        this(new HashMap<>());
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
        Map<String, List<String>> groupsToItems = groups.keySet().stream().collect(Collectors.groupingBy(k -> groups.get(k)));
        Map<String, Descriptif> urlToDesc = new HashMap<>();
        keyToDescriptifs.values().forEach(desc -> {
            if(desc.error == null || desc.error.isEmpty()) {
                urlToDesc.put(desc.url, desc);
            }
        });
        Map<String, Set<String>> missingGroupsToUrls = new HashMap<>();
        groupsToItems.forEach((s, strings) -> {
            Descriptif desc = keyToDescriptifs.get(s);
            if(desc == null || (desc.error != null && !desc.error.isEmpty())) {
                strings.forEach(s1 -> {
                    Descriptif desc1 = keyToDescriptifs.get(s1);
                    if(desc1 != null && (desc1.error == null || desc1.error.isEmpty())) {
                        missingGroupsToUrls.computeIfAbsent(s, z -> new HashSet<>()).add(desc1.url);
                    }
                });
            }
        });

        missingGroupsToUrls.forEach((s, urls) -> {
            List<Descriptif> descriptifs = urls.stream()
                    .map(url -> urlToDesc.get(url))
                    .filter(d -> d!= null)
                    .collect(Collectors.toList());
            Descriptif desc = mergeDescriptifs(descriptifs);
            keyToDescriptifs.put(s, desc);
        });

    }

    public void injectLas(Map<String, String> lasCorrespondance) {
        lasCorrespondance.forEach((lasKey,genKey) -> {
            keyToDescriptifs.computeIfAbsent(
                    lasKey,
                    z-> keyToDescriptifs.get(genKey)
            );
        });
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


    public record Descriptif(
            String presentation,
            String poursuite,
            String metiers,
            @NotNull String url,
            String error,
            @NotNull String type,

            List<String> multiUrls,

            String summary
    ) {


        public Descriptif(String presentation, String url, String type) {
            this(presentation, null, null, url, null, type, null, null);
        }

        public static Descriptif addToDescriptif(String addendum, Descriptif o) {
            if(o == null) {
                return new Descriptif(addendum, null, "addendum from null");
            }
            if(o.type.equals("error")) {
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
                    o.summary
            );
        }

        public static Descriptif mergeDescriptifs(List<Descriptif> descriptifs) {
            if(descriptifs.isEmpty()) {
                return new Descriptif("",null,null,null,"empty merge", "error", null, null);
            } else if(descriptifs.size() == 1) {
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
            List<String> urls = descriptifs.stream().map(d -> d.url).filter(u -> u != null && !u.isEmpty()).distinct().collect(Collectors.toList());
            return new Descriptif(presentation, poursuite, metiers, urls.isEmpty() ? null : urls.get(0), null, "merge", urls, summaries);
        }

        public static Descriptif setSummary(Descriptif o, String summary) {
            if(o == null) {
                return new Descriptif(summary,null, "summary");
            } else {
                return new Descriptif(
                        o.presentation,
                        o.poursuite,
                        o.metiers,
                        o.url,
                        o.error,
                        o.type,
                        o.multiUrls,
                        summary
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
            if(summary != null) return summary;
            StringBuilder b = new StringBuilder();
            if(presentation != null) {
                b.append("<p>" + presentation + "</p>");
            }
            if(poursuite != null) {
                b.append("<br/><br/>").append("<p>" + poursuite+ "</p>");
            }
            if(metiers != null) {
                b.append("<br/><br/>").append("<p>" + metiers+ "</p>");
            }
            String result = b.toString();
            result.replaceAll("<h3>", "<br>");
            result.replaceAll("</h3>", "<br/>");
            return result;
        }
    }

    public static Descriptif getError(String error, @NotNull String url) {
        return new Descriptif(null, null, null, url, error, "error", null, null);
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
        return new Descriptif(presentation.toString(), poursuite, metiers, url, null, type, null, null);
    }

    public static Descriptifs.Descriptif getDescriptif(String html, @NotNull String url, @NotNull String type) {
        return new Descriptifs.Descriptif(html, null, null, url, null, type, null, null);
    }


}
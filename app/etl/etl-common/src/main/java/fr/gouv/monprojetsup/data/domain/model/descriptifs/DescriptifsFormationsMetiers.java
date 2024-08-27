package fr.gouv.monprojetsup.data.domain.model.descriptifs;


import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.Helpers;
import fr.gouv.monprojetsup.data.domain.model.metiers.MetiersScrapped;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.Constants.cleanup;

public record DescriptifsFormationsMetiers(

        //indexed by formation
    Map<String, DescriptifFormation> keyToDescriptifs
) {

    public DescriptifsFormationsMetiers() {
        this(new HashMap<>());
    }

    public static Link toAvenirs(String uri, String label) {
        if(uri == null) return null;
        uri =  uri
                .replace(
                "www.terminales2022-2023.fr","www.onisep.fr")
                .replace(Constants.ONISEP_URL1, Constants.EXPLORER_AVENIRS_URL)
                .replace(Constants.ONISEP_URL2, Constants.EXPLORER_AVENIRS_URL)
                ;
        return new Link(label, uri);
    }

    public static String toParcoursupCarteUrl(@NotNull Collection<String> psupIds) {
        return Constants.CARTE_PARCOURSUP_PREFIX_URI
                + psupIds.stream().distinct()
                .map(fl -> fl + "x").collect(Collectors.joining("%20"));
    }


    /*
    */

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
                        noDataGroupsToUrls.computeIfAbsent(s, z -> new HashSet<>()).add(desc1.getUrl());
                    }
                });
            }
        });

        keyToDescriptifs.values().forEach(desc -> {
            if(desc.isViable()) {
                urlToDesc.put(desc.getUrl(), desc);
            }
        });

        noDataGroupsToUrls.forEach((s, urls) -> {
            List<DescriptifFormation> descriptifs = urls.stream()
                    .map(urlToDesc::get)
                    .filter(Objects::nonNull)
                    .toList();
            DescriptifFormation desc = DescriptifFormation.mergeDescriptifs(descriptifs);
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

    public void push(DescriptifsFormationsMetiers descriptifs) {
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
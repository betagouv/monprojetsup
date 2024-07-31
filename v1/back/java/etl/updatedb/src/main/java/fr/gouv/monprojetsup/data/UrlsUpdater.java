package fr.gouv.monprojetsup.data;

import fr.gouv.monprojetsup.suggestions.domain.Constants;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs.DescriptifsFormations;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.OnisepData;
import lombok.val;

import java.util.*;

public class UrlsUpdater {
    private static void addUrl(String key, String uri, String label, Map<String, List<DescriptifsFormations.Link>> urls) {
        if (uri != null && !uri.isEmpty()) {
            if (uri.contains("www.terminales2022-2023.fr")) {
                //deprecated uri
                return;
            }
            val url = DescriptifsFormations.toAvenirs(uri, label);
            val liste = urls.computeIfAbsent(Constants.cleanup(key), z -> new ArrayList<>());
            if (!liste.contains(url)) liste.add(url);
        }
    }

    public static Map<String, List<DescriptifsFormations.Link>> updateUrls(
            OnisepData onisepData,
            Map<String, DescriptifsFormations.Link> links,
            Set<String> lasMpsKeys,
            DescriptifsFormations descriptifs,
            Map<String,String> psupKeytoMpsKey
    ) {
        val urls = new HashMap<String, List<DescriptifsFormations.Link>>();
        onisepData.metiers().metiers().forEach((s, metier) -> addUrl(
                s,
                "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/" + s.replace('_', '.'),
                metier.lib(), urls
        ));

        for (val entry : links.entrySet()) {
            addUrl(entry.getKey(), entry.getValue().uri(), entry.getValue().label(), urls);
        }
        onisepData.metiers().metiers().values().forEach(metier -> {
            if (metier.urlRome() != null && !metier.urlRome().isEmpty()) {
                addUrl(metier.id(), metier.urlRome(), metier.libRome(), urls);
            }
        });
        onisepData.domainesWeb().domaines().forEach(domainePro
                -> addUrl(Constants.cleanup(domainePro.id()), domainePro.url(), domainePro.nom(), urls)
        );
        descriptifs.keyToDescriptifs().forEach((key, descriptif) -> {
            if (descriptif.isCorrectedUrl()) {
                urls.remove(Constants.cleanup(key));
            }
            Collection<String> multiUrls = descriptif.multiUrls();
            if (multiUrls != null) {
                multiUrls.forEach(url2 -> addUrl(key, url2, url2, urls));
            }
        });
        addUrl(Constants.gFlCodToFrontId(Constants.PASS_FL_COD), Constants.URL_ARTICLE_PAS_LAS, "Les études de santé", urls);
        urls.entrySet().forEach(e -> {
            if(lasMpsKeys.contains(e.getKey())) {
                val keyLas = e.getKey();
                addUrl(keyLas, Constants.URL_ARTICLE_PAS_LAS, "Les études de santé", urls);
            }
        });
        psupKeytoMpsKey.forEach((psupKey, mpsKey) -> {
            urls.getOrDefault(psupKey, List.of()).forEach(s -> addUrl(mpsKey, s.uri(), s.label(), urls));
        });

        return urls;
    }
}

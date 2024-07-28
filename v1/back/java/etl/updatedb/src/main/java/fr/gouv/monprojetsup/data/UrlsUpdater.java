package fr.gouv.monprojetsup.data;

import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.onisep.OnisepData;
import lombok.val;

import java.util.*;

public class UrlsUpdater {
    private static void addUrl(String key, String uri, String label, Map<String, List<Descriptifs.Link>> urls) {
        if (uri != null && !uri.isEmpty()) {
            if (uri.contains("www.terminales2022-2023.fr")) {
                //deprecated uri
                return;
            }
            val url = Descriptifs.toAvenirs(uri, label);
            val liste = urls.computeIfAbsent(Constants.cleanup(key), z -> new ArrayList<>());
            if (!liste.contains(url)) liste.add(url);
        }
    }

    public static Map<String, List<Descriptifs.Link>> updateUrls(
            OnisepData onisepData,
            Map<String, Descriptifs.Link> links,
            Map<String, String> lasCorrespondance,
            Descriptifs descriptifs
    ) {
        val urls = new HashMap<String, List<Descriptifs.Link>>();
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
        lasCorrespondance.forEach((keyLas, keyGen) -> {
            addUrl(keyLas, Constants.URL_ARTICLE_PAS_LAS, "Les études de santé", urls);
            urls.getOrDefault(keyGen, List.of()).forEach(s -> addUrl(keyLas, s.uri(), s.label(), urls));
        });
        return urls;
    }
}

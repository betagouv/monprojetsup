package fr.gouv.monprojetsup.data.infrastructure.model.descriptifs;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.infrastructure.onisep.OnisepData;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UrlsUpdater {
    private static void addUrl(String key, String uri, String label, Map<String, List<DescriptifsFormationsMetiers.Link>> urls) {
        if (uri != null && !uri.isEmpty()) {
            if (uri.contains("www.terminales2022-2023.fr")) {
                //deprecated uri
                return;
            }
            val url = DescriptifsFormationsMetiers.toAvenirs(uri, label);
            val liste = urls.computeIfAbsent(Constants.cleanup(key), z -> new ArrayList<>());
            if (!liste.contains(url)) liste.add(url);
        }
    }

    public static @NotNull Map<String, @NotNull List<DescriptifsFormationsMetiers.Link>> updateUrls(
            OnisepData onisepData,
            Map<String, DescriptifsFormationsMetiers.Link> links,
            Set<String> lasMpsKeys,
            DescriptifsFormationsMetiers descriptifs,
            Map<String,String> psupKeytoMpsKey
    ) {
        val urls = new HashMap<String, List<DescriptifsFormationsMetiers.Link>>();
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

package fr.gouv.monprojetsup.data.model.liens;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.model.descriptifs.DescriptifsFormationsMetiers;
import fr.gouv.monprojetsup.data.model.onisep.OnisepData;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class UrlsUpdater {



    private static void addUrl(String key, String uri, String label, Map<String, List<DescriptifsFormationsMetiers.Link>> urls) {
        if (uri != null && !uri.isEmpty()) {

            val liste = urls.computeIfAbsent(Constants.cleanup(key), z -> new ArrayList<>());
            val firstWithSameUri = liste.stream().filter(s -> s.uri().equals(uri)).findFirst();

            label = capitalizeFirstLetter(label).replace(".", " ").trim();
            if(firstWithSameUri.isEmpty()) {
                if (uri.contains("francetravail") && !label.startsWith("France Travail")) {
                    label = "France Travail - " + label;
                } else if ((uri.contains("onisep") || uri.contains("avenirs"))
                        && !label.contains("Onisep")
                ) {
                    //label = "Onisep - " + label;
                }
            }

            val url = DescriptifsFormationsMetiers.toAvenirs(uri, label);

            if(firstWithSameUri.isEmpty()) {
                liste.add(url);
            } else if(!firstWithSameUri.get().label().contains(url.label())){
                val newLabel = firstWithSameUri.get().label() + " / " + url.label();
                liste.remove(firstWithSameUri.get());
                liste.add(new DescriptifsFormationsMetiers.Link(newLabel, url.uri()));
            }
        }
    }


    public static String capitalizeFirstLetter(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return sentence;
        }
        // Trim leading and trailing spaces
        sentence = sentence.trim();
        // Capitalize the first character and concatenate it with the rest of the string
        return sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
    }

    public static @NotNull Map<String, @NotNull List<DescriptifsFormationsMetiers.Link>> updateUrls(
            @NotNull OnisepData onisepData,
            @NotNull Map<String, @NotNull String> links,
            @NotNull Map<String, @NotNull String> lasToGeneric,
            @NotNull DescriptifsFormationsMetiers descriptifs,
            @NotNull Map<String,@NotNull String> psupKeytoMpsKey,
            @NotNull List<String> mpsIds,
            @NotNull Map<String, @NotNull String> labels
    ) {
        //metiers
        val urls = new HashMap<String, List<DescriptifsFormationsMetiers.Link>>();
        onisepData.metiersIdeo().forEach(metier -> {
            addUrl(
                    metier.ideo(),
                    Constants.NEW_ONISEP_METIERS_SLUG_PREFIX + metier.ideo().replace('_', '.'),
                    metier.lib(), urls
            );
            if (metier.urlRome() != null && !metier.urlRome().isEmpty()) {
                addUrl(metier.ideo(), metier.urlRome(), metier.libRome(), urls);
            }
            metier.urls().forEach(url -> addUrl(metier.ideo(), url.valeur(), url.commentaire(), urls));
        });

        for (val entry : links.entrySet()) {
            addUrl(entry.getKey(), entry.getValue(), labels.getOrDefault(entry.getKey(), entry.getValue()), urls);
        }

        //formations
        descriptifs.keyToDescriptifs().forEach((key, descriptif) -> {
            val cleanedupKey = Constants.cleanup(key);
            if (descriptif.isCorrectedUrl()) {
                urls.remove(cleanedupKey);
            }
            Collection<String> multiUrls = descriptif.multiUrls();
            if (multiUrls != null && !multiUrls.isEmpty()) {
                multiUrls.forEach(url2 -> addUrl(cleanedupKey, url2, url2, urls));
            }
        });

        psupKeytoMpsKey.forEach(
                (psupKey, mpsKey) -> {
                    val l = new ArrayList<>(urls.getOrDefault(psupKey, List.of()));
                    l.forEach(s -> addUrl(mpsKey, s.uri(), s.label(), urls));
                }
                );

        /* traitement spécifique études de santé */
        addUrl(Constants.gFlCodToMpsId(Constants.PASS_FL_COD), Constants.URL_ARTICLE_PAS_LAS, Constants.LABEL_ARTICLE_PAS_LAS, urls);
        lasToGeneric.forEach((keyLas, keyGeneric) -> {
            //ajout article études de santé
            addUrl(keyLas, Constants.URL_ARTICLE_PAS_LAS, Constants.LABEL_ARTICLE_PAS_LAS, urls);
            urls.getOrDefault(keyGeneric, List.of()).forEach(s -> addUrl(keyLas, s.uri(), s.label(), urls));
        });

        val mpsIdToPsupIds = psupKeytoMpsKey.entrySet().stream().collect(
                Collectors.groupingBy(Map.Entry::getValue,
                        Collectors.mapping(Map.Entry::getKey,
                                Collectors.toList()))
        );
        mpsIds.forEach(mpsId -> {
            val psupIds = mpsIdToPsupIds.getOrDefault(mpsId, List.of(mpsId));
            addUrl(mpsId, DescriptifsFormationsMetiers.toParcoursupCarteUrl(psupIds), "L'offre de formation", urls);
        });

        return urls;
    }

    private UrlsUpdater() {}
}

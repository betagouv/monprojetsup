package fr.gouv.monprojetsup.data.model.liens;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.model.descriptifs.DescriptifsFormationsMetiers;
import fr.gouv.monprojetsup.data.model.metiers.MetierIdeo;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.LABEL_ARTICLE_PAS_LAS;

public class UrlsUpdater {


    private static final String DESCRIPTIFS_FORMATIONS_METIERS = "DescriptifsFormationsMetiers";
    private static final String METIERS_IDEO_DU_SUP = "MetiersIdeoDuSup";
    public static final String IDEO_HOTLINE = "IdeoHotline";
    private static final String AJOUTS_MPS = "AjoutsMps";
    private static final String LAS_TO_GENERIC = " LasToGeneric" ;
    public static final String CARTE_PSUP = "CarteParcoursup";
    private static final String PREFIX_ONISEP = "Fiche détaillée Onisep - ";
    private static final String PREFIX_FT = "France Travail - ";

    private static void addUrl(String key, String uri, String label, String source, Map<String, List<DescriptifsFormationsMetiers.Link>> urls) {
        if (uri != null && !uri.isEmpty()) {

            uri = uri.trim();
            if(uri.contains(" ")) {
                val index = uri.lastIndexOf(' ');
                label = uri.substring(0, index);
                uri = uri.substring(index+1);
            }

            val liste = urls.computeIfAbsent(Constants.cleanup(key), z -> new ArrayList<>());
            String finalUri = uri;
            val firstWithSameUri = liste.stream().filter(s -> s.uri().equals(finalUri)).findFirst();

            label = capitalizeFirstLetter(label).replace(".", " ").trim();
            if(firstWithSameUri.isEmpty()) {
                if (uri.contains("francetravail") && !label.startsWith("France Travail")) {
                    label = PREFIX_FT + label;
                } else if ((uri.contains("onisep") || uri.contains("avenirs"))
                        && !label.contains("Onisep")
                ) {
                    label = PREFIX_ONISEP + label;
                }
            }

            label = label.replace(" - en apprentissage", "");
            val url = DescriptifsFormationsMetiers.toAvenirs(uri, label, source);

            if(firstWithSameUri.isEmpty()) {
                liste.add(url);
            } else if(!firstWithSameUri.get().label().contains(url.label())){
                //concaténation des différents labels aboutissant à la même source
                val gcp = greatestCommonPrefix(firstWithSameUri.get().label(), url.label());
                val newLabel =
                        firstWithSameUri.get().label()
                        + " / "
                        + url.label().substring(gcp)
                        ;
                liste.remove(firstWithSameUri.get());
                liste.add(new DescriptifsFormationsMetiers.Link(newLabel, url.uri(), firstWithSameUri.get().source()));
            }
        }
    }

    private static int greatestCommonPrefix(String label, String label1) {
        int i = 0;
        while(i < label.length() && i < label1.length() && label.charAt(i) == label1.charAt(i)) {
            i++;
        }
        return i;
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
            @NotNull List<MetierIdeo> metiers,
            @NotNull Map<String, @NotNull String> liensIdeoHotline,
            @NotNull Map<String, @NotNull String> lasToGeneric,
            @NotNull Map<String,@NotNull String> psupKeytoMpsKey,
            @NotNull List<String> mpsIds,
            @NotNull Map<String, @NotNull String> labels,
            @NotNull Map<String, @NotNull Collection<String>> liensAIgnorer,
            @NotNull Map<String, @NotNull Collection<String>> extraUrls
    ) {
        //metiers
        val urls = new HashMap<String, List<DescriptifsFormationsMetiers.Link>>();
        metiers.forEach(metier -> {
            addUrl(
                    metier.ideo(),
                    Constants.NEW_ONISEP_METIERS_SLUG_PREFIX + metier.ideo().replace('_', '.'),
                    metier.lib(),
                    DESCRIPTIFS_FORMATIONS_METIERS,
                    urls
            );
            if (metier.urlRome() != null && !metier.urlRome().isEmpty()) {
                addUrl(metier.ideo(), metier.urlRome(), metier.libRome(), METIERS_IDEO_DU_SUP, urls);
            }
            metier.urls().forEach(url -> addUrl(metier.ideo(), url.valeur(), url.commentaire(), METIERS_IDEO_DU_SUP, urls));
        });

        val aIgnorer = liensAIgnorer.entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(s -> !s.isBlank()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        for (val entry : liensIdeoHotline.entrySet()) {
            if(!aIgnorer.contains(entry.getKey())) {
                addUrl(entry.getKey(), entry.getValue(), labels.getOrDefault(entry.getKey(), entry.getValue()), IDEO_HOTLINE, urls);
            }
        }

        extraUrls.forEach((key, extraLinks) -> {
            val cleanupExtraLinks = extraLinks.stream().map(String::trim).filter(s -> !s.isBlank()).toList();
            if(!cleanupExtraLinks.isEmpty()) {
                val cleanedupKey = Constants.cleanup(key);
                cleanupExtraLinks.forEach(e -> addUrl(cleanedupKey, e, getLabel(labels, cleanedupKey, e), AJOUTS_MPS, urls));
            }
        });

        psupKeytoMpsKey.forEach(
                (psupKey, mpsKey) -> {
                    val l = new ArrayList<>(urls.getOrDefault(psupKey, List.of()));
                    l.forEach(s -> addUrl(mpsKey, s.uri(), s.label(), s.source(), urls));
                }
                );

        /* traitement spécifique études de santé */
        addUrl(
                Constants.gFlCodToMpsId(Constants.PASS_FL_COD),
                Constants.URL_ARTICLE_PAS_LAS,
                LABEL_ARTICLE_PAS_LAS,
                LABEL_ARTICLE_PAS_LAS,
                urls
        );
        lasToGeneric.forEach((keyLas, keyGeneric) -> {
            //ajout article études de santé
            addUrl(
                    keyLas,
                    Constants.URL_ARTICLE_PAS_LAS,
                    LABEL_ARTICLE_PAS_LAS,
                    LABEL_ARTICLE_PAS_LAS,
                    urls
            );
            urls.getOrDefault(keyGeneric, List.of()).forEach(s ->
                    addUrl(
                            keyLas,
                            s.uri(),
                            s.label(),
                            s.source() + LAS_TO_GENERIC, urls
                    )
            );
        });

        val mpsIdToPsupIds = psupKeytoMpsKey.entrySet().stream().collect(
                Collectors.groupingBy(Map.Entry::getValue,
                        Collectors.mapping(Map.Entry::getKey,
                                Collectors.toList()))
        );
        mpsIds.forEach(mpsId -> {
            val psupIds = mpsIdToPsupIds.getOrDefault(mpsId, List.of(mpsId));
            addUrl(mpsId, DescriptifsFormationsMetiers.toParcoursupCarteUrl(psupIds), "L'offre de formation", CARTE_PSUP, urls);
        });

        return urls;
    }

    private static String getLabel(@NotNull Map<String, String> labels, String cleanedupKey, String url) {
        String label = labels.getOrDefault(cleanedupKey, url);
        return label;
    }

    private UrlsUpdater() {}
}

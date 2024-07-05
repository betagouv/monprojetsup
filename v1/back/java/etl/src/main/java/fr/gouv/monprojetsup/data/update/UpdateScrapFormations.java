package fr.gouv.monprojetsup.data.update;

import com.google.gson.GsonBuilder;
import com.opencsv.exceptions.CsvValidationException;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UpdateScrapFormations {

    private static final Logger LOGGER = Logger.getLogger(UpdateScrapFormations.class.getSimpleName());

    public static void main(String[] args) throws IOException, CsvValidationException {

        Descriptifs descriptifs;
        try {
            descriptifs = Serialisation.fromJsonFile(
                    DataSources.getSourceDataFilePath(DataSources.ONISEP_DESCRIPTIFS_FORMATIONS_PATH),
                    Descriptifs.class
            );
            Serialisation.toJsonFile(
                    DataSources.getSourceDataFilePath(DataSources.ONISEP_DESCRIPTIFS_FORMATIONS_PATH) + ".backup",
                    descriptifs,
                    true
            );
        } catch (Exception e) {
            descriptifs = new Descriptifs();
        }

        Map<String, Integer> statsOk = descriptifs.keyToDescriptifs().values().stream()
                .filter(d -> !d.type().equals("error"))
                .collect(Collectors.groupingBy(
                        d -> d.type()
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        d -> d.getKey(),
                        d -> d.getValue().size()
                ));
        Map<String, Integer> statsKo = descriptifs.keyToDescriptifs().values().stream()
                .filter(d -> d.type().equals("error") && d.error() != null)
                .collect(Collectors.groupingBy(d -> d.error()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        d -> d.getKey(),
                        d -> d.getValue().size()
                ));

        LOGGER.info("Loaded descriptifs");
        LOGGER.info(new GsonBuilder().setPrettyPrinting().create().toJson(statsOk));
        LOGGER.info(new GsonBuilder().setPrettyPrinting().create().toJson(statsKo));

        Map<String, Descriptifs.Descriptif> descKo = new HashMap<>(descriptifs.keyToDescriptifs());
        descKo.keySet().retainAll(
                descriptifs.keyToDescriptifs().entrySet().stream()
                        .filter(d -> d.getValue().type().equals("error"))
                        .map(e -> e.getKey())
                        .toList()
        );
        Serialisation.toJsonFile("liensOnisepRedirigesPageAccueil.json",
                descKo.entrySet().stream()
                        .filter(e -> e.getValue().error() != null && e.getValue().error().startsWith("redirected"))
                        .collect(Collectors.toMap(
                                e -> e.getKey(),
                                e-> e.getValue()
                                )
                ),
                true);
        Serialisation.toJsonFile("liensOnisepRechercheSansArticleSpecificique.json",
                descKo.entrySet().stream()
                        .filter(e -> e.getValue().error() != null && e.getValue().error().startsWith("recherche"))
                        .collect(Collectors.toMap(
                                        e -> e.getKey(),
                                        e-> e.getValue()
                                )
                        ),
                true);
        //www.terminales2021-2022.fr
        Serialisation.toJsonFile("liensOnisepLienCasse.json",
                descKo.entrySet().stream()
                        .filter(e -> e.getValue().error() != null && e.getValue().error().startsWith("www.terminales2021-2022.fr"))
                        .collect(Collectors.toMap(
                                        e -> e.getKey(),
                                        e-> e.getValue()
                                )
                        ),
                true);


        testDomHasNotChanged();

        ServerData.load();

        //pour chaque url du type terminales2022
        for (Map.Entry<String, String> entry : ServerData.statistiques.liensOnisep.entrySet()) {
            String flKey = entry.getKey();

            //si déjà connu et pas d'erreur et correcte alors pas la peine de réessayer
            Descriptifs.Descriptif known = descriptifs.keyToDescriptifs().get(flKey);
            if (known != null
                    && known.error() == null
                    && !known.type().equals("error")
                    && known.presentation() != null
                    && !known.presentation().isEmpty()
                    || known != null && known.isRedirectedToDefaultPage()
                    || known != null && known.isOutDated()
            //|| known != null && known.isRecherche()
            )
            {
                continue;
            }

            String url = entry.getValue();
            LOGGER.info("Processing " + url);
            Descriptifs.Descriptif desc = getDescriptifFromUrl(url);
            boolean hasError = true;
            if (desc == null) {
                desc = Descriptifs.getError("getDescriptifFromUrl returned null", url);
                //LOGGER.info("Fail");
            } else if (desc.error() == null && desc.presentation() == null) {
                desc = Descriptifs.getError("getDescriptifFromUrl returned null presentation", url);
            } else if (desc.error() == null && desc.presentation().isEmpty()) {
                desc = Descriptifs.getError("getDescriptifFromUrl returned empty presentation", url);
            } else {
                hasError = desc.hasError();
            }
            if (hasError) {
                LOGGER.info("Failed: " + desc.error());
            } else {
                LOGGER.info("Success");
            }
            descriptifs.keyToDescriptifs().put(
                    flKey,
                    desc
            );
            Serialisation.toJsonFile(DataSources.getSourceDataFilePath(DataSources.ONISEP_DESCRIPTIFS_FORMATIONS_PATH), descriptifs, true);
        }
    }

    private static @Nullable Descriptifs.Descriptif getDescriptifFromUrl(String url) {
        if(url.startsWith("www")) {
            url = "https://" + url;
        }
        String unHtmlizedUrl = Jsoup.parse(url).text();
        if(!url.equals(unHtmlizedUrl)) {
            int i = 0;
        }
        try {

            Document doc = Jsoup.connect(unHtmlizedUrl).get();

            boolean isRedirected = doc.getElementById("jexplore-les-possibles") != null;
            if (isRedirected) {
                return Descriptifs.getError("redirected to https://www.terminales2022-2023.fr/", url);
            }

            List<String> selectors = List.of(
                    "article #objectifs-formation .editor .ezrichtext-field",
                    ".introduction-text",
                    "#formation-description-full",
                    "#acces-formation .ezrichtext-field",
                    "#type-de-formation",
                    "#poursuites-etudes"
            );
            for (String sel : selectors) {
                Elements elts = doc.select(sel);
                List<String> psTextxs = elts.stream().map(Element::html).toList();
                if (!psTextxs.isEmpty()) {
                    return Descriptifs.getDescriptif(psTextxs, url, sel);
                }
            }
            boolean  isRecherche = url.contains("recherche?");
            if(isRecherche) {
                List<String> searchSelectors = List.of(
                        //mesures phsysiques
                        ".search-ui-result-inner .search-grouped-block a",

                        //failed try for getting https://www.onisep.fr/recherche?context=formation&text=BUT%20Génie%20chimique%20génie%20des%20procédés
                        //failed beacuse the search results seem to be dynamic
                        //we would need to emaulate Firefox https://stackoverflow.com/questions/36111805/how-to-make-jsoup-wait-for-the-complete-pageskip-a-progress-page-to-load
                        "search-ui-result-outer table a"
                );
                for (String selector : searchSelectors) {
                    Element elt = doc.select(selector).first();
                    if (elt != null) {
                        URL netUrl = new URL(unHtmlizedUrl);
                        URL subUrl = new URL(
                                netUrl.getProtocol(),
                                netUrl.getHost(),
                                elt.attr("href")
                        );
                        String subUrlStr = subUrl.toExternalForm();
                        return getDescriptifFromUrl(subUrlStr);
                    }
                }
                if(unHtmlizedUrl.contains("¬_query_type=true")) {
                    return getDescriptifFromUrl(unHtmlizedUrl.replace("¬_query_type=true", ""));
                }
                return Descriptifs.getError("recherche, pas d'article spécifique", url);
            }
            return null;
        } catch (Exception e) {
            Descriptifs.Descriptif desc = Descriptifs.getError(e.getMessage(), url);
            return desc;
        }
    }


    /**
     * Quelques tests pour vérifie rla connexion internet et que le dom n' pas trop changé
     *
     * A compléter pour être plus exhasutif sur les différents cas listés dans le gros if
     * à la fin de getDescriptifFromUrl
     *
     * avec un test par cas
     * @throws IOException
     */
    private static void testDomHasNotChanged() throws IOException {
                /*  */
        //http://www.terminales2022-2023.fr/http/redirection/formation/slug/FOR.519
        Descriptifs.Descriptif test3 = getDescriptifFromUrl("http://www.terminales2022-2023.fr/http/redirection/formation/slug/FOR.519");
        assert test3.presentation() != null;

        Descriptifs.Descriptif test1 = getDescriptifFromUrl("https://www.terminales2022-2023.fr/ressources/univers-formation/formations/Post-bac/bts-metiers-de-la-chimie");
        assert test1.presentation() != null;

        Descriptifs.Descriptif test2 = getDescriptifFromUrl("https://www.terminales2022-2023.fr/ressources/univers-formation/formations/Post-bac/licence-mention-informatique");
        assert test2.metiers() != null;

    }
}

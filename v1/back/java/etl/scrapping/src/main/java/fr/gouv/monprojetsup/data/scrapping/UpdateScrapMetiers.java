package fr.gouv.monprojetsup.data.scrapping;

import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.model.disciplines.LiensMetiersThemesOnisep;
import fr.gouv.monprojetsup.data.model.metiers.Metiers;
import fr.gouv.monprojetsup.data.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@SpringBootApplication
public class UpdateScrapMetiers implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger(UpdateScrapMetiers.class.getSimpleName());
    private final DataSources sources;

    @Autowired
    public UpdateScrapMetiers(DataSources sources) {
        this.sources = sources;
    }

    @Override
    public void run(String... args) throws Exception {
        //charger les liens terminales 22/23

        String filename = sources.getSourceDataFilePath(DataSources.ONISEP_DESCRIPTIFS_METIERS_PATH);

        //ONISEP_DESCRIPTIFS_METIERS_PATH
        MetiersScrapped metiers;
        try {
            metiers = Serialisation.fromJsonFile(filename, MetiersScrapped.class);
            Serialisation.toJsonFile(filename + ".backup", metiers, true);
        } catch (Exception e) {
            metiers = new MetiersScrapped();
        }

        LiensMetiersThemesOnisep liens = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.METIERS_THEMATIQUES_PAIRES_PATH),
                LiensMetiersThemesOnisep.class
        );
        Set<String> keys = liens.contents().content().stream().map(LiensMetiersThemesOnisep.Content::keyMetier).collect(Collectors.toSet());

        for (String key : keys) {
            MetiersScrapped.MetierScrap known = metiers.metiers().get(key);
            if(known != null && !known.isError()) {
                continue;
            }

            MetiersScrapped.MetierScrap desc = getMetierFromUrl(key);

            metiers.metiers().put(key, desc);

            Serialisation.toJsonFile(filename, metiers, true);

        }
    }

    private static MetiersScrapped.MetierScrap getMetierFromUrl(String key) {
        String url = Metiers.Metier.ONISEP_BASE_URL + key;
        LOGGER.info("Processing " + url);

        if (url.startsWith("www")) {
            url = "https://" + url;
        }

        List<String> warnings = new ArrayList<>();
        String unHtmlizedUrl = Jsoup.parse(url).text();
        try {

            Document doc = Jsoup.connect(unHtmlizedUrl).get();

            Elements elts = doc.select("article");

            if(elts.isEmpty()) {
                return MetiersScrapped.MetierScrap.getError("no article div", warnings);

            }
            String nom = null;
            String accroche = null;
            String metier = null;
            String etudes = null;
            String emploi = null;
            String salaire = null;
            String competences = null;
            String ou = null;

            for (Element elt : elts) {
                boolean used = false;
                for (Element element : elt.select("h1.background-decoration")) {
                    used = true;
                    if(nom != null) warnings.add("several noms");
                    nom = element.html();
                }
                for (Element element : elt.select("div.text-xl")) {
                    used = true;
                    if(accroche != null) warnings.add("several accroches");
                    accroche = element.html();
                }
                for (Element element : elt.select(".border-brand")) {
                    Elements elements1 = element.select(".editor .ezrichtext-field");
                    if (elements1.isEmpty()) {
                        warnings.add(".border-brand with empty content");
                        continue;
                    }
                    if (elements1.size() > 1) {
                        warnings.add(".border-brand with several contents");
                    }
                    String content = elements1.first().html();
                    Elements elements2 = element.select("h2");
                    if (elements2.isEmpty()) {
                        warnings.add(".border-brand with no h2");
                        continue;
                    }
                    if (elements2.size() > 1)
                        warnings.add(".border-brand with several h2");
                    String title = elements2.first().html();
                    if (title.toLowerCase().contains("métier")) {
                        metier = content;
                        used = true;
                    } else if (title.toLowerCase().contains("études")) {
                        etudes = content;
                        used = true;
                    } else if (title.toLowerCase().contains("compétences")) {
                        competences = content;
                        used = true;
                    } else if (title.toLowerCase().contains("exercer")) {
                        ou = content;
                        used = true;
                    } else if (title.toLowerCase().contains("ploi et secteur")) {
                        emploi = content;
                        used = true;
                    } else if (title.toLowerCase().contains("salaire")) {
                        salaire = content;
                        used = true;
                    } else {
                        warnings.add("titre non exploité: " + title);
                    }
                }
                if(!used) {
                    warnings.add("article unused");
                }
            }
            if (metier == null) {
                return MetiersScrapped.MetierScrap.getError("pas de metier", warnings);
            }
            return MetiersScrapped.MetierScrap.get(key, url, nom, accroche, metier, etudes, emploi, competences, ou, salaire, warnings);
        } catch (Exception e) {
            return MetiersScrapped.MetierScrap.getError(e, warnings);
        }
    }

}

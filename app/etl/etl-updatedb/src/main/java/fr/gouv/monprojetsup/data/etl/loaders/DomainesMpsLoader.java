package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.onisep.DomainesMps;
import fr.gouv.monprojetsup.data.domain.model.onisep.SousDomaineWeb;
import fr.gouv.monprojetsup.data.domain.model.thematiques.CategorieThematiques;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.etl.loaders.OnisepDataLoader.loadDomainesSousDomaines;

public class DomainesMpsLoader {


    //to remove when data fixed
    private static final boolean FORCE_ONISEP_DOMAINES = true;

    public static @NotNull List<@NotNull CategorieThematiques> loadDomainesMps(DataSources sources) throws Exception {

        val groupes = new HashMap<String, CategorieThematiques>();
        val categories = new ArrayList<CategorieThematiques>();

        var groupe = "";
        var emojig = "";

        val listStringStringMap = CsvTools.readCSV(
                sources.getSourceDataFilePath(DataSources.DOMAINES_MPS_PATH),
                '\t');

        for (val stringStringMap : listStringStringMap)
        {
            val id = stringStringMap.getOrDefault("id","");
            if(id.isEmpty()) continue;
            val regroupement = stringStringMap.getOrDefault("regroupement","").trim();
            if (!regroupement.isEmpty()) {
                groupe = regroupement;
                val emojiGroupe = stringStringMap.getOrDefault("Emoji","");
                if (!emojiGroupe.isEmpty()) {
                    emojig = emojiGroupe;
                } else {
                    throw new RuntimeException("Groupe " + groupe + " sans emoji dans " + DataSources.DOMAINES_MPS_PATH);
                }
            }
            val emoji = stringStringMap.getOrDefault("Emojis", "").trim();
            val label = stringStringMap.getOrDefault("label", "").trim();
            if (groupe.isEmpty()) throw new RuntimeException("Groupe vide dans " + DataSources.DOMAINES_MPS_PATH);
            var cat = groupes.get(groupe);
            if (cat == null) {
                cat = new CategorieThematiques(
                                groupe,
                                emojig,
                                new ArrayList<>()
                        );
                groupes.put(groupe,cat);
                categories.add(cat);
            }
            cat.items().add(
                    new CategorieThematiques.Item(
                            id,
                            label,
                            emoji
                    )
            );
        }
        if(FORCE_ONISEP_DOMAINES) {
            List<SousDomaineWeb> sousDomainesWeb = new ArrayList<>(loadDomainesSousDomaines(sources));

            Map<String, CategorieThematiques> catsMap
                    = sousDomainesWeb.stream().map(SousDomaineWeb::domaineOnisep)
                    .distinct()
                    .collect(Collectors.toMap(
                            Constants::cleanup,
                            domainePro -> new CategorieThematiques(
                                    domainePro,
                                    "\uD83C\uDF31",
                                    new ArrayList<>()
                            )
                    ));

            for (val domainePro : sousDomainesWeb) {
                val cat = catsMap.get(Constants.cleanup(domainePro.domaineOnisep()));
                cat.items().add(
                        new CategorieThematiques.Item(
                                domainePro.ideo(),
                                domainePro.sousDomaineOnisep(),
                                "\uD83C\uDF0E"
                        )
                );
            }

            categories.clear();
            categories.addAll(catsMap.values());
        }
        return categories;
    }

    public static DomainesMps load(DataSources sources) throws Exception {
        DomainesMps res = new DomainesMps();

        String groupe = "";
        String emojig = "";
        for (Map<String, String> stringStringMap : CsvTools.readCSV(sources.getSourceDataFilePath(DataSources.DOMAINES_MPS_PATH), '\t')) {
            String id = stringStringMap.get("id").trim();
            String regroupement = stringStringMap.get("regroupement").trim();
            if (!regroupement.isEmpty()) groupe = regroupement;
            String emojiGroupe = stringStringMap.get("Emoji");
            if (!emojiGroupe.isEmpty()) emojig = emojiGroupe;
            String emoji = stringStringMap.get("Emojis");
            String label = stringStringMap.get("label");
            if (groupe.isEmpty()) throw new RuntimeException("Groupe vide dans " + DataSources.DOMAINES_MPS_PATH);
            if (emojig.isEmpty()) throw new RuntimeException("Groupe sans emoji dans " + DataSources.DOMAINES_MPS_PATH);
            res.add(id, label, groupe, emojig, emoji);
        }

        if(FORCE_ONISEP_DOMAINES) {
            List<SousDomaineWeb> sousDomainesWeb = new ArrayList<>(loadDomainesSousDomaines(sources));
            res.setSousDomainesWeb(sousDomainesWeb);
        }

        return res;
    }

    private DomainesMpsLoader() {
    }
}

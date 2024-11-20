package fr.gouv.monprojetsup.data.model.descriptifs;


import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.model.metiers.MetiersScrapped;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public record DescriptifsFormationsMetiers(

        //indexed by formation
    Map<String, DescriptifFormation> keyToDescriptifs
) {

    public DescriptifsFormationsMetiers() {
        this(new HashMap<>());
    }

    public static Link toAvenirs(String uri, String label, String source) {
        if(uri == null) return null;
        uri =  uri
                .replace(
                "www.terminales2022-2023.fr","www.onisep.fr")
                .replace(Constants.ONISEP_URL1, Constants.EXPLORER_AVENIRS_URL)
                .replace(Constants.ONISEP_URL2, Constants.EXPLORER_AVENIRS_URL)
                ;
        return new Link(label, uri, source);
    }

    private static Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public static String toParcoursupCarteUrl(@NotNull Collection<String> psupIds) {
        String normalized = psupIds.stream().distinct()
                //.map(fl -> fl + "x")
                //remove special cgaracters
                .collect(Collectors.joining(" "))
                .toLowerCase()
                .replaceAll("[-/]", " ")
                .trim();

        //remove accents
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[^\\p{ASCII}\\s]", "");

        //remove non chars
        normalized = normalized.replaceAll("[^a-zA-Z0-9]", " ");

        return Constants.CARTE_PARCOURSUP_PREFIX_URI
                + URLEncoder.encode(normalized,
                StandardCharsets.UTF_8);
    }


    public void injectLas(Map<String, String> lasCorrespondance) {
        lasCorrespondance.forEach((lasKey,genKey) -> keyToDescriptifs.computeIfAbsent(
                lasKey,
                z-> keyToDescriptifs.get(genKey)
        ));
    }

    public void inject(MetiersScrapped metiersScrapped) {
        metiersScrapped.metiers().values().forEach(m -> {
            String cs = m.key();
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

    public DescriptifFormation get(String key) {
        return keyToDescriptifs.get(key);
    }

    @Nullable
    public String getDescriptifGeneralFront(@NotNull String flCod) {
        val desc = keyToDescriptifs.get(flCod);
        if(desc == null) return null;
        return Helpers.removeHtml(desc.getDescriptifGeneralFront()).trim();
    }

    @Nullable
    public String getDescriptifDiplomeFront(@NotNull String flCod) {
        val desc = keyToDescriptifs.get(flCod);
        if(desc == null) return null;
        return Helpers.removeHtml(desc.getDescriptifDiplomeFront());
    }

    public void inject(String id, DescriptifFormation descriptif) {
        keyToDescriptifs.put(id, descriptif);
    }


    public record Link(String label, String uri, String source) {

    }
}
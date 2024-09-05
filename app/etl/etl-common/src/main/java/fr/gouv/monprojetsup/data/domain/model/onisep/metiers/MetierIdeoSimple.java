package fr.gouv.monprojetsup.data.domain.model.onisep.metiers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.onisep.SousDomaineWeb;
import jakarta.xml.bind.annotation.XmlElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;


        /*"libelle_metier":"poseur \/ poseuse de voies ferrées",
        "lien_site_onisepfr":"http:\/\/www.onisep.fr\/http\/redirections\/metier\/slug\/MET.7776",
        "nom_publication":"Les métiers du Grand Paris Express",
        "collection":"Pourquoi pas moi ?",
        "annee":2020,
        "gencod":9782273015127,
        "gfe":"GFE C : bâtiment",
        "codeRome":"F1702",
        "libelle_rome":"Construction de routes et voies",
        "lien_rome":"https:\/\/candidat.pole-emploi.fr\/marche-du-travail\/fichemetierrome?codeRome=F1702",
        "domainesous-domaine":"mécanique\/travail des métaux| construction, architecture, travaux publics\/travaux publics"},{"libelle_metier":"chargé \/ chargée de recherche en acoustique musicale","lien_site_onisepfr":"http:\/\/www.onisep.fr\/http\/redirections\/metier\/slug\/MET.715","nom_publication":"","collection":"pas de publication Onisep spécifique","annee":null,"gencod":null,"gfe":"GFE H : électricité, énergie, électronique","codeRome":"",*/


public  record MetierIdeoSimple(
            String libelle_metier,
            String lien_site_onisepfr,
            String nom_publication,
            String collection,
            Integer annee,
            Long gencod,
            String gfe,
            String code_rome,
            String libelle_rome,
            String lien_rome,
            @SerializedName("domainesous-domaine")//gson
            @JsonProperty("domainesous-domaine")//jackson
            @XmlElement(name = "domainesous-domaine")//jaxb
            String domaines
    ) {
        public Set<SousDomaineWeb> getDomaines(Map<String, SousDomaineWeb> sousDomainesWeb) {
            if (domaines == null) return Collections.emptySet();
            return SousDomaineWeb
                    .extractDomaines(domaines, sousDomainesWeb);
        }

        public @NotNull String idMps() {
            return Constants.cleanup(idIdeo());
        }

        public @NotNull String idIdeo() {
            String url = lien_site_onisepfr();
            if (url == null) {
                throw new RuntimeException("Impossible to extract metiers fl style MET.7776 from the url of " + this);
            }
            int index = url.lastIndexOf("/");
            if (index < 0) {
                throw new RuntimeException("Impossible to extract metiers fl style MET.7776 from the url of " + this);
            }
            String metierID = url.substring(index + 1);
            if (!metierID.contains("MET.")) {
                throw new RuntimeException("Impossible to extract metiers fl style MET.7776 from the url of " + this);
            }
            return metierID;
        }

    public List<String> getMotsCles() {
        List<String> result = new ArrayList<>();
        if(domaines != null) {
            result.addAll(Arrays.stream(domaines.split("\\|")).toList());
        }
        return result;
    }
}

package fr.gouv.monprojetsup.data.infrastructure.onisep;

import java.util.Collections;
import java.util.List;
import java.util.Set;

    public  record MetierOnisep(
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
            String domaines
    ) {
        public Set<DomainePro> getDomaines(List<DomainePro> domainesPro) {
            if (domaines == null) return Collections.emptySet();
            return DomainePro.extractDomaines(domaines, domainesPro);
        }

        /*"libelle_metier":"poseur \/ poseuse de voies ferr\u00e9es",
        "lien_site_onisepfr":"http:\/\/www.onisep.fr\/http\/redirections\/metier\/slug\/MET.7776",
        "nom_publication":"Les m\u00e9tiers du Grand Paris Express",
        "collection":"Pourquoi pas moi ?",
        "annee":2020,
        "gencod":9782273015127,
        "gfe":"GFE C : b\u00e2timent",
        "codeRome":"F1702",
        "libelle_rome":"Construction de routes et voies",
        "lien_rome":"https:\/\/candidat.pole-emploi.fr\/marche-du-travail\/fichemetierrome?codeRome=F1702",
        "domainesous-domaine":"m\u00e9canique\/travail des m\u00e9taux| construction, architecture, travaux publics\/travaux publics"},{"libelle_metier":"charg\u00e9 \/ charg\u00e9e de recherche en acoustique musicale","lien_site_onisepfr":"http:\/\/www.onisep.fr\/http\/redirections\/metier\/slug\/MET.715","nom_publication":"","collection":"pas de publication Onisep sp\u00e9cifique","annee":null,"gencod":null,"gfe":"GFE H : \u00e9lectricit\u00e9, \u00e9nergie, \u00e9lectronique","codeRome":"",*/

    }

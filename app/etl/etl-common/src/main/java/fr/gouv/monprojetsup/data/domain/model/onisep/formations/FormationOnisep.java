package fr.gouv.monprojetsup.data.domain.model.onisep.formations;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record FormationOnisep(
        /*
        {
    "code_nsf": "340",
    "sigle_type_formation": "",
    "libelle_type_formation": "formation d'école spécialisée",
    "libelle_formation_principal": "MPA stratégie et politique de défense",
    "sigle_formation": "",
    "duree": "2 ans",
    "niveau_de_sortie_indicatif": "Bac + 5",
    "code_rncp": "",
    "niveau_de_certification": "0",
    "libelle_niveau_de_certification": "non inscrit au RNCP",
    "tutelle": "non renseigné",
    "url_et_id_onisep": "http://www.onisep.fr/http/redirection/formation/slug/FOR.3469",
    "domainesous-domaine": "économie, droit, politique/droit international | économie, droit, politique/sciences politiques"
  }*/
        String code_nsf,
        String sigle_type_formation,
        String libelle_type_formation,
        String libelle_formation_principal,
        String sigle_formation,
        String duree,
        String niveau_de_sortie_indicatif,
        String code_rncp,
        String niveau_de_certification,
        String libelle_niveau_de_certification,
        String tutelle,
        String url_et_id_onisep,
        String domainesous_domaine,

        NiveauEtudes niveau_etudes)
{
        public @Nullable String identifiant() {
                if(url_et_id_onisep == null) return null;
                int pos = url_et_id_onisep.lastIndexOf('/');
                if(pos < 0) return null;
                return url_et_id_onisep.substring(pos+1);
        }

        private static final Set<String> niveauxCertif = Set.of("5","6","7","8");
    public boolean isFormationDuSup() {
        return (niveau_etudes() != null && niveau_etudes().isFormationDuSup())
                || niveau_de_certification != null && niveau_de_certification.toLowerCase().contains("bac +")
                || niveau_de_certification != null && niveauxCertif.contains(niveau_de_certification)
                || niveau_de_sortie_indicatif != null && niveau_de_sortie_indicatif.toLowerCase().contains("bac +");
    }

    public record NiveauEtudes(String id, String libelle) {
        public boolean isFormationDuSup() {
            return libelle.toLowerCase().contains("bac +");
        }
    }
}

package fr.gouv.monprojetsup.data.domain.model.onisep.formations;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public record FormationIdeoSimple(
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
        @SerializedName("code_nsf")
        String codeNsf,
        @SerializedName("sigle_type_formation")
        String sigleTypeFormation,
        @SerializedName("libelle_type_formation")
        String libelleTypeFormation,
        @SerializedName("libelle_formation_principal")
        String libelleFormationPrincipal,
        @SerializedName("sigle_formation")
        String sigleFormation,
        String duree,
        @SerializedName("niveau_de_sortie_indicatif")
        String niveauDeSortieIndicatif,
        @SerializedName("code_rncp")
        String codeRncp,
        @SerializedName("niveau_de_certification")
        String niveauDeCertification,
        @SerializedName("libelle_niveau_de_certification")
        String libelleNiveauDeCertification,
        String tutelle,
        @SerializedName("url_et_id_onisep")
        String urlEtIdOnisep,
        @SerializedName("domainesous-domaine")
        String domainesousDomaine,

        List<String> enseignements
) {
    public @Nullable String identifiant() {
        if (urlEtIdOnisep == null) return null;
        int pos = urlEtIdOnisep.lastIndexOf('/');
        if (pos < 0) return null;
        return urlEtIdOnisep.substring(pos + 1);
    }

    private static final Set<String> niveauxCertif = Set.of("5", "6", "7", "8");

    public boolean estFormationDuSup() {
        return (libelleNiveauDeCertification != null && libelleNiveauDeCertification.toLowerCase().contains("bac +")
                || niveauDeCertification != null && niveauxCertif.contains(niveauDeCertification)
                || niveauDeSortieIndicatif != null && niveauDeSortieIndicatif.toLowerCase().contains("bac +")
        );
    }

    public List<String> getMotsCles() {
        List<String> result = new ArrayList<>();
        if (codeNsf != null) result.add("codeNsf" + codeNsf);
        if (sigleTypeFormation != null) result.add(sigleTypeFormation);
        if (sigleFormation != null) result.add(sigleFormation);
        if (libelleTypeFormation != null) result.add(libelleTypeFormation);
        if (libelleFormationPrincipal != null) result.add(libelleFormationPrincipal);
        if (sigleFormation != null) result.add(sigleFormation);
        if (duree != null) result.add("duree" + duree.replaceAll(" ", "_"));
        if (domainesousDomaine != null) {
            result.addAll(
                    Arrays.stream(domainesousDomaine
                            .split("|"))
                            .map(String::trim)
                            .filter(s -> !s.isBlank())
                            .toList()
            );
        }
        if (enseignements != null) result.addAll(enseignements);
        return result;
    }

    public boolean aUnIdentifiantIdeo() {
        return identifiant() != null;
    }
}

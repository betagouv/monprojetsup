package fr.gouv.monprojetsup.data.model.onisep.formations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import fr.gouv.monprojetsup.data.Constants;
import jakarta.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public record FicheFormationIdeo(
        // "identifiant": "FOR.8943"
        @NotNull
        String identifiant,

        // "code_scolarite": "1702230B",
        String code_scolarite,

        // "libelle_complet": "diplôme d'ingénieur de l'École supérieure de fonderie et de forge (ESFF) en convention avec l'Ecole nationale supérieure d'arts et métiers (ENSAM) en partenariat avec l'Institut d'études supérieures de fonderie et de forge (IESFF)",*/
        String libelle_complet,

        // "sigle": "IESFF",
        String sigle,

        //"libelle_generique": "diplôme d'ingénieur de l'École supérieure de fonderie et de forge (ESFF) en convention avec l'Ecole nationale supérieure d'arts et métiers (ENSAM)",
        String libelle_generique,

        //"libelle_specifique": "en partenariat avec l'Institut d'études supérieures de fonderie et de forge (IESFF)",
        String libelle_specifique,

        //"type_option": "autre type",
        String type_option,

        /*"type_Formation": {
          "type_formation_sigle": "",
          "type_formation_libelle_court": "ING",
          "type_formation_libelle": "diplôme d'ingénieur"
        },*/
        TypeFormation type_Formation,

        //"duree_formation": "3 ans",
        String duree_formation,

        //"descriptif_format_court": "",
        String descriptif_format_court,

        //"descriptif_acces": "",
        String descriptif_acces,

        //"url": "https://www.francecompetences.fr/recherche/rncp/37588/",
        String url,

        /*
        "nsf_discipline": {
          "NSF_discipline_code": 223,
          "NSF_discipline_libelle": "Métallurgie"
        },*/
        @SerializedName("nsf_discipline")
        @JsonProperty("nsf_discipline")
        @XmlElement(name = "nsf_discipline")
        NsfDiscipline nsfDiscipline,


        // IGNORED "nsf_fonction": {
        //          "NSF_fonction_code": "F",
        //          "NSF_fonction_libelle": "Application à une technologie ou à une activité de production"
        //        },

        //"niveau_certification": "niveau 7 (bac + 5)",
        @SerializedName("niveau_certification")//gson
        @JsonProperty("niveau_certification")//jackson
        @XmlElement(name = "niveau_certification")//jaxb
        String niveauCertification,

        /*"niveau_etudes": {
          "id": "REF.410",
          "libelle": "Bac + 5"
        },*/
        @SerializedName("niveau_etudes")//gson
        @JsonProperty("niveau_etudes")//jackson
        @XmlElement(name = "niveau_etudes")//jaxb
        NiveauEtudes niveauEtudes,

        //"niv_code": 170,
        //"niv_code": "16X",
        String niv_code,

        /*
        "nature_certificat": {
          "libelle_nature_certificat": [
            "Titre habilité par la Commission des titres d'ingénieur",
            "Diplôme conférant le grade de master"
          ]
        },*/
        List<String> nature_certificat,

        //"attendus": "",
        String attendus,

        //"element_enseignement": "",
        List<String> element_enseignement,

        //"sous_tutelle": "Ministère chargé de l'Enseignement supérieur et de la Recherche",
        String sous_tutelle,

        //"descriptif_poursuite_etudes": "",
        String descriptif_poursuite_etudes,

        //"poursuites_etudes": "",
        /*  <poursuites_etudes>
            <poursuite_etudes>
                <type_Poursuite>poursuite d'études conditionnelle</type_Poursuite>
                <formation_poursuite_Etudes>licence pro mention industries agroalimentaires : gestion, production et valorisation</formation_poursuite_Etudes>
                <formation_poursuite_Etudes>licence pro mention qualité, hygiène, sécurité, santé, environnement</formation_poursuite_Etudes>
                <formation_poursuite_Etudes>licence pro mention valorisation des agro-ressources</formation_poursuite_Etudes>
                <formation_poursuite_Etudes>licence pro mention métiers de la qualité</formation_poursuite_Etudes>
                <formation_poursuite_Etudes>licence pro mention bio-industries et biotechnologies</formation_poursuite_Etudes>
                <formation_poursuite_Etudes>licence pro mention génie des procédés et bioprocédés industriels</formation_poursuite_Etudes>
                <formation_poursuite_Etudes>licence pro mention commercialisation des produits alimentaires</formation_poursuite_Etudes>
                <formation_poursuite_Etudes>Classe agro véto post BTSA et BTS</formation_poursuite_Etudes>
                <formation_poursuite_Etudes>licence pro mention chimie analytique, contrôle, qualité, environnement</formation_poursuite_Etudes>
                <formation_poursuite_Etudes>licence pro mention métiers de l'instrumentation, de la mesure et du contrôle qualité</formation_poursuite_Etudes>
                <formation_poursuite_Etudes>licence pro mention chimie : formulation</formation_poursuite_Etudes>
            </poursuite_etudes>
        </poursuites_etudes>
        */
        PoursuitesEtudes poursuites_etudes,

        /*
        "metiers_formation": {
          "metier": [
            {
              "id": "MET.706",
              "libelle": "ingénieur / ingénieure en fonderie",
              "synonymes": ""
            },
            {
              "id": "MET.351",
              "libelle": "ingénieur / ingénieure métallurgiste",
              "synonymes": ""
            }
          ]
        },*/
        List<MetierFormation> metiers_formation,

        /* IGNORED
        "publications": {
          "publication": {
            "code_librairie": 9782273016032,
            "titre_publication": "Ecoles d'ingénieurs",
            "editeur": "ONISEP",
            "annee": 2022,
            "collection": "Dossiers"
          }
        },*/
        /*
        "sources_numeriques": "",
        "sources_numeriques": {
          "source": [
            "http://www.myctc.fr",
            "http://www.cordonnerie.org"
          ]
        },*/
        List<String> sources_numeriques,

        /*
        "sous_domaines_web": {
          "sous_domaine_web": [
            {
              "id": "T-IDEO2.4338",
              "libelle": "aéronautique"
            },
            {
              "id": "T-IDEO2.4362",
              "libelle": "fonction production (généralités)"
            },
            {
              "id": "T-IDEO2.4355",
              "libelle": "matériaux"
            },
            {
              "id": "T-IDEO2.4342",
              "libelle": "mécanique (généralités)"
            },
            {
              "id": "T-IDEO2.4359",
              "libelle": "métallurgie, sidérurgie"
            },
            {
              "id": "T-IDEO2.4363",
              "libelle": "méthodes industrialisation"
            },
            {
              "id": "T-IDEO2.4341",
              "libelle": "travail des métaux"
            }
          ]
        }, */
        @SerializedName("sous_domaines_web")//gson
        @JsonProperty("sous_domaines_web")//jackson
        @XmlElement(name = "sous_domaines_web")//jaxb
        List<SousDomaineWeb> sousDomainesWeb,

        /* IGNORED
        "creation_date": "20/06/2023",
                "modification_date": "27/06/2023"
                */
        String creation_date,
        String modification_date

) {
    public static FicheFormationIdeo setId(FicheFormationIdeo f, String newId) {
        if (Objects.equals(f.identifiant, newId)) return f;
        return new FicheFormationIdeo(
                newId,
                f.code_scolarite,
                f.libelle_complet,
                f.sigle,
                f.libelle_generique,
                f.libelle_specifique,
                f.type_option,
                f.type_Formation,
                f.duree_formation,
                f.descriptif_format_court,
                f.descriptif_acces,
                f.url,
                f.nsfDiscipline,
                f.niveauCertification,
                f.niveauEtudes,
                f.niv_code,
                f.nature_certificat,
                f.attendus,
                f.element_enseignement,
                f.sous_tutelle,
                f.descriptif_poursuite_etudes,
                f.poursuites_etudes,
                f.metiers_formation,
                f.sources_numeriques,
                f.sousDomainesWeb,
                f.creation_date,
                f.modification_date
        );
    }

    public List<String> getMotsCles() {
        List<String> result = new ArrayList<>(List.of(
                identifiant,
                libelle_complet,
                Objects.requireNonNullElse(sigle, ""),
                Objects.requireNonNullElse(libelle_generique, ""),
                Objects.requireNonNullElse(libelle_specifique, ""),
                Objects.requireNonNullElse(type_Formation.type_formation_sigle, ""),
                Objects.requireNonNullElse(type_Formation.type_formation_libelle_court, ""),
                Objects.requireNonNullElse(type_Formation.type_formation_libelle, ""),
                "duree" + Objects.requireNonNullElse(duree_formation, "").replaceAll(" ", ""),
                Objects.requireNonNullElse(nsfDiscipline.NSF_discipline_libelle, ""),
                Objects.requireNonNullElse(niveauCertification, ""),
                Objects.requireNonNullElse(niveauEtudes.libelle, ""),
                //Objects.requireNonNullElse(attendus,""),
                Objects.requireNonNullElse(sous_tutelle, "")
                //Objects.requireNonNullElse(descriptif_poursuite_etudes, "")
        ));
        if (poursuites_etudes != null && poursuites_etudes.poursuite_etudes != null) {
            result.addAll(poursuites_etudes.poursuite_etudes);
        }
        if (metiers_formation != null) {
            for (MetierFormation metier : metiers_formation) {
                result.add(Constants.cleanup(metier.id + "x"));
                result.add(metier.nom_metier);
                if (metier.synonymes != null) {
                    for (Synonyme synonyme : metier.synonymes) {
                        result.add(synonyme.nom_metier);
                        result.add(synonyme.libelle_masculin);
                        result.add(synonyme.libelle_feminin);
                    }
                }
            }
        }
        if (nature_certificat != null) result.addAll(nature_certificat);
        if (element_enseignement != null) result.addAll(element_enseignement);
        if (sousDomainesWeb != null) {
            for (SousDomaineWeb sousDomaine : sousDomainesWeb) {
                result.add(Constants.cleanup(sousDomaine.id + "x"));
                result.add(sousDomaine.libelle);
            }
        }
        result.removeIf(Objects::isNull);
        result.removeIf(String::isBlank);
        return result;
    }

    public boolean estFormationDuSup() {
        return (
                niveauCertification != null && niveauCertification.toLowerCase().contains("bac +")
                        || niveauEtudes != null && niveauEtudes.libelle.toLowerCase().contains("bac +")
                        || estBpjeps()
        );
    }

    private boolean estBpjeps() {
        return type_Formation != null && Objects.equals(type_Formation.type_formation_sigle, "BPJEPS");
    }

    public boolean estIEP() {
        return sigle != null && sigle.equals("IEP");
    }

    public boolean estEcoleIngenieur() {
        return type_Formation.type_formation_libelle_court.equals("ING")
                || type_Formation.type_formation_libelle.contains("diplôme d'ingénieur");
    }

    public boolean estEcoleCommerce() {
        return type_Formation.type_formation_libelle_court.equals("GECOM")
                || type_Formation.type_formation_libelle.contains("diplôme d'école de commerce");
    }

    public boolean estEcoleArchitecture() {
        return type_Formation.type_formation_libelle_court.equals("ARCHI")
                || type_Formation.type_formation_libelle.contains("diplôme des écoles d'architecture");
    }

    public boolean estEcoleArt() {
        return type_Formation.type_formation_libelle_court.equals("DNA")
                || type_Formation.type_formation_libelle.contains("DNA");
    }

    public boolean estDiplomeConservationRestauration() {
        return Objects.requireNonNullElse(nsfDiscipline.NSF_discipline_code, -1) == Constants.CODE_NSF_CONSERVATION_RESTAURATION;
    }

    public boolean estDMA() {
        return type_Formation.type_formation_libelle_court.equals("DMA")
                || type_Formation.type_formation_libelle.contains("DMA");
    }

    public Collection<Pair<String, String>> getSousdomainesWeb() {
        return sousDomainesWeb.stream().map(sousDomaineWeb -> Pair.of(sousDomaineWeb.id, sousDomaineWeb.libelle)).toList();
    }

    /*"type_Formation": {
                      "type_formation_sigle": "",
                      "type_formation_libelle_court": "ING",
                      "type_formation_libelle": "diplôme d'ingénieur"
                    },*/
    public record TypeFormation(
            String type_formation_sigle,
            String type_formation_libelle_court,
            String type_formation_libelle
    ) {

    }

    /*
            "nsf_discipline": {
              "NSF_discipline_code": 223,
              "NSF_discipline_libelle": "Métallurgie"
            },*/
    public record NsfDiscipline(
            @SerializedName("NSF_discipline_code")//gson
            @JsonProperty("NSF_discipline_code")//jackson
            @XmlElement(name = "NSF_discipline_code")//jaxb
            Integer NSF_discipline_code,
            @SerializedName("NSF_discipline_libelle")//gson
            @JsonProperty("NSF_discipline_libelle")//jackson
            @XmlElement(name = "NSF_discipline_libelle")//jaxb
            String NSF_discipline_libelle
    ) {

    }

    /*"niveau_etudes": {
              "id": "REF.410",
              "libelle": "Bac + 5"
            },*/
    public record NiveauEtudes(
            String id,
            String libelle
    ) {

    }

    /* "nature_certificat": {
              "libelle_nature_certificat": [
                "Titre habilité par la Commission des titres d'ingénieur",
                "Diplôme conférant le grade de master"
              ]
            },*/
    public record NatureCertificat(
            List<String> libelle_nature_certificat
    ) {

    }

    /*
            <metiers_formation>
                <metier>
                    <id>MET.904</id>
                    <libelle>constructeur / constructrice de routes et d'aménagements urbains</libelle>
                    <synonymes>
                        <synonyme>
                            <libelle>maçon / maçonne travaux routiers</libelle>
                            <libelle_feminin>maçonne travaux routiers</libelle_feminin>
                        </synonyme>
                        <synonyme>
                            <libelle>maçon / maçonne VRD (voiries réseaux divers)</libelle>
                            <libelle_feminin>maçonne VRD (voiries réseaux divers)</libelle_feminin>
                        </synonyme>
                        <synonyme>
                            <libelle>constructeur / constructrice en VRD (voiries réseaux divers)</libelle>
                            <libelle_feminin>constructrice en VRD (voiries réseaux divers)</libelle_feminin>
                        </synonyme>
                        <synonyme>
                            <libelle>applicateur /applicatrice de revêtements routiers</libelle>
                            <libelle_feminin>applicatrice de revêtements routiers</libelle_feminin>
                        </synonyme>
                        <synonyme>
                            <libelle>constructeur / constructrice en voirie urbaine</libelle>
                            <libelle_feminin>constructrice en voirie urbaine</libelle_feminin>
                        </synonyme>
                        <synonyme>
                            <libelle>maçon / maçonne en VRD (voiries réseaux divers)</libelle>
                            <libelle_feminin>maçonne en VRD (voiries réseaux divers)</libelle_feminin>
                        </synonyme>
                        <synonyme>
                            <libelle>ouvrier / ouvrière routière</libelle>
                            <libelle_feminin>ouvrière routière</libelle_feminin>
                        </synonyme>
                    </synonymes>
                </metier>

            */
    public record Synonyme(
            String nom_metier,
            String libelle_masculin,
            String libelle_feminin
    ) {

    }

    public record MetierFormation(
            String id,
            String nom_metier,
            String libelle_feminin,
            String libelle_masculin,
            List<Synonyme> synonymes
    ) {

    }

        /*
        "sources_numeriques": {
          "source": [
            "http://www.myctc.fr",
            "http://www.cordonnerie.org"
          ]
        },*/


    public record SousDomaineWeb(
            String id,
            String libelle
    ) {

    }


    /*
    <poursuites_etudes>
             <poursuite_etudes>
                 <type_Poursuite>poursuite d'études conditionnelle</type_Poursuite>
                 <formation_poursuite_Etudes>licence pro mention industries agroalimentaires : gestion, production et valorisation</formation_poursuite_Etudes>
                 <formation_poursuite_Etudes>licence pro mention qualité, hygiène, sécurité, santé, environnement</formation_poursuite_Etudes>
                 <formation_poursuite_Etudes>licence pro mention valorisation des agro-ressources</formation_poursuite_Etudes>
                 <formation_poursuite_Etudes>licence pro mention métiers de la qualité</formation_poursuite_Etudes>
                 <formation_poursuite_Etudes>licence pro mention bio-industries et biotechnologies</formation_poursuite_Etudes>
                 <formation_poursuite_Etudes>licence pro mention génie des procédés et bioprocédés industriels</formation_poursuite_Etudes>
                 <formation_poursuite_Etudes>licence pro mention commercialisation des produits alimentaires</formation_poursuite_Etudes>
                 <formation_poursuite_Etudes>Classe agro véto post BTSA et BTS</formation_poursuite_Etudes>
                 <formation_poursuite_Etudes>licence pro mention chimie analytique, contrôle, qualité, environnement</formation_poursuite_Etudes>
                 <formation_poursuite_Etudes>licence pro mention métiers de l'instrumentation, de la mesure et du contrôle qualité</formation_poursuite_Etudes>
                 <formation_poursuite_Etudes>licence pro mention chimie : formulation</formation_poursuite_Etudes>
             </poursuite_etudes>
    <poursuites_etudes>
 */
    public record PoursuitesEtudes(
            List<String> poursuite_etudes
    ) {
    }


}

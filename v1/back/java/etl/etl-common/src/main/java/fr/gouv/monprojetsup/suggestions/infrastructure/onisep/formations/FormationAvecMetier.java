package fr.gouv.monprojetsup.suggestions.infrastructure.onisep.formations;

import java.util.List;


public record FormationAvecMetier(
        // "identifiant": "FOR.8943"
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
        NsfDiscipline nsf_discipline,

        // IGNORED "nsf_fonction": {
        //          "NSF_fonction_code": "F",
        //          "NSF_fonction_libelle": "Application à une technologie ou à une activité de production"
        //        },

        //"niveau_certification": "niveau 7 (bac + 5)",
        String niveau_certification,

        /*"niveau_etudes": {
          "id": "REF.410",
          "libelle": "Bac + 5"
        },*/
        NiveauEtudes niveau_etudes,

        //"niv_code": 170,
        //"niv_code": "16X",
        String niv_code,

        //"annee_premiere_session": "",
        String annee_premiere_session,

        //"annee_derniere_session": "",
        String annee_derniere_session,

        /*
        "nature_certificat": {
          "libelle_nature_certificat": [
            "Titre habilité par la Commission des titres d'ingénieur",
            "Diplôme conférant le grade de master"
          ]
        },*/
        NatureCertificat nature_certificat,

        //"attendus": "",
        String attendus,

        //"element_enseignement": "",
        ElementEnseignement element_enseignement,

        //"sous_tutelle": "Ministère chargé de l'Enseignement supérieur et de la Recherche",
        String sous_tutelle,

        //"descriptif_poursuite_etudes": "",
        String descriptif_poursuite_etudes,

        //"poursuites_etudes": "",
        /*  "poursuites_etudes": {
          "poursuite_etudes": {
            "type_Poursuite": "poursuite d'études conditionnelle",
            "formation_poursuite_Etudes": [
              "Classe préparatoire ATS ingénierie industrielle",
              "licence pro mention maintenance des systèmes industriels, de production et d'énergie",
              "licence pro mention maintenance et technologie : contrôle industriel",
              "licence pro mention maintenance et technologie : organisation de la maintenance",
              "licence pro mention maintenance et technologie : systèmes pluritechniques"
            ]
          }
        },*/
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
        MetiersFormation metiers_formation,

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
        SourcesNumerique sources_numeriques,

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
        SousDomainesWeb sous_domaines_web,

        /* IGNORED
        "creation_date": "20/06/2023",
                "modification_date": "27/06/2023"
                */
        String creation_date,
        String modification_date
) {
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
                Integer NSF_discipline_code,
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
                String libelle,
                String libelle_feminin
        ) {

        }

        public record SynonymeList(
                List<Synonyme> synonyme
        ) {
        }

        public record MetierFormation(
                String id,
                String libelle,
                SynonymeList synonyme
        ) {

        }

        public record MetiersFormation(
                List<MetierFormation> metier
        ) {

        }

        /*
        "sources_numeriques": {
          "source": [
            "http://www.myctc.fr",
            "http://www.cordonnerie.org"
          ]
        },*/
        public record SourcesNumerique(List<String> source) {
        }

        public record SousDomaineWeb(
                String id,
                String libelle
        ) {

        }

        public record SousDomainesWeb(
                List<SousDomaineWeb> sous_domaine_web
        ) {

        }

   /*  "poursuites_etudes": {
          "poursuite_etudes": {
            "type_Poursuite": "poursuite d'études conditionnelle",
            "formation_poursuite_Etudes": [
              "Classe préparatoire ATS ingénierie industrielle",
              "licence pro mention maintenance des systèmes industriels, de production et d'énergie",
              "licence pro mention maintenance et technologie : contrôle industriel",
              "licence pro mention maintenance et technologie : organisation de la maintenance",
              "licence pro mention maintenance et technologie : systèmes pluritechniques"
            ]
          }
        },*/

        public record PoursuiteEtudes(
                String type_Poursuite,
                List<String> formation_poursuite_Etudes
        ) {

        }

        public record PoursuitesEtudes(
                PoursuiteEtudes poursuite_etudes
        ) {

        }

        /*"element_enseignement_libelle": [
                        "projet urbain et métropolisation",
                        "paysage",
                        "métropoles de l'Arc Pacifique"
                        ]*/
        public record ElementEnseignement(
                List<String> element_enseignement_libelle
        ) {

        }


}

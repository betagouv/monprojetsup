package fr.gouv.monprojetsup.data.onisep;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.DataSources.ONISEP_FICHES_METIERS;

public record FichesMetierOnisep(
        FicheMetierOnisepContainer metiers
) {

    public static FichesMetierOnisep loadFichesMetierOnisep(DataSources sources) throws IOException {
        FichesMetierOnisep orig = Serialisation.fromJsonFile(sources.getSourceDataFilePath(ONISEP_FICHES_METIERS), FichesMetierOnisep.class);
        FichesMetierOnisep copy = new FichesMetierOnisep(new FicheMetierOnisepContainer(new ArrayList<>()));
        orig.metiers.metier.forEach(f -> {
                    FicheMetierOnisep fiche = new FicheMetierOnisep(
                            Constants.cleanup(f.identifiant),
                            f.nom_metier,
                            f.libelle_feminin,
                            f.libelle_masculin, f.synonymes,
                            f.romesV3,
                            f.competences,
                            f.condition_travail,
                            f.nature_travail,
                            f.acces_metier,
                            f.vie_professionnelle,
                            f.accroche_metier,
                            f.formats_courts,
                            f.niveau_acces_min,
                            f.formations_min_requise,
                            f.sources_numeriques,
                            cleanup(f.secteurs_activite),
                            f.centres_interet,
                            f.metiers_associes
                    );
                    copy.metiers.metier().add(fiche);

                }
        );
        return copy;
    }

    private static SecteursActivites cleanup(@Nullable SecteursActivites toClean) {
        SecteursActivites result = new SecteursActivites(new ArrayList<>());
        if (toClean == null) return result;
        toClean.secteur_activite.forEach(s -> result.secteur_activite.add(
                new SecteurActivite(
                        Constants.cleanup(s.id).replace(Constants.SEC_ACT_PREFIX_IN_FILES, Constants.SEC_ACT_PREFIX_IN_GRAPH),
                        s.libelle
                )
        ));
        return result;
    }

    public Set<String> getMetiersOfDomain(String domaine) {
        String key = Constants.cleanup(domaine).replace(Constants.SEC_ACT_PREFIX_IN_FILES, Constants.SEC_ACT_PREFIX_IN_GRAPH);
        return metiers.metier().stream()
                .filter(m -> m.secteurs_activite.hasDomain(key))
                .map(m -> m.identifiant)
                .collect(Collectors.toSet());
    }

    public @Nullable FicheMetierOnisep get(String dkey) {
        String key = Constants.cleanup(dkey);
        return metiers.metier.stream().filter(m -> Constants.cleanup(m.identifiant).equals(key)).findFirst().orElse(null);
    }

    public record FicheMetierOnisepContainer(List<FicheMetierOnisep> metier) {

    }

    public record FicheMetierOnisep(

    /*        <identifiant>MET.31</identifiant>*/
            String identifiant,
            /*
        <nom_metier>cartographe</nom_metier>*/
            String nom_metier,
            /*
        <libelle_feminin>cartographe</libelle_feminin>*/
            String libelle_feminin,
            /*
        <libelle_masculin>cartographe</libelle_masculin>*/
            String libelle_masculin,
            /*
        <synonymes>
            <synonyme>cartographe géomaticien/ne</synonyme>
            <synonyme>cartographe SIG</synonyme>
            <synonyme>concepteur/trice de cartes</synonyme>
            <synonyme>dessinateur/trice cartographe</synonyme>
            <synonyme>ingénieur/e cartographe</synonyme>
        </synonymes>*/
            Synonymes synonymes,
            /*
        <romesV3>
            <romeV3>M1808</romeV3>
        </romesV3>*/
            Romesv3 romesV3,
            /*
        <competences><![CDATA[
            <h5>Informatique omniprésente</h5>
            <p>Pour traiter numériquement des données de terrain, les cartographes doivent maîtriser l'outil informatique. Utilisant désormais les SIG (systèmes d'information géographique), des compétences en programmation constituent un réel plus à l'embauche. Une mise à jour régulière et l'apprentissage des nouveaux outils et logiciels sont nécessaires. La connaissance de l'anglais est également requise.</p>
            <h5>Une part de créativité</h5>
            <p>Les cartographes doivent également posséder d'un certain sens artistique. Précision et créativité permettent de choisir des présentations et des styles différents en fonction du projet en cours. En effet, il n'y a rien de commun entre une carte destinée à une collectivité locale, pour prendre la décision d'implanter ou non un centre commercial par exemple, celle qui sert au grand public pour se repérer dans une ville ou celle que vous consultez sur votre téléphone. Les cartographes s'adaptent aux demandes et respectent les délais imposés. Avec de l'expérience, les cartographes deviennent force de proposition.</p>
            <h5>Rigueur de mise</h5>
            <p>Pédagogues, les cartographes se mettent au niveau de leurs interlocuteurs. Leurs qualités : rigueur, esprit d'analyse et de synthèse, sens du détail et capacités rédactionnelles.</p>]]></competences>*/
            String competences,
        /*
        <condition_travail><![CDATA[
            <h5>Surtout sur ordinateur</h5>
            <p>Les cartographes topographes exercent sur le terrain. Leurs observations et les mesures utilisées (topographie, toponymie, géodésie) sont généralement effectuées par les spécialistes de la cartographie et de la topographie de l'IGN (Institut national de l'information géographique et forestière). Ces derniers fournissent les documents-ressources pour les cartographes topographes qui travaillent essentiellement sur ordinateur, dans un bureau. Réunions, surtout lors du lancement et de la restitution des résultats de travaux de recherche font partie du métier. Les cartographes géographes peuvent se déplacer mais leur travail a évolué depuis la généralisation des drones.</p>
            <h5>Collectivités ou entreprises</h5>
            <p>Les SIG (systèmes d'information géographique) permettent à une commune de visualiser son parc immobilier, de gérer ses permis de construire, de réaliser la simulation d'une crue en 3D, d'une évolution urbaine, etc. Les grandes entreprises d'aménagement, de travaux publics, de gestion de l'eau, etc. sont également utilisatrices.</p>
            <h5>Plusieurs niveaux</h5>
            <p>Les cartographes travaillent en toute autonomie ou en équipe. Ils sont en relation avec les utilisateurs ou les chefs de projet.</p>]]></condition_travail>*/
            String condition_travail,
        /*
        <nature_travail><![CDATA[
            <h5>Géographe ou topographe</h5>
            <p>On distingue 2 grandes catégories de cartographes. Les cartographes topographes, de l'IGN (Institut national de l'information géographique et forestière) par exemple, sont spécialistes de la représentation physique d'un terrain (élévations, cours d'eau, etc.). Ils ou elles réalisent des cartes à partir de relevés, de photos aériennes... Les cartographes géographes réalisent des cartes sur des sujets précis : répartition de la population, évolution des scrutins politiques, espérance de vie... en croisant des données statistiques et géographiques.</p>
            <h5>L'indispensable informatique</h5>
            <p>Depuis l'apparition des SIG (systèmes d'information géographique), les cartographes doivent savoir administrer des bases de données et en exploiter les données nécessaires à leur travail. Après avoir interprété les données, les cartographe choisissent leur mode de représentation graphique (2D ou 3D pour une image en relief), qui sera ensuite réalisée sur un support papier ou en images virtuelles.</p>
            <h5>Des applications diverses</h5>
            <p>Les applications de la cartographie sont multiples : simuler des évolutions urbaines, suivre des évolutions environnementales comme les crues, optimiser un service de livraison, gérer l'éclairage urbain... Les cartographes font aussi le lien avec les utilisateurs et apportent leur expertise aux commanditaires.</p>]]></nature_travail>*/
            String nature_travail,
            /*
        <acces_metier><![CDATA[
            <p>De plus en plus d'employeurs demandent un niveau bac + 5 à l'embauche.</p>
            <p>Niveau bac + 5</p>
            <p>Master mentions géomatique ; géographie ; master géographie, aménagement, environnement et développement</p>
            <p>Diplôme d'ingénieur de l'École nationale des sciences géographiques</p>]]></acces_metier>*/
            String acces_metier,
            /*
        <vie_professionnelle><![CDATA[
            <h3>Salaire</h3>
            <h5>Salaire du débutant</h5>
            <p>À partir du Smic au niveau technicien dans la fonction publique territoriale. Davantage pour les ingénieurs et dans le privé.</p>
            <h3>Intégrer le marché du travail</h3>
            <h5>Débouchés limités</h5>
            <p>Le nombre d'emplois reste limité même si les employeurs potentiels sont multiples. Les postes se situent dans les services ou entreprises de cartographie : Michelin, Service hydrographique et océanographique de la marine à Brest, syndicats d'initiative ou sociétés de conseil, BRGM (Bureau de recherches géologiques et minières), EDF, Air France... ainsi que dans les quelques maisons d'édition spécialisées. On trouve aussi des cartographes en bureau d'études, cabinets de géomètres, collectivités territoriales ou de l'État.</p>
            <h5>Mais de nouveaux acteurs</h5>
            <p>D'ici 2026, l'IGN (Institut national de l'information géographique et forestière) aura cartographié l'ensemble du territoire français en 3D, tandis que les GAFA (géants du web) visent la planète ! Sites, applications, etc. sont de plus en plus nombreux à développer des cartes virtuelles et recherchent des cartographes spécialistes des bases de données et du traitement numérique des données de terrain. Leurs compétences en graphisme sont appréciées pour se différencier des concurrents.</p>
            <h5>Multiplier les compétences</h5>
            <p>Pour évoluer, les cartographes ont intérêt à avoir une double ou triple compétence. Par exemple, en marketing ou en développement informatique, de plus en plus nécessaire dans le métier. Des évolutions de carrière existent : postes de chef ou cheffe de projet, par exemple.</p>]]></vie_professionnelle>*/
            String vie_professionnelle,
            /*            /*
        <accroche_metier><![CDATA[
            <p>Les cartographes exploitent des données pour fournir une représentation visuelle d'un territoire, concernant des notions abstraites (carte politique, économique, démographique,...) ou par des éléments physiques (carte marine, routière, géologique...).</p>]]></accroche_metier>
            */
            String accroche_metier,
            /*
        <formats_courts>
            <format_court>
                <type>Fiche metier (Documentation)</type>
                <libelle>cartographe</libelle>
                <descriptif><![CDATA[
                    <p>Du plan de métro à la carte électorale, la cartographie s'affiche partout. Il existe une grande variété de cartes, qui permettent la connaissance, la représentation, la gestion et l'aménagement d'un territoire, par exemple. Le travail des cartographes a beaucoup évolué avec l'outil informatique et les SIG (systèmes d'information géographique). Les débouchés se situent dans les collectivités territoriales, les grandes entreprises, les bureaux d'études, de géomètres ou topographes, l'IGN, etc.</p>]]></descriptif>
            </format_court>
            <format_court>
                <type>Dico des métiers</type>
                <libelle>cartographe</libelle>
                <descriptif><![CDATA[
                    <p>Les cartographes exploitent des données pour fournir une représentation visuelle d'un territoire, concernant des notions abstraites (carte politique, économique, démographique...) ou par des éléments physiques (carte marine, routière, géologique...). Leur maîtrise des SIG (systèmes d'information géographique) leur permettent, par exemple, de permettre à une commune de visualiser son parc immobilier, de gérer ses permis de construire ou de réaliser une simulation en 3D d'une crue. Le nombre d'emplois reste limité même si les employeurs potentiels sont multiples (bureaux d'études, cabinets de topographes, sites de webmapping...). Les cartographe allient rigueur, maîtrise des outils informatiques spécifiques et un certain sens artistique pour la création des cartes.</p>
                    <h5>Durée des études</h5>
                    <h4>Après le bac</h4>
                    <p>5 ans pour préparer un master (géographie ; géomatique ; géographie, aménagement, environnement et développement) ou le diplôme d'ingénieur de l'ENSG (École nationale des sciences géographiques).</p>]]></descriptif>
            </format_court>
        </formats_courts>*/
        FormatsCourts formats_courts,
                /*
        <statuts>
            <statut>
                <id>T-ITM.9</id>
                <id_ideo1>100215</id_ideo1>
                <libelle>salarié</libelle>
            </statut>
            <statut>
                <id>T-ITM.1</id>
                <id_ideo1>100214</id_ideo1>
                <libelle>fonctionnaire</libelle>
            </statut>
        </statuts>
        <metiers_associes/>
        <niveau_acces_min>
            <id>REF.410</id>
            <libelle>Bac + 5</libelle>
        </niveau_acces_min>*/
            NiveauAccesMin niveau_acces_min,
            /*
        <formations_min_requise>
            <formation_min_requise>
                <id>FOR.1013</id>
                <libelle>diplôme d'ingénieur de l'École nationale des sciences géographiques</libelle>
            </formation_min_requise>
            <formation_min_requise>
                <id>FOR.4551</id>
                <libelle>mastère spé. DeSIgeo, décision et systèmes d'information géolocalisée (ENSG - Géomatique - École des Ponts ParisTech - CNAM)</libelle>
            </formation_min_requise>
            <formation_min_requise>
                <id>FOR.4660</id>
                <libelle>master géomatique</libelle>
            </formation_min_requise>
            <formation_min_requise>
                <id>FOR.5188</id>
                <libelle>master géographie</libelle>
            </formation_min_requise>
            <formation_min_requise>
                <id>FOR.5189</id>
                <libelle>master géographie, aménagement, environnement et développement</libelle>
            </formation_min_requise>
        </formations_min_requise>*/
            FormationsMinRequise formations_min_requise, /*
        <publications/>
        <sources_numeriques>
            <source>
                <valeur>http://www.shom.fr</valeur>
                <commentaire>Site du Service hydrographique et océanographique de la marine.</commentaire>
            </source>
            <source>
                <valeur>http://www.ign.fr</valeur>
                <commentaire>Site de l'IGN (Institut national de l'information géographique et forestière).</commentaire>
            </source>
        </sources_numeriques>*/
            SourcesNumeriques sources_numeriques,
            /*
        "secteurs_activite": {
          "secteur_activite": [
            {
              "id": "T-IDEO2.4852",
              "libelle": "Agriculture"
            },
            {
              "id": "T-IDEO2.4863",
              "libelle": "Bâtiment et travaux publics (BTP)"
            },
            {
              "id": "T-IDEO2.4865",
              "libelle": "Commerce et distribution"
            },
            {
              "id": "T-IDEO2.4848",
              "libelle": "Énergie"
            },
            {
              "id": "T-IDEO2.5668",
              "libelle": "Industries"
            }
          ]
        },*/
            SecteursActivites secteurs_activite,
        /*
        <centres_interet>
            <centre_interet>
                <id>T-IDEO2.5718</id>
                <libelle>j'ai un bon coup de crayon</libelle>
            </centre_interet>
        </centres_interet>*/
        CentresInterets centres_interet,
            MetiersAssocies metiers_associes
            /*
    </metier>
    */

    ) {

        public boolean isMetierSup(Collection<String> idFormationsSup) {
            return niveau_acces_min == null
                    || niveau_acces_min.id == null
                    || niveau_acces_min.id.isBlank()
                    || niveau_acces_min.equals("null")
                    || niveau_acces_min().libelle().contains("Bac + ")
                    || formations_min_requise != null && formations_min_requise.isSup(idFormationsSup);
        }

    }

    public record MetiersAssocies(
            List<MetierAssocie> metier_associe
    ) {
        public record MetierAssocie(
                String id,
                String libelle
        ) {

        }
    }

    public record FormatsCourts(
            List<FormatCourt> format_court
    )
    {

    }

    public record FormatCourt(
            /*<format_court>
                <type>Fiche metier (Documentation)</type>
                <libelle>cartographe</libelle>
                <descriptif><![CDATA[
                    <p>Du plan de métro à la carte électorale, la cartographie s'affiche partout. Il existe une grande variété de cartes, qui permettent la connaissance, la représentation, la gestion et l'aménagement d'un territoire, par exemple. Le travail des cartographes a beaucoup évolué avec l'outil informatique et les SIG (systèmes d'information géographique). Les débouchés se situent dans les collectivités territoriales, les grandes entreprises, les bureaux d'études, de géomètres ou topographes, l'IGN, etc.</p>]]></descriptif>
            </format_court>
           */
            String type,
            String libelle,
            String descriptif
    ) {

    }

    public record NiveauAccesMin(
            /*<niveau_acces_min>
            <id>REF.410</id>
            <libelle>Bac + 5</libelle>
        </niveau_acces_min>*/
            String id,
            String libelle
    ) {


    }

    public record FormationsMinRequise(
            List<FormationMinRequise> formation_min_requise
    ) {

        public boolean isSup(Collection<String> idFormationsSup) {
            return formation_min_requise != null && formation_min_requise.stream().anyMatch(f -> idFormationsSup.contains(f.id));
        }
    }

            public record FormationMinRequise(
        /*            <formation_min_requise>
                <id>FOR.1013</id>
                <libelle>diplôme d'ingénieur de l'École nationale des sciences géographiques</libelle>
            </formation_min_requise>
    */
    String id,
    String libelle
    ) {

    }

    public record SourcesNumeriques(
            List<SourceNumerique> sources_numeriques
    ) {

    }


    public record SourceNumerique(
            /*<source>
                <valeur>http://www.shom.fr</valeur>
                <commentaire>Site du Service hydrographique et océanographique de la marine.</commentaire>
            </source>*/
            String valeur,
            String commentaire
    ) {

    }

    public record SecteursActivites(
            List<SecteurActivite> secteur_activite
    ) {
        boolean hasDomain(String key) {
            return secteur_activite.stream().anyMatch(s -> s.id.equals(key));
        }

    }

    public record SecteurActivite(
            String id,
            String libelle
    ) {

    }

    public record CentresInterets(
            List<CentreInteret> centre_interet
    ){

    }
    public record CentreInteret(
            /*            <centre_interet>
                <id>T-IDEO2.5718</id>
                <libelle>j'ai un bon coup de crayon</libelle>
            </centre_interet>
        */
            String id,
            String libelle
    ) {

    }

    public record Synonymes(
            List<String> synonyme
    ) {

    }

    public record Romesv3(
            List<String> romeV3
    ) {

    }
}

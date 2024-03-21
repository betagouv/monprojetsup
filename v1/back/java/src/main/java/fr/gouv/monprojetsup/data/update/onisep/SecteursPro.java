package fr.gouv.monprojetsup.data.update.onisep;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.tools.Serialisation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record SecteursPro(
        List<SecteurPro> domaines
) {


    public static SecteursPro load() throws IOException {
        SecteursPro secteurs = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.SECTEURS_PRO_PATH),
                SecteursPro.class
        );
        SecteursPro copy = new SecteursPro(new ArrayList<>());
        secteurs.domaines.forEach(sec ->
            copy.domaines.add(new SecteurPro(
                    Constants.cleanup(sec.id).replace(Constants.SEC_ACT_PREFIX_IN_FILES, Constants.SEC_ACT_PREFIX_IN_GRAPH),
                    sec.nom,
                    sec.description,
                    sec.url
            ))
        );
        return copy;
    }


    /*{"domaines" : [
     {
       "id": "T-IDEO2.4852",
       "nom": "Agriculture",
       "description": "Traitement des champs guidé par GPS, suivi de cultures sous serres par ordinateur, robots de traite, automates distribuant l'alimentation des animaux... les nouvelles technologies font évoluer le métier d'agriculteur. Un métier qui génère de nombreux emplois dans les activités de production, de conseil et de commercialisation.",
       "url": "https://www.onisep.fr/metier/decouvrir-le-monde-professionnel/agriculture/les-metiers-et-l-emploi-dans-l-agriculture"
     },*/
    public record SecteurPro(
            String id,
            String nom,
            String description,
            String url
    )
    {

    }
}

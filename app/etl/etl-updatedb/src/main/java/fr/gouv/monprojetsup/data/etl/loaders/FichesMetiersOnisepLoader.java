package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.onisep.FichesMetierOnisep;
import fr.gouv.monprojetsup.data.etl.sources.DataSources;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;

import static fr.gouv.monprojetsup.data.etl.sources.DataSources.ONISEP_FICHES_METIERS;

public class FichesMetiersOnisepLoader {
    public static FichesMetierOnisep loadFichesMetierOnisep(DataSources sources) throws IOException {
        FichesMetierOnisep orig = Serialisation.fromJsonFile(sources.getSourceDataFilePath(ONISEP_FICHES_METIERS), FichesMetierOnisep.class);
        FichesMetierOnisep copy = new FichesMetierOnisep(new FichesMetierOnisep.FicheMetierOnisepContainer(new ArrayList<>()));
        orig.metiers().metier().forEach(f -> {
                    FichesMetierOnisep.FicheMetierOnisep fiche = new FichesMetierOnisep.FicheMetierOnisep(
                            Constants.cleanup(f.identifiant()),
                            f.nom_metier(),
                            f.libelle_feminin(),
                            f.libelle_masculin(), f.synonymes(),
                            f.romesV3(),
                            f.competences(),
                            f.condition_travail(),
                            f.nature_travail(),
                            f.acces_metier(),
                            f.vie_professionnelle(),
                            f.accroche_metier(),
                            f.formats_courts(),
                            f.niveau_acces_min(),
                            f.formations_min_requise(),
                            f.sources_numeriques(),
                            cleanup(f.secteurs_activite()),
                            f.centres_interet(),
                            f.metiers_associes()
                    );
                    copy.metiers().metier().add(fiche);

                }
        );
        return copy;
    }

    private static FichesMetierOnisep.SecteursActivites cleanup(@Nullable FichesMetierOnisep.SecteursActivites toClean) {
        FichesMetierOnisep.SecteursActivites result = new FichesMetierOnisep.SecteursActivites(new ArrayList<>());
        if (toClean == null) return result;
        toClean.secteur_activite().forEach(s -> result.secteur_activite().add(
                new FichesMetierOnisep.SecteurActivite(
                        Constants.cleanup(s.id()).replace(Constants.SEC_ACT_PREFIX_IN_FILES, Constants.SEC_ACT_PREFIX_IN_GRAPH),
                        s.libelle()
                )
        ));
        return result;
    }
}

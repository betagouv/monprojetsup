package fr.gouv.monprojetsup.data.etl.loaders;

import fr.gouv.monprojetsup.data.domain.model.specialites.Specialites;
import fr.gouv.monprojetsup.data.domain.model.stats.AdmisMatiereBacAnnee;
import fr.gouv.monprojetsup.data.domain.model.stats.AdmisMatiereBacAnneeStats;
import fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques.ANNEE_LYCEE_TERMINALE;
import static fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques.TOUS_BACS_CODE;
import static fr.gouv.monprojetsup.data.tools.Serialisation.fromJsonFile;

public class SpecialitesLoader {
    public static Specialites load(PsupStatistiques statistiques, DataSources sources) throws IOException {

        Specialites specs = fromJsonFile(sources.getSourceDataFilePath(DataSources.SPECIALITES_FILENAME), Specialites.class);

        AdmisMatiereBacAnneeStats stats = statistiques.admisMatiereBacAnneeStats;

        if(specs.specialitesParBac().isEmpty()) {
            //on supprime des stats ce qui n'est pas des nbAdmisParSpecialite
            stats.stats().removeIf(
                    admisMatiereBacAnnee -> !specs.specialites().containsKey(admisMatiereBacAnnee.iMtCod())
            );
            Map<String, List<AdmisMatiereBacAnnee>> statsParBac = stats.stats().stream()
                    .filter(s -> specs.specialites().containsKey(s.iMtCod()))
                    .collect(Collectors.groupingBy(
                            AdmisMatiereBacAnnee::bac
                    ));

            statsParBac.forEach((s, admisMatiereBacAnnees) -> specs.specialitesParBac().put(s,
                    admisMatiereBacAnnees.stream()
                            .filter(admisMatiereBacAnnee -> admisMatiereBacAnnee.annLycee() == ANNEE_LYCEE_TERMINALE)
                            .filter(admisMatiereBacAnnee -> admisMatiereBacAnnee.nb() > 50)
                            .map(AdmisMatiereBacAnnee::iMtCod)
                            .collect(Collectors.toSet())
                    ));
        }

        specs.specialitesParBac().put(
                TOUS_BACS_CODE,
                specs.specialitesParBac().entrySet().stream()
                        .flatMap(e -> e.getValue().stream())
                        .collect(Collectors.toSet())
                );
        /* hack to fix data update bug  */
        if(specs.specialitesParBac().get(TOUS_BACS_CODE).isEmpty()) {
            specs.specialitesParBac().get(TOUS_BACS_CODE).addAll(specs.specialites().keySet());
        }
        return specs;
    }

    private SpecialitesLoader() {

    }
}

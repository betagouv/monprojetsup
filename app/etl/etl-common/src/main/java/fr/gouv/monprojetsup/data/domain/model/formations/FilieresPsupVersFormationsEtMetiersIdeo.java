package fr.gouv.monprojetsup.data.domain.model.formations;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.PsupToIdeoCorrespondance;

import java.util.Arrays;
import java.util.List;

import static fr.gouv.monprojetsup.data.domain.Constants.TYPE_FORMATION_PREFIX;

public record FilieresPsupVersFormationsEtMetiersIdeo(
        String gFlCod,
        String gFrCod,
        String gFrLib,
        String gFlLib,
        List<String> ideoFormationsIds,
        List<String> ideoMetiersIds
) {
    public static List<FilieresPsupVersFormationsEtMetiersIdeo> get(
            PsupToIdeoCorrespondance lines
    ) {

        return lines.psupToIdeo2().stream().map(line ->
                {
                    List<String> ideoFormationsIds =
                            Arrays.stream(line.IDS_IDEO2()
                                            .split(";"))
                                    .map(String::trim)
                                    .filter(s -> !s.isBlank())
                                    .toList();

                    List<String> ideoMetiersIds =
                            Arrays.stream(line.METIER_IDEO2().split(";"))
                                    .map(String::trim)
                                    .map(Constants::cleanup)
                                    .toList();

                    return new FilieresPsupVersFormationsEtMetiersIdeo(
                            Constants.gFlCodToFrontId(line.G_FL_COD()),
                            TYPE_FORMATION_PREFIX + line.G_FR_COD(),
                            line.G_FR_LIB(),
                            line.G_FL_LIB(),
                            ideoFormationsIds,
                            ideoMetiersIds
                    );
                }
        ).toList();
    }

}

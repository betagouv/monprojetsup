package fr.gouv.monprojetsup.data.domain.model.formations;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.PsupToIdeoCorrespondance;

import java.util.Arrays;
import java.util.List;

import static fr.gouv.monprojetsup.data.domain.Constants.TYPE_FORMATION_PREFIX;

public record FiliereToFormationsOnisep(
        String gFlCod,
        String gFrCod,
        String gFrLib,
        String gFlLib,
        List<String> ideoIds
) {
    public static List<FiliereToFormationsOnisep> getFilieres(
            PsupToIdeoCorrespondance lines
    ) {

        return lines.psupToIdeo2().stream().map(line ->
                new FiliereToFormationsOnisep(
                        Constants.gFlCodToFrontId(line.G_FL_COD()),
                        TYPE_FORMATION_PREFIX + line.G_FR_COD(),
                        line.G_FR_LIB(),
                        line.G_FL_LIB(),
                        Arrays.stream(line.IDS_IDEO2()
                                        .split(";"))
                                .map(String::trim)
                                .filter(s -> !s.isBlank())
                                .toList()
                )
        ).toList();
    }

}

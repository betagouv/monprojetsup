package fr.gouv.monprojetsup.data.infrastructure.onisep;

import java.util.List;

public record LienThematiqueFormation2(
        List<ThematiqueFormationPaireOnisep2> paires
) {

    public record ThematiqueFormationPaireOnisep2(
            String flCod,
            String Themas
    ) {

    }
}
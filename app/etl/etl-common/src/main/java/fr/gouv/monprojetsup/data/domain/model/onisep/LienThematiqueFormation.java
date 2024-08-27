package fr.gouv.monprojetsup.data.domain.model.onisep;

import java.util.List;

public record LienThematiqueFormation(
        List<ThematiqueFormationPaireOnisep> paires
) {
    public record ThematiqueFormationPaireOnisep(
            Integer gFlCod,
            String item
    ) {

    }
}

package fr.gouv.monprojetsup.suggestions.data.update.onisep;

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
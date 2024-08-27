package fr.gouv.monprojetsup.data.domain.model.onisep.billy;


import java.util.List;


public record PsupToOnisepLines(
        List<PsupToOnisepLine> psupToIdeo2
) {
    public record PsupToOnisepLine(
            //"G_FR_COD": "43",
            String G_FR_COD,
            //"G_FR_LIB": "BTS - Services",
            String G_FR_LIB,
            //"G_FL_COD": "358",
            String G_FL_COD,
            //"G_FL_LIB": "M\u00e9tiers de l'esth\u00e9tique-cosm\u00e9tique-parfumerie",
            String G_FL_LIB,
            //"IDS_IDEO2": "FOR.6162 ; FOR.6163 ; FOR.6164",
            String IDS_IDEO2,
            //"METIER_IDEO2": "formulateur / formulatrice ; esth\u00e9ticien-cosm\u00e9ticien / esth\u00e9ticienne-cosm\u00e9ticienne"
            String METIER_IDEO2
    ) {

    }
}

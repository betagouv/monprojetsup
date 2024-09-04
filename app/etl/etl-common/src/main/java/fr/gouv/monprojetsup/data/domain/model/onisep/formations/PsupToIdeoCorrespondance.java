package fr.gouv.monprojetsup.data.domain.model.onisep.formations;


import fr.gouv.monprojetsup.data.domain.Constants;
import lombok.val;

import java.util.List;
import java.util.Map;


public record PsupToIdeoCorrespondance(
        List<PsupToOnisepLine> psupToIdeo2
) {

    public record PsupToOnisepLine(
            //"G_FR_COD": "43",
            String G_FR_COD,
            //"G_FR_LIB": "BTS - Services",
            String G_FR_LIB,
            //"G_FL_COD": "358",
            String G_FL_COD,
            //"G_FL_LIB": "Métiers de l'esthétique-cosmétique-parfumerie",
            String G_FL_LIB,
            //"IDS_IDEO2": "FOR.6162 ; FOR.6163 ; FOR.6164",
            String IDS_IDEO2,
            //"METIER_IDEO2": "formulateur / formulatrice ; esthéticien-cosméticien / esthéticienne-cosméticienne"
            String METIER_IDEO2
    ) {

        public String getMpsId() {
            return Constants.gFlCodToFrontId(G_FL_COD);
        }

        public boolean isIEP() {
            return G_FR_COD.equals(Integer.toString(Constants.IEP_PSUP_FR_COD));
        }

        public boolean isEcoleIngenieur() {
            return G_FR_COD.equals(Integer.toString(Constants.ECOLE_INGE_PSUP_FR_COD));
        }

        public boolean isEcoleCommerce() {
            return G_FR_COD.equals(Integer.toString(Constants.ECOLE_COMMERCE_PSUP_FR_COD))
                    || G_FR_COD.equals(Integer.toString(Constants.CUPGE_ECO_GESTION_FR_COD));
        }

        public boolean isEcoleArchitecture() {
            return G_FL_COD.equals(Integer.toString(Constants.ECOLE_ARCHI_PSUP_FL_COD))
                    || G_FL_COD.equals(Integer.toString(Constants.ECOLE_ARCHI_INGE_PSUP_FL_COD));
        }

        public boolean isEcoleArt() {
            return G_FR_COD.equals(Integer.toString(Constants.ECOLE_ART_PSUP_FR_COD))
                    || G_FR_COD.equals(Integer.toString(Constants.DIPLOME_ART_PSUP_FR_COD))
            ;
        }

        public boolean isEcoleconservationRestauration() {
            return G_FL_COD.equals(Integer.toString(Constants.ECOLE_CONSERVATION_RESTAURATION_PSUP_FL_COD));
        }

        public boolean isDMA() {
            return G_FR_COD.equals(Integer.toString(Constants.DMA_PSUP_FR_COD));
        }
    }

    public static PsupToIdeoCorrespondance fromCsv(List<Map<String, String>> csv) {
        val result = csv.stream().map(line ->
            new PsupToOnisepLine(
                    line.get("G_FR_COD"),
                    line.get("G_FR_LIB"),
                    line.get("G_FL_COD"),
                    line.get("G_FL_LIB"),
                    line.get("IDS_IDEO2"),
                    line.get("METIER_IDEO2")
            )).toList();
        return new PsupToIdeoCorrespondance(result);
    }

}

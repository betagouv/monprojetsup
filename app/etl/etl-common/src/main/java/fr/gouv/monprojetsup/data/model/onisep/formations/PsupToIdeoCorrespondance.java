package fr.gouv.monprojetsup.data.model.onisep.formations;


import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.tools.CsvTools;
import lombok.val;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.gouv.monprojetsup.data.Constants.*;


public record PsupToIdeoCorrespondance(
        List<PsupToOnisepLine> psupToIdeo2
) {

    public void generateDiagnostic(Set<String> formationsIdeo) throws IOException {
        try(val csvTools = CsvTools.getWriter(
                Constants.DIAGNOSTICS_OUTPUT_DIR + "psupToIdeoCorrespondanceIdeosInconnus.csv")) {
            csvTools.appendHeaders(List.of(
                    "G_FR_COD type formation psup",
                    "G_FR_LIB type formation psup",
                    "G_FL_COD filiere psup",
                    "G_FL_LIB filiere psup",
                    "IDS_IDEO2_ORIGINAUX",
                    "IDS_IDEO2_INCONNUS"
            ));
            for (PsupToOnisepLine line : psupToIdeo2) {
                val ideosInconnus = Arrays.stream(line.IDS_IDEO2.split(";"))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .filter(ideo -> !formationsIdeo.contains(ideo))
                        .toList();
                if (!ideosInconnus.isEmpty()) {
                    csvTools.append(List.of(
                            Integer.toString(line.G_FR_COD),
                            line.G_FR_LIB,
                            Integer.toString(line.G_FL_COD),
                            line.G_FL_LIB,
                            line.IDS_IDEO2,
                            String.join(" ; ", ideosInconnus)
                    ));
                }
            }
        }
    }

    public record PsupToOnisepLine(
            //"G_FR_COD": "43",
            int G_FR_COD,
            //"G_FR_LIB": "BTS - Services",
            String G_FR_LIB,
            //"G_FL_COD": "358",
            int G_FL_COD,
            //"G_FL_LIB": "Métiers de l'esthétique-cosmétique-parfumerie",
            String G_FL_LIB,
            //"IDS_IDEO2": "FOR.6162 ; FOR.6163 ; FOR.6164",
            String IDS_IDEO2,
            //"METIER_IDEO2": "formulateur / formulatrice ; esthéticien-cosméticien / esthéticienne-cosméticienne"
            String METIER_IDEO2,

            String LIENONISEP
    ) {

        public boolean isIEP() {
            return G_FR_COD == IEP_PSUP_FR_COD;
        }

        public boolean isEcoleIngenieur() {
            return G_FR_COD == Constants.ECOLES_INGE_PSUP_FR_COD;
        }

        public boolean isEcoleCommerce() {
            return G_FR_COD == Constants.ECOLE_COMMERCE_PSUP_FR_COD
                    //|| G_FR_COD.equals(Integer.toString(Constants.CUPGE_ECO_GESTION_FR_COD))
                ;
        }

        public boolean isEcoleArchitecture() {
            return G_FL_COD == ECOLE_ARCHI_PSUP_FL_COD
                    || G_FL_COD == ECOLE_ARCHI_INGE_PSUP_FL_COD;
        }

        public boolean isEcoleArt() {
            return G_FR_COD == ECOLE_ART_PSUP_FR_COD
                    || G_FR_COD == DIPLOME_ART_PSUP_FR_COD
            ;
        }

        public boolean isEcoleconservationRestauration() {
            return G_FL_COD == ECOLE_CONSERVATION_RESTAURATION_PSUP_FL_COD;
        }

        public boolean isDMA() {
            return G_FR_COD == DMA_PSUP_FR_COD;
        }
    }

    public static PsupToIdeoCorrespondance fromCsv(List<Map<String, String>> csv) {
        val result = csv.stream().map(line ->
            new PsupToOnisepLine(
                    Integer.parseInt(line.get("G_FR_COD").trim()),
                    line.get("G_FR_LIB"),
                    Integer.parseInt(line.get("G_FL_COD").trim()),
                    line.get("G_FL_LIB"),
                    line.get("IDS_IDEO2"),
                    line.get("METIER_IDEO2"),
                    line.get("LIENONISEP")
            )).toList();
        return new PsupToIdeoCorrespondance(result);
    }

}

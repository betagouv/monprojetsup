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
                    "gFrCod type formation psup",
                    "gFrLib type formation psup",
                    "gFlCod filiere psup",
                    "gFlLib filiere psup",
                    "IDS_IDEO2_ORIGINAUX",
                    "IDS_IDEO2_INCONNUS"
            ));
            for (PsupToOnisepLine line : psupToIdeo2) {
                val ideosInconnus = Arrays.stream(line.idsideos.split(";"))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .filter(ideo -> !formationsIdeo.contains(ideo))
                        .toList();
                if (!ideosInconnus.isEmpty()) {
                    csvTools.append(List.of(
                            Integer.toString(line.gFrCod),
                            line.gFrLib,
                            Integer.toString(line.gFlCod),
                            line.gFlLib,
                            line.idsideos,
                            String.join(" ; ", ideosInconnus)
                    ));
                }
            }
        }
    }

    public record PsupToOnisepLine(
            //"gFrCod": "43",
            int gFrCod,
            //"gFrLib": "BTS - Services",
            String gFrLib,
            //"gFlCod": "358",
            int gFlCod,
            //"gFlLib": "Métiers de l'esthétique-cosmétique-parfumerie",
            String gFlLib,
            //"idsideos": "FOR.6162 ; FOR.6163 ; FOR.6164",
            String idsideos,
            String onisepLink
    ) {

        public boolean isIEP() {
            return gFrCod == IEP_PSUP_FR_COD;
        }

        public boolean isEcoleIngenieur() {
            return gFrCod == Constants.ECOLES_INGE_PSUP_FR_COD;
        }

        public boolean isEcoleCommerce() {
            return gFrCod == Constants.ECOLE_COMMERCE_PSUP_FR_COD
                    //|| gFrCod.equals(Integer.toString(Constants.CUPGE_ECO_GESTION_FR_COD))
                ;
        }

        public boolean isEcoleArchitecture() {
            return gFlCod == ECOLE_ARCHI_PSUP_FL_COD
                    || gFlCod == ECOLE_ARCHI_INGE_PSUP_FL_COD;
        }

        public boolean isEcoleArt() {
            return gFrCod == ECOLE_ART_PSUP_FR_COD
                    || gFrCod == DIPLOME_ART_PSUP_FR_COD
            ;
        }

        public boolean isEcoleconservationRestauration() {
            return gFlCod == ECOLE_CONSERVATION_RESTAURATION_PSUP_FL_COD;
        }

        public boolean isDMA() {
            return gFrCod == DMA_PSUP_FR_COD;
        }
    }

    public static PsupToIdeoCorrespondance fromCsv(List<Map<String, String>> csv) {
        val result = csv.stream().map(line ->
            new PsupToOnisepLine(
                    Integer.parseInt(line.get("CODEFORMATION").trim()),
                    line.get("LIBELLEFORMATION"),
                    Integer.parseInt(line.get("CODESPECIALITE").trim()),
                    line.get("LIBELLESPECIALITE"),
                    line.get("LIS_ID_ONI2"),
                    line.get("LIENONISEP")
            )).toList();
        return new PsupToIdeoCorrespondance(result);
    }

}

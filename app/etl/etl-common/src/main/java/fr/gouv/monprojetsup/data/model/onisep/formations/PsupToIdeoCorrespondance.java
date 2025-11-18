package fr.gouv.monprojetsup.data.model.onisep.formations;


import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.tools.CsvTools;
import lombok.val;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.gouv.monprojetsup.data.Constants.DIPLOME_ART_PSUP_FR_COD;
import static fr.gouv.monprojetsup.data.Constants.ECOLE_ARCHI_INGE_PSUP_FL_COD;
import static fr.gouv.monprojetsup.data.Constants.ECOLE_ARCHI_PSUP_FL_COD;
import static fr.gouv.monprojetsup.data.Constants.ECOLE_ART_PSUP_FR_COD;
import static fr.gouv.monprojetsup.data.Constants.ECOLE_CONSERVATION_RESTAURATION_PSUP_FL_COD;
import static fr.gouv.monprojetsup.data.Constants.IEP_PSUP_FR_COD;


public record PsupToIdeoCorrespondance(
        List<PsupToOnisepLine> psupToIdeo2
) {

    public void generateDiagnostic(Set<String> formationsIdeo) throws IOException {
        val outputPath = Path.of(Constants.DIAGNOSTICS_OUTPUT_DIR + "psupToIdeoCorrespondanceIdeosInconnus.csv");
        boolean used = false;
        try(val csvTools = CsvTools.getWriter(outputPath.toString())) {
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
                    used = true;
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
        if(!used) {
            outputPath.toFile().delete();
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

    }

    public static PsupToIdeoCorrespondance fromCsv(List<Map<String, String>> csv) {
        val result = csv.stream().map(line ->
            new PsupToOnisepLine(
                    Integer.parseInt(line.get("CODEFORMATION").trim()),
                    line.get("LIBELLÉFORMATION"),
                    Integer.parseInt(line.get("CODESPÉCIALITÉ").trim()),
                    line.get("LIBELLÉSPÉCIALITÉ"),
                    "",
                    line.get("LIENONISEP")
            )).toList();
        return new PsupToIdeoCorrespondance(result);
    }

}

package fr.gouv.monprojetsup.data.domain.model.formations;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.PsupToIdeoCorrespondance;
import jakarta.validation.constraints.NotNull;
import lombok.val;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.Constants.TYPE_FORMATION_PREFIX;

public record FilieresPsupVersFormationsEtMetiersIdeo(
        String gFlCod,
        String gFrCod,
        String gFrLib,
        String gFlLib,
        @NotNull List<@NotNull String> ideoFormationsIds,
        @NotNull List<@NotNull String> ideoMetiersIds
) {

    private static final Logger logger = Logger.getLogger(FilieresPsupVersFormationsEtMetiersIdeo.class.getSimpleName());
    public static List<FilieresPsupVersFormationsEtMetiersIdeo> compute(
            PsupToIdeoCorrespondance lines,
            List<FormationIdeoDuSup> formationsIdeo) {

        try {
            lines.generateDiagnostic(formationsIdeo.stream().map(FormationIdeoDuSup::ideo).collect(Collectors.toSet()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        val ideoKeysIEP = formationsIdeo.stream()
                .filter(FormationIdeoDuSup::estIEP)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysIEP.isEmpty()) {
            throw new IllegalStateException("Pas d'IEP dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour science Po: " + String.join(" ; ", ideoKeysIEP));
        }


        val ideoKeysEcoleIngenieur = formationsIdeo.stream()
                .filter(FormationIdeoDuSup::estEcoleIngenieur)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysEcoleIngenieur.isEmpty()) {
            throw new IllegalStateException("Pas d'école d'ingénieur dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour écoles d'ingénieur: " + String.join(" ; ", ideoKeysEcoleIngenieur));
        }


        val ideoKeysEcoleCommerce = formationsIdeo.stream()
                .filter(FormationIdeoDuSup::estEcoleCommerce)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysEcoleCommerce.isEmpty()) {
            throw new IllegalStateException("Pas d'école de commerce dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour écoles de commerce: " + String.join(" ; ", ideoKeysEcoleCommerce));
        }

        val ideoKeysEcoleArchi = formationsIdeo.stream()
                .filter(FormationIdeoDuSup::estEcoleArchitecture)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysEcoleArchi.isEmpty()) {
            throw new IllegalStateException("Pas d'école d'architecture dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour écoles de architecture: " + String.join(" ; ", ideoKeysEcoleArchi));
        }

        val ideoKeysEcoleArt = formationsIdeo.stream()
                .filter(FormationIdeoDuSup::estEcoleArt)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysEcoleArt.isEmpty()) {
            throw new IllegalStateException("Pas d'école d'art dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour écoles d'art: " + String.join(" ; ", ideoKeysEcoleArt));
        }

        val ideoKeysConservationRestauration = formationsIdeo.stream()
                .filter(FormationIdeoDuSup::estConservationRestauration)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysConservationRestauration.isEmpty()) {
            throw new IllegalStateException("Pas de formations de conservation ou restauration dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour formations de conservation ou restauration: " + String.join(" ; ", ideoKeysConservationRestauration));
        }

        val ideoKeysDMA = formationsIdeo.stream()
                .filter(FormationIdeoDuSup::estDMA)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysDMA.isEmpty()) {
            throw new IllegalStateException("Pas de DMA dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour les DMA: " + String.join(" ; ", ideoKeysDMA));
        }

        val formationsIdeoIndexed = formationsIdeo.stream()
                .collect(Collectors.toMap(
                        FormationIdeoDuSup::ideo,
                        f -> f
                ));


        return new ArrayList<>( lines.psupToIdeo2().stream().map(line ->
                {
                    String mpsId = line.getMpsId();

                    ArrayList<String> ideoFormationsIds =
                            new ArrayList<>(
                            Arrays.stream(line.IDS_IDEO2()
                                            .split(";"))
                                    .map(String::trim)
                                    .filter(s -> !s.isBlank())
                                    .toList());

                    List<String> ideoMetiersIds =
                            new ArrayList<>(Arrays.stream(line.METIER_IDEO2().split(";"))
                                    .map(String::trim)
                                    .map(Constants::cleanup)
                                    .toList());

                    ideoMetiersIds.addAll(ideoFormationsIds.stream()
                            .map(formationsIdeoIndexed::get)
                            .filter(Objects::nonNull)
                            .map(FormationIdeoDuSup::metiers)
                            .flatMap(List::stream)
                            .distinct()
                            .toList()
                    );

                    if(line.isIEP()) ideoFormationsIds.addAll(ideoKeysIEP);
                    if(line.isEcoleIngenieur()) ideoFormationsIds.addAll(ideoKeysEcoleIngenieur);
                    if(line.isEcoleCommerce()) ideoFormationsIds.addAll(ideoKeysEcoleCommerce);
                    if(line.isEcoleArchitecture()) ideoFormationsIds.addAll(ideoKeysEcoleArchi);
                    if(line.isEcoleArt()) ideoFormationsIds.addAll(ideoKeysEcoleArt);
                    if(line.isEcoleconservationRestauration()) ideoFormationsIds.addAll(ideoKeysConservationRestauration);
                    if(line.isDMA()) ideoFormationsIds.addAll(ideoKeysDMA);

                    return new FilieresPsupVersFormationsEtMetiersIdeo(
                            mpsId,
                            TYPE_FORMATION_PREFIX + line.G_FR_COD(),
                            line.G_FR_LIB(),
                            line.G_FL_LIB(),
                            ideoFormationsIds,
                            ideoMetiersIds
                    );
                }
        ).toList());
    }


}

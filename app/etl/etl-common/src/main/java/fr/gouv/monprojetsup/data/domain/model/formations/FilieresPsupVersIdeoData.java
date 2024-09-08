package fr.gouv.monprojetsup.data.domain.model.formations;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.PsupToIdeoCorrespondance;
import jakarta.validation.constraints.NotNull;
import lombok.val;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.Constants.TYPE_FORMATION_PREFIX;
import static java.util.stream.Collectors.toCollection;

public record FilieresPsupVersIdeoData(
        String gFlCod,
        String gFrCod,
        String gFrLib,
        String gFlLib,
        @NotNull List<@NotNull String> ideoFormationsIds,
        @NotNull List<@NotNull String> ideoMetiersIds,
        @NotNull List<@NotNull String> ideoDomainesIds

) {

    private static final Logger logger = Logger.getLogger(FilieresPsupVersIdeoData.class.getSimpleName());
    public static List<FilieresPsupVersIdeoData> compute(
            PsupToIdeoCorrespondance lines,
            Map<String, FormationIdeoDuSup> formationsIdeo
    ) {

        try {
            lines.generateDiagnostic(formationsIdeo.values().stream().map(FormationIdeoDuSup::ideo).collect(Collectors.toSet()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        val ideoKeysIEP = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estIEP)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysIEP.isEmpty()) {
            throw new IllegalStateException("Pas d'IEP dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour science Po: " + String.join(" ; ", ideoKeysIEP));
        }


        val ideoKeysEcoleIngenieur = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estEcoleIngenieur)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysEcoleIngenieur.isEmpty()) {
            throw new IllegalStateException("Pas d'école d'ingénieur dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour écoles d'ingénieur: " + String.join(" ; ", ideoKeysEcoleIngenieur));
        }


        val ideoKeysEcoleCommerce = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estEcoleCommerce)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysEcoleCommerce.isEmpty()) {
            throw new IllegalStateException("Pas d'école de commerce dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour écoles de commerce: " + String.join(" ; ", ideoKeysEcoleCommerce));
        }

        val ideoKeysEcoleArchi = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estEcoleArchitecture)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysEcoleArchi.isEmpty()) {
            throw new IllegalStateException("Pas d'école d'architecture dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour écoles de architecture: " + String.join(" ; ", ideoKeysEcoleArchi));
        }

        val ideoKeysEcoleArt = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estEcoleArt)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysEcoleArt.isEmpty()) {
            throw new IllegalStateException("Pas d'école d'art dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour écoles d'art: " + String.join(" ; ", ideoKeysEcoleArt));
        }

        val ideoKeysConservationRestauration = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estConservationRestauration)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysConservationRestauration.isEmpty()) {
            throw new IllegalStateException("Pas de formations de conservation ou restauration dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour formations de conservation ou restauration: " + String.join(" ; ", ideoKeysConservationRestauration));
        }

        val ideoKeysDMA = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estDMA)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysDMA.isEmpty()) {
            throw new IllegalStateException("Pas de DMA dans les formations Ideo du Sup");
        } else {
            logger.info("Codes IDEO pour les DMA: " + String.join(" ; ", ideoKeysDMA));
        }

        return new ArrayList<>( lines.psupToIdeo2().stream().map(line ->
                {
                    String mpsId = line.getMpsId();

                    ArrayList<String> ideoFormationsIds =
                            new ArrayList<>(
                            Arrays.stream(line.IDS_IDEO2()
                                            .split(";"))
                                    .map(String::trim)
                                    .filter(s -> !s.isBlank())
                                    .distinct()
                                    .sorted()
                                    .toList());


                            List<String> ideoMetiersIds =
                            new ArrayList<>(Arrays.stream(line.METIER_IDEO2().split(";"))
                                    .map(String::trim)
                                    .map(Constants::cleanup)
                                    .filter(s -> !s.isBlank())
                                    .distinct()
                                    .sorted()
                                    .toList());

                    ideoMetiersIds.addAll(ideoFormationsIds.stream()
                            .map(formationsIdeo::get)
                            .filter(Objects::nonNull)
                            .map(FormationIdeoDuSup::metiers)
                            .flatMap(Collection::stream)
                            .distinct()
                            .sorted()
                            .toList()
                    );

                    val ideoDomainesIds = ideoFormationsIds.stream()
                            .map(formationsIdeo::get)
                            .filter(Objects::nonNull)
                            .map(FormationIdeoDuSup::libellesOuClesSousdomainesWeb)
                            .flatMap(Collection::stream)
                            .distinct()
                            .sorted()
                            .toList();


                    if(line.isIEP()) ideoFormationsIds.addAll(ideoKeysIEP);
                    if(line.isEcoleIngenieur()) ideoFormationsIds.addAll(ideoKeysEcoleIngenieur);
                    if(line.isEcoleCommerce()) ideoFormationsIds.addAll(ideoKeysEcoleCommerce);
                    if(line.isEcoleArchitecture()) ideoFormationsIds.addAll(ideoKeysEcoleArchi);
                    if(line.isEcoleArt()) ideoFormationsIds.addAll(ideoKeysEcoleArt);
                    if(line.isEcoleconservationRestauration()) ideoFormationsIds.addAll(ideoKeysConservationRestauration);
                    if(line.isDMA()) ideoFormationsIds.addAll(ideoKeysDMA);

                    return new FilieresPsupVersIdeoData(
                            mpsId,
                            TYPE_FORMATION_PREFIX + line.G_FR_COD(),
                            line.G_FR_LIB(),
                            line.G_FL_LIB(),
                            ideoFormationsIds.stream().sorted().collect(toCollection(ArrayList::new)),
                            ideoMetiersIds.stream().sorted().collect(toCollection(ArrayList::new)),
                            ideoDomainesIds.stream().sorted().collect(toCollection(ArrayList::new))
                    );
                }
        ).toList());
    }


    public void inheritMetiersAndDomainesFrom(FilieresPsupVersIdeoData rich) {
        this.ideoMetiersIds().addAll(rich.ideoMetiersIds());
        this.ideoDomainesIds().addAll(rich.ideoDomainesIds());
    }
}

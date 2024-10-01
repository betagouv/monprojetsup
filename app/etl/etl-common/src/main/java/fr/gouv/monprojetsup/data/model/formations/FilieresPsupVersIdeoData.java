package fr.gouv.monprojetsup.data.model.formations;

import fr.gouv.monprojetsup.data.model.onisep.formations.PsupToIdeoCorrespondance;
import jakarta.validation.constraints.NotNull;
import lombok.val;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.gFlCodToMpsId;
import static fr.gouv.monprojetsup.data.Constants.gFrCodToMpsId;
import static java.util.stream.Collectors.toCollection;

public record FilieresPsupVersIdeoData(
        int gFlCod,
        int gFrCod,
        String gFrLib,
        String gFlLib,
        @NotNull List<@NotNull String> ideoFormationsIds,
        @NotNull List<@NotNull String> ideoMetiersIds,
        @NotNull List<@NotNull String> libellesOuClesSousdomainesWeb

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

        val result =  new ArrayList<>( lines.psupToIdeo2().stream().map(line ->
                {
                    ArrayList<String> ideoFormationsIds =
                            new ArrayList<>(
                                    Arrays.stream(line.IDS_IDEO2()
                                                    .split(";"))
                                            .map(String::trim)
                                            .filter(s -> !s.isBlank())
                                            .distinct()
                                            .sorted()
                                            .toList());

                    if(line.isIEP()) ideoFormationsIds.addAll(ideoKeysIEP);
                    if(line.isEcoleIngenieur()) ideoFormationsIds.addAll(ideoKeysEcoleIngenieur);
                    if(line.isEcoleCommerce()) ideoFormationsIds.addAll(ideoKeysEcoleCommerce);
                    if(line.isEcoleArchitecture()) ideoFormationsIds.addAll(ideoKeysEcoleArchi);
                    if(line.isEcoleArt()) ideoFormationsIds.addAll(ideoKeysEcoleArt);
                    if(line.isEcoleconservationRestauration()) ideoFormationsIds.addAll(ideoKeysConservationRestauration);
                    if(line.isDMA()) ideoFormationsIds.addAll(ideoKeysDMA);

                    val libellesOuClesSousdomainesWeb = majorityItems(ideoFormationsIds.stream()
                            .map(formationsIdeo::get)
                            .filter(Objects::nonNull)
                            .map(FormationIdeoDuSup::libellesOuClesSousdomainesWeb)
                            .flatMap(Collection::stream)
                            .toList()
                    );

                    val  ideoMetiersIds = ideoFormationsIds.stream()
                            .map(formationsIdeo::get)
                            .filter(Objects::nonNull)
                            .map(FormationIdeoDuSup::metiers)
                            .flatMap(Collection::stream)
                            .distinct()
                            .sorted()
                            .toList();

                    return new FilieresPsupVersIdeoData(
                            line.G_FL_COD(),
                            line.G_FR_COD(),
                            line.G_FR_LIB(),
                            line.G_FL_LIB(),
                            ideoFormationsIds.stream().sorted().collect(toCollection(ArrayList::new)),
                            new ArrayList<>(ideoMetiersIds),
                            new ArrayList<>(libellesOuClesSousdomainesWeb)
                    );
                }
        ).toList());

        /*
        //just to be sure
        result.forEach(
                fil -> fil.ideoFormationsIds().stream()
                        .map(formationsIdeo::get)
                        .filter(Objects::nonNull)
                        .forEach( f -> {
                            fil.ideoMetiersIds().addAll(f.metiers());
                            fil.domainesWeb().addAll(f.domainesWeb());
                        })
        );*/

        return result;

    }

    /* returns the sorte dlist of items that appear often in the list */
    private static List<String> majorityItems(List<String> list) {
        val counts = new HashMap<String, Integer>();
        list.forEach(
                item -> counts.put(item, counts.getOrDefault(item, 0) + 1)
        );
        val maxCount = counts.values().stream().max(Integer::compareTo).orElse(0);
        return counts.entrySet().stream()
                .filter(e -> e.getValue() >= maxCount / 2)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }

    public static void injectLiensFormationsPsupMetiers(
            List<FilieresPsupVersIdeoData> filieresPsupToFormationsMetiersIdeo,
            Map<String,List<String>> psupToMetiersIdeo
    ) {
        filieresPsupToFormationsMetiersIdeo.forEach(
                fil -> {
                    val gFlCod = fil.gFlCod();
                    val metiersFiliere = psupToMetiersIdeo.get(gFlCodToMpsId(gFlCod));
                    if(metiersFiliere != null) {
                        fil.ideoMetiersIds().addAll(metiersFiliere);
                    }
                    val gFrCod = fil.gFrCod();
                    val metiersTypeformation = psupToMetiersIdeo.get(gFrCodToMpsId(gFrCod));
                    if(metiersTypeformation != null) {
                        fil.ideoMetiersIds().addAll(metiersTypeformation);
                    }

                }
        );
    }

    public static void injectLiensFormationsIdeoMetiers(
            List<FilieresPsupVersIdeoData> filieresPsupToFormationsMetiersIdeo,
            Map<String, List<String>> formationsIdeoToMetiersIdeo) {
        filieresPsupToFormationsMetiersIdeo.forEach(
                fil -> {
                    fil.ideoFormationsIds.forEach(
                            ideoId -> {
                                val metiers = formationsIdeoToMetiersIdeo.get(ideoId);
                                if (metiers != null) {
                                    fil.ideoMetiersIds.addAll(metiers);
                                }
                            }
                    );
                }
        );
    }


    public void inheritMetiersAndDomainesFrom(FilieresPsupVersIdeoData rich) {
        this.ideoMetiersIds().addAll(rich.ideoMetiersIds());
        this.libellesOuClesSousdomainesWeb().addAll(rich.libellesOuClesSousdomainesWeb());
    }

    public String mpsId() {
        return gFlCodToMpsId(gFlCod());
    }
}

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
        }


        val ideoKeysEcoleIngenieur = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estEcoleIngenieur)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysEcoleIngenieur.isEmpty()) {
            throw new IllegalStateException("Pas d'école d'ingénieur dans les formations Ideo du Sup");
        }


        val ideoKeysEcoleCommerce = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estEcoleCommerce)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysEcoleCommerce.isEmpty()) {
            throw new IllegalStateException("Pas d'école de commerce dans les formations Ideo du Sup");
        }

        val ideoKeysEcoleArchi = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estEcoleArchitecture)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysEcoleArchi.isEmpty()) {
            throw new IllegalStateException("Pas d'école d'architecture dans les formations Ideo du Sup");
        }

        val ideoKeysEcoleArt = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estEcoleArt)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysEcoleArt.isEmpty()) {
            throw new IllegalStateException("Pas d'école d'art dans les formations Ideo du Sup");
        }

        val ideoKeysConservationRestauration = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estConservationRestauration)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysConservationRestauration.isEmpty()) {
            throw new IllegalStateException("Pas de formations de conservation ou restauration dans les formations Ideo du Sup");
        }

        val ideoKeysDMA = formationsIdeo.values().stream()
                .filter(FormationIdeoDuSup::estDMA)
                .map(FormationIdeoDuSup::ideo)
                .distinct()
                .toList();
        if(ideoKeysDMA.isEmpty()) {
            throw new IllegalStateException("Pas de DMA dans les formations Ideo du Sup");
        }
        
        return new ArrayList<>( lines.psupToIdeo2().stream().map(line ->
                {
                    ArrayList<String> ideoFormationsIds1 =
                            new ArrayList<>(
                                    Arrays.stream(line.IDS_IDEO2()
                                                    .split(";"))
                                            .map(String::trim)
                                            .filter(s -> !s.isBlank())
                                            .distinct()
                                            .sorted()
                                            .toList());

                    if(line.isIEP()) ideoFormationsIds1.addAll(ideoKeysIEP);
                    if(line.isEcoleIngenieur()) ideoFormationsIds1.addAll(ideoKeysEcoleIngenieur);
                    if(line.isEcoleCommerce()) ideoFormationsIds1.addAll(ideoKeysEcoleCommerce);
                    if(line.isEcoleArchitecture()) ideoFormationsIds1.addAll(ideoKeysEcoleArchi);
                    if(line.isEcoleArt()) ideoFormationsIds1.addAll(ideoKeysEcoleArt);
                    if(line.isEcoleconservationRestauration()) ideoFormationsIds1.addAll(ideoKeysConservationRestauration);
                    if(line.isDMA()) ideoFormationsIds1.addAll(ideoKeysDMA);

                    val libellesOuClesSousdomainesWeb1 = majorityItems(ideoFormationsIds1.stream()
                            .map(formationsIdeo::get)
                            .filter(Objects::nonNull)
                            .map(FormationIdeoDuSup::libellesOuClesSousdomainesWeb)
                            .flatMap(Collection::stream)
                            .toList()
                    );

                    val ideoMetiersIds1 = ideoFormationsIds1.stream()
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
                            ideoFormationsIds1.stream().sorted().collect(toCollection(ArrayList::new)),
                            new ArrayList<>(ideoMetiersIds1),
                            new ArrayList<>(libellesOuClesSousdomainesWeb1)
                    );
                }
        ).toList());

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

    public static void replaceLiensFormationsPsupMetiers(
            List<FilieresPsupVersIdeoData> filieresPsupToFormationsMetiersIdeo,
            Map<String,List<String>> psupToMetiersIdeo
    ) {
        filieresPsupToFormationsMetiersIdeo.forEach(
                fil -> {
                    val gFlCod = fil.gFlCod();
                    val metiersFiliere = psupToMetiersIdeo.get(gFlCodToMpsId(gFlCod));
                    if(metiersFiliere != null && !metiersFiliere.isEmpty()) {
                        fil.ideoMetiersIds().clear();
                        fil.ideoMetiersIds().addAll(metiersFiliere);
                    } else {
                        val gFrCod = fil.gFrCod();
                        val metiersTypeformation = psupToMetiersIdeo.get(gFrCodToMpsId(gFrCod));
                        if (metiersTypeformation != null && !metiersTypeformation.isEmpty()) {
                            fil.ideoMetiersIds().clear();
                            fil.ideoMetiersIds().addAll(metiersTypeformation);
                        }
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

package fr.gouv.monprojetsup.data.model.formations;

import fr.gouv.monprojetsup.data.model.onisep.formations.PsupToIdeoCorrespondance;
import jakarta.validation.constraints.NotNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.AVENIRS_URL;
import static fr.gouv.monprojetsup.data.Constants.ONISEP_URL1;
import static fr.gouv.monprojetsup.data.Constants.ONISEP_URL2;
import static fr.gouv.monprojetsup.data.Constants.gFlCodToMpsId;
import static fr.gouv.monprojetsup.data.Constants.gFrCodToMpsId;

public record FilierePsupVersIdeoData(
        int gFlCod,
        int gFrCod,
        @NotNull ArrayList<@NotNull String> ideoFormationsIds,
        @NotNull ArrayList<@NotNull String> ideoMetiersIds,
        @NotNull ArrayList<@NotNull String> libellesOuClesSousdomainesWeb,
        @Nullable String liensVersSiteAvenir

        ) {

    public static List<FilierePsupVersIdeoData> compute(
            PsupToIdeoCorrespondance lines,
            Map<String, FormationIdeoDuSup> formationsIdeo,
            Map<String, @NotNull Set<String>> oldIdeoToNewIdeo) {

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
                                    Arrays.stream(line.idsideos()
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

                    //on augmente avec les nouveaux codes ideo
                    ideoFormationsIds1.addAll(
                            ideoFormationsIds1.stream()
                                    .flatMap(fl -> oldIdeoToNewIdeo.getOrDefault(fl, Set.of()).stream())
                                    .collect(Collectors.toSet())
                    );
                    //on augmente avec les vieux codes ideo
                    ArrayList<String> finalIdeoFormationsIds = ideoFormationsIds1;
                    Set<String> toAdd = oldIdeoToNewIdeo.entrySet()
                                            .stream().filter(e  -> finalIdeoFormationsIds.stream().anyMatch(f -> e.getValue().contains(f)))
                                            .map(Map.Entry::getKey)
                                                            .collect(Collectors.toSet());
                    ideoFormationsIds1.addAll(toAdd);

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

                    ideoFormationsIds1 = new ArrayList<>(ideoFormationsIds1.stream().distinct().sorted().toList());
                    ideoFormationsIds1.removeAll(oldIdeoToNewIdeo.keySet());

                    var lien = line.onisepLink()
                            .replace(ONISEP_URL1, AVENIRS_URL)
                            .replace(ONISEP_URL2, AVENIRS_URL)
                            ;
                    if(lien.isBlank() || lien.contains("slug") || lien.contains("recherche")) {
                        lien = null;
                    }

                    return new FilierePsupVersIdeoData(
                            line.gFlCod(),
                            line.gFrCod(),
                            ideoFormationsIds1,
                            new ArrayList<>(ideoMetiersIds1),
                            new ArrayList<>(libellesOuClesSousdomainesWeb1),
                            lien
                    );
                }
        ).toList());

    }

    /* returns the sorted list of items that appear often in the list */
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
            List<FilierePsupVersIdeoData> filieresPsupToFormationsMetiersIdeo,
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


    public void inheritMetiersAndDomainesFrom(FilierePsupVersIdeoData rich) {
        this.ideoMetiersIds().addAll(rich.ideoMetiersIds());
        this.libellesOuClesSousdomainesWeb().addAll(rich.libellesOuClesSousdomainesWeb());
    }

    public String mpsId() {
        return gFlCodToMpsId(gFlCod());
    }

    public void updateOldToNewIdeo(Map<String, Set<String>> oldIdeoToNewIdeo) {
        Set<String> toAdd = ideoFormationsIds.stream().flatMap(fl -> oldIdeoToNewIdeo.getOrDefault(fl, Set.of()).stream()).collect(Collectors.toSet());
        if(!toAdd.isEmpty()) {
            ideoFormationsIds.addAll(toAdd);
            ideoFormationsIds.removeAll(oldIdeoToNewIdeo.keySet());
        }
    }
}

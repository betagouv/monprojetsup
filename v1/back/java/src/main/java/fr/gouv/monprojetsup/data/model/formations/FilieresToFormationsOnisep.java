package fr.gouv.monprojetsup.data.model.formations;

import fr.gouv.monprojetsup.data.update.onisep.billy.PsupToOnisepLines;
import fr.gouv.monprojetsup.data.update.onisep.formations.Formation;
import fr.gouv.monprojetsup.data.update.onisep.formations.FormationAvecMetier;
import fr.gouv.monprojetsup.data.update.onisep.formations.Formations;
import fr.gouv.monprojetsup.data.update.onisep.formations.FormationsAvecMetiers;
import fr.gouv.monprojetsup.tools.DictApproxInversion;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.FILIERE_PREFIX;
import static fr.gouv.monprojetsup.data.Constants.TYPE_FORMATION_PREFIX;


public record FilieresToFormationsOnisep(
        List<FiliereToFormationsOnisep> filieres
) {

    public record FormationOnisep(
            @NotNull String ideo,
            String descriptifFormatCourt,
            String descriptifAcces,
            String url,
            String attendus,
            FormationAvecMetier.PoursuitesEtudes poursuitesEtudes,
            FormationAvecMetier.SourcesNumerique sourcesNumeriques,
            @NotNull List<String> metiers
    ) {

        public FormationOnisep(String ideo, @NotNull FormationAvecMetier f) {
            this(
                    ideo,
                    f.descriptif_format_court(),
                    f.descriptif_acces(),
                    f.url(),
                    f.attendus(),
                    f.poursuites_etudes(),
                    f.sources_numeriques(),
                    f.metiers_formation() == null
                            ? new ArrayList<>()
                            : f.metiers_formation().metier().stream().map(m -> m.id()).toList()
            );
        }

        public FormationOnisep(String ideo, @NotNull Formation f) {
            this(
                    ideo,
                    null,
                    null,
                    f.url_et_id_onisep(),
                    null,
                    null,
                    null,
                    new ArrayList<>()
            );
        }

    }

    public record FiliereToFormationsOnisep(
            String gFlCod,
            String gFrCod,
            String gFrLib,
            String gFlLib,
            List<FormationOnisep> formationOniseps
    ) {
    }

    public static FilieresToFormationsOnisep getFilieres(
            Formations formations,
            FormationsAvecMetiers formationsAvecMetiers,
            PsupToOnisepLines lines
    ) {
        Map<String, Formation> formationsPerKey
                = formations.formations().stream().distinct()
                .collect(Collectors.toMap(
                        f -> f.identifiant(),
                        f -> f
                ));
        Map<String, FormationAvecMetier> formationsAvecMetiersPerKey
                = formationsAvecMetiers
                .formations().formation()
                .stream()
                .collect(Collectors.toMap(
                        f -> f.identifiant(),
                        f -> f
                ));

        Map<String, FiliereToFormationsOnisep> lSansMetier =
                        lines.psupToIdeo2().stream().map(line ->
                                new FiliereToFormationsOnisep(
                                        FILIERE_PREFIX + line.G_FL_COD(),
                                        TYPE_FORMATION_PREFIX + line.G_FR_COD(),
                                        line.G_FR_LIB(),
                                        line.G_FL_LIB(),
                                        Arrays.stream(line.IDS_IDEO2()
                                                        .split(";"))
                                                .map(id -> id.trim())
                                                .filter(key -> formationsPerKey.containsKey(key))
                                                .map(key -> ImmutablePair.of(
                                                        key,
                                                        formationsPerKey.get(key))
                                                )
                                                .map(pair -> new FormationOnisep(
                                                                pair.left,
                                                                pair.right)
                                                )
                                                .toList()
                                )
                        ).collect(
                                Collectors.toMap(
                                        e -> e.gFlCod(),
                                        e -> e
                                )
                        );

        Map<String, FiliereToFormationsOnisep> lAvecMetier =
                lines.psupToIdeo2().stream().map(line ->
                        new FiliereToFormationsOnisep(
                                FILIERE_PREFIX + line.G_FL_COD(),
                                TYPE_FORMATION_PREFIX + line.G_FR_COD(),
                                line.G_FR_LIB(),
                                line.G_FL_LIB(),
                                Arrays.stream(line.IDS_IDEO2()
                                                .split(";"))
                                        .map(id -> id.trim())
                                        .filter(key -> formationsAvecMetiersPerKey.containsKey(key))
                                        .map(key -> ImmutablePair.of(
                                                key,
                                                formationsAvecMetiersPerKey.get(key))
                                        )
                                        .map(pair -> new FormationOnisep(
                                                        pair.left,
                                                        pair.right)
                                        )
                                        .toList()
                        )
                ).collect(
                        Collectors.toMap(
                                e -> e.gFlCod(),
                                e -> e
                        )
                );

        Map<String, FiliereToFormationsOnisep> result = new HashMap<>(lSansMetier);
        result.putAll(lAvecMetier);

        return new FilieresToFormationsOnisep(new ArrayList<>(result.values()));
    }

    /**
     * maps full string to MET.* code, with spell check
     * @param metierLabel
     * @return
     */
    public @Nullable String findFormationKey(String label) {
        return DictApproxInversion.findKey(
                label,
                filieres.stream()
                        .collect(Collectors.toMap(
                                f -> f.gFlCod(),
                                f -> f.gFlLib()
                                )
                        )
        );
    }
}

package fr.gouv.monprojetsup.suggestions.dto;

import fr.gouv.monprojetsup.suggestions.algo.Affinite;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class GetAffinitiesServiceDTO {
    public record Request(

            @Schema(name = "profile", description = "Profil utilisé pour évaluer l'affinité.")
            @NotNull ProfileDTO profile,

            @Schema(name = "inclureScores", description = "Inclure les scores aux différents critères dans la réponse.", example = "true")
            @Nullable Boolean inclureScores

    ) {

    }

    public record Response(
            ResponseHeader header,
            @Schema(
                    description =
                            """
                               Liste des formations dans l'ordre d'affichage, ainsi que le score d'affinité dans l'intervalle [0.0 , 1.0].
                               """
            )
            List<Affinity> affinites,

            @Schema(
                    description =
                            """
                               Liste des métiers dans l'ordre d'affichage, triés par affinité.
                               """
            )
            List<String> metiers

    ) {

        public Response(
                @NotNull List<Pair<String, @NotNull Map<String, @NotNull Double>>> affinities,
                @NotNull List<String> metiers
                ) {
            this(
                    new ResponseHeader(),
                    affinities.stream()
                            .map(e -> new Affinity(
                                    e.getLeft(),
                                    e.getRight().get(Affinite.CHAMP_SCORE_AGGREGE),
                                    e.getRight())
                            )
                            .toList(),
                    metiers
            );
        }

    }

    public record Affinity(
            String key,
            @Schema(
                    description =
                            """
                               Score d'affinité entre 0.0 et 1.0. Précision 6 décimales.
                               """
            )
            double affinite,

            @Schema(
                    description =
                            """
                               Scores obtenus aux différents critères. Précision 6 décimales.
                               """
            )
            @Nullable Map<String,Double> scores

            ) {
    }
}

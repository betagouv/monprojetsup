package fr.gouv.monprojetsup.suggestions.dto;

import fr.gouv.monprojetsup.suggestions.server.ResponseHeader;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetAffinitiesServiceDTO {
    public record Request(

            @Schema(name = "profile", description = "Profil utilisé pour évaluer l'affinité.")
            @NotNull ProfileDTO profile

    ) {

    }

    public record Response(
            ResponseHeader header,
            @Schema(
                    description =
                            """
                               Liste des formations dans l'ordre d'affichage, ainsi que le score d'affinité dans l'intervalle [0.0 , 1.0]. 
                               """,
                    required = true
            )
            List<Affinity> affinites,

            @Schema(
                    description =
                            """
                               Liste des métiers dans l'ordre d'affichage, triés par affinité.
                               """,
                    required = true
            )
            List<String> metiers

    ) {

        public Response(@NotNull List<Pair<String, Double>> affinities, @NotNull List<String> metiers) {
            this(
                    new ResponseHeader(),
                    affinities.stream()
                            .map(p -> new Affinity(p.getLeft(), p.getRight()))
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
                               """,
                    required = true
            )
            double affinite

    ) {
        public double getSortScore(int searchScore) {
            return affinite + 10000.0 * searchScore;
        }
    }
}

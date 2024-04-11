package fr.gouv.monprojetsup.data.dto;

import fr.gouv.monprojetsup.common.server.ResponseHeader;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetFormationsAffinitiesServiceDTO {
    public record Request(

            @Schema(name = "profile", description = "Profil utilisé pour évaluer l'affinité.")
            @NotNull ProfileDTO profile

    ) {

    }

    public record Response(
            ResponseHeader header,
            @Schema(
                    name = "affinites",
                    description =
                            """
                               Renvoie la liste des formations dans l'ordre d'affichage, ainsi que le score d'affinité, entre 0.0 et 1.0.
                               Précision 6 décimales. 
                               """,
                    required = true
            )
            List<Affinity> affinites
    ) {

        public Response(@NotNull List<Pair<String, Double>> affinites) {
            this(
                    new ResponseHeader(),
                    affinites.stream()
                            .map(p -> new Affinity(p.getLeft(), p.getRight()))
                            .toList()
            );
        }

    }

    public record Affinity(
            String key,
            double affinite

    ) {
    }
}

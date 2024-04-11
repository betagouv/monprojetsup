package fr.gouv.monprojetsup.data.dto;

import fr.gouv.monprojetsup.common.server.ResponseHeader;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SortMetiersByAffinityServiceDTO {
    public record Request(

            @Schema(name = "profile", description = "Profil utilisé pour trier les métiers.")
            @NotNull ProfileDTO profile,

            @ArraySchema(arraySchema = @Schema(name = "keys", example = "[\"MET_450\",\"MET_883\",\"MET_77\"]",description = "Liste des clés métiers dont le tri est attendu."))
            @NotNull List<String> keys

    ) {

    }

    public record Response(
            ResponseHeader header,
            @Schema(
                    name = "clesTriees",
                    description = "Clés triées par ordre décroissant d'affinité. Le meilleur métier en premier. Les métiers apparaissant dans la corbeille du profil sont exclus du résultat",
                    required = true
            )
            @NotNull List<String> clesTriees
    ) {

        public Response(@NotNull List<String> clesTriees) {
            this(
                    new ResponseHeader(),
                    clesTriees
            );
        }
    }
}

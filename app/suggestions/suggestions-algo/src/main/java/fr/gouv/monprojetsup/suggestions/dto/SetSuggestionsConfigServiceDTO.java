package fr.gouv.monprojetsup.suggestions.dto;

import fr.gouv.monprojetsup.suggestions.algo.Config;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

public class SetSuggestionsConfigServiceDTO {

    public record Request(

            @Schema(description = "Nouvelle config de l'algorithme de suggestions")
            @NotNull Config config

    ) {

    }

    public record Response(
            ResponseHeader header,
            @Schema(description = "Config actuelle de l'algorithme de suggestions")
            @NotNull Config config

    ) {

        public Response(
                @NotNull Config config
                ) {
            this(
                    new ResponseHeader(),
                    config
            );
        }

    }

}

package fr.gouv.monprojetsup.data.model.bacs;

import org.jetbrains.annotations.NotNull;

public record Bac(
        @NotNull String key,
        @NotNull String label
    ) {

    public @NotNull String idCarteParcoursup() {
        if(key.startsWith("G")) {
            return "1";
        }
        if(key.startsWith("S")) {
            return "2";
        }
        if(key.startsWith("P")) {
            return "3";
        }
        return "0";
    }
}

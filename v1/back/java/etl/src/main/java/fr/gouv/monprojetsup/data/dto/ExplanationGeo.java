package fr.gouv.monprojetsup.data.dto;

import org.jetbrains.annotations.Nullable;

public record ExplanationGeo(int distance, String city, @Nullable String form) {

    public ExplanationGeo merge2(ExplanationGeo other) {
        if (other == null) return this;
        return (this.distance <= other.distance) ? this : other;
    }
}

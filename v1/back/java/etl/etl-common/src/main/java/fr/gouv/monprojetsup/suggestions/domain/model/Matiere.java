package fr.gouv.monprojetsup.suggestions.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Matiere (
    @NotNull Integer id,
    @NotNull String label,
    boolean estSpecialite,
    @NotNull List<String> bacs)
{

}
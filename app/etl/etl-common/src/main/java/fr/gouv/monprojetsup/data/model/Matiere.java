package fr.gouv.monprojetsup.data.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Matiere (
    @NotNull String idMps,
    @NotNull Integer idPsup,
    @NotNull String label,
    boolean estSpecialite,
    @NotNull List<String> bacs)
{

    @NotNull
    public static String idPsupToIdMps(long key) {
        return "mat" + key;
    }
    @NotNull
    public static String idSpeBacPsupToIdMps(long key) {
        return "sp" + key;
    }
}

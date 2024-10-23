package fr.gouv.monprojetsup.data.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Specialite(
    @NotNull String idMps,
    @NotNull Integer idPsup,
    @NotNull String label,
    boolean estSpecialite,
    @NotNull List<String> bacs)
{

    @NotNull
    public static String idPsupMatToIdMps(long key) {
        return "mat" + key;
    }
    @NotNull
    public static String idSpeBacPsupToIdMps(long key) {
        return "sp" + key;
    }
}

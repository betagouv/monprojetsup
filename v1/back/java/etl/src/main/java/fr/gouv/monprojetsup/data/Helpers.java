package fr.gouv.monprojetsup.data;

import org.jetbrains.annotations.NotNull;

public class Helpers {
    /* ************************************************************************
    ************************* HELPERS to determine type from key ***********************
     */
    public static boolean isFiliere(@NotNull String key) {
        return key.startsWith(Constants.FILIERE_PREFIX)
                || key.startsWith(Constants.TYPE_FORMATION_PREFIX);
    }

    public static boolean isMetier(@NotNull String key) {
        return key.startsWith(Constants.MET_PREFIX);
    }

    public static boolean isTheme(@NotNull String key) {
        return key.startsWith(Constants.THEME_PREFIX);
    }

    public static boolean isInteret(@NotNull String key) {
        return key.startsWith(Constants.CENTRE_INTERETS_ONISEP)
                || key.startsWith(Constants.CENTRE_INTERETS_ROME);
    }
}

package fr.gouv.monprojetsup.suggestions.data;

import fr.gouv.monprojetsup.data.Constants;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Helpers {
    /* ************************************************************************
    ************************* HELPERS to determine type from key ***********************
     */
    public static boolean isFiliere(@NotNull String key) {
        return key.startsWith(fr.gouv.monprojetsup.data.Constants.FILIERE_PREFIX)
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

    public static String removeHtml(@Nullable String txt) {
        if(txt == null) return null;
        var res = StringEscapeUtils.unescapeHtml4(txt);
        //remove any html tag
        res = res.replaceAll("<[^>]*>", "");
        //replace any html character by utf8
        return res;
    }
}

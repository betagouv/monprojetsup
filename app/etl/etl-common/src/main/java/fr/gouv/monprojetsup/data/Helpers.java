package fr.gouv.monprojetsup.data;

import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

public class Helpers {
    /* ************************************************************************
    ************************* HELPERS to determine type from key ***********************
     */

    public static String removeHtml(@Nullable String txt) {
        if(txt == null) return null;
        var res = StringEscapeUtils.unescapeHtml4(txt);
        //remove any html tag
        res = res.replaceAll("<[^>]*>", "");
        //replace any html character by utf8
        return res;
    }

}

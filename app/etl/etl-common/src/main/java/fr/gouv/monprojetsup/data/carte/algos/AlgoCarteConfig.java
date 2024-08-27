/* Copyright 2021 © Ministère de l'Enseignement Supérieur, de la Recherche et de
l'Innovation,
    Hugo Gimbert (hugo.gimbert@enseignementsup.gouv.fr)

    This file is part of Algorithmes-de-parcoursup.

    Algorithmes-de-parcoursup is free software: you can redistribute it and/or modify
    it under the terms of the Affero GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Algorithmes-de-parcoursup is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Affero GNU General Public License for more details.

    You should have received a copy of the Affero GNU General Public License
    along with Algorithmes-de-parcoursup.  If not, see <http://www.gnu.org/licenses/>.

 */
package fr.gouv.monprojetsup.data.carte.algos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoCarteConfig implements Serializable {

    public static final String FORMATION = "FORMATION";
    public static final String ETABLISSEMENT = "ETABLISSEMENT";
    public static final String LOCALISATION = "LOCALISATION";
    public static final String CITY = "CITY";
    public static final String FILIERE = "FILIERE";
    public static final String TYPE_FORMATION = "TYPE_FORMATION";
    public static final String FIL_ONISEP = "FIL_ONISEP";

    /* DESCRIPTION_WEB is a special category from which the keywords of filieres are extracted */
    public static final String DESCRIPTION_WEB = "DESCRIPTION_WEB";

    public final Map<String, String> fieldToSourceType;

    /* special mode where words and chains imported from database are not split. Used for orientation. */
    public final boolean noSplitMode = true;

    /* special mode where onisep words are not imported from database. Used for orientation. */
    public final boolean noOnisep = true;
    public final boolean specificTreatmentforLAS = true;

    public final List<String> ignoredWords;

    public AlgoCarteConfig() {

        fieldToSourceType = Stream.of(new Object[][]{
                {"g_ta_mot_cle", FORMATION},
                {"f_get_mots_cle_formation_mdr", FORMATION},
                {"langues_l1_formation_mdr", FORMATION},
                {"g_ta_des_deb", DESCRIPTION_WEB},
                {"g_ta_des_ens", DESCRIPTION_WEB},
                {"g_ta_des_ens_dis", DESCRIPTION_WEB},
                {"g_ta_dis_reu", DESCRIPTION_WEB},
                {"g_ti_mot_cle", ETABLISSEMENT},
                {"g_ea_sig_mot_rec", ETABLISSEMENT},
                {"f_get_regime_hebergement", LOCALISATION},
                {"g_ea_lib_aff", ETABLISSEMENT},
                {"fc_g_lib_eta_cal_ref", ETABLISSEMENT},
                {"fc_g_lib_eta_cal_ref2", ETABLISSEMENT},
                {"g_ea_lib_mdr", ETABLISSEMENT},
                {"g_ea_com", CITY},
                {"g_ea_vil_pri", LOCALISATION},
                {"g_dp_lib", LOCALISATION},
                {"g_aa_lib", LOCALISATION},
                {"f_get_info_region_mdr", LOCALISATION},
                {"g_ta_lib_mdr", FORMATION},
                {"g_fl_lib", FILIERE},
                {"g_fl_sig", FILIERE},
                {"g_fl_lib_aff", FORMATION},
                {"g_fl_sig_mot_rec", FILIERE},
                {"f_get_mots_cles_mentions", FILIERE},
                {"f_mots_cle_formation_ing_mdr", FORMATION},
                {"f_get_mots_cles_filiere_onisep_mdr", FIL_ONISEP},
                {"g_fr_lib", TYPE_FORMATION},
                {"g_fr_lib_aff", TYPE_FORMATION},
                {"g_fr_sig_mot_rec", TYPE_FORMATION},
                {"f_get_mots_cle_type_formation", TYPE_FORMATION},
                {"g_tf_mot_cle_mdr", TYPE_FORMATION},
        }).collect(Collectors.toMap(data -> ((String) data[0]).toLowerCase(), data -> (String) data[1]));


        ignoredWords = Stream.of(new String[]{
                "", "le", "la", "en", "à", "pour", "les", "et", "de", "dans", "du", "aux", "ta", "ti"
        }).collect(Collectors.toList());

    }
}

package fr.gouv.monprojetsup.suggestions.data.model.formations;

import org.jetbrains.annotations.Nullable;

public record ActionFormationOni(
        String action_de_formation_af_identifiant_onisep,//key
        String formation_for_libelle,
        String for_url_et_id_onisep,
        String ens_code_uai,
        String ens_url_et_id_onisep
) {

    public @Nullable String getIdOnisep() {
        if(for_url_et_id_onisep == null) return null;
        int i = for_url_et_id_onisep.lastIndexOf("/");
        if(i < 0) return null;
        return for_url_et_id_onisep.substring(i + 1).trim();
    }
}

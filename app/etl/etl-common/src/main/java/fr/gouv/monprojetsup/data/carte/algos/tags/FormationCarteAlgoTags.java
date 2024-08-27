package fr.gouv.monprojetsup.data.carte.algos.tags;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static fr.gouv.monprojetsup.data.carte.algos.Filiere.LAS_CONSTANT;

public class FormationCarteAlgoTags implements Serializable {

    public FormationCarteAlgoTags(int gTaCod, int gFlCod, boolean isLAS) {
        this.gTaCod = gTaCod;
        this.gFlCod = gFlCod + (isLAS ? LAS_CONSTANT : 0);
    }

    /* code formation */
    public final int gTaCod;

    /* code filiere */
    @SuppressWarnings("unused")
    public final int gFlCod;

    /* les données textuelles indexées par champ */
    public final Map<String,String> donnees = new HashMap<>();

}

package fr.gouv.monprojetsup.data.psup;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FormationCarteAlgoTags implements Serializable {

    public FormationCarteAlgoTags(int gTaCod, int gFlCod) {
        this.gTaCod = gTaCod;
        this.gFlCod = gFlCod;
    }

    /* code formation */
    public final int gTaCod;

    /* code filiere */
    @SuppressWarnings("unused")
    public final int gFlCod;

    /* les données textuelles indexées par champ */
    public final Map<String,String> donnees = new HashMap<>();

}

package fr.gouv.monprojetsup.data.carte.algos.explicabilite.reco;

import fr.gouv.monprojetsup.data.carte.algos.Filiere;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AlgoFiliereSimilairesDetailsCalculFormationScore implements Serializable {

    public final int filIdx;
    public final String filLib;
    public final int score;
    private Map<String, Integer> motsClesProximite;

    public AlgoFiliereSimilairesDetailsCalculFormationScore(Filiere fil, int score) {
        this.filLib = fil.libelle;
        this.filIdx = fil.cle;
        this.score = score;
    }

    public AlgoFiliereSimilairesDetailsCalculFormationScore(Filiere fil, int proximiteSemantique, Map<String, Integer> motsClesProximite) {
        this.filLib = fil.libelle;
        this.filIdx = fil.cle;
        this.score = proximiteSemantique;
        this.motsClesProximite = motsClesProximite;
    }

    public AlgoFiliereSimilairesDetailsCalculFormationScore(Filiere f4, AlgoFiliereSimilairesDetailsCalculFormationScore r) {
        this.filLib = f4.libelle;
        this.filIdx = f4.cle;
        this.score = r.score;
        this.motsClesProximite = new HashMap<>(r.motsClesProximite);
    }
}

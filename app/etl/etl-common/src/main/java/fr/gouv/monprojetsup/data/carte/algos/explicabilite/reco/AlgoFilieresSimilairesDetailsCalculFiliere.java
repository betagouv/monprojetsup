package fr.gouv.monprojetsup.data.carte.algos.explicabilite.reco;


import fr.gouv.monprojetsup.data.carte.algos.Filiere;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlgoFilieresSimilairesDetailsCalculFiliere implements Serializable {
    public final int codeFiliere;

    public final String libelle;
    public final List<AlgoFiliereSimilairesDetailsCalculFormationScore> recoOnisepCorrelation = new ArrayList<>();
    public final List<AlgoFiliereSimilairesDetailsCalculFormationScore> recoMotsCles = new ArrayList<>();
    public final List<AlgoFiliereSimilairesDetailsCalculFormationScore> recoCorrelation = new ArrayList<>();

    public AlgoFilieresSimilairesDetailsCalculFiliere(Filiere f1) {
        this.codeFiliere = f1.cle;
        this.libelle = f1.toString();
    }

    public int getCorrelation(int gFlCod) {
        return recoCorrelation.stream().filter(r -> r.filIdx == gFlCod).map(r -> r.score).findAny().orElse(0);
    }
    public int getProximiteSemantique(int gFlCod) {
        return recoMotsCles.stream().filter(r -> r.filIdx == gFlCod).map(r -> r.score).findAny().orElse(0);
    }

    private AlgoFilieresSimilairesDetailsCalculFiliere() {
        this.codeFiliere = -1;
        this.libelle = "";
    }

}

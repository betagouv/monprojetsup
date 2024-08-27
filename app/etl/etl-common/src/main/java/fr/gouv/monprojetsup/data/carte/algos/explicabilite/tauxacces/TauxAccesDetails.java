package fr.gouv.monprojetsup.data.carte.algos.explicabilite.tauxacces;

import fr.gouv.monprojetsup.data.carte.algos.explicabilite.tauxacces.GroupeAffectationUID;
import fr.gouv.monprojetsup.data.carte.algos.explicabilite.tauxacces.TauxAccesDetailsGroupe;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TauxAccesDetails implements Serializable {

    /* classé par formation */
    public final Map<GroupeAffectationUID, TauxAccesDetailsGroupe> data = new TreeMap<>();

    public void addNbCandidats(int gTaCod, int cGpCod, int nb) {
        data.computeIfAbsent(new GroupeAffectationUID(cGpCod,gTaCod, gTaCod), z -> new TauxAccesDetailsGroupe())
                .nbCandidats = nb;
    }


    public void addDernierAppele(int gTaCod, int cGpCod, int rang) {
        data.computeIfAbsent(new GroupeAffectationUID(cGpCod,gTaCod, gTaCod), z -> new TauxAccesDetailsGroupe())
                .rangDernierAppele = rang;
    }

    public void addNbSousBarre(int gTaCod, int cGpCod, int nb) {
        data.computeIfAbsent(new GroupeAffectationUID(cGpCod,gTaCod, gTaCod), z -> new TauxAccesDetailsGroupe())
                .nbCanAuDessusBarre = nb;

    }

    /* maps gtacod to taux acces */
    public Map<Integer, Integer> getTauxAcces() {
        Map<Integer, Integer> nbCandidatsAudessusBarre =
                data.entrySet().stream().collect(Collectors.groupingBy(e -> e.getKey().gTaCod))
                        .entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().stream().mapToInt(z -> z.getValue().nbCanAuDessusBarre).sum()
                                )
                        );
        Map<Integer, Integer> nbCandidats =
                data.entrySet().stream().collect(Collectors.groupingBy(e -> e.getKey().gTaCod))
                        .entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                        e -> e.getValue().stream().mapToInt(z -> z.getValue().nbCandidats).sum()
                                )
                        );

        return nbCandidats.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                    int nbCand = e.getValue();
                    int nbAdm = nbCandidatsAudessusBarre.getOrDefault(e.getKey(), 0);
                    if(nbAdm <= 0) return -1;
                    if(nbCand <= 0) return -1;
                    int res = (int) Math.round(100.0 * nbCandidatsAudessusBarre.getOrDefault(e.getKey(), 0) / e.getValue());
                    res = Math.max(1,res);
                    res = Math.min(100,res);
                    return res;
                }
        ));

    }
}

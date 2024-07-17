package fr.gouv.monprojetsup.suggestions.data.update.psup;

import java.util.HashMap;
import java.util.Map;

public record DureesEtudes(
        //durees des etudes dans chaque filieres, indexées par "fl1234"
        Map<String, Integer> durees
) {
    public DureesEtudes() {
        this(new HashMap<>());
    }

    public void add(String fl, int gFrCod, String gFlLib, String gFrLib, String gFrSig) {
        int duree;
        if (gFlLib.contains("4") || gFrLib.contains("4")) {
            duree = 4;
        } else if (gFlLib.contains("3") || gFrLib.contains("3")) {
            duree = 3;
        } else if (gFlLib.contains("5") || gFrLib.contains("5")) {
            duree = 5;
        } else if (gFrCod <= 27) {
            duree = 5;//les prepas / ecoles / CMI
        } else if (gFrSig.contains("BTS")
                || gFrSig.contains("BUT")
                || gFrSig.contains("DCG")
                || gFrSig.contains("DE")
                || gFrSig.contains("3")
                || gFrSig.contains("LP")
                || gFrSig.contains("FVGL")
        ) {
            duree = 3;
        } else if (gFrSig.contains("L1")
                || gFrSig.contains("CUPGE")
                || gFrSig.contains("CPES")
                || gFrSig.contains("5")
                || gFrLib.contains("Sciences politiques")
                || gFrSig.contains("Véto")
        ) {
            duree = 5;
        } else if (gFrSig.contains("FP")
                || gFrSig.contains("DEUST")
                || gFrSig.contains("DMA")
                || gFrSig.contains("DNA")
                || gFrSig.contains("BPJEPS")
        ) {
            duree = 2;
        } else if (gFrSig.contains("FCIL") || gFrSig.contains("MC") || gFrSig.contains("CSA")) {
            duree = 1;
        } else if (gFrSig.contains("D-Etab")
                || gFrSig.contains("DU")
                || gFrSig.contains("MAN")
                || gFrSig.contains("Ann. Prép")
        ) {
            //Delicat: passerelles vers le SUP donc au moins un an mais ensuite? On va dire 1+3 car pas professionnalisant
            duree = 4;
        } else if (gFrSig.contains("DNSP")) {
            duree = 3;
        } else if (gFrSig.equals("DSP")) {
            duree = 1;
        } else if (gFlLib.contains("Bac +1")) {
            duree = 3;
        } else if (gFrCod == 69) {//Brevet de maitrise compagnons du tour de France
            duree = 2;
        } else if (gFrCod == 49) {//Certificat ede spécialisation
            duree = 1;
        } else {
            throw new RuntimeException("Unknown formation duree for g_fr_cod=" + gFrCod);
        }
        //DU FP DEUST
        durees.put(fl, duree);
    }
}
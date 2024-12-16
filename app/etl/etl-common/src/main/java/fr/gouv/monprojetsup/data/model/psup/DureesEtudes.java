package fr.gouv.monprojetsup.data.model.psup;

import java.util.HashMap;
import java.util.Map;

record DureesEtudes(
        //durees des etudes dans chaque filieres, indexées par "fl1234"
        Map<String, Integer> durees
) {
    public DureesEtudes() {
        this(new HashMap<>());
    }

    public void add(String fl, int gFrCod, String gFlLib, String gFrLib, String gFrSig) {
        Integer duree = getDuree(gFrCod, gFlLib, gFrLib, gFrSig);
        if(duree == null) {
            throw new RuntimeException("Unknown formation duree for g_fr_cod=" + gFrCod);
        }
        durees.put(fl, duree);
    }

    public static Integer getDuree(int gFrCod, String gFlLib, String gFrLib, String gFrSig) {
        Integer duree = null;
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
        } else if (
                gFrSig.contains("FCIL")
                        || gFrSig.contains("MC")
                        || gFrSig.contains("CSA")
                        || gFrSig.contains("SousOf")
                        || gFrCod == 95000//Sousof bis
        ) {
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
        } else if (gFrSig.contains("DSP")) {
            duree = 1;
        } else if (gFlLib.contains("Bac +1")) {
            duree = 1;
        } else if (gFlLib.contains("Bac +2")) {
            duree = 2;
        } else if (gFlLib.contains("bac +2")) {
            duree = 2;
        } else if (gFlLib.contains("Bac+6")) {
            duree = 6;
        } else if (gFrCod == 69) {//Brevet de maitrise compagnons du tour de France
            duree = 2;
        } else if (gFrCod == 49) {//Certificat ede spécialisation
            duree = 1;
        } else if (gFrSig.equals("EA-AHAA")) {
            duree = 5;
        } else if (gFrSig.contains("CMI")) {
            duree = 5;
        } else if (gFrSig.contains("CPGE")) {
            duree = 5;
        } else if (gFrSig.contains("CS") || gFrLib.contains("Formation professionnelle")) {
            duree = 1;
        } else if (gFrCod == 550003) {//certif orthophoniste
            duree = 3;
        } else if (gFrCod == 550004) {//certif orthoptiste
            duree = 3;
        } else if (gFrSig.contains("ScPo")) {//sciences po
            duree = 5;
        } else if (gFrLib.contains("Classe prépa")) {//sciences po
            duree = 5;
        }
        return duree;
    }
}

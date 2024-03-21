package fr.gouv.monprojetsup.data.model.eds;

import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.tools.Serialisation;


public class AnalyzeEDS {

    public static void main(String[] args) throws Exception {

        ServerData.load();
        
        EDSAggAnalysis analyses = ServerData.getEDS(
                ServerData.backPsupData,
                ServerData.statistiques,
                ServerData.specialites,
                true,
                true);

        Serialisation.toJsonFile("EDS_quali_vs_quanti.json", analyses, true);

        Serialisation.toJsonFile("eds.json", analyses.getFrontData(), true);

    }

}

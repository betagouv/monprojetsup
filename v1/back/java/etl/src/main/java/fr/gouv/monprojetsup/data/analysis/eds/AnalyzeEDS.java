package fr.gouv.monprojetsup.data.analysis.eds;

import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.attendus.AttendusDetailles;
import fr.gouv.monprojetsup.data.model.specialites.SpecialitesLoader;
import fr.gouv.monprojetsup.data.tools.Serialisation;


public class AnalyzeEDS {

    public static void main(String[] args) throws Exception {

        ServerData.load();
        
        AttendusDetailles analyses = AttendusDetailles.getAttendusDetailles(
                ServerData.backPsupData,
                ServerData.statistiques,
                SpecialitesLoader.load(),
                true,
                true);

        Serialisation.toJsonFile("EDS_quali_vs_quanti.json", analyses, true);

        Serialisation.toJsonFile("eds.json", analyses.getSimplifiedFrontVersion(), true);

    }

}

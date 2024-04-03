package fr.gouv.monprojetsup.data.analysis.eds;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.eds.Attendus;
import fr.gouv.monprojetsup.data.model.specialites.Specialites;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.update.psup.PsupData;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class AnalyzeEDS {

    public static void main(String[] args) throws Exception {

        ServerData.load();
        
        EDSAggAnalysis analyses = getEDS(
                ServerData.backPsupData,
                ServerData.statistiques,
                ServerData.specialites,
                true,
                true);

        Serialisation.toJsonFile("EDS_quali_vs_quanti.json", analyses, true);

        Serialisation.toJsonFile("eds.json", analyses.getFrontData(), true);

    }

    public static EDSAggAnalysis getEDS(PsupData backPsupData, PsupStatistiques statistiques, Specialites specialites, boolean specifiques, boolean prettyPrint) {

        EDSAggAnalysis analyses = new EDSAggAnalysis();

        backPsupData.filActives().forEach(gFlCod -> {
            String key = Constants.gFlCodToFrontId(gFlCod);
            String gFlLib = statistiques.nomsFilieres.get(key);
            if(gFlLib != null) {
                String ppkey = prettyPrint ? ServerData.getDebugLabel(key) : key;
                //les nbAdmisEDS
                EDSAnalysis analysis = analyses.analyses().computeIfAbsent(ppkey, z -> new EDSAnalysis(
                        gFlCod,
                        gFlLib,
                        backPsupData.getAttendus(gFlCod),
                        backPsupData.getRecoPremGeneriques(gFlCod),
                        backPsupData.getRecoTermGeneriques(gFlCod)
                ));
                Map<Integer, Integer> statsEds = statistiques.getStatsSpec(key);
                if(statsEds != null) {
                    statsEds.forEach((iMtCod, pct) -> {
                        String name = specialites.specialites().get(iMtCod);
                        if(name != null) {
                            analysis.nbAdmisEDS().put(pct, name);
                        }
                    });
                }
                Map<Integer, Integer> statsEds2 = statistiques.getStatsSpecCandidats(key);
                if(statsEds2 != null) {
                    statsEds2.forEach((iMtCod, pct) -> {
                        String name = specialites.specialites().get(iMtCod);
                        if(name != null) {
                            analysis.nbCandidatsEDS().put(pct, name);
                        }
                    });
                }

                if(specifiques) {
                    //les messages
                    //on aggrege tous les codes jurys de la filiere
                    List<Integer> gTaCods = ServerData.getFormationsFromFil(key).stream().map(f -> f.gTaCod).toList();
                    Set<String> juryCodes = backPsupData.getJuryCodesFromGTaCods(gTaCods);
                    analysis.recosScoPremSpecifiques().putAll(backPsupData.getRecoScoPremiere(juryCodes));
                    analysis.recosScoTermSpecifiques().putAll(backPsupData.getRecoScoTerminale(juryCodes));
                }
            }
        });
        return analyses;
    }

    public static Map<String, Attendus> getEDSSimple(PsupData psupData, PsupStatistiques data, Specialites specs, boolean specifiques) {
        EDSAggAnalysis eds = getEDS(psupData, data, specs, specifiques, false);
        return eds.getFrontData();
    }
}

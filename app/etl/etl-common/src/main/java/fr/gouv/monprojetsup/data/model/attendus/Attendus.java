package fr.gouv.monprojetsup.data.model.attendus;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.model.psup.PsupData;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public record Attendus(
        String attendus,
        String recoEDS,
        String recoEDSPrem,
        String recoEDSTerm) {

    public static Map<String, Attendus> getAttendusSimplifies(PsupData backPsupData) {
        val result = new HashMap<String, Attendus>();

        val filActives = new HashSet<>(backPsupData.filActives());
        filActives.addAll(backPsupData.las());
        val tousAttendus = backPsupData.getAttendus();

        filActives.forEach(gFlCod -> {
            String key = Constants.gFlCodToMpsId(gFlCod);

            val recoPremGeneriques = backPsupData.getRecoPremGeneriques(gFlCod);
            val recoTermGeneriques = backPsupData.getRecoTermGeneriques(gFlCod);
            val attendus = tousAttendus.get(gFlCod);
            boolean takeRecoPrem = (recoPremGeneriques != null
                    && (!recoPremGeneriques.equals(recoTermGeneriques))
                    && !recoPremGeneriques.contains("Pas de données pour cette fili")
            );

            boolean takeRecoGen = !takeRecoPrem && recoTermGeneriques != null;

            boolean takeRecoTerm = !takeRecoGen &&
                    recoTermGeneriques != null
                    && !recoTermGeneriques.contains("Pas de données pour cette fili");

            if (attendus != null || takeRecoGen || takeRecoTerm) {
                result.put(key, new Attendus(
                        compress(attendus),
                        takeRecoGen ? compress(recoTermGeneriques) : null,
                        takeRecoPrem ? compress(recoPremGeneriques) : null,
                        takeRecoTerm ? compress(recoTermGeneriques) : null
                ));
            }
        });
        return result;
    }

    public static @Nullable String compress(@Nullable String attendus) {
        if(attendus == null) return null;
        if(attendus.contains("ne comprend pas")) return null;
        int i = attendus.toLowerCase().lastIndexOf("cadrage national");
        if(i > 0) {
            attendus = attendus.substring(i + 16);
            i = attendus.indexOf("<br/><br/>");
            if(i > 0) {
                attendus = attendus.substring(i + 10);
            }
        }
        return attendus.replaceAll("null", " ");
    }

    @Nullable
    public String getConseilsFront() {
        if(recoEDS != null && !recoEDS.isEmpty()) {
            return Helpers.removeHtml(recoEDS);
        }
        return null;
    }

    @Nullable
    public String getAttendusFront() {
        if(attendus != null && !attendus.isEmpty()) {
            return Helpers.removeHtml(attendus);
        }
        return null;
    }


}

package fr.gouv.monprojetsup.data.infrastructure.model.attendus;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.Helpers;
import fr.gouv.monprojetsup.data.infrastructure.model.specialites.Specialites;
import fr.gouv.monprojetsup.data.infrastructure.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.infrastructure.psup.PsupData;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static fr.gouv.monprojetsup.data.infrastructure.model.attendus.AttendusDetailles.AttenduDetaille.compress;

public record Attendus(
        String attendus,
        String recoEDS,
        String recoEDSPrem,
        String recoEDSTerm) {

    public static Map<String, Attendus> getAttendus(
            PsupData psupData,
            PsupStatistiques data,
            Specialites specs,
            boolean specifiques) {
        AttendusDetailles eds = AttendusDetailles.getAttendusDetailles(
                psupData, data, specs, specifiques, false
        );
        return eds.getSimplifiedFrontVersion();
    }

    public static Map<String, Attendus> getAttendusSimplifies(PsupData backPsupData) {
        val result = new HashMap<String, Attendus>();

        val filActives = new HashSet<>(backPsupData.filActives());
        filActives.addAll(backPsupData.las());

        filActives.forEach(gFlCod -> {
            String key = Constants.gFlCodToFrontId(gFlCod);

            val recoPremGeneriques = backPsupData.getRecoPremGeneriques(gFlCod);
            val recoTermGeneriques = backPsupData.getRecoTermGeneriques(gFlCod);
            val attendus = backPsupData.getAttendus(gFlCod);
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

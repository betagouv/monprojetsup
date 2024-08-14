package fr.gouv.monprojetsup.suggestions.infrastructure.model.attendus;

import fr.gouv.monprojetsup.suggestions.domain.Helpers;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.specialites.Specialites;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.infrastructure.psup.PsupData;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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
        AttendusDetailles eds = AttendusDetailles.getAttendusDetailles(psupData, data, specs, specifiques, false);
        return eds.getSimplifiedFrontVersion();
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
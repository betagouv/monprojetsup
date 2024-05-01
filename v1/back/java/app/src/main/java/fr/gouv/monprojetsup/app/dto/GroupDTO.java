package fr.gouv.monprojetsup.app.dto;

import java.util.Set;

public record GroupDTO(
        String name,
        String id,
        String lycee,
        String classe,
        Set<String> admins,
        Set<String> members,
        Set<String> waiting,
        String registrationToken,
        String adminToken,
        boolean isOpenedForNewMembers,

        String expeENSGroup,

        StatsGroup stats
) {

    public record StatsGroup(
            int nStudents,
            int nbConnected,
            int nbProfileOk,
            int nbObjectiveCompleted
            ) {
    }
}

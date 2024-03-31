package fr.gouv.monprojetsup.web.dto;

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

        String expeENSGroup
) {
}

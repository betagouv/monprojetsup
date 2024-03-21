package fr.gouv.monprojetsup.web.dto;

import fr.gouv.monprojetsup.web.db.model.Lycee;
import fr.gouv.monprojetsup.web.db.model.User;

import java.util.*;

public record AdminInfosDTO(
        User.Role role,

        User.UserTypes type,

        User.UserTypes apparentType,
        Set<String> users,
        List<GroupDTO> groups,

        List<GroupDTO> openGroups,
        List<String> acccountCreationModeration,//list of logins waiting for validation

        List<Lycee> lycees,

        Boolean checkEmails,//checking emails of new accounts

        int profileCompleteness, //0 profile needed 1 preferences needed 2 ok,

        Map<String, User.UserTypes> roles,

        User.UserConfig config//config

        ){

    public AdminInfosDTO(
            User.Role role,
            User.UserTypes type,
            User.UserTypes appType,
            List<Lycee> lycees,
            Boolean checkEmails,
            int profileCompleteness,
            User.UserConfig config) {
        this(role,
                type,
                appType,
                new TreeSet<>(Comparator.comparing(x -> x.toLowerCase(Locale.ROOT))),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                lycees,
                checkEmails,
                profileCompleteness,
                new HashMap<>(),
                config
                );
    }

    public void sort() {
        groups.sort(Comparator.comparing(GroupDTO::name));
    }
}

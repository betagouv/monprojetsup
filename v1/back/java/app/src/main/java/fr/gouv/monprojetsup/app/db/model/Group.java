package fr.gouv.monprojetsup.app.db.model;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.dto.GroupDTO;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.app.db.model.Group.GROUPS_COLL_NAME;
import static fr.gouv.monprojetsup.app.db.model.User.normalizeUser;


@SuppressWarnings("ConstantConditions")
@Document(GROUPS_COLL_NAME)
@Data
public final class Group {
    public static final String GROUPS_COLL_NAME = "groups";

    public static final String LYCEES_FIELD = "lycee";
    public static final String ADMINS_FIELD = "admins";
    public static final String MEMBERS_FIELD = "members";
    public static final String WAITING_FIELD = "waiting";
    public static final String EXPE_ENS_GROUPE = "expeENSGroupe";
    public static final String REGISTRATION_TOKEN_FIELD = "registrationToken";
    public static final String ADMIN_REGISTRATION_TOKEN_FIELD = "adminToken";
    public static final String EXPE_ENS_GROUPE_FIELD = "expeENSGroupe";

    private @NotNull String name;

    private @Field("id") @NotNull String id;

    private @NotNull String lycee;
    private @NotNull String classe;

    private @NotNull Set<String> admins = new HashSet<>();
    private @NotNull Set<String> members = new HashSet<>();
    private @NotNull Set<String> waiting = new HashSet<>();


    private @Nullable String registrationToken;
    private @Nullable String adminToken;

    private String expeENSGroupe = "";

    private boolean isOpenedForNewMembers;

    public Group(Lycee lycee,
                 Classe classe,
                 String login,
                 Set<String> admins,
                 boolean isMember,
                 boolean isWaiting,
                 boolean isOpenedForNewMembers
    ) {
        this.name = lyceeToGroupName(lycee, classe);
        this.lycee = lycee.getId();
        this.classe = classe.index();
        this.id = lyceeToGroupId(lycee, classe);
        this.admins.addAll(admins);
        if(isMember) this.members.add(login);
        if(isWaiting) this.waiting.add(login);
        this.isOpenedForNewMembers = isOpenedForNewMembers;
    }


    /* used to create mini group */
    public Group(@NotNull String id, @NotNull String lycee, @NotNull String classe, @NotNull Set<String> admins, boolean isOpenedForNewMembers) {
        this.id = id;
        this.name = DB.lyceeToGroupName(lycee, classe);
        this.lycee = lycee;
        this.classe = classe;
        this.admins.addAll(admins);
        this.isOpenedForNewMembers = isOpenedForNewMembers;
    }

    public Group(String id, String lycee, String classe, Set<String> admins, boolean b, String expeENSGroupe) {
        this(id, lycee, classe, admins, b);
        this.expeENSGroupe = expeENSGroupe;
    }

    public Group(Lycee lycee, Classe classe, String login, Set<String> admins, boolean contains, boolean contains1, boolean b, String expeENSGroupe) {
        this(lycee, classe, login, admins, contains, contains1, b);
        this.expeENSGroupe = expeENSGroupe;
    }

    private Group(@NotNull String lycee, @NotNull String sid) {
        this.name = DB.lyceeToGroupName(lycee, sid);
        this.id = sid;
        this.lycee = lycee;
        this.classe = "";
    }


    private Group(Lycee lycee, Classe classe) {
        this.name = lyceeToGroupName(lycee, classe);
        this.id = lyceeToGroupId(lycee, classe);
        this.lycee = lycee.getId();
        this.classe = classe.index();
    }

    public static Group getNewGroup(String lycee, String sid) {
        return new Group(lycee, sid);
    }
    public static Group getNewGroup(Lycee lycee, Classe classe) {
        return new Group(lycee, classe);
    }



    public static GroupDTO miniGroup(Lycee lycee, Classe classe, String login, Group group) {
        return new Group(
                lycee,
                classe,
                login,
                group.admins(),
                group.members.contains(login),
                group.waiting.contains(login),
                true,
                group.expeENSGroupe).toDTO();
    }

    public Group miniGroup() {
        return new Group(id, lycee, classe, admins, true, expeENSGroupe);
    }


    public static String lyceeToGroupName(Lycee lycee, Classe classe) {
        return DB.lyceeToGroupName(lycee.getName(),classe.name());
    }

    public static String lyceeToGroupId(Lycee lycee, Classe classe) {
        return lyceeToGroupId(lycee.getId(),classe.index());
    }
    public static String lyceeToGroupId(String lyceeId, String classeId) {
        return lyceeId + "_" + classeId;
    }

    public Set<String> admins() {
        return admins.stream().map(User::normalizeUser).collect(Collectors.toSet());
    }

    public Set<String> members() {
        return members.stream().map(User::normalizeUser).collect(Collectors.toSet());
    }
    public Set<String> membersWaiting() {
        return waiting.stream().map(User::normalizeUser).collect(Collectors.toSet());
    }

    public void remove(String user) {
        admins.remove(normalizeUser(user));
        members.remove(normalizeUser(user));
    }

    /**
     * @param openGroup
     * @return true if the group was changed
     */
    public boolean setIsOpenedForNewMembers(boolean openGroup) {
        boolean changed = false;
        if(openGroup && registrationToken == null) {
            changed = true;
            registrationToken = RandomStringUtils.random(8, true, false).toUpperCase();
        } else if(!openGroup && registrationToken != null) {
            changed = true;
            registrationToken = null;
        }
        if(openGroup && adminToken == null) {
            changed = true;
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 5; i++) {
                String g = RandomStringUtils.random(5, true, false).toUpperCase().replace('I','L').replace('O','H');
                sb.append(g);
                if(i < 4) {
                    sb.append("-");
                }
            }
            adminToken = sb.toString();
        } else if(!openGroup && adminToken != null) {
            changed = true;
            adminToken = null;
        }
        if(adminToken != null) {
            String newToken = adminToken.replace('I','L').replace('O','H');
            changed |= !newToken.equals(adminToken);
            adminToken = newToken;
        }
        if(registrationToken != null) {
            String newToken = registrationToken.replace('I','L').replace('O','H');
            changed |= !newToken.equals(registrationToken);
            registrationToken = newToken;
        }
        changed |= isOpenedForNewMembers != openGroup;
        isOpenedForNewMembers = openGroup;
        return changed;
    }

    public String token() {
        return registrationToken;
    }


    public boolean isOpened() {
        return isOpenedForNewMembers;
    }

    public boolean updateFrom(Group other) {
        int before = admins.size() + members.size() + waiting.size();
        admins.addAll(other.admins());
        members.addAll(other.members());
        int after = admins.size() + members.size() + waiting.size();
        return before != after;
    }

    public void addMember(String login) {
        login = normalizeUser(login);
        waiting.remove(login);
        members.add(login);
    }

    public void addAdmin(String login) {
        login = normalizeUser(login);
        waiting.remove(login);
        admins.add(login);
    }

    public void removeMember(String login) {
        login = normalizeUser(login);
        members.remove(login);
        admins.remove(login);
        waiting.remove(login);
    }


    public boolean hasMember(String login) {
        login = normalizeUser(login);
        return members.contains(login) || waiting.contains(login);
    }

    public void removeAdmin(String login) {
        login = normalizeUser(login);
        admins.remove(login);
    }

    /**
     * @return true if the group was changed by init
     */
    public boolean init() {

        boolean changed = false;

        //creates token if needed
        changed |= setIsOpenedForNewMembers(isOpenedForNewMembers);

        int before = admins.size() + members.size() + waiting.size();
        admins.addAll(
                admins.stream().map(User::normalizeUser).toList()
        );
        members.addAll(
                members.stream().map(User::normalizeUser).toList()
        );
        waiting.addAll(
                waiting.stream().map(User::normalizeUser).toList()
        );
        int after = admins.size() + members.size() + waiting.size();
        changed |= before != after;

        admins.removeIf(s -> !s.equals(normalizeUser(s)));
        members.removeIf(s -> !s.equals(normalizeUser(s)));
        waiting.removeIf(s -> !s.equals(normalizeUser(s)));

        if(id == null) {
            changed = true;
            id = lyceeToGroupId(lycee, name);
        }
        if(waiting == null) {
            changed = true;
            waiting = new HashSet<>();
        }
        if(admins == null) {
            changed = true;
            admins = new HashSet<>();
        }
        if(members == null) {
            changed = true;
            members = new HashSet<>();
        }

        return changed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (isOpenedForNewMembers != group.isOpenedForNewMembers) return false;
        if (!name.equals(group.name)) return false;
        if (!id.equals(group.id)) return false;
        if (!lycee.equals(group.lycee)) return false;
        if (!classe.equals(group.classe)) return false;
        if (!Objects.equals(admins, group.admins)) return false;
        if (!Objects.equals(members, group.members)) return false;
        if (!Objects.equals(waiting, group.waiting)) return false;
        if (!Objects.equals(registrationToken, group.registrationToken))
            return false;
        return Objects.equals(adminToken, group.adminToken);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + lycee.hashCode();
        result = 31 * result + classe.hashCode();
        result = 31 * result + (admins != null ? admins.hashCode() : 0);
        result = 31 * result + (members != null ? members.hashCode() : 0);
        result = 31 * result + (waiting != null ? waiting.hashCode() : 0);
        result = 31 * result + (registrationToken != null ? registrationToken.hashCode() : 0);
        result = 31 * result + (adminToken != null ? adminToken.hashCode() : 0);
        result = 31 * result + (isOpenedForNewMembers ? 1 : 0);
        return result;
    }

    public GroupDTO toDTO() {
        return new GroupDTO(
                name,
                id,
                lycee,
                classe,
                admins,
                members,
                waiting,
                registrationToken,
                adminToken,
                isOpenedForNewMembers,
                expeENSGroupe
        );
    }

    public void resetRegistrationCode() {
        registrationToken = RandomStringUtils.random(8, true, false).toUpperCase();
    }

    private Group() {
        lycee = "";
        classe = "";
        name = "";
        id = "";
    }

    public boolean hasAdmin(String login) {
        return admins != null && admins.contains(normalizeUser(login));
    }

    public boolean isExpeENSGroupeTest() {
            return expeENSGroupe != null && !expeENSGroupe.isEmpty() && expeENSGroupe.equals("T");
    }

    public boolean isExpeENSGroup() {
        return  expeENSGroupe != null && !expeENSGroupe.isEmpty();
    }
}

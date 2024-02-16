package fr.gouv.monprojetsup.web.db.model;

import fr.gouv.monprojetsup.tools.Serialisation;
import fr.gouv.monprojetsup.tools.csv.CsvTools;
import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.web.dto.GroupDTO;
import fr.gouv.monprojetsup.web.server.WebServer;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static fr.gouv.monprojetsup.web.db.DB.CLASSE_DEMO;
import static fr.gouv.monprojetsup.web.db.DB.LYCEE_DEMO;
import static fr.gouv.monprojetsup.web.db.model.Group.lyceeToGroupId;
import static fr.gouv.monprojetsup.web.db.model.Group.miniGroup;
import static fr.gouv.monprojetsup.web.db.model.User.normalizeUser;

@Getter
public class Groups {

    private static final Logger LOGGER = Logger.getLogger(Groups.class.getSimpleName());

    private final List<Group> groups = new ArrayList<>();

    /**
     * immutable once server is started
     **/
    private final List<Lycee> lycees = new ArrayList<>();


    public synchronized @NotNull Group findOrCreateDemoGroup() {
        Group answer = groups.stream().filter(x -> x.getClasse().contains(CLASSE_DEMO)).findAny().orElse(null);
        if (answer == null) {
            Lycee lycDemo = lycees.stream().filter(l -> l.getId().equals(LYCEE_DEMO)).findAny().orElse(null);
            if (lycDemo == null) {
                lycDemo = new Lycee(LYCEE_DEMO, LYCEE_DEMO + " (lycée fictif)",
                        List.of(new Classe(CLASSE_DEMO, CLASSE_DEMO + " (classe fictive)",
                                Classe.Niveau.premiere,
                                false,
                                false,
                                "G"
                                ,"")
                        ),
                        new HashSet<>()
                );
                lycees.add(lycDemo);
            }
            answer = createNewGroup(lycDemo, lycDemo.getClasses().get(0));
        }
        return answer;
    }

    public synchronized @NotNull Group findGroupWithAccessCode(String code) throws DBExceptions.UserInputException.WrongAccessCodeException {
        Group group = groups.stream().filter(g -> g.isAccessibleWithToken(code)).findAny().orElse(null);
        if (group == null) {
            throw new DBExceptions.UserInputException.WrongAccessCodeException();
        }
        return group;
    }

    public synchronized @NotNull Group findGroupWithAdminAccessCode(String code) throws DBExceptions.UserInputException.WrongAccessCodeException {
        Group group = groups.stream().filter(g -> g.isAdminAccessibleWithToken(code)).findAny().orElse(null);
        if (group == null) {
            throw new DBExceptions.UserInputException.WrongAccessCodeException();
        }
        return group;
    }

    public synchronized void acceptUserCreation(String login) {
        groups.forEach(group -> {
            if (group.membersWaiting().contains(login)) {
                group.addMember(login);
            }
        });
        updateGroupData();
    }

    public synchronized boolean isEvalENS(Set<String> lycees) {
        return lycees.stream().anyMatch(lyceesExpeENS::contains);
    }
    public boolean isEvalENSTest(Set<String> lycees) {
        return false;
    }


    public synchronized boolean isEvalIndivisible(Set<String> lycees) {
        return lycees.stream().anyMatch(lyceesExpeIndivisible::contains);
    }

    /* cached info */
    private final transient Map<String, Group> members = new ConcurrentHashMap<>();
    private final transient Map<String, Group> idToGroup = new ConcurrentHashMap<>();

    private final transient Set<String> lyceesExpeENS = new HashSet<>();
    private final transient Set<String> lyceesExpeIndivisible = new HashSet<>();

    public synchronized @Nullable Group getGroup(String login) {
        return members.get(login);
    }

    public synchronized Group createNewGroup(@NotNull Lycee lycee, @NotNull String sid) throws DBExceptions.EmptyGroupIdException {
        if (sid.isEmpty()) {
            throw new DBExceptions.EmptyGroupIdException();
        }
        String gid = Normalizer.normalize(lyceeToGroupId(lycee.getId(), sid).toLowerCase(), Normalizer.Form.NFKD);
        Group current = groups.stream().filter(g -> gid.equals(g.getId())).findAny().orElse(null);
        if (current == null) {
            current = Group.getNewGroup(lycee.getId(), gid);
            groups.add(current);
            updateGroupData();
        }
        return  current;
    }


    @NotNull
    public synchronized Group createNewGroup(@NotNull Lycee lycee, @NotNull Classe classe) {
        String sid = lyceeToGroupId(lycee, classe);
        Group current = groups.stream().filter(g -> sid.equals(g.getId())).findAny().orElse(null);
        if (current == null) {
            current = Group.getNewGroup(lycee, classe);
            groups.add(current);
            updateGroupData();
            current.init();
            current.setIsOpenedForNewMembers(true);
        }
        return current;
    }

    public synchronized void deleteGroup(@NotNull String id) {
        groups.removeIf(g -> id.equals(g.getId()));
        updateGroupData();
    }

    @NotNull
    public synchronized Group findGroup(String id) throws DBExceptions.EmptyGroupIdException, DBExceptions.UnknownGroupException {
        if (id == null || id.isEmpty()) {
            throw new DBExceptions.EmptyGroupIdException();
        }
        Group group = groups.stream().filter(
                g -> g.getId().equals(id)
        ).findAny().orElse(null);
        if (group == null) {
            throw new DBExceptions.UnknownGroupException();
        }
        return group;
    }

    public synchronized void deleteUser(String user) {
        groups.forEach(g -> g.remove(user));
        updateGroupData();
    }

    public synchronized List<Group> addOrRemoveMember(
            String groupId,
            String memberLogin,
            boolean addMember,
            @Nullable String groupToken,
            boolean verifyToken
    ) throws DBExceptions.EmptyGroupIdException, DBExceptions.UnknownGroupException, DBExceptions.UserInputException.InvalidGroupTokenException {
        memberLogin = normalizeUser(memberLogin);
        Group g = findGroup(groupId);
        boolean rightToken = !verifyToken || (groupToken != null && groupToken.equals(g.token()));
        if(!rightToken) {
            throw new DBExceptions.UserInputException.InvalidGroupTokenException();
        }
        if (addMember) {
            String finalMemberLogin = memberLogin;
            List<Group> changedGroups = new ArrayList<>(this.groups.stream().filter(gr -> gr.hasMember(finalMemberLogin)).toList());
            changedGroups.forEach(gr -> gr.members().remove(finalMemberLogin));
            g.addMember(memberLogin);
            changedGroups.add(g);
            updateGroupData();
            return changedGroups;
        } else {
            g.removeMember(memberLogin);
            updateGroupData();
            return List.of(g);
        }
    }

    public synchronized void addAdmin(String groupId, String login) throws DBExceptions.UnknownGroupException, DBExceptions.EmptyGroupIdException {
        login = normalizeUser(login);
        Group grp = findGroup(groupId);
        grp.addAdmin(normalizeUser(login));
        updateGroupData();
    }

    public synchronized void updateGroupData() {
        members.clear();
        groups.forEach(g -> g.members().forEach(m -> members.put(m, g)));
        groups.forEach(g -> g.membersWaiting().forEach(m -> members.put(m, g)));

        idToGroup.clear();
        groups.forEach(g -> idToGroup.put(g.getId(), g));

        lyceesExpeENS.clear();
        lyceesExpeENS.addAll(lycees.stream().filter(Lycee::isExpeENS).map(Lycee::getId).toList());

        lyceesExpeIndivisible.clear();
        lyceesExpeIndivisible.addAll(lycees.stream().filter(Lycee::isExpeIndivisible).map(Lycee::getId).toList());

    }

    @NotNull
    public static synchronized Groups loadClassesCsvFile(String expeFilename) {
        Groups ainjecter = new Groups();
        try {
            if(expeFilename == null) return ainjecter;

        List<Map<String, String>> lines = CsvTools.readCSV(expeFilename, ';');

            Map<String, Lycee> lyceesIndexed = new HashMap<>();
            lines.forEach(m -> {
                String proviseur = m.get("MAIL");
                String lyceeCode = m.get("UAI");
                String lyceeName = m.get("INTITULE ETAB");
                String classe = m.get("INTITULE CLASSE");
                String expeENSGroup = m.get("GROUPE");
                boolean expeENS = !m.containsKey("EXPE_ENS") || m.get("EXPE_ENS").equals("1");
                boolean expeIndivisible = m.containsKey("EXPE_INDIVISIBLE") && m.get("EXPE_INDIVISIBLE").equals("1");
                String niveauStr = m.getOrDefault("NIVEAU", "terminale");
                Classe.Niveau niveau = Classe.Niveau.valueOf(niveauStr);
                if (proviseur == null || lyceeCode == null || lyceeName == null || classe == null)
                    throw new RuntimeException("Malformed '" + expeFilename + "'");
                if(!lyceeName.isEmpty() && !classe.isEmpty()) {
                    Lycee lycee = lyceesIndexed.get(lyceeCode);
                    if (lycee == null) {
                        lycee = new Lycee(lyceeCode, lyceeName, new ArrayList<>(), new HashSet<>(Set.of(proviseur)));
                        lyceesIndexed.put(lyceeCode, lycee);
                    }
                    lycee.setExpeENS(expeENS);
                    lycee.setExpeIndivisible(expeIndivisible);
                    boolean isPro = false;
                    boolean isTechno = false;
                    String serie = "";
                    if (classe.contains("STMG")) {
                        isTechno = true;
                        serie = "STMG";
                    }
                    if (classe.contains("ST2S")) {
                        isTechno = true;
                        serie = "ST2S";
                    }
                    if (classe.contains("AGORA")) {
                        isPro = true;
                        serie = "AGORA";
                    }
                    lycee.addClassse(
                            new Classe(
                                    classe,
                                    classe,
                                    niveau,
                                    isPro,
                                    isTechno,
                                    serie,
                                    expeENSGroup
                            )
                    );
                }
            });
            ainjecter.lycees.addAll(lyceesIndexed.values());
        } catch (Exception e) {
            WebServer.LOGGER.severe(e.getMessage());
        }
        return ainjecter;
    }


    public synchronized Pair<List<Group>, List<Lycee>> loadGroupsToInjectFromCsvFile(String filename) {
        Pair<List<Group>, List<Lycee>> result = Pair.of(new ArrayList<>(), new ArrayList<>());
        Groups aInjecter2 = loadClassesCsvFile(filename);
        List<Lycee> newLycees = inject(aInjecter2).getRight();
        result.getRight().addAll(newLycees);
        aInjecter2.lycees.forEach(lycee -> lycee.getClasses().forEach(classe -> {
            String groupId = lyceeToGroupId(lycee, classe);
            Group group = idToGroup.get(groupId);
            if (group == null) {
                group = createNewGroup(lycee, classe);
                result.getLeft().add(group);
            } else if(!Objects.equals(
                    group.getExpeENSGroupe() == null ? "" : group.getExpeENSGroupe(),
                    classe.expeENSGroup() == null ? "" : classe.expeENSGroup()
            )
            ){
                group.setExpeENSGroupe(classe.expeENSGroup());
                result.getLeft().add(group);
            }
        }));
        updateGroupData();
        return result;
    }



    public synchronized void init() throws IOException {


        groups.removeIf(Objects::isNull);
        groups.removeIf(g -> g.getLycee() == null || g.getLycee().isEmpty());

        lycees.removeIf(l -> l == null || l.getId() == null || l.getId().isEmpty());
        lycees.forEach(Lycee::init);

        findOrCreateDemoGroup();

        createMissingGroups();

        groups.removeIf(g -> g.getName() == null || g.getName().isEmpty());

        groups.forEach(Group::init);

        updateGroupData();

        save("groupsLastInit.json");

    }

    public synchronized void save(String filename) throws IOException {
        Serialisation.toJsonFile(filename, this, true);
    }

    private Pair<List<Group>,List<Lycee>> inject(Groups aInjecter) {
        List<Group> newGroups = new ArrayList<>();
        List<Lycee> newLycees = new ArrayList<>();
        aInjecter.lycees.forEach(lycee -> {
            lycee.init();
            Lycee existing = lycees.stream().filter(l -> lycee.getId().equals(l.getId())).findFirst().orElse(null);
            if (existing == null) {
                LOGGER.info("Création d'un nouveau lycée '" + lycee.getName() + "' avec les classes " +lycee.getClasses().toString());
                lycees.add(lycee);
                newLycees.add(lycee);
            } else {
                boolean changed = existing.updateFrom(lycee);
                if(changed) newLycees.add(existing);
            }
        });

        aInjecter.groups.forEach(group -> {
            Group existing = groups.stream().filter(
                    g -> group.getName().equals(g.getName())
            ).findFirst().orElse(null);
            if (existing == null) {
                groups.add(group);
                newGroups.add(group);
            } else {
                boolean changed = existing.updateFrom(group);
                if(changed) newGroups.add(group);
            }
        });
        updateGroupData();
        return Pair.of(newGroups, newLycees);
    }



    public synchronized List<Group> createMissingGroups() {
        updateGroupData();
        List<Group> newGroups = computeMissingGroups(lycees, idToGroup.keySet());
        groups.addAll(newGroups);
        updateGroupData();
        return newGroups;
    }

    public static synchronized List<Group> computeMissingGroups(Collection<Lycee> lycees, Set<String> knownGroups) {
        return lycees.stream().flatMap(lycee -> lycee.getClasses().stream().map(classe -> {
            String groupId = lyceeToGroupId(lycee, classe);
            if (knownGroups.contains(groupId))
                return null;

            Group group = Group.getNewGroup(lycee, classe);
            group.init();
            group.setIsOpenedForNewMembers(true);
            return group;

        })).filter(Objects::nonNull).toList();
    }

    public synchronized AdminInfosDTO getAdminInfos(
            String login,
            User.Role role,
            User.UserTypes type,
            User.UserTypes appType,
            Set<String> lyceesUser,
            int profileCompleteness,
            User.UserConfig config
    ) {

        //on liste les lycées de l'utilisateur
        List<Lycee> lyceesUserItems =
                lycees.stream().filter(l ->
                        role == User.Role.ADMIN
                                || lyceesUser.contains(l.getId())
                ).toList();

        AdminInfosDTO result = new AdminInfosDTO(
                role,
                type,
                appType,
                lyceesUserItems,
                WebServer.config().isConfirmEmailOnAccountCreation(),
                profileCompleteness,
                config
        );

        List<GroupDTO> groups = this.groups.stream().map(Group::toDTO).toList();

        if (role == User.Role.ADMIN) {
            result.groups().clear();
            result.groups().addAll(groups);
            return result;
        } else if (role == User.Role.TEACHER) {
            result.groups().addAll(
                    groups.stream()
                            .filter(g -> g.admins().contains(login)).toList()
            );
            result.openGroups().addAll(
                    groups.stream()
                            .filter(g ->
                                    !g.admins().contains(login)
                                            && lyceesUser.contains(g.lycee())
                            ).toList()
            );
            return result;
        } else {//student only sees the list of admins, if he is in a group
            Group group = getGroup(login);
            if (group != null) {
                //le lycéen est déjà dans un groupe
                //on renvoie le groupe et l'élève
                lycees.stream()
                        .filter(l -> l.getId().equals(group.getLycee()) || l.getName().equals(group.getLycee()))
                        .findAny()
                        .ifPresent(
                                lyc -> lyc.getClasses().stream()
                                        .filter(c -> c.index().equals(group.getClasse()) || c.name().equals(group.getClasse()))
                                        .findAny()
                                        .ifPresent(
                                                cla -> result.groups().add(miniGroup(lyc, cla, login, group))//au passage le lycéen reçoit le flag ENS
                                        )
                        );
                //on inject la donneé groupe ENS pour simplifier le travail côté front
                config.setExpeENSGroup(group.getExpeENSGroupe());
            } else {
                lycees.stream()
                        .filter(l -> lyceesUser.contains(l.getId()))
                        .findAny()
                        .ifPresent(
                                lyc -> result.openGroups().addAll(
                                        getOpenGroupsLycee(lyc).stream()
                                                .map(Group::toDTO)
                                                .toList()
                                )
                        );
            }
            return result;
        }
    }

    private synchronized Collection<Group> getOpenGroupsLycee(Lycee lycee) {
        return
                groups.stream()
                        .filter(g -> Objects.equals(g.getLycee(), lycee.getName()))
                        .filter(Group::isOpened)
                        .map(Group::miniGroup)
                        .toList();
    }

    public synchronized Lycee getLycee(String lycee) {
        return lycees.stream().filter(l -> l.getId().equals(lycee)).findAny().orElse(null);
    }

    public synchronized void loadGroups(List<Group> toList) {
        groups.clear();
        groups.addAll(toList);
        updateGroupData();
    }

    public synchronized void loadLycees(List<Lycee> toList) {
        lycees.clear();
        lycees.addAll(toList);
        updateGroupData();
    }

    public synchronized void upsert(Lycee lycee) {
        lycees.removeIf(l -> l.getId().equals(lycee.getId()));
        lycees.add(lycee);
        updateGroupData();
    }

    public synchronized void upsert(Group group) {
        groups.removeIf(g -> g.getId().equals(group.getId()));
        groups.add(group);
        updateGroupData();
    }

}

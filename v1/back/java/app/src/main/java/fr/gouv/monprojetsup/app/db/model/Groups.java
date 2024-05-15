package fr.gouv.monprojetsup.app.db.model;

import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import fr.gouv.monprojetsup.app.server.WebServer;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static fr.gouv.monprojetsup.app.db.DB.CLASSE_DEMO;
import static fr.gouv.monprojetsup.app.db.DB.LYCEE_DEMO;
import static fr.gouv.monprojetsup.app.db.model.Group.lyceeToGroupId;

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

    /* cached info */
    private final transient Map<String, Group> members = new ConcurrentHashMap<>();
    private final transient Map<String, Group> idToGroup = new ConcurrentHashMap<>();

    private final transient Set<String> lyceesExpeENS = new HashSet<>();
    private final transient Set<String> lyceesExpeIndivisible = new HashSet<>();

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
        current.setExpeENSGroupe(classe.expeENSGroup());
        return current;
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
    public static synchronized Groups loadClassesCsvFile(String expeFilename, char separator) {
        Groups ainjecter = new Groups();
        try {
            if(expeFilename == null) return ainjecter;

        List<Map<String, String>> lines = CsvTools.readCSV(expeFilename, separator);

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
        Groups aInjecter2 = loadClassesCsvFile(filename, ',');
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


}

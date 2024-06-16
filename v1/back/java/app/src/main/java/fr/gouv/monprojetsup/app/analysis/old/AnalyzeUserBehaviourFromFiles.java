package fr.gouv.monprojetsup.app.analysis.old;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.app.analysis.WordSearch;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import fr.gouv.monprojetsup.app.db.model.Group;
import fr.gouv.monprojetsup.app.db.model.User;
import fr.gouv.monprojetsup.app.log.ServerTrace;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Helpers.isFiliere;
import static java.lang.Math.abs;

@SuppressWarnings("ALL")
public class AnalyzeUserBehaviourFromFiles {


    public static void main(String[] args) throws IOException {

        String directory = "data";

        /* load all users as a list of users from the file "usersExpeENS.json" */

        List<Group> groups = Serialisation.fromJsonFile("groups.json",
                new TypeToken<List<Group>>(){}.getType()
        );



        List<User> usersOfInterest
                = Serialisation.fromJsonFile(
                "users.json",
                new TypeToken<List<User>>(){}.getType()
        );

                /* loading all json files in the directory, containign arrays of ServerTrace,
        and store all the traces in a Map, indexed by timestamp
         */

        List<ServerTrace> allTraces = Collections.synchronizedList(new ArrayList<>());

        Arrays.stream(Objects.requireNonNull(new File(directory).listFiles())).parallel()
                .filter(f -> f.getName().startsWith("traces"))
                .filter(f -> f.getName().endsWith(".json"))
                .forEach(f -> {
                            ServerTrace[] tracesArray = new ServerTrace[0];
                            try {
                                tracesArray = new ObjectMapper().readValue(f, ServerTrace[].class);
                            } catch (IOException e) {

                            }
                            Collections.addAll(allTraces, tracesArray);
                        }
                );

        WordSearch.analyseWordSearch(allTraces);

        Set<String> testGroups = groups.stream().map(Group::getExpeENSGroupe).collect(Collectors.toSet());

        for(String testGroup : testGroups) {
            Set<String> members = groups.stream()
                    .filter(g -> g.getExpeENSGroupe().equals(testGroup))
                    .flatMap(g -> g.getMembers().stream())
                    .collect(Collectors.toSet());
            analyze(
                    groups.stream().filter(g -> g.getExpeENSGroupe().equals(testGroup)).toList(),
                    usersOfInterest.stream().filter(u -> members.contains(u.login())).toList(),
                    allTraces,
                    testGroup
            );
        }

    }

    public static void analyze(
            List<Group> groups,
            List<User> users,
            List<ServerTrace> tracesList,
            String suffix) throws IOException {

        Map<String, ServerTrace> traces = new HashMap<>();
        tracesList.forEach(serverTrace -> {
            traces.put(serverTrace.timestamp(), serverTrace);
        });

        try(OutputStreamWriter fos = new OutputStreamWriter(
                Files.newOutputStream(Path.of("report_" + suffix + ".json"))
        )) {

            //creates a logger using fos


            if (traces.isEmpty()) {
                fos.append("No data to analyze for group ").append(suffix).append(")").append("\n");
                return;
            }

            Set<String> loginsOfInterest = users.stream().map(User::login).collect(Collectors.toSet());

            Set<Group> groupsOfInterest = groups.stream()
                    .filter(g -> g.getMembers().stream().anyMatch(loginsOfInterest::contains))
                    .collect(Collectors.toSet());


            Map<String, Group> usertoGroup = new HashMap<>();
            groupsOfInterest.forEach(group -> group.getMembers().stream()
                    .filter(loginsOfInterest::contains)
                    .forEach(login -> usertoGroup.put(login, group)));


            Map<String, Long> lycees = users.stream()
                    .flatMap(u -> u.getLycees().stream())
                    .collect(Collectors.groupingBy(u -> u, Collectors.counting()));


            Map<String, Long> groupes =
                    usertoGroup.values().stream().collect(Collectors.groupingBy(Group::getName, Collectors.counting()));
            List<String> realGroups = groupes.entrySet().stream().filter(e -> e.getValue() >= 5).map(Map.Entry::getKey).toList();

            fos.append("Périmètre des statistiques: lycéens du groupe '" + suffix + "'").append("\n");
            fos.append("Lycées: " + lycees.size()).append("\n");
            fos.append("Classes/groupes: " + groupes.size()).append("\n");
            fos.append("Lycéens: " + users.size()).append("\n");

            fos.append("Lycées avec au moins 5 lycéens: " + lycees.entrySet().stream()
                    .filter(e -> e.getValue() >= 5).count()).append("\n");
            fos.append("Classes/groupes avec au moins 5 lycéens: " + realGroups.size()).append("\n");
            fos.append("\t\t " + String.join("\n\t\t", realGroups)).append("\n");

            List<User> usersOfInterestWithFavori = new ArrayList<>(users);
            /* keeping only users having selected a formation favori */
            usersOfInterestWithFavori.removeIf(u -> u.pf().suggApproved().stream().noneMatch(s -> isFiliere(s.fl())));
            //print the number of users fof interest
            fos.append("Lycéens avec au moins une formation en favori: " + usersOfInterestWithFavori.size()).append("\n");

            //computing the set of localdatetime, rounded to hour, for which we observe at least one trace
            List<String> hours = traces.keySet().stream()
                    .map(LocalDateTime::parse)
                    .map(ldt -> ldt.withMinute(0).withSecond(0).withNano(0))
                    .map(LocalDateTime::toString)
                    .distinct()
                    .sorted()
                    .toList();
            Serialisation.toJsonFile("hours.json", hours, true);

            traces.values().removeIf(st -> st.event() == null);
            traces.values().removeIf(st -> !loginsOfInterest.contains(st.origin()));
            //print the number of traces of users of interest
            fos.append("Traces d'évènement: " + traces.size()).append("\n");

            // Analyzing the traces
            //compute the first date where the occurence of an event containing "openUrl" has been seen
            LocalDateTime firstOpenUrlTimestamp = traces.values().stream()
                    .filter(st -> st.event() != null && st.event().contains("openUrl"))
                    .map(ServerTrace::timestamp)
                    .map(LocalDateTime::parse)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            // if(firstOpenUrlTimestamp == null) throw new RuntimeException("No occurence of an event containing openUrl");

            //LOGGER.info("First date where openurl was observed: " + firstOpenUrlTimestamp);

            //remove from traces all elements whose keys is strictly earlier to firstOpenUrlTimestamp
            if (firstOpenUrlTimestamp != null) {
                traces.entrySet().removeIf(e -> LocalDateTime.parse(e.getKey()).isBefore(firstOpenUrlTimestamp));
            }

            //list all users with an event of type UpdateProfileService and params containing a key of type suggestions
            ///with a "fl" field coantining "fl
            List<Pair<ServerTrace, Map>> updateProfileServiceWithSuggestions = traces.values().stream().filter(st ->
                            st.event().contains("UpdateProfileService") || st.event().contains("UpdateProfileHandler")
                    )
                    .filter(st -> st.param() instanceof Map)
                    .map(st -> Pair.of(st, (Map<String, Object>) st.param()))
                    .map(p -> Pair.of(p.getLeft(), p.getRight().get("suggestions")))
                    .filter(p -> p.getRight() instanceof List)
                    .map(p -> Pair.of(p.getLeft(), (List) p.getRight()))
                    .map(p -> Pair.of(p.getLeft(), p.getRight().size() == 1 ? p.getRight().get(0) : null))
                    .filter(p -> p.getRight() instanceof Map)
                    .map(p -> Pair.of(p.getLeft(), (Map) p.getRight()))
                    .toList();


            analyseFormationsSuggestions(traces, usertoGroup, users, fos, suffix);

            Serialisation.toJsonFile("suggestionsObserved_" + suffix + ".json", updateProfileServiceWithSuggestions, true);

            List<ServerTrace> ajoutFavoriFormation = updateProfileServiceWithSuggestions.stream()
                    .map(p -> Pair.of(p.getLeft(), (Map<String, Object>) p.getRight()))
                    .filter(p -> {
                                Map<String, Object> m = p.getRight();
                                if (m.get("fl") instanceof String fl && m.get("status") instanceof Double status) {
                                    return isFiliere(fl) && (Math.round(status) == 1L);
                                }
                                if (m.get("fl") instanceof String fl && m.get("status") instanceof Integer status) {
                                    return isFiliere(fl) && (status == 1);
                                }
                                return false;
                            }
                    )
                    .map(Pair::getLeft)
                    .sorted(Comparator.comparing(ServerTrace::timestamp))
                    .toList();

            List<ServerTrace> filieresSuggestions = traces.values().stream()
                    .filter(st -> st.event() != null && (st.event().contains("front reloadSuggestions fl")
                            || st.event().contains("front reloadSuggestions fr")
                            || st.event().contains("front reloadSuggestions formations"))
                    )
                    .toList();

            Set<String> candidatesWithFilieresSuggestions = filieresSuggestions.stream()
                    .map(ServerTrace::origin)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            fos.append("Lycéens ayant vu une ou plusieurs suggestions de filières: " + candidatesWithFilieresSuggestions.size()).append("\n");

            Map<String, List<String>> groupsWithFilieresSuggestions =
                    candidatesWithFilieresSuggestions.stream()
                            .map(id -> Pair.of(id, usertoGroup.get(id)))
                            .filter(p -> p.getRight() != null)
                            .collect(Collectors.groupingBy(p -> p.getRight().getId()))
                            .entrySet().stream().collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            e -> e.getValue().stream().map(Pair::getLeft).toList()
                                    )
                            );
            Serialisation.toJsonFile("groupsWithFilieresSuggestions" + suffix + ".json", groupsWithFilieresSuggestions, true);

            /*reloadSuggestions fl*/

            Serialisation.toJsonFile("addSingleFiliereToFavoris" + suffix + ".json", ajoutFavoriFormation, true);


            List<ServerTrace> openPsupUrl = traces.values().stream()
                    .filter(st -> st.event().contains("openUrl") && st.event().contains("parcoursup"))
                    .toList();
            List<ServerTrace> openOnisepUrl = traces.values().stream()
                    .filter(st -> st.event().contains("openUrl") && (st.event().contains("www.onisep.fr") || st.event().contains("avenirs")))
                    .toList();

            List<String> usersHavingOpenedPsupUrl = openPsupUrl.stream()
                    .map(ServerTrace::origin)
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .toList();
            List<String> usersHavingOpenedOnisepUrl = openOnisepUrl.stream()
                    .map(ServerTrace::origin)
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .toList();

            //print the two sizes of the sets of users
            fos.append("Lycéens ayant visité au moins une url parcoursup: " + usersHavingOpenedPsupUrl.size()).append("\n");
            fos.append("Lycéens ayant visité au moins une url onisep: " + usersHavingOpenedOnisepUrl.size()).append("\n");
        }
    }

    private static void analyseFormationsSuggestions(
            Map<String, ServerTrace> traces,
            Map<String, Group> usertoGroup, List<User> users, OutputStreamWriter fos, String suffix) throws IOException {

        Map<String,List<ServerTrace>> connectionsOfUserOfInterestsInTestsGroupAfterStratification = traces.values().stream()
                .filter(st -> st.origin() != null)
                .collect(Collectors.groupingBy(ServerTrace::origin));

        detailedNavigationAnalysis(
                "traces_utilisateurs_" + suffix + ".json",
                connectionsOfUserOfInterestsInTestsGroupAfterStratification,
                usertoGroup,
                users,
                fos
        );


    }

    public static boolean anonymize= false;

    private static void detailedNavigationAnalysis(
            String filename,
            Map<String, List<ServerTrace>> connectionsOfUserOfInterestsInTestsGroupAfterStratification,
            Map<String, Group> usertoGroup,
            List<User> usersList,
            OutputStreamWriter fos) throws IOException {

        Map<String, User> users = new HashMap<>();
        usersList.forEach(u -> users.put(u.login(), u));

        Map<String,List<String>> tracesOfUsersHavingNotSeenSuggestions
                = reorderEventsByTimestamps(connectionsOfUserOfInterestsInTestsGroupAfterStratification);

        Map<String, List<Pair<String,List<String>>>> details = new HashMap<>();
        tracesOfUsersHavingNotSeenSuggestions.forEach((k,v) -> {
            Group group = usertoGroup.get(k);
            if(group != null) {
                List<Pair<String,List<String>>> m = details.computeIfAbsent(group.getId(), s -> new ArrayList<>());
                if(!v.isEmpty()) {
                    List<List<String>> vs = stratify(v);
                    vs.forEach(l -> m.add(Pair.of(anonymize ? ("" + abs(k.hashCode())) : k, l)));
                }
            } else {
                //LOGGER.info("No group for user " + k);
            }
        });
        details.values().forEach(l -> l.sort(
                Comparator.comparing(p -> p.getRight().get(0)
        )));

        Serialisation.toJsonFile(
                filename,
                details,
                true
        );

        Set<String> nbUsersWithRealNavigation = new HashSet<>();
        Map<String, LocalDateTime> datesConnexions = new HashMap<>();

        Set<String> retours = new HashSet<>();
        Set<String> allUSers = new HashSet<>();

        Map<String, Set<String>> suggestionsVues = new HashMap<>();

        try(CsvTools csv = new CsvTools(filename.replace("json","csv"), ',')) {
            csv.appendHeaders(List.of(
                    "Lycée",
                    "Groupe",
                    "Date et heure",
                    "Utilisateur",
                    "Durée de navigation (min)",
                    "nombre de détails",
                    "nombre d'url parcoursup",
                    "nombre d'url onisep",
                    "nombre de clicks",
                    "nb favoris",
                    "groupe expé scien",
                    "retour"
                    ));
            AtomicInteger i = new AtomicInteger(1);
            Map<String, String> userToIndex = new HashMap<>();
            List<Pair<String, List<String>>> l =
                    details.values().stream().flatMap(List::stream)
                            .sorted(Comparator.comparing(p -> p.getRight().get(0))).toList();

            //for (Map.Entry<String, List<Pair<String, List<String>>>> entry : details.entrySet()) {
                //String g = entry.getKey();
                //List<Pair<String, List<String>>> v = entry.getValue();
                for (Pair<String, List<String>> p : l) {
                    long dur = duration(p.getRight());
                    if(dur <= 0) continue;
                    if(!aVuUneSuggestionDeFiliere(p.getRight())) continue;
                    String user = p.getLeft();
                    Group gr = usertoGroup.get(user);
                    if(gr == null) continue;
                    if(gr.getLycee().contains("Demo")) continue;

                    allUSers.add(user);
                    //calcul des suggestions vues
                    suggestionsVues.computeIfAbsent(user, s -> new HashSet<>()).addAll(getSuggestions(p.getRight()));
                    //lycée
                    csv.append(gr == null ? "null" : gr.getLycee());
                    //group
                    csv.append(gr == null ? "null" : gr.getId());
                    //Date et heure
                    String ldtString = p.getRight().get(0).substring(0, p.getRight().get(0).indexOf('.'));
                    LocalDateTime ldt = LocalDateTime.parse(ldtString);
                    boolean retour = datesConnexions.get(user) != null && Duration.between(datesConnexions.get(user), ldt).toDays() >= 1;
                    if(retour) retours.add(user);

                    datesConnexions.put(user, ldt);

                    csv.append(ldt.toString());
                    //user
                    csv.append(p.getLeft());
                    //csv.append(userToIndex.computeIfAbsent(p.getLeft(), z -> "" + i.getAndIncrement()));
                    //csv.append(anonymize ? (userToIndex.computeIfAbsent(p.getLeft(), z -> "" + i.getAndIncrement()) ) : p.getLeft());
                    if(dur > 10) nbUsersWithRealNavigation.add(p.getLeft());
                    ///user
                    csv.append(dur);
                    //nb details
                    csv.append(nbDetails(p.getRight()));

                    //nb url psup
                    csv.append(nbUrlPsup(p.getRight()));
                    //nb url onisep
                    csv.append(nbUrlOnisep(p.getRight()));
                    //nb clicks
                    csv.append(p.getRight().size());
                    //expe scientique
                    csv.append(gr == null ? "null group" : gr.getExpeENSGroupe());
                    //retour
                    csv.append(retour ? "retour" : "");
                    csv.newLine();
                }
            //}


        }

        fos.append("Lycéens " + allUSers.size()).append("\n");
        fos.append("Lycéens revenus " + retours.size()).append("\n");

        try(CsvTools csv = new CsvTools("lyceen" + filename.replace("json","csv"), ',')) {
            csv.appendHeaders(List.of(
                    "lycéens",
                    "nb favoris",
                    "nb favoris formations",
                    "nb favoris metiers",
                    "nb favoris formations suggestions"
            ));
            for (Map.Entry<String, User> entry : users.entrySet()) {
                String s = entry.getKey();
                User user = entry.getValue();
                if (allUSers.contains(s)) {
                    csv.append(s);
                    csv.append(user.pf().suggApproved().size());
                    csv.append(user.pf().suggApproved().stream().filter(s1 -> isFiliere(s1.fl())).count());
                    csv.append(user.pf().suggApproved().stream().filter(s1 -> !isFiliere(s1.fl())).count());
                    csv.append(user.pf().suggApproved().stream().filter(s1 -> isFiliere(s1.fl())
                            && suggestionsVues.get(s).contains(s1.fl())
                            ).count());
                    csv.newLine();
                }
            }
        }

        try(CsvTools csv = new CsvTools("lyceen_sugg_" + filename.replace("json","csv"), ',')) {
            csv.appendHeaders(List.of(
                    "lycéens",
                    "nb favoris",
                    "nb favoris formations",
                    "nb favoris metiers",
                    "nb favoris formations suggestions"
            ));
            for (Map.Entry<String, User> entry : users.entrySet()) {
                String s = entry.getKey();
                if (!allUSers.contains(s)) continue;
                User user = entry.getValue();
                csv.append(s);
                csv.append(user.pf().suggApproved().size());
                csv.append(user.pf().suggApproved().stream().filter(s1 -> isFiliere(s1.fl())).count());
                csv.append(user.pf().suggApproved().stream().filter(s1 -> !isFiliere(s1.fl())).count());
                csv.append(user.pf().suggApproved().stream().filter(s1 -> isFiliere(s1.fl())
                        && suggestionsVues.get(s).contains(s1.fl())
                ).count());
                csv.newLine();
            }
        }

        Map<String,Integer> statsLyceesNbUsers = new HashMap<>();
        Map<String,Integer> statsLyceesFavoris = new HashMap<>();

        fos.append(statsLyceesNbUsers.toString()).append("\n");
        fos.append(statsLyceesFavoris.toString()).append("\n");
        fos.append("Lycéens ayant navigué au moins 10 minutes : " + nbUsersWithRealNavigation.size()).append("\n");

    }


    private static List<List<String>> stratify(List<String> v) {
        List<LocalDateTime> l = v.stream().map(s -> LocalDateTime.parse(s.substring(0, s.indexOf('.')))).toList();
        List<List<String>> result = new ArrayList<>();
        List<String> current = new ArrayList<>();
        LocalDateTime last = null;
        int i = 0;
        for (LocalDateTime localDateTime : l) {
            boolean gap = last != null && Duration.between(last, localDateTime).toMinutes() > 15;
            if (gap) {
                result.add(current);
                current = new ArrayList<>();
            }
            current.add(v.get(i));
            last = localDateTime;
            i++;
        }
        result.add(current);
        return result;
    }

    private static boolean aVuUneSuggestionDeFiliere(List<String> right) {
        return right.stream().anyMatch(s ->
                s.contains("reloadSuggestions fl")
                        || s.contains("reloadSuggestions fr")
                        || s.contains("reloadSuggestions formations"));
    }

    private static long nbUrlPsup(List<String> right) {
        return right.stream().filter(s -> s.contains("openUrl") && s.contains("parcoursup")).count();
    }

    private static long nbDetails(List<String> right) {
        return right.stream().filter(s -> s.contains("doOpenDetailsModal")).count();
    }
    private static long nbUrlOnisep(List<String> right) {
        return right.stream().filter(s -> s.contains("openUrl") && ( s.contains("www.onisep.fr") || s.contains("avenirs"))).count();
    }

    private static Collection<String> getSuggestions(List<String> right) {
        List<String> result = new ArrayList<>();
        final String pattern = "front reloadSuggestions ";
        right.forEach(s -> {
            int i = s.indexOf(pattern);
            if (i >= 0) {
                String liste = s.substring(i + pattern.length());
                String[] items = liste.split(",");
                for (String item : items) {
                    result.add(item.trim());
                }
            }
        });
        return result;
    }


    private static long duration(List<String> right) {
        List<LocalDateTime> l = right.stream().map(s -> LocalDateTime.parse(s.substring(0, s.indexOf('.')))).toList();
        if(l.isEmpty()) return 0L;
        LocalDateTime start = l.get(0);
        LocalDateTime end = l.get(l.size() - 1);
        Duration duration = Duration.between(start, end);
        return duration.toMinutes();
    }

    private static Map<String, List<String>> reorderEventsByTimestamps(Map<String, List<ServerTrace>> map) {
        Map<String, List<String>> result = new HashMap<>();
        map.forEach((k, v) -> {
            List<String> l = v.stream().sorted(Comparator.comparing(ServerTrace::timestamp))
                .map(serverTrace -> serverTrace.timestamp() + " - " + serverTrace.event()).toList();
            result.put(k, l);
        });
        return result;
    }
}

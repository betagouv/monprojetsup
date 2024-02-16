package fr.gouv.monprojetsup.tools.perfs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.tools.Serialisation;
import fr.gouv.monprojetsup.tools.csv.CsvTools;
import fr.gouv.monprojetsup.web.db.model.Group;
import fr.gouv.monprojetsup.web.db.model.Groups;
import fr.gouv.monprojetsup.web.db.model.User;
import fr.gouv.monprojetsup.web.log.ServerTrace;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.ServerData.isFiliere;
import static java.lang.Math.abs;

public class AnalyzeUserBehaviour {

    private static final Logger LOGGER = Logger.getLogger(AnalyzeUserBehaviour.class.getName());
    public static void main(String[] args) throws IOException {

        String directory = "traces";

        /* load all users as a list of users from the file "usersExpeENS.json" */

        Groups groups = Serialisation.fromJsonFile("groups.json", Groups.class);
        groups.init();

        List<User> usersOfInterest
                = Serialisation.fromJsonFile(
                "usersExpeENS.json",
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


        analyze(groups.getGroups(), usersOfInterest, allTraces);
    }

    public static void analyze(
            List<Group> groups,
            List<User> users,
            List<ServerTrace> tracesList
    ) throws IOException {


        Map<String, ServerTrace> traces = tracesList.stream().collect(Collectors.toMap(
                ServerTrace::timestamp,
                st -> st
        ));

        if(traces.isEmpty()) throw new RuntimeException("No data to analyze");

        Set<String> loginsOfInterest =  users.stream().map(User::login).collect(Collectors.toSet());

        Set<Group> groupsOfInterest = groups.stream()
                .filter(g -> g.getMembers().stream().anyMatch(loginsOfInterest::contains))
                .collect(Collectors.toSet());


        Map<String, Group> usertoGroup = new HashMap<>();
        groupsOfInterest.forEach(group -> group.getMembers().stream()
                .filter(loginsOfInterest::contains)
                .forEach(login -> usertoGroup.put(login, group)));


        Map<String,Long> lycees = users.stream()
                .flatMap(u -> u.getLycees().stream())
                .collect(Collectors.groupingBy(u -> u, Collectors.counting()));


        Map<String,Long> groupes =
                usertoGroup.values().stream().collect(Collectors.groupingBy(Group::getName, Collectors.counting()));
        List<String> realGroups = groupes.entrySet().stream().filter(e -> e.getValue() >= 5).map(Map.Entry::getKey).toList();

        LOGGER.info("Périmètre des statistiques: lycéens du groupe test");
        LOGGER.info("Lycées: " + lycees.size());
        LOGGER.info("Classes/groupes: " + groupes.size());
        LOGGER.info("Lycéens: " + users.size());


        LOGGER.info("Lycées avec au moins 5 lycéens: " + lycees.entrySet().stream()
                .filter(e -> e.getValue() >= 5).count());
        LOGGER.info("Classes/groupes avec au moins 5 lycéens: " + realGroups.size());
        LOGGER.info("\t\t " + String.join("\n\t\t", realGroups));




        List<User> usersOfInterestWithFavori = new ArrayList<>(users);
        /* keeping only users having selected a formation favori */
        usersOfInterestWithFavori.removeIf(u -> u.pf().suggApproved().stream().noneMatch(s -> isFiliere(s.fl())));
        //print the number of users fof interest
        LOGGER.info("Lycéens avec au moins une formation en favori: " + usersOfInterestWithFavori.size());




        //computing the set of localdatetime, rounde dto hour, for which we observe at least one trace
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
        LOGGER.info("Traces d'évènement: " + traces.size());

        // Analyzing the traces
        //compute the first date where the occurecne of an event containing "openUrl" has been seen
        LocalDateTime firstOpenUrlTimestamp = traces.values().stream()
                .filter(st -> st.event() != null && st.event().contains("openUrl")  )
                .map(ServerTrace::timestamp)
                .map(LocalDateTime::parse)
                .min(LocalDateTime::compareTo)
                .orElse(null);
       // if(firstOpenUrlTimestamp == null) throw new RuntimeException("No occurence of an event containing openUrl");

        //LOGGER.info("First date where openurl was observed: " + firstOpenUrlTimestamp);

        //remove from traces all elements whose keys is strictly earlier to firstOpenUrlTimestamp
        traces.entrySet().removeIf(e -> LocalDateTime.parse(e.getKey()).isBefore(firstOpenUrlTimestamp));

        //list all users with an event of type UpdateProfileService and params containing a key of type suggestions
        ///with a "fl" field coantining "fl
        List<Pair<ServerTrace, Map>> updateProfileServiceWithSuggestions =  traces.values().stream().filter(st ->
                        st.event().contains("UpdateProfileService") || st.event().contains("UpdateProfileHandler")
                )
                .filter(st -> st.param() instanceof Map)
                .map(st -> Pair.of(st, (Map<String, Object>)st.param()))
                .map(p -> Pair.of(p.getLeft(),p.getRight().get("suggestions")))
                .filter(p -> p.getRight() instanceof List)
                .map(p -> Pair.of(p.getLeft(), (List)p.getRight()))
                .map(p  -> Pair.of(p.getLeft(), p.getRight().size() == 1 ? p.getRight().get(0) : null))
                .filter(p -> p.getRight() instanceof Map)
                .map(p -> Pair.of(p.getLeft(), (Map)p.getRight()))
                .toList();


        analyseFormationsSuggestions(traces, usertoGroup);

        Serialisation.toJsonFile("suggestionsObserved.json", updateProfileServiceWithSuggestions, true);

        List<ServerTrace> updateProfileServices = updateProfileServiceWithSuggestions.stream()
                .map(p -> Pair.of(p.getLeft(), (Map<String, Object>)p.getRight()))
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

        Set<String> candidatesWithFiliersSuggestions = filieresSuggestions.stream()
                .map(ServerTrace::origin)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        LOGGER.info("Lycéens ayant vu une ou plusieurs suggestions de filières: " + candidatesWithFiliersSuggestions.size());

        Map<String, List<String>> groupsWithFilieresSuggestions =
                candidatesWithFiliersSuggestions.stream()
                .map(id -> Pair.of(id, usertoGroup.get(id)))
                .filter(p -> p.getRight() != null)
                .collect(Collectors.groupingBy(p -> p.getRight().getId()))
                .entrySet().stream().collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().stream().map(Pair::getLeft).toList()
                        )
                )
                ;
        Serialisation.toJsonFile("groupsWithFilieresSuggestions.json", groupsWithFilieresSuggestions,true);

        /*reloadSuggestions fl*/

        Serialisation.toJsonFile("addSingleFiliereToFavorisByENSUserEvents.json", updateProfileServices, true);


        List<ServerTrace> openPsupUrl = traces.values().stream()
                .filter(st -> st.event().contains("openUrl") &&  st.event().contains("parcoursup"))
                .toList();
        List<ServerTrace> openOnisepUrl = traces.values().stream()
                .filter(st -> st.event().contains("openUrl") &&  st.event().contains("www.onisep.fr"))
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
        LOGGER.info("Lycéens ayant visité au moins une url parcoursup: " + usersHavingOpenedPsupUrl.size());
        LOGGER.info("Lycéens ayant visité au moins une url onisep: " + usersHavingOpenedOnisepUrl.size());
    }

    private static void analyseFormationsSuggestions(
            Map<String, ServerTrace> traces,
            Map<String, Group> users) throws IOException {

        Map<String,List<ServerTrace>> connectionsOfUserOfInterestsInTestsGroupAfterStratification = traces.values().stream()
                .filter(st -> st.origin() != null)
                .collect(Collectors.groupingBy(ServerTrace::origin));

        detailedNavigationAnalysis(
                "traces_utilisateurs.json",
                connectionsOfUserOfInterestsInTestsGroupAfterStratification,
                users
        );


    }

    public static boolean anonymize= true;

    private static void detailedNavigationAnalysis(
            String filename,
            Map<String, List<ServerTrace>> connectionsOfUserOfInterestsInTestsGroupAfterStratification,
            Map<String, Group> usertoGroup) throws IOException {
        Map<String,List<String>> tracesOfUsersHavingNotSeenSuggestions
                = f(connectionsOfUserOfInterestsInTestsGroupAfterStratification);

        Map<String, List<Pair<String,List<String>>>> details = new HashMap<>();
        tracesOfUsersHavingNotSeenSuggestions.forEach((k,v) -> {
            Group group = usertoGroup.get(k);
            if(group != null) {
                List<Pair<String,List<String>>> m = details.computeIfAbsent(group.getId(), s -> new ArrayList<>());
                if(!v.isEmpty()) {
                    List<List<String>> vs = stratify(v);
                    vs.forEach(l -> m.add(Pair.of(anonymize ? (""+abs(k.hashCode())) : k, l)));
                }
            } else {
                LOGGER.info("No group for user " + k);
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
        try(CsvTools csv = new CsvTools(filename.replace("json","csv"), ',')) {
            csv.append(List.of("Group", "Date et heure", "Utilisateur","Durée de navigation (min)", "exploration formations"));
            AtomicInteger i = new AtomicInteger(1);
            Map<String, String> userToIndex = new HashMap<>();
            for (Map.Entry<String, List<Pair<String, List<String>>>> entry : details.entrySet()) {
                String g = entry.getKey();
                List<Pair<String, List<String>>> v = entry.getValue();
                for (Pair<String, List<String>> p : v) {
                    csv.append(g);
                    csv.append(p.getRight().get(0).substring(0, p.getRight().get(0).indexOf('.')));
                    csv.append(anonymize ? (userToIndex.computeIfAbsent(p.getLeft(), z -> "" + i.getAndIncrement()) ) : p.getLeft());
                    long dur = duration(p.getRight());
                    if(dur > 10) nbUsersWithRealNavigation.add(p.getLeft());
                    csv.append(dur);
                    csv.append(aVisiteUneFormation(p.getRight()));
                    csv.newLine();
                }
            }


        }

        LOGGER.info("Lycéens ayant navigué au moins 10 minutes : " + nbUsersWithRealNavigation.size());

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

    private static String aVisiteUneFormation(List<String> right) {
        if(right.stream().anyMatch(s -> s.contains("reloadSuggestions fl") || s.contains("reloadSuggestions fr"))) {
            return "oui";
        }
        return "";
    }

    private static long duration(List<String> right) {
        List<LocalDateTime> l = right.stream().map(s -> LocalDateTime.parse(s.substring(0, s.indexOf('.')))).toList();
        if(l.isEmpty()) return 0L;
        LocalDateTime start = l.get(0);
        LocalDateTime end = l.get(l.size() - 1);
        Duration duration = Duration.between(start, end);
        return duration.toMinutes();
    }

    private static Map<String, List<String>> f(Map<String, List<ServerTrace>> map) {
        Map<String, List<String>> result = new HashMap<>();
        map.forEach((k, v) -> {
            List<String> l = v.stream().sorted(Comparator.comparing(ServerTrace::timestamp))
                .map(serverTrace -> serverTrace.timestamp() + " - " + serverTrace.event()).toList();
            result.put(k, l);
        });
        return result;
    }
}

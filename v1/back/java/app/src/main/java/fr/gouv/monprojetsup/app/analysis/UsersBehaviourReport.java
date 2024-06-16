package fr.gouv.monprojetsup.app.analysis;

import fr.gouv.monprojetsup.app.db.model.Group;
import fr.gouv.monprojetsup.app.db.model.User;
import fr.gouv.monprojetsup.app.log.ServerTrace;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class UserBehaviour {

    @Getter
    private final transient ServerTrace trace;
    public final String summary;
    public UserBehaviour(ServerTrace trace) {
        this.trace = trace;
        this.summary = trace.toSummary();
    }

    public boolean isTransition() {
        return trace.isTransitionOfPoc3();
    }
}

public record UsersBehaviourReport(
        String begin,
        String end,
        Map<String, GroupBehaviourReport> groupsBehaviours
) {

    private static final int MIN_NAV_TIME_MINUTES = 10;

    public static UsersBehaviourReport getReport(
            Map<String, Group> usersToGroups,
            List<ServerTrace> traces,
            boolean anonymize) {

        if(traces.isEmpty()) {
            return new UsersBehaviourReport(
                    LocalDateTime.now().toString(),
                    LocalDateTime.now().toString(),
                    new HashMap<>()
            );
        }

        //sort traces by timestamp
        traces = traces.stream()
                .map(trace -> Pair.of(LocalDateTime.parse(trace.timestamp()), trace))
                .sorted(Comparator.comparing(Pair::getLeft))
                .map(Pair::getRight)
                .toList();

        String begin = traces.get(0).timestamp();
        String end = traces.get(traces.size() - 1).timestamp();

        Map<Group, List<ServerTrace>> behaviours = traces.stream()
                .filter(trace -> usersToGroups.containsKey(trace.origin()))
                .collect(Collectors.groupingBy(t -> usersToGroups.get(t.origin())
                )
                );

        return new UsersBehaviourReport(
                begin,
                end,
                behaviours.entrySet().stream()
                        .map(entry -> new GroupBehaviourReport(
                                entry.getKey().getLycee(),
                                Group.lyceeToGroupId(entry.getKey().getLycee(), entry.getKey().getClasse()),
                                entry.getValue(),
                                anonymize

                        ))
                        .collect(Collectors.toMap(GroupBehaviourReport::groupe, gb -> gb))
        );
    }

    private static final AtomicInteger anonymizedCounter = new AtomicInteger(1);
    private static final ConcurrentHashMap<String, Integer> anonymized = new ConcurrentHashMap<>();
    private static String doAnonymize(boolean anonymize, String origin) {
        if(!anonymize) return origin;
        return anonymized.computeIfAbsent(
                origin,
                k -> anonymizedCounter.getAndIncrement()
        ).toString();
    }

    static UsersBehaviourReport analyze(
            List<Group> groups,
            List<User> users,
            List<ServerTrace> traces,
            boolean onlyToday,
            boolean anonymize
    ) {
        groups = new ArrayList<>(groups);
        users = new ArrayList<>(users);
        traces = new ArrayList<>(traces);

        traces.removeIf(trace -> trace.toSummary().contains("empty"));

        Map<String, Group> usertoGroup = new HashMap<>();
        groups.forEach(group -> group.getMembers()
                .forEach(login -> usertoGroup.put(login, group)));

        if(onlyToday) {
            LocalDateTime now = LocalDateTime.now();
            if(now.getHour() <= 6) {
                now = now.minusDays(1);
            }
            LocalDateTime today = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
            traces.removeIf(e -> LocalDateTime.parse(e.timestamp()).isBefore(today));
        }

        Set<String> scope = groups.stream().flatMap(g -> g.getMembers().stream()).collect(Collectors.toSet());
        scope.retainAll(traces.stream().map(ServerTrace::origin).collect(Collectors.toSet()));
        scope.retainAll(users.stream().map(User::getId).collect(Collectors.toSet()));

        users.removeIf(u -> !scope.contains(u.getId()));
        groups.removeIf(g -> g.getMembers().stream().noneMatch(scope::contains));
        traces.removeIf(t -> !scope.contains(t.origin()));
        usertoGroup.keySet().retainAll(scope);

        return  getReport(usertoGroup, traces, anonymize);

    }

    public String getMacrosStats() {
        StringBuilder sb = new StringBuilder();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy à HH:mm");

        sb.append("Statistiques sur la période ")
                .append(LocalDateTime.parse(begin).format(formatter))
                .append(" - ")
                .append(LocalDateTime.parse(end).format(formatter))
                .append("\n")
                .append("\n");

        sb
                .append("Lycées: ")
                .append(groupsBehaviours.values().stream().map(g -> g.lycee).distinct().count())
                .append("\n")
                .append("Classes/groupes: ").append(groupsBehaviours.values().stream().map(g -> g.groupe).distinct().count())
                .append("\n")
                .append("Lycéens: ")
                .append(groupsBehaviours
                        .values().stream()
                        .mapToLong(g -> (long) g.individualBehaviours.size())
                        .sum())
                .append("\n");

        List<GroupBehaviourReport> realGroups = groupsBehaviours.values().stream()
                .filter(g -> g.individualBehaviours.size() >= 5)
                .toList();

        sb
                .append("Lycées avec au moins 5 lycéens: ").append(realGroups.stream().map(g -> g.lycee).distinct().count())
                .append("\n")
                .append("Classes/groupes avec au moins 5 lycéens: ").append(realGroups.size())
                .append("\n");
        if(realGroups.size() < 10) {
            sb.append(realGroups.stream().map(g -> g.groupe)
                            .collect(
                                    Collectors.joining("\n\t\t", "\t\t", "\n")
                            )
                    )
                    .append("\n");
        }
        sb                .append("Traces d'évènements: " + groupsBehaviours.values().stream().mapToLong(
                        g -> g.nbTraces())
                        .sum()
                )
                .append("\n")
                .append("Lycéens ayant navigué au moins 10 minutes: "
                        + groupsBehaviours.values().stream().mapToLong(b -> b.nbUsersWithNavLongerThan(MIN_NAV_TIME_MINUTES)).sum()
                )
                /*
                .append("\n")
                .append("Lycéens ayant vu une ou plusieurs suggestions de formations: "
                        + groupsBehaviours.values().stream().mapToLong(GroupBehaviourReport::nbUsersSeeingFiliereReco).sum()
                )
                .append("\n")
                .append("Lycéens ayant ouvert au moins une fiche: "
                        + groupsBehaviours.values().stream().mapToLong(GroupBehaviourReport::nbUsersSeeingDetails).sum()
                )*/
                .append("\n")
                .append("Lycéens ayant visité au moins une url parcoursup: "
                        + groupsBehaviours.values().stream().mapToLong(GroupBehaviourReport::nbUsersSeeingPsupUrl).sum()
                )
                .append("\n")
                .append("Lycéens ayant visité au moins une url onisep: "
                        + groupsBehaviours.values().stream().mapToLong(GroupBehaviourReport::nbUsersSeeingOnisepUrl).sum()
                )
                .append("\n")
        ;
        List<UserSession> reports = groupsBehaviours.values().stream()
                .flatMap(g -> g.individualBehaviours.values().stream())
                .toList();


        sb.append("durée médiane de complétion du profil: ")
                .append(medianProfileCompletionTime(reports))
                .append("\n");

        sb.append("durée médiane sur chaque écran:\n");
        medianScreensDurations(reports).forEach(
                p -> sb.append("\t").append(p.getLeft()).append(": ").append(p.getRight()).append("\n\n")
        );

        sb.append("pourcentage des affichage favoris parmi ceux qui ont mis au moins un favori: ");
        sb.append(pctShowFavorisKnowingOneFavori(reports) + "%").append("\n");

        sb.append("\n")
                .append("\n")
                .append("\n");

        return sb.toString();

    }

    private int pctShowFavorisKnowingOneFavori(List<UserSession> reports) {
        int nbUsersWithFavori = nbUsersWithFavoris(reports);
        if(nbUsersWithFavori == 0) return -1;
        int nbVisitingSelectionScreen = nbVisitingSelectionScreen(reports);
        return 100 * nbVisitingSelectionScreen / nbUsersWithFavori;
    }

    private int nbVisitingSelectionScreen(List<UserSession> reports) {
        return (int) reports.stream()
                .filter(r -> r.behaviour.stream().anyMatch(b -> Objects.equals(b.getTrace().toScreen(),"selection")))
                .count();
    }

    private int nbUsersWithFavoris(List<UserSession> reports) {
        return (int) reports.stream().filter(r -> r.behaviour.stream().anyMatch(b -> b.getTrace().isSuggestionsUpdate())).count();
    }

    record DurationStats(
            int pctLessThan5sec,
            int pctLessThan10sec,
            int pctLessThan30sec,
            int pctLessThan2min,
            int pctLessThan5min,
            int moreThan5min,
            int medianSeconds
    ) {
        public static DurationStats of(List<Duration> list) {
            if(list.isEmpty()) return new DurationStats(0,0,0,0,0,0, 0);
            int lessThan5sec = 0;
            int lessThan10sec = 0;
            int lessThan30sec = 0;
            int lessThan2min = 0;
            int lessThan5min = 0;
            int medianSeconds = 0;
            int moreThan5min = 0;
            for (Duration duration : list) {
                if(duration.getSeconds() < 5) lessThan5sec++;
                else if(duration.getSeconds() < 10) lessThan10sec++;
                else if(duration.getSeconds() < 30) lessThan30sec++;
                else if(duration.getSeconds() < 120) lessThan2min++;
                else if(duration.getSeconds() < 300) lessThan5min++;
                else moreThan5min++;
            }
            medianSeconds = (int) medianDuration(list).getSeconds();
            return new DurationStats(
                    lessThan5sec * 100 / list.size(),
                    lessThan10sec * 100 / list.size(),
                    lessThan30sec * 100 / list.size(),
                    lessThan2min * 100 / list.size(),
                    lessThan5min * 100 / list.size(),
                    moreThan5min * 100 / list.size(),
                    medianSeconds
            );
        }

        @Override
        public String toString() {
            return "Durée médiane: " + medianSeconds + " secondes\n" +
                    ((pctLessThan5sec > 0) ? ("Durée inférieure à 5 secondes: " + pctLessThan5sec + "%\n") : "") +
                    ((pctLessThan10sec > 0) ? ("Durée inférieure à 10 secondes: " + pctLessThan10sec + "%\n") : "") +
                    ((pctLessThan30sec > 0) ? ("Durée inférieure à 30 secondes: " + pctLessThan30sec + "%\n") : "") +
                    ((pctLessThan2min > 0) ? ("Durée inférieure à 2 minutes: " + pctLessThan2min + "%\n") : "") +
                    ((pctLessThan5min > 0) ? ("Durée inférieure à 5 minutes: " + pctLessThan5min + "%\n") : "") +
                    ((moreThan5min > 0) ? ("Durée supérieure à 5 minutes: " + moreThan5min + "%\n") : "") ;
        }

    }

    private List<Pair<String,DurationStats>> medianScreensDurations(List<UserSession> reports) {
        Map<String,List<Pair<String,Duration>>> durations = reports.stream()
                .flatMap(  t -> t.screensDurations().stream())
                .filter(p -> ! p.getLeft().contains("null") && ! p.getLeft().contains("undefined"))
                .collect(Collectors.groupingBy(Pair::getLeft));

        return durations.entrySet().stream().map(
                e -> Pair.of(
                            e.getKey(),
                            DurationStats.of(
                                e.getValue().stream().map(Pair::getRight).toList()
                            )
                        )
        ).toList();
    }


    private DurationStats medianProfileCompletionTime(List<UserSession> reports) {
        List<Duration> durations = reports.stream()
                .map(UserSession::profileCompletionTime)
                .filter(Objects::nonNull)
                .toList();
        return DurationStats.of(durations);
    }

    private static Duration medianDuration(List<Duration> durations) {
        if(durations.isEmpty()) return Duration.ZERO;
        durations = durations.stream().sorted().toList();
        if(durations.size() % 2 == 0) {
            return Duration.between(
                    LocalDateTime.MIN,
                    LocalDateTime.MIN.plusSeconds(
                            durations.get(durations.size() / 2).getSeconds() +
                                    durations.get(durations.size() / 2 - 1).getSeconds()
                    )
            );
        } else {
            return durations.get(durations.size() / 2);
        }
    }


    public record GroupBehaviourReport(
            String lycee,
            String groupe,
            Map<String, UserSession> individualBehaviours
    ) {
        public GroupBehaviourReport(String lycee, String groupe, List<ServerTrace> value, boolean anonymize) {
            this(lycee,groupe, new HashMap<>());
            List<UserBehaviour> behaviours = new ArrayList<>();
            LocalDateTime last = null;
            Map<String,Integer> sessionIds = new HashMap<>();
            Map<String,LocalDateTime> lastTime = new HashMap<>();
            for (ServerTrace serverTrace : value) {
                LocalDateTime now = LocalDateTime.parse(serverTrace.timestamp());
                String origin = doAnonymize(anonymize,serverTrace.origin()) + " - " + sessionIds.computeIfAbsent(serverTrace.origin(), k -> 1);
                if (lastTime.containsKey(origin)
                        && now.isAfter(lastTime.get(origin).plusMinutes(20))) {
                    int newSessionId = sessionIds.get(serverTrace.origin()) + 1;
                    sessionIds.put(serverTrace.origin(), newSessionId);
                    lastTime.put(serverTrace.origin(), now);
                    origin = doAnonymize(anonymize, serverTrace.origin()) + " - " + newSessionId;
                }
                val behav = individualBehaviours
                        .computeIfAbsent(origin, k -> new UserSession());
                behav.behaviour.add(new UserBehaviour(serverTrace));
            }
        }

        public long nbTraces() {
            return individualBehaviours.values().stream().mapToLong(ub -> ub.behaviour.size()).sum();
        }
        public long nbUsersWithNavLongerThan(int nbMinutes) {
            return individualBehaviours.values().stream().filter(ub -> ub.navigationTimeInMinutes() >= nbMinutes).count();
        }

        public long nbUsersSeeingFiliereReco() {
            return individualBehaviours.values().stream().filter(UserSession::hasSeenFiliereReco).count();
        }

        public long nbUsersSeeingPsupUrl() {
            return individualBehaviours.values().stream().filter(UserSession::hasSeenPsupUrl).count();
        }

        public long nbUsersSeeingOnisepUrl() {
            return individualBehaviours.values().stream().filter(UserSession::hasSeenOnisepUrl).count();
        }

        public long nbUsersSeeingDetails() {
            return individualBehaviours.values().stream().filter(UserSession::hasSeenDetails).count();
        }
    }


    public record UserSession(
            List<UserBehaviour> behaviour
    ) {
        public UserSession() {
            this(new ArrayList<>());
        }

        long navigationTimeInMinutes() {
            List<LocalDateTime> dates = behaviour.stream()
                    .map(st -> LocalDateTime.parse(st.getTrace().timestamp()))
                    .sorted()
                    .toList();
            if(dates.size() <= 1) return 0;
            LocalDateTime begin = dates.get(0);
            LocalDateTime end = dates.get(dates.size() - 1);
            Duration duration = Duration.between(begin, end);
            return duration.toMinutes();
        }

        public boolean hasSeenFiliereReco() {
            return behaviour.stream().anyMatch(b -> b.getTrace().isFiliereRecoofPoc2());
        }

        public boolean hasSeenPsupUrl() {
            return behaviour.stream().anyMatch(b -> b.getTrace().isPsupUrl());
        }

        public boolean hasSeenOnisepUrl() {
            return behaviour.stream().anyMatch(b -> b.getTrace().isOnisepUrl());
        }

        public static UserSession from(List<ServerTrace> traces) {
            return new UserSession(traces.stream().map(UserBehaviour::new).toList());
        }

        public boolean hasSeenDetails() {
            return behaviour.stream().anyMatch(b -> b.getTrace().isSeeingDetailsOfPoc2());
        }

        public @Nullable Duration profileCompletionTime() {
            //if we find the right events, we compute
            val transitions = behaviour.stream().filter(UserBehaviour::isTransition).map(UserBehaviour::getTrace).toList();
            if(transitions.isEmpty()) return null;
            val debut = transitions.stream().filter(ServerTrace::isDebutInscriptionPoc3).findFirst().orElse(null);
            val fin = transitions.stream().filter(ServerTrace::isFinInscriptionPoc3).findFirst().orElse(null);
            if(debut == null || fin == null) return null;
            return Duration.between(
                    LocalDateTime.parse(debut.timestamp()),
                    LocalDateTime.parse(fin.timestamp())
            );
        }

        public List<Pair<String,Duration>> screensDurations() {
            List<Pair<String,Duration>> result = new ArrayList<>();
            val transitions =
                    behaviour.stream()
                            .filter(UserBehaviour::isTransition)
                            .map(UserBehaviour::getTrace)
                            .toList();
            LocalDateTime last = null;
            for(val transition: transitions) {
                String fromScreen = transition.fromScreen();
                val now = LocalDateTime.parse(transition.timestamp());
                if(last != null) {
                    result.add(Pair.of(fromScreen, Duration.between(last, now)));
                }
                last = now;
            }
            return result.stream().collect(Collectors.groupingBy(Pair::getLeft))
                    .entrySet().stream()
                    .map(e -> Pair.of(e.getKey(), e.getValue().stream().map(Pair::getRight).reduce(Duration.ZERO, Duration::plus)))
                    .toList();
        }
    }



}



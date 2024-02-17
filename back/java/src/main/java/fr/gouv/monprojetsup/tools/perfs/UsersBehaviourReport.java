package fr.gouv.monprojetsup.tools.perfs;

import fr.gouv.monprojetsup.web.db.model.Group;
import fr.gouv.monprojetsup.web.log.ServerTrace;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

}

public record UsersBehaviourReport(
        String begin,
        String end,
        Map<String, GroupBehaviourReport> groupsBehaviours
) {

    private static final int MIN_NAV_TIME_MINUTES = 10;

    public static UsersBehaviourReport get(
            Map<String, Group> usersToGroups,
            List<ServerTrace> traces,
            boolean anonymize) {

        if(traces.isEmpty()) throw new RuntimeException("No traces to analyze");

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
                                entry.getKey().getName(),
                                entry.getValue().stream()
                                        .collect(Collectors.groupingBy(
                                                ServerTrace::origin,
                                                Collectors.mapping(trace -> trace, Collectors.toList())
                                        ))
                                        .values().stream()
                                        .collect(Collectors.toMap(
                                                tracesForUser -> doAnonymize(anonymize,tracesForUser.get(0).origin()),
                                                tracesForUser -> UserBehaviourReport.from(tracesForUser)
                                        ))
                        ))
                        .collect(Collectors.toMap(GroupBehaviourReport::groupe, gb -> gb))
        );
    }

    private static AtomicInteger anonymizedCounter = new AtomicInteger(1);
    private static ConcurrentHashMap<String, Integer> anonymized = new ConcurrentHashMap<>();
    private static String doAnonymize(boolean anonymize, String origin) {
        if(!anonymize) return origin;
        return anonymized.computeIfAbsent(
                origin,
                k -> anonymizedCounter.getAndIncrement()
        ).toString();
    }

    public String getMacrosStats() {
        StringBuilder sb = new StringBuilder();

        sb.append("Statistiques sur la période ")
                .append(begin)
                .append(" - ")
                .append(end)
                .append("\n")
                .append("\n");

        sb
                .append("Lycées: ")
                .append(groupsBehaviours.values().stream().map(g -> g.lycee).count())
                .append("\n")
                .append("Classes/groupes: ").append(groupsBehaviours.values().stream().map(g -> g.groupe).count())
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
                .append("\n")
                .append(realGroups.stream().map(g -> g.groupe)
                        .collect(
                                Collectors.joining("\n\t\t", "\t\t", "\n")
                        )
                )
                .append("\n")
                .append("Traces d'évènements: " + groupsBehaviours.values().stream().mapToLong(
                        g -> g.nbTraces())
                        .sum()
                )
                .append("\n")
                .append("Lycéens ayant navigué au moins 10 minutes: "
                        + groupsBehaviours.values().stream().mapToLong(b -> b.nbUsersWithNavLongerThan(MIN_NAV_TIME_MINUTES)).sum()
                )
                .append("\n")
                .append("Lycéens ayant vu une ou plusieurs suggestions de filières: "
                        + groupsBehaviours.values().stream().mapToLong(GroupBehaviourReport::nbUsersSeeingFiliereReco).sum()
                )
                .append("\n")
                .append("Lycéens ayant visité au moins une url parcoursup: "
                        + groupsBehaviours.values().stream().mapToLong(GroupBehaviourReport::nbUsersSeeingPsupUrl).sum()
                )
                .append("\n")
                .append("Lycéens ayant visité au moins une url onisep: "
                        + groupsBehaviours.values().stream().mapToLong(GroupBehaviourReport::nbUsersSeeingOnisepUrl).sum()
                )
                .append("\n")
                .append("\n")
                .append("\n")
                .append("\n")
        ;

        return sb.toString();

    }

    public record GroupBehaviourReport(
            String lycee,
            String groupe,
            Map<String, UserBehaviourReport> individualBehaviours
    ) {
        public long nbTraces() {
            return individualBehaviours.values().stream().mapToLong(ub -> ub.behaviour.size()).sum();
        }
        public long nbUsersWithNavLongerThan(int nbMinutes) {
            return individualBehaviours.values().stream().filter(ub -> ub.navigationTimeInMinutes() >= nbMinutes).count();
        }

        public long nbUsersSeeingFiliereReco() {
            return individualBehaviours.values().stream().filter(UserBehaviourReport::hasSeenFiliereReco).count();
        }

        public long nbUsersSeeingPsupUrl() {
            return individualBehaviours.values().stream().filter(UserBehaviourReport::hasSeenPsupUrl).count();
        }

        public long nbUsersSeeingOnisepUrl() {
            return individualBehaviours.values().stream().filter(UserBehaviourReport::hasSeenOnisepUrl).count();
        }
    }


    public record UserBehaviourReport(
            List<UserBehaviour> behaviour
    ) {
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
            return behaviour.stream().anyMatch(b -> b.getTrace().isFiliereReco());
        }

        public boolean hasSeenPsupUrl() {
            return behaviour.stream().anyMatch(b -> b.getTrace().isPsupUrl());
        }

        public boolean hasSeenOnisepUrl() {
            return behaviour.stream().anyMatch(b -> b.getTrace().isOnisepUrl());
        }

        public static UserBehaviourReport from(List<ServerTrace> traces) {
            return new UserBehaviourReport(traces.stream().map(UserBehaviour::new).toList());
        }
    }



}



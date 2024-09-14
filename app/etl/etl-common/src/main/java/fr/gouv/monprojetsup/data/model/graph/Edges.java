package fr.gouv.monprojetsup.data.model.graph;

import fr.gouv.monprojetsup.data.Constants;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class Edges {
    @Getter
    private final @NotNull Map<@NotNull String, @NotNull Map<@NotNull String, @NotNull Double>> edges = new HashMap<>();
    private final @NotNull Map<@NotNull String, @NotNull Map<@NotNull String, @NotNull Double>> backEdges = new HashMap<>();

    private boolean cleanupKeys = true;//defaults to true for not creating problems in front

    public Edges() {
    }

    public Edges(Edges edges) {
        edges.edges.forEach((k, m) -> this.edges.put(k, new HashMap<>(m)));
        edges.backEdges.forEach((k, m) -> this.backEdges.put(k, new HashMap<>(m)));
        this.cleanupKeys = edges.cleanupKeys;
    }

    public Edges(Map<String, Collection<String>> medges, double weight, boolean reverse) {
        medges.forEach((s, strings) -> {
            strings.forEach(s1 -> {
                this.put(s, s1, reverse, weight);
            });
        });
    }

    public void put(String a, Collection<String> bs) {
        bs.forEach(b -> this.put(a, b, true, 1.0));
    }

    public boolean hasEdge(String a, String b) {
        String ca = cleanupKeys ? Constants.cleanup(a) : a;
        String cb = cleanupKeys ? Constants.cleanup(b) : b;
        if(ca.equals(cb)) return true;
        if(!edges.containsKey(ca)) return false;
        return edges.get(ca).containsKey(cb);
    }

    public void put(String a, String b) {
        this.put(a, b, true, 1.0);
    }

    public void put(String a, String b, boolean reverse, double weight) {
        String ca = cleanupKeys ? Constants.cleanup(a) : a;
        String cb = cleanupKeys ? Constants.cleanup(b) : b;
        if(!ca.equals(cb)) {//no self loops
            edges.computeIfAbsent(ca, z -> new HashMap<>()).put(cb, weight);
            backEdges.computeIfAbsent(cb, z -> new HashMap<>()).put(ca, weight);
            if (reverse) {
                edges.computeIfAbsent(cb, z -> new HashMap<>()).put(ca, weight);
                backEdges.computeIfAbsent(ca, z -> new HashMap<>()).put(cb, weight);
            }
        }
    }


    public Map<String, Double> getSuccessors(String a) {
        return new HashMap<>(edges.getOrDefault(a, Collections.emptyMap()));
    }

    public Map<String, Double> getPredecessors(String a) {
        return new HashMap<>(backEdges.getOrDefault(a, Collections.emptyMap()));
    }

    public int size() {
        return edges.values().stream().mapToInt(Map::size).sum();
    }


    public Set<Path> computePathesFrom(String node, int maxDistance) {
        return computePathesFromInternal(node, maxDistance);
    }


    private Set<Path> computePathesFromInternal(String node, int maxDistance) {
        Set<Path> resultat = new HashSet<>();
        Path pi = new Path(node, 1.0);
        resultat.add(pi);

        Map<String, List<Path>> frontLine = new HashMap<>();
        frontLine.computeIfAbsent(node, z -> new ArrayList<>()).add(pi);

        Set<String> visited = new HashSet<>();
        visited.add(node);

        for (int distance = 1; distance <= maxDistance; distance++) {
            Map<String, List<Path>> newFrontLine = new HashMap<>();
            //important, we work on a copy of visited
            Set<String> newVisited = new HashSet<>(visited);
            for (Map.Entry<String, List<Path>> entry : frontLine.entrySet()) {
                String n = entry.getKey();
                List<Path> paths = entry.getValue();
                double inDegreeScoreAppliedWhenNIsAnIntermediaryNode = 1.0 / Math.max(1, Math.sqrt(inDegree(n)));
                Map<String, Double> newNodes = getSuccessors(n);
                newNodes.keySet().removeAll(visited);
                newVisited.addAll(newNodes.keySet());
                newNodes.forEach((nn, weight) ->
                        paths.forEach(path -> {
                            double inDegreeMultiplier = path.size() <= 1 ? 1.0 : inDegreeScoreAppliedWhenNIsAnIntermediaryNode;
                            Path newPath = new Path(path, nn, weight * inDegreeMultiplier);
                            resultat.add(newPath);
                            newFrontLine.computeIfAbsent(nn, z -> new ArrayList<>()).add(newPath);
                        })
                );
            }
            visited = newVisited;
            frontLine = newFrontLine;
        }
        return resultat;
    }

    /**
     * @param node
     * @return the number of edges going to node
     */
    private int inDegree(String node) {
        Map<String, Double> backs = backEdges.get(node);
        return backs == null ? 0 : backs.size();
    }

    /**
     * add some edges with initial weight
     *
     * @param o
     * @param reverse
     * @param weight
     */
    public void putAll(@Nullable Edges o, boolean reverse, double weight) {
        if (o != null) putAll(o.edges(), reverse, weight);
    }

    public void putAll(@Nullable Edges o) {
        this.putAll(o, true);
    }

    public void putAll(@Nullable Edges o, boolean reverse) {
        if (o != null) putAll(o.edges(), reverse, 1.0);
    }

    public void putAll(Map<String, Set<String>> edges) {
        this.putAll(edges, true, 1.0);
    }


    public void putAll(Map<String, Set<String>> o, boolean reverse, double weight) {
        o.forEach((s1, strings) ->
                strings.forEach(s2 -> this.put(s1, s2, reverse, weight)));
    }

    public void clear() {
        edges.clear();
    }

    public @NotNull Map<String, Set<String>> edges() {
        return edges.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> Collections.unmodifiableSet(e.getValue().keySet())
        ));
    }
    public @NotNull Set<String> keys() {
        //Returns an unmodifiable view of the keyset of edges
        return Collections.unmodifiableSet(edges.keySet());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Edges) obj;
        return Objects.equals(this.edges, that.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edges);
    }

    @Override
    public String toString() {
        return "Edges[" +
                "edges=" + edges + ']';
    }


    public void setCleanupKeys(boolean cleanupKeys) {
        this.cleanupKeys = cleanupKeys;
    }


    public Set<String> preStar(Set<String> recoNodes) {
        Set<String> result = new HashSet<>(recoNodes);
        Set<String> frontier = new HashSet<>(recoNodes);
        while (true) {
            frontier =
                    frontier.stream()
                            .map(backEdges::get)
                            .filter(Objects::nonNull)
                            .flatMap(strings -> strings.keySet().stream())
                            .collect(Collectors.toSet());
            frontier.removeAll(result);
            if (frontier.isEmpty()) {
                break;
            }
            result.addAll(frontier);
        }
        return result;
    }


    public Set<String> nodes() {
        return Collections.unmodifiableSet(edges.keySet());
    }

    public void retainAll(Set<String> useful) {
        edges.keySet().retainAll(useful);
        backEdges.keySet().retainAll(useful);
        edges.values().forEach(m -> m.keySet().retainAll(useful));
        backEdges.values().forEach(m -> m.keySet().retainAll(useful));
    }

    public void createLabelledGraphFrom(Edges edgesKeys, Map<String, String> globalDict) {
        clear();
        edgesKeys.edges.forEach((k, m) -> {
            String label = globalDict.get(k);
            if (label == null) label = k;
            String finalKey = label;
            m.entrySet().forEach(e -> {
                String s = e.getKey();
                String val = globalDict.get(s);
                if (val == null) val = s;
                put(finalKey, val, false, e.getValue());
            });
        });

    }

    /**
     * the specifics inherit from the generics
     *
     * @param specificToGeneric
     * @param coef
     */
    public void addEdgesFromMoreGenericItem(Map<String, String> specificToGeneric, double coef) {

        specificToGeneric.forEach((specific, generic) -> {
            String genericc = cleanupKeys ? Constants.cleanup(generic) : generic;
            Map<String, Double> edges = this.edges.get(genericc);
            if (edges != null) {
                edges.forEach((target, weight) -> put(specific, target, false, weight* coef));
            }
            Map<String, Double> backEdges = this.backEdges.get(generic);
            if (backEdges != null) {
                backEdges.forEach((origin, weight) -> put(origin, specific, false, weight * coef));
            }
        });
        /*
        Set<String> toErase = specificToGeneric.entrySet().stream().filter(e -> !e.getValue().equals(e.getKey()))
                .map(e -> e.getKey())
                .collect(Collectors.toSet());
                */
    }

    public void addTransitiveClosure(String theme, Set<String> transitiveClosureOfTheme) {
        List<ImmutableTriple<String,String ,Double>> triplets = new ArrayList<>();
        edges.forEach(
                (s, stringDoubleMap)
                        -> stringDoubleMap.forEach(
                        (s1, weight) -> {
                            if (s1.equals(theme)) {
                                transitiveClosureOfTheme.forEach(
                                        s2 -> triplets.add(ImmutableTriple.of(s,s2,weight)) );
                            }
                        }
                )
        );
        for (ImmutableTriple<String, String, Double> t : triplets) {
            put(t.left, t.middle, false, t.right);
        }
    }

    public void eraseNodes(Set<String> toErase) {
        edges.keySet().removeAll(toErase);
        edges.values().forEach(stringDoubleMap -> stringDoubleMap.keySet().removeAll(toErase));
        backEdges.keySet().removeAll(toErase);
        backEdges.values().forEach(stringDoubleMap -> stringDoubleMap.keySet().removeAll(toErase));
    }

    public void addNode(String gFlCodToFrontId) {
        edges.put(gFlCodToFrontId, new HashMap<>());
        backEdges.put(gFlCodToFrontId, new HashMap<>());
    }

    public boolean hasNode(String a) {
        String ca = cleanupKeys ? Constants.cleanup(a) : a;
        return  edges.containsKey(ca);
    }

    public Set<String> targets() {
        return backEdges.keySet();
    }

    public void remove(Set<String> toRemove) {
        edges.keySet().removeAll(toRemove);
        backEdges.keySet().removeAll(toRemove);
        edges.values().forEach(m -> m.keySet().removeAll(toRemove));
        backEdges.values().forEach(m -> m.keySet().removeAll(toRemove));

    }

    public Map<String, Set<String>> minus(Edges edgesMetiersFilieres) {
        val toRemove = edgesMetiersFilieres.edges();
        Map<String, Set<String>> result = new TreeMap<>();
        edges.forEach((s, stringDoubleMap) -> {
            Set<String> toRemove2 = toRemove.getOrDefault(s, Set.of());
            Set<String> newSet = new TreeSet<>(stringDoubleMap.keySet());
            newSet.removeAll(toRemove2);
            result.put(s, newSet);
        });
        return result;
    }
}

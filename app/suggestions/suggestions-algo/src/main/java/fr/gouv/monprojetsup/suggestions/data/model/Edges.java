package fr.gouv.monprojetsup.suggestions.data.model;

import fr.gouv.monprojetsup.data.model.Edge;
import fr.gouv.monprojetsup.suggestions.Constants;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class Edges {
    @Getter
    private final @NotNull Map<String, Map<String, Double>> edges = new HashMap<>();
    private final @NotNull Map<String, Map<String, Double>> backEdges = new HashMap<>();


    public void put(String a, String b, boolean reverse, double weight) {
        //defaults to true for not creating problems in front
        String ca = Constants.cleanup(a);
        String cb = Constants.cleanup(b);
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
     * @param node the node
     * @return the number of edges going to node
     */
    private int inDegree(String node) {
        Map<String, Double> backs = backEdges.get(node);
        return backs == null ? 0 : backs.size();
    }



    public void putAll(List<Edge> edges, boolean reverse, double weight) {
        edges.forEach(edge ->
                this.put(edge.src(), edge.dst(), reverse, weight));
    }

    public void putAll(List<Edge> edges) {

        putAll(edges, true, 1.0);

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
            m.forEach((s, value) -> {
                String val = globalDict.get(s);
                if (val == null) val = s;
                put(finalKey, val, false, value);
            });
        });

    }

    /**
     * the specifics inherit from the generics
     *
     * @param poorToRich the edges from the specifics to the generics
     * @param coef             the coefficient to apply to the weights
     */
    public void inheritEdgesFromRicherItem(List<Edge> poorToRich, double coef) {

        poorToRich.forEach(e -> {
            val poor = e.src();//e.g. las
            val rich = e.dst();//e.g. not las
            Map<String, Double> edgesFromRich = this.edges.get(Constants.cleanup(rich));
            if (edgesFromRich != null) {
                edgesFromRich.forEach((target, weight) -> put(poor, target, false, weight* coef));
            }
            Map<String, Double> edgesToRich = this.backEdges.get(Constants.cleanup(rich));
            if (edgesToRich != null) {
                edgesToRich.forEach((origin, weight) -> put(origin, poor, false, weight * coef));
            }
        });
    }

    public void replaceSpecificByGeneric(List<Edge> specificToGeneric, double coef) {

        specificToGeneric.forEach(e -> {
            val specific = e.src();//e.g. fl4041 = CMI meca
            val generic = e.dst();//e.g. fr22 = CMI en général
            Map<String, Double> edgesFromSpecific = this.edges.get(Constants.cleanup(specific));
            if (edgesFromSpecific != null) {
                edgesFromSpecific.forEach((target, weight) -> put(generic, target, false, weight* coef));
            }
            Map<String, Double> edgesToSpecific = this.backEdges.get(Constants.cleanup(specific));
            if (edgesToSpecific != null) {
                edgesToSpecific.forEach((origin, weight) -> put(origin, generic, false, weight * coef));
            }
        });

        /* now we remove the specifics and obtain a smaller graph */
        val allSpecifics = specificToGeneric.stream().map(Edge::src).collect(Collectors.toSet());
        val allGenerics = specificToGeneric.stream().map(Edge::dst).collect(Collectors.toSet());
        val allSpecificsWhichAreNotGeneric = allSpecifics.stream().filter(s -> !allGenerics.contains(s)).collect(Collectors.toSet());
        eraseNodes(allSpecificsWhichAreNotGeneric);

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

}

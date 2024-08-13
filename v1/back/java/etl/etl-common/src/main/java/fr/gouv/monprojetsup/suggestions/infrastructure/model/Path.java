package fr.gouv.monprojetsup.suggestions.infrastructure.model;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record Path(
        List<String> nodes,

        Double weight
) {
    public Path(String node, double weight) {
        this(new ArrayList<>(), weight);
        nodes.add(node);
    }

    public Path(Path path, String nn, double score) {
        //decreasing with respect to degree because we should give less weight to less specific stuff
        //but not as fast as x --> 1/x because 3 links of indegree 3 are better than one line of indegree 1
        this(new ArrayList<>(path.nodes), path.weight * score);
        if(path.size() > 0 && path.nodes.get(0).equals("T_ITM_1112") && nn.equals("fl13")) {
            int i = 0;
        }
        if(path.size() > 0 && path.nodes.get(0).equals("T_ITM_1044") && nn.equals("fl13")   ) {
            int i = 0;
        }

        nodes.add(nn);
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public int size() {
        return nodes.size();
    }

    @Override
    public String toString() {
        return nodes.stream().collect(Collectors.joining(" -- ")) + " [sc" + (int) (10000 * weight) + "]";
    }

    public String toString(Map<String, String> keyToLabel) {
        return nodes.stream().map(key -> keyToLabel.getOrDefault(key, "") + " " + key).collect(Collectors.joining(" -- "));
    }

    public double score() {
        return weight;
        //return 1.0 / Math.pow(nodes.size(),4);//we want the score to decrease quickly with the distance
    }

    public boolean endsWith(String node) {
        return !nodes.isEmpty() && nodes.get(nodes.size() - 1).equals(node);
    }

    public @Nullable String last() {
        return nodes.isEmpty() ? null : nodes.get(nodes.size() - 1);
    }

    @Nullable
    public String first() {
        return nodes.isEmpty() ? null : nodes.get(0);
    }

}

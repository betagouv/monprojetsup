package fr.gouv.monprojetsup.data.tools.nlp;

import fr.gouv.monprojetsup.data.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.ServerData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
* Class used to load words database and semantic relations between them,
* from https://github.com/ContentSide/French_Knowledge_Graph/tree/master/resources/datasets/Filtered_Nodes_Edges

* n1 and n2 are the IDs of the words.
*  TODO: use logger.
 */

record Relation(Integer n1, Integer n2, Integer weight){}

record Match(String word, Integer weight){}

public class Correspondances {
    public final Map<String, List<Match>> links;
    //Rather a map from string (user input) to list of strings + scores (correspondances)
    public Correspondances() throws IOException {
        this.links = getLinks(
                getNonFilteredWords(),
                getRelations(),
                getKeywords()
        );
    }

    private static final Logger logger = Logger.getLogger(Correspondances.class.getName());

    private List<List<String>> readCSV(String path) {
        List<List<String>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                data.add(Arrays.asList(values));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    /*
    * Load the words database.
    * // TODO: use parameter path.
    * Présence de mots vulgaires dans la base de données, d'où la nécessité de la fonction FilterWordsList().
    */
    public Map<Integer, String> getNonFilteredWords() {
        String path = "../data/words.csv";
        HashMap<Integer, String> words = new HashMap<>();
        List<List<String>> rawWords = readCSV(path);
        for (List<String> rawWord : rawWords) {
            try {
                words.put(Integer.parseInt(rawWord.get(0)), rawWord.get(1));
            } catch (NumberFormatException ignored) {}
        }
        if (!words.isEmpty()) {
            logger.info(words.size() + " words loaded.");
        }
        return words;
    }

    public Set<Relation> getRelations() {
        // TODO: use parameter path.
        String path = "../data/semantic_relations.csv";
        HashSet<Relation> relations = new HashSet<>();
        List<List<String>> rawRelations = readCSV(path);
        for (List<String> rawRelation : rawRelations) {
            try {
                relations.add(new Relation(Integer.parseInt(rawRelation.get(1)),
                        Integer.parseInt(rawRelation.get(2)),
                        Integer.parseInt(rawRelation.get(4))));
            } catch (NumberFormatException ignored) {}
        }
        if (!relations.isEmpty()) {
            logger.info(relations.size() + " relations loaded.");
        }
        return relations;
    }

    /*
    * Get the ONISEP keywords from tagsSources.json
    */
    private Set<String> getKeywords() throws IOException {
        ServerData.load();
        TagsSources tagsSources = TagsSources.load(ServerData.backPsupData.getCorrespondances());
        Set<String> tags = tagsSources.getTags();
        logger.info(tags.size() + " ONISEP tags loaded.");
        return tags;
    }

    private Map<String, List<Match>> getLinks(
            Map<Integer, String> nonFilteredWords,
            Set<Relation> relations,
            Set<String> tags) {
        Map<Integer,String> onisepIds =
                nonFilteredWords.entrySet().stream()
                        .filter(e -> tags.contains(e.getValue()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ));

        /*
        Map<Integer,String> onisepIds2 = new HashMap<>(nonFilteredWords);
        onisepIds2.values().removeIf( v -> !tags.contains(v));
        */

        Set <String> parcoursupDescriptifs = getDescriptifsWordsSet();
        Map<String, List<Match>> result = new HashMap<>();

        relations.forEach(relation -> {
            final int n1 = relation.n1();
            final int n2 = relation.n2();

            String w1 = nonFilteredWords.get(n1);
            String w2 = nonFilteredWords.get(n2);

            if(w1 != null && w2 != null) {

                // Si le mot 1 est un mot ONISEP et le mot 2 un mot de description de parcoursup
                if (onisepIds.containsKey(n1)
                        && w2.length()>1
                        && !parcoursupDescriptifs.contains(w2)) {
                    Match r = new Match(w1, relation.weight());
                    result.computeIfAbsent(w2, z -> new ArrayList<>())
                            .add(r);
                }

                else if (onisepIds.containsKey(n2)
                        && w1.length()>1
                        && !parcoursupDescriptifs.contains(w1)) {
                    Match r = new Match(w2, relation.weight());
                    result.computeIfAbsent(w1, z -> new ArrayList<>())
                            .add(r);
                }
            }
        });
        return result;
    }

    private static Set<String> getDescriptifsWordsSet() {

        HashSet<String> descriptionsWords = new HashSet<>();

        try {
            Object objDescriptifs = new JSONParser().parse(new FileReader("../data/filieres.json"));
            JSONObject jsonDescriptifs = (JSONObject) objDescriptifs;
            JSONArray jsonDescriptions = ((JSONArray) jsonDescriptifs.get("descriptions"));

            for (Object description : jsonDescriptions) {
                JSONObject jsonDescription = (JSONObject) description;

                String text = jsonDescription.get("debouches") + (String) jsonDescription.get("enseignements");

                if (!text.isEmpty()) {
                    text = new String (text.getBytes(), StandardCharsets.UTF_8);
                    // Remove html tags.
                    text = text.replaceAll("<[^>]*>", " ");
                    // Remove non-alphanumeric characters.
                    text = text.replaceAll("[-'`~!@#$%^&*()_|+=?;:\"/,.<>{}]", " ");

                    // Split words.
                    String[] words = text.split(" ");
                    for (String word : words) {
                        if (word.length() > 1) {
                            descriptionsWords.add(word);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return descriptionsWords;
    }


    /*
     * Testing.
     */
    public static void main(String[] args) {
//        Correspondances init = new Correspondances();
//
//        try(OutputStreamWriter os = new OutputStreamWriter(
//                Files.newOutputStream(Path.of("output.json")),
//                StandardCharsets.UTF_8)
//
//        ) {
//            new GsonBuilder().setPrettyPrinting().create().toJson(init, os);
//        }
        System.out.print(getDescriptifsWordsSet());
    }
}

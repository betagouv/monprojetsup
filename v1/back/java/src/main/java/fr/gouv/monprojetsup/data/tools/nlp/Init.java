package fr.gouv.monprojetsup.data.tools.nlp;

import fr.gouv.monprojetsup.data.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.ServerData;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Init {

    private static final String WORDS_FILENAME = "words.csv";
    private static final String RELATIONS_FILENAME = "semantic_relations.csv";
    private final String wordsDataDir;

    public Init(String wordsDataDir) {
        this.wordsDataDir = wordsDataDir + "/";
    }
    private Init() {
        this.wordsDataDir = null;
    }

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

    public String IDtoWord(int ID, List<List<String>> words){
        for (List<String> word : words){
            if (word.get(0).equals(Integer.toString(ID))){
                return word.get(1);
            }
        }
        return null;
    }

    public int WordtoID(String word, List<List<String>> words){
        for (List<String> currentWord : words){
            if (currentWord.get(1).equals(word)){
                return Integer.valueOf(currentWord.get(0));
            }
        }
        return -1;
    }

    /*
     * Get every words from the CSV file. 
     */
    private List<List<String>> GetRawWords() {
        String path = wordsDataDir + WORDS_FILENAME;
        List<List<String>> words = readCSV(path);
        return words;
    }

    /* 
    * Get semantic relations between words (IDs). 
    */ 
    public List<List<String>> GetRelations() {
        String path = wordsDataDir + RELATIONS_FILENAME;
        List<List<String>> relations = readCSV(path);
        return relations;
    }

    /* 
    * Get the ONISEP keywords from tagsSources.json
    */
    private @NotNull Set<String> GetONISEPKeywords() {
        try{
            TagsSources tagsSources = TagsSources.load(ServerData.backPsupData.getCorrespondances());
            if(tagsSources != null) {
                Set<String> keywords = tagsSources.getTags();
                return keywords;
            } else {
                return Collections.emptySet();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<List<String>> FilterWordsList(List<List<String>> words, List<List<String>> relations) {
        List<List<String>> filteredWords = new ArrayList<List<String>>();
        Similar similar = new Similar();

        // Testing 
        // ONISEPKeywords.add("mathématiques");
        // ONISEPKeywords.add("mathématique");
        // To replace with : 
        Set<String> ONISEPKeywords = GetONISEPKeywords();

        for (String tag : ONISEPKeywords) {
            System.out.println("Processing " + tag);
            List<List<String>> similarWords = similar.GetSimilarWords(tag, words, relations);

            for (List<String> similarWord : similarWords) {
                if (!filteredWords.contains(similarWord)) {
                    filteredWords.add(similarWord);
                }
            }
        }

        return filteredWords;
    }

    /*
     * Get the filtered words (only keep words reachable by ONISEP keywords).
     * /!\ Présence de mot vulgaires dans les données, d'où la nécessité de filtrer.
     */
    public List<List<String>> GetWords() {
        String path = wordsDataDir + WORDS_FILENAME;
        List<List<String>> words = readCSV(path);
        words = FilterWordsList(words, GetRelations());
        return words;
    }
    

}

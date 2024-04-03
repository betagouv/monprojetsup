package fr.gouv.monprojetsup.data.tools.nlp;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import opennlp.tools.stemmer.PorterStemmer;

public class KeywordsAdder {

    private static final String searchResultPath = "/Users/benoitboidin/Desktop/labri.nosync/orientation/java/" +
            "src/main/java/fr/parcoursup/orientation/keywords/Liste-des-recherches-faites-search-result (1).csv";
    private static final String descriptifsPath = "/Users/benoitboidin/Desktop/labri.nosync/orientation/java/" +
            "data/filieres.json";
    private static final String codeWordTablePath = "/Users/benoitboidin/Desktop/labri.nosync/orientation/python/" +
            "keyword_extractor/data/code_word_table.csv";

    private static List<List<String>> ReadCSV(String path) {
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

}

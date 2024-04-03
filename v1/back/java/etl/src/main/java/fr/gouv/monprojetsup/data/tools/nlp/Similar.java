package fr.gouv.monprojetsup.data.tools.nlp;

import java.util.List;
import java.util.ArrayList;

public class Similar {

    public List<String> IDtoWord(int ID, List<List<String>> words){
        for (List<String> word : words){
            if (word.get(0).equals(Integer.toString(ID))){
                return word;
            }
        }
        return null;
    }   
    
    public int WordtoID(String word, List<List<String>> words){
        for (List<String> currentWord : words){
            if (currentWord.size() >= 2 && currentWord.get(1).equals(word)){
                return Integer.valueOf(currentWord.get(0));
            }
        }
        return -1;
    }

    /* 
    * Find similar words to the input String, based on semantic_relations.csv
    */
    public List<List<String>> GetSimilarWords(String inputWord, List<List<String>> words, List<List<String>> relations){
        int ID = WordtoID(inputWord, words);
        List<List<String>> similarWords = new ArrayList<>();

        for (List<String> relation : relations){
            try{
                if (Integer.valueOf(relation.get(1)) == ID){
                    similarWords.add(IDtoWord(Integer.parseInt(relation.get(2)), words));
                }
            } catch (NumberFormatException e){
                continue;
            }
        }
        return similarWords;
    }


}

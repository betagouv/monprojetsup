package fr.gouv.parcoursup.carte;

import com.google.gson.Gson;
import fr.gouv.parcoursup.carte.modele.modele.JsonCarte;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PrintStatistics {
    public static void main(String[] args) throws IOException {

        try (Reader r = new InputStreamReader(Files.newInputStream(Paths.get("data/psupdata.json")))) {
            JsonCarte data = new Gson().fromJson(r, JsonCarte.class);
            System.out.println(String.join(System.lineSeparator(),
                    data.groupes[JsonCarte.IDX_TYPE_ETAB].getItems().size() + " types d'établissements",
                    data.groupes[JsonCarte.IDX_TYPE_FORMATION].getItems().size() + " types de formations",
                    data.groupes[JsonCarte.IDX_MENTION].getItems().size() + " mentions et spécialités",
                    data.etablissements.size() + " établissements",
                    data.formations.size() + " actions de formation"
                    )
            );

        }
    }
}
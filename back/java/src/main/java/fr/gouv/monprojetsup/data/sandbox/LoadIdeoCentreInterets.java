package fr.gouv.monprojetsup.data.sandbox;



import fr.gouv.monprojetsup.tools.Serialisation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


record CentreInterets(
        String id,
        String libelle
) {

}

record CentresInterets (
        List<CentreInterets> centres_interets
) {
    public CentresInterets() {
        this(new ArrayList<>());
    }
}

public class LoadIdeoCentreInterets {

    private static final Logger LOGGER = Logger.getLogger(LoadIdeoCentreInterets.class.getSimpleName());

    public static void main(String[] args) throws IOException {

        CentresInterets centres_interets = Serialisation.fromJsonFile("ideo-centres_interets.json", CentresInterets.class);

        LOGGER.info(centres_interets.centres_interets().size() + " centres d'intérêts ont été chargés.");

        centres_interets.centres_interets().stream().limit(5).forEach(metier -> {
            LOGGER.info(metier.toString());
        });
    }


}

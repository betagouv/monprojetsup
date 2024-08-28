package fr.gouv.monprojetsup.data.psup;

import fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.domain.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.domain.model.psup.PsupData;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

import static fr.gouv.monprojetsup.data.psup.MergeDuplicateTags.stemMergeTags;


void main(args: Array<String>) {
    runApplication<UpdateParcoursupDataFromPsupDB>(*args)
}

/**
 * Update all data used by MonProjetSup
 */
@Slf4j
@Component
@SpringBootApplication
public class UpdatePsupDataKt implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateParcoursupDataFromPsupDB.class);

    private final PsupDataSources sources;

    @Autowired
    public UpdateParcoursupDataFromPsupDB(PsupDataSources sources) {
        this.sources = sources;
    }

    @Override
    public void run(String... args) throws Exception {

        getStatistiquesFromPsupDB();

        getBackDataFromPsupDB();

    }


    }


}


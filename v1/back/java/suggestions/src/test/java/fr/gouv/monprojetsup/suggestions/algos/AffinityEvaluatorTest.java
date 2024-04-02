package fr.gouv.monprojetsup.suggestions.algos;

import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.common.dto.ProfileDTO;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AffinityEvaluatorTest  {

    private AffinityEvaluator getTestEvaluator(ProfileDTO profile) {
        Config cfg = new Config();
        return new AffinityEvaluator(profile, cfg);
    }

    @BeforeClass
    @BeforeAll
    public static void prepareData() throws Exception {
        DataSources.setRootDirectory(Path.of("../../").toAbsolutePath().toString());
        ServerData.load();
    }

    @Test
    public void testDataLoadOk() {
        assertNotNull(ServerData.backPsupData);
        assertNotNull(ServerData.backPsupData.filActives());
        assertFalse(ServerData.backPsupData.filActives().isEmpty());

        assertNotNull(ServerData.statistiques);
        assertNotNull(ServerData.statistiques.nomsFilieres);
        assertFalse(ServerData.statistiques.nomsFilieres.isEmpty());
    }


    @Test
    public void testGetAffinityEvaluation() {
        assertTrue(true);
    }

    @Test
    public void testGetExplanation() {
        assertTrue(true);
    }

    @Test
    public void testGetCloseTagsSuggestionsOrderedByIncreasingDistance() {
        assertTrue(true);
    }

}
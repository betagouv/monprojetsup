package fr.gouv.monprojetsup.suggestions.algos;

import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AffinityEvaluatorTest {

    @Autowired
    @Lazy
    SuggestionServer server;

    private AffinityEvaluator getTestEvaluator(ProfileDTO profile) {
        Config cfg = new Config();
        return new AffinityEvaluator(profile, cfg);
    }

    @BeforeClass
    @BeforeAll
    public static void prepareData() throws Exception {
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
        //TODO
        assertTrue(true);
    }

    @Test
    public void testGetExplanation() {
        //TODO
        assertTrue(true);
    }

    @Test
    public void testGetCloseTagsSuggestionsOrderedByIncreasingDistance() {
        //TODO
        assertTrue(true);
    }

}
package fr.gouv.monprojetsup.suggestions.algos;

import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
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
        SuggestionsData.load();
    }

    @Test
    public void testDataLoadOk() {
        assertNotNull(SuggestionsData.getDescriptifs());
        /** etc */
        assertFalse(SuggestionsData.getLabels().isEmpty());
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
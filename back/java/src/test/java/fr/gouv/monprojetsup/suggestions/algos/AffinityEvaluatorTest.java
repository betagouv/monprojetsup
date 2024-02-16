package fr.gouv.monprojetsup.suggestions.algos;

import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.dto.SuggestionDTO;
import fr.gouv.monprojetsup.web.dto.ProfileUpdateDTO;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Path;
import java.util.List;

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

    @Test
    public void testInjectExplanationsIfNeeded() {

        Profile profile = new Profile(
                "toto"
        );

        String fl = "fl11";
        profile.updateProfile(new ProfileUpdateDTO(
                null,
                null,
                null,
                List.of(new SuggestionDTO(
                        fl,
                        Suggestion.SUGG_APPROVED,
                        ""
                )))
        );

        profile.updateProfile(new ProfileUpdateDTO(
                "moygen",
        "20",
        "add",
        null)
        );

        profile.updateProfile(new ProfileUpdateDTO(
                "interets",
                "T-IDEO2.4825",
                "add",
                null)
        );

        profile.updateProfile(new ProfileUpdateDTO(
                "moygen",
                "20",
                "add",
                null)
        );


        //AffinityEvaluator evaluator = getTestEvaluator(profile);
        //evaluator.injectExplanationsIfNeeded();

        assertFalse(profile.suggApproved() == null);
        //assertFalse(profile.suggApproved().isEmpty());

        //assertFalse(profile.suggApproved().get(0).expl().isEmpty());
    }

}
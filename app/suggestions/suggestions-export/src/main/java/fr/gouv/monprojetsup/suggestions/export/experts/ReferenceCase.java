package fr.gouv.monprojetsup.suggestions.export.experts;

import fr.gouv.monprojetsup.suggestions.algo.Suggestion;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.dto.ChoiceDTO;

import java.util.ArrayList;
import java.util.List;

public record ReferenceCase(
        String name,
        ProfileDTO pf,
        List<String> expectations,
        List<String> rejections,

        List<Suggestion> suggestions
) {

    public ReferenceCase(String name, ProfileDTO dto) {
        this(name, dto, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public void turnFavorisToExpectations() {
        if(pf != null) {
            expectations.addAll(pf.suggApproved().stream().map(ChoiceDTO::id).toList());
            rejections.addAll(pf.suggRejected().stream().map(ChoiceDTO::id).toList());
            pf.removeAllFormationChoices();
        }
    }
}

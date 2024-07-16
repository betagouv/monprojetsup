package fr.gouv.monprojetsup.suggestions.export.experts;

import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.dto.SuggestionDTO;
import fr.gouv.monprojetsup.suggestions.algos.Suggestion;

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
            expectations.addAll(pf.suggApproved().stream().map(SuggestionDTO::fl).toList());
            rejections.addAll(pf.suggRejected().stream().map(SuggestionDTO::fl).toList());
            pf.removeAllFormationChoices();
        }
    }
}

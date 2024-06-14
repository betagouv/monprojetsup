package fr.gouv.monprojetsup.suggestions.analysis;

import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;
import fr.gouv.monprojetsup.suggestions.algos.Suggestion;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

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
        } else {
            int i = 0;
        }
    }
}

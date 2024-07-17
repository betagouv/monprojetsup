package fr.gouv.monprojetsup.suggestions.export.experts;

import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


@Component
public class DocsGenerator {


    private final SuggestionsData data;
    private final SuggestionsGenerator suggestionsGenerator;

    @Autowired
    public DocsGenerator(SuggestionsData data, SuggestionsGenerator suggestionsGenerator)
    {
        this.data = data;
        this.suggestionsGenerator = suggestionsGenerator;
    }

    public void generate() throws Exception {


        //load Map<String, ProfileDTO> profiles = new HashMap<>(); from profilsExperts.json
        ReferenceCases cases;
        try {
            cases = ReferenceCases.loadFromFile(SuggestionsGenerator.REF_CASES_WITH_SUGGESTIONS);
        } catch (Exception e) {
            SuggestionsGenerator.LOGGER.info("Generating ref profiles");
            suggestionsGenerator.generate();
            cases = ReferenceCases.loadFromFile(SuggestionsGenerator.REF_CASES_WITH_SUGGESTIONS);
        }

        cases = toHumanReadable(cases);

        cases.toDetails("détails", true, data.getLabels());

        try (
                OutputStreamWriter fos = new OutputStreamWriter(
                        Files.newOutputStream(Path.of("profiles_expectations_suggestions.txt")),
                        StandardCharsets.UTF_8
                )
        ) {
            fos.write(cases.resume(data.getLabels()));
        }

        cases.resumeCsv("profiles_expectations_suggestions.csv", data.getLabels());

    }

    private ReferenceCases toHumanReadable(ReferenceCases cases) {
        ReferenceCases res = new ReferenceCases();
        for (ReferenceCase c : cases.cases()) {
            res.cases().add(new ReferenceCase(
                    c.name(),
                    c.pf(),
                    toHumanReadable(c.expectations()),
                    toHumanReadable(c.rejections()),
                            c.suggestions()
            ));
        }
        return res;
    }

    private List<String> toHumanReadable(List<String> filieres) {
        return filieres.stream().map(f -> data.getLabel(f,f)).toList();
    }



}

package fr.gouv.monprojetsup.suggestions.export.experts;

import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.port.LabelsPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


@Component
public class DocsGenerator {


    private final SuggestionsData data;
    private final SuggestionsGenerator suggestionsGenerator;
    private final Map<String, String> labels;

    @Autowired
    public DocsGenerator(SuggestionsData data, SuggestionsGenerator suggestionsGenerator, LabelsPort labelsPort)
    {
        this.data = data;
        this.suggestionsGenerator = suggestionsGenerator;
        this.labels = labelsPort.retrieveLabels();
    }

    public void generate(String filename) throws Exception {


        //load Map<String, ProfileDTO> profiles = new HashMap<>(); from profilsExperts.json
        ReferenceCases cases;
        try {
            cases = ReferenceCases.loadFromFile(SuggestionsGenerator.REF_CASES_WITH_SUGGESTIONS);
        } catch (Exception e) {
            SuggestionsGenerator.LOGGER.info("Generating ref profiles");
            suggestionsGenerator.generate(filename);
            cases = ReferenceCases.loadFromFile(SuggestionsGenerator.REF_CASES_WITH_SUGGESTIONS);
        }

        cases = toHumanReadable(cases);

        cases.toDetails("détails", true, data.getDebugLabels());

        try (
                OutputStreamWriter fos = new OutputStreamWriter(
                        Files.newOutputStream(Path.of("profiles_expectations_suggestions.txt")),
                        StandardCharsets.UTF_8
                )
        ) {
            fos.write(cases.resume(data.getDebugLabels()));
        }

        cases.resumeCsv("profiles_expectations_suggestions.csv", data.getDebugLabels());

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
        return filieres.stream().map(f -> labels.getOrDefault(f,f)).toList();
    }



}

package fr.gouv.monprojetsup.suggestions.data.update.psup;

public record DescriptifFormation(
        int code,
        int codeTypeFormation,
        int codeFiliere,
        String libFiliere,
        String libVoeu,
        String debouches,
        String enseignement
) {

}

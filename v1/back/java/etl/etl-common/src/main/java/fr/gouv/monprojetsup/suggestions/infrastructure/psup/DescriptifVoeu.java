package fr.gouv.monprojetsup.suggestions.infrastructure.psup;

public record DescriptifVoeu(
        int code,
        int codeTypeFormation,
        int codeFiliere,
        String libFiliere,
        String libVoeu,
        String debouches,
        String enseignement
) {

}

package fr.gouv.monprojetsup.data.psup;

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

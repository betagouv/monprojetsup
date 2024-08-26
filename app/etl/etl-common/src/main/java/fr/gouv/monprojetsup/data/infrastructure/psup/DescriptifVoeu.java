package fr.gouv.monprojetsup.data.infrastructure.psup;

import java.io.Serializable;

public record DescriptifVoeu(
        int code,
        int codeTypeFormation,
        int codeFiliere,
        String libFiliere,
        String libVoeu,
        String debouches,
        String enseignement
) implements Serializable {

}

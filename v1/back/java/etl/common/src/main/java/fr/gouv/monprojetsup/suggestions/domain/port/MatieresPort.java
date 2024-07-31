package fr.gouv.monprojetsup.suggestions.domain.port;

import fr.gouv.monprojetsup.suggestions.domain.model.Matiere;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MatieresPort {

    @NotNull List<@NotNull Matiere> retrieveSpecialites();

}

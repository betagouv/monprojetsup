package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.domain.model.Matiere;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MatieresPort {

    @NotNull List<@NotNull Matiere> retrieveSpecialites();

}

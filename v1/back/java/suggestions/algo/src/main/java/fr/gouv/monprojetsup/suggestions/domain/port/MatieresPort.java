package fr.gouv.monprojetsup.suggestions.domain.port;

import fr.gouv.monprojetsup.suggestions.domain.entity.Matiere;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class MatieresPort {

    abstract public @NotNull List<@NotNull Matiere> retrieveSpecialites();

}

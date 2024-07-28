package fr.gouv.monprojetsup.data.domain.port;

import fr.gouv.monprojetsup.data.domain.entity.Matiere;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class MatieresPort {

    abstract public @NotNull List<@NotNull Matiere> retrieveSpecialites();

}

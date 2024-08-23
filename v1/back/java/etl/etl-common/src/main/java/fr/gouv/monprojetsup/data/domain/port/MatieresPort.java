package fr.gouv.monprojetsup.data.domain.port;

import fr.gouv.monprojetsup.data.domain.model.Matiere;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MatieresPort {

    @NotNull List<@NotNull Matiere> retrieveSpecialites();

    void saveAll(@NotNull List<@NotNull Matiere> matieres);

}

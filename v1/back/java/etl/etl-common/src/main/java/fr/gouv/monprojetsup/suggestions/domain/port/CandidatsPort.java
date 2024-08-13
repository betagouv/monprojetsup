package fr.gouv.monprojetsup.suggestions.domain.port;

import fr.gouv.monprojetsup.suggestions.domain.model.Candidat;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public interface CandidatsPort {

    List<Candidat> findAll();

    void save(@NotNull Candidat candidat);

    void saveAll(@NotNull Collection<@NotNull Candidat> candidat);

}

package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.formation.entity.FormationVoeuEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FormationsVoeuxPort {

    @NotNull List<@NotNull FormationVoeuEntity> findAll();

    List<String> getFormationsOfVoeu(@NotNull String voeuId);

}

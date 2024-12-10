package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.formation.entity.FormationVoeuEntity;
import fr.gouv.monprojetsup.data.model.LatLng;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FormationsVoeuxPort {

    @NotNull List<@NotNull FormationVoeuEntity> findAll();

    List<String> getFormationsOfVoeu(@NotNull String voeuId);

    @NotNull List<@NotNull Pair<@NotNull String, @NotNull LatLng>> getVoeuxOfFormation(String formationId);
}

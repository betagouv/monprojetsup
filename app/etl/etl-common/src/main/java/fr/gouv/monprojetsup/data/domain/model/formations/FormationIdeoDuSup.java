package fr.gouv.monprojetsup.data.domain.model.formations;

import fr.gouv.monprojetsup.data.domain.model.onisep.formations.FicheFormationIdeo;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.FormationIdeoSimple;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record FormationIdeoDuSup(
        @NotNull String ideo,//preserved in ideo style FOR.xxxx
        String descriptifFormatCourt,
        String descriptifAcces,
        String descriptifPoursuite,
        String url,
        String attendus,
        //FormationAvecMetier.PoursuitesEtudes poursuitesEtudes,
        List<String> sourcesNumeriques,
        @NotNull List<String> metiers,//preserved in ideo style MET.xxxx
        @NotNull List<String> motsCles

) {

    public FormationIdeoDuSup(@NotNull FicheFormationIdeo f) {
        this(
                f.identifiant(),
                f.descriptif_format_court(),
                f.descriptif_acces(),
                f.descriptif_poursuite_etudes(),
                f.url(),
                f.attendus(),
                //f.poursuites_etudes(),
                f.sources_numeriques(),
                f.metiers_formation() == null
                        ? new ArrayList<>()
                        : f.metiers_formation().stream().map(FicheFormationIdeo.MetierFormation::id).toList(),
                f.getMotsCles()
        );
    }

    public FormationIdeoDuSup(@NotNull FormationIdeoSimple f) {
        this(
                Objects.requireNonNull(f.identifiant()),
                null,
                null,
                null,
                f.urlEtIdOnisep(),
                //null,
                null,
                null,
                new ArrayList<>(),
                f.getMotsCles()
        );
    }

}

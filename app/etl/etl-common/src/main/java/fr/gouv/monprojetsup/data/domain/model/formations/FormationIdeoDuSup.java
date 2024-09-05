package fr.gouv.monprojetsup.data.domain.model.formations;

import fr.gouv.monprojetsup.data.domain.model.onisep.SousDomaineWeb;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.FicheFormationIdeo;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.FormationIdeoSimple;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        @NotNull List<String> motsCles,
        @NotNull List<String> libellesOuClesSousdomainesWeb,
        boolean estIEP,
        boolean estEcoleIngenieur,
        boolean estEcoleCommerce,
        boolean estEcoleArchitecture,
        boolean estEcoleArt,
        boolean estConservationRestauration,
        boolean estDMA

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
                f.getMotsCles(),
                f.sousDomainesWeb().stream().map(FicheFormationIdeo.SousDomaineWeb::id).toList(),
                f.estIEP(),
                f.estEcoleIngenieur(),
                f.estEcoleCommerce(),
                f.estEcoleArchitecture(),
                f.estEcoleArt(),
                f.estDiplomeConservationRestauration(),
                f.estDMA()
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
                f.getMotsCles(),
                List.of(f.domainesousDomaine()),
                f.estIEP(),
                f.estEcoleIngenieur(),
                f.estEcoleCommerce(),
                f.estEcoleArchitecture(),
                f.estEcoleArt(),
                f.estConservationRestauration(),
                f.estDMA()
        );
    }

    public List<String> getSousdomainesWebMpsIds(Map<String, SousDomaineWeb> sousDomainesWeb) {
        List<String> result = new ArrayList<>();
        for (String libelleDomaine : libellesOuClesSousdomainesWeb) {
            val clesDomaine = SousDomaineWeb.extractDomaines(libelleDomaine, sousDomainesWeb);
            result.addAll(clesDomaine.stream().map(SousDomaineWeb::mpsId).toList());
        }
        return result;
    }

}

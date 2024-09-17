package fr.gouv.monprojetsup.data.model.formations;

import fr.gouv.monprojetsup.data.model.onisep.SousDomaineWeb;
import fr.gouv.monprojetsup.data.model.onisep.formations.FicheFormationIdeo;
import fr.gouv.monprojetsup.data.model.onisep.formations.FormationIdeoSimple;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public record FormationIdeoDuSup(
        @NotNull String ideo,//preserved in ideo style FOR.xxxx
        @NotNull String label,
        String descriptifFormatCourt,
        String descriptifAcces,
        String descriptifPoursuite,
        String url,
        String attendus,
        //FormationAvecMetier.PoursuitesEtudes poursuitesEtudes,
        List<String> sourcesNumeriques,
        @NotNull HashSet<String> metiers,//preserved in ideo style MET.xxxx
        @NotNull Set<String> motsCles,
        @NotNull Set<String> libellesOuClesSousdomainesWeb,
        boolean estIEP,
        boolean estEcoleIngenieur,
        boolean estEcoleCommerce,
        boolean estEcoleArchitecture,
        boolean estEcoleArt,
        boolean estConservationRestauration,
        boolean estDMA

) {

    public void inheritFrom(FormationIdeoDuSup rich) {
        metiers.addAll(rich.metiers);
        motsCles.addAll(rich.motsCles);
        libellesOuClesSousdomainesWeb.addAll(rich.libellesOuClesSousdomainesWeb);
    }

    public FormationIdeoDuSup(@NotNull FicheFormationIdeo f) {
        this(
                f.identifiant(),
                f.libelle_complet(),
                f.descriptif_format_court(),
                f.descriptif_acces(),
                f.descriptif_poursuite_etudes(),
                f.url(),
                f.attendus(),
                //f.poursuites_etudes(),
                f.sources_numeriques(),
                f.metiers_formation() == null
                        ? new HashSet<>()
                        : f.metiers_formation().stream().map(FicheFormationIdeo.MetierFormation::id)
                            .collect(Collectors.toCollection(HashSet::new)),
                new HashSet<>(f.getMotsCles()),
                f.sousDomainesWeb().stream().map(FicheFormationIdeo.SousDomaineWeb::id)
                        .collect(Collectors.toCollection(HashSet::new)),
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
                f.libelleFormationPrincipal(),
                null,
                null,
                null,
                f.urlEtIdOnisep(),
                //null,
                null,
                null,
                new HashSet<>(),
                new HashSet<>(f.getMotsCles()),
                new HashSet<>(List.of(f.domainesousDomaine())),
                f.estIEP(),
                f.estEcoleIngenieur(),
                f.estEcoleCommerce(),
                f.estEcoleArchitecture(),
                f.estEcoleArt(),
                f.estConservationRestauration(),
                f.estDMA()
        );
    }

    public static List<String> getSousdomainesWebMpsIds(Collection<String> libellesOuClesSousdomainesWeb, Map<String, SousDomaineWeb> sousDomainesWeb) {
        List<String> result = new ArrayList<>();
        for (String libelleDomaine : libellesOuClesSousdomainesWeb) {
            val domaines = SousDomaineWeb.extractDomaines(libelleDomaine, sousDomainesWeb);
            result.addAll(domaines.stream().map(SousDomaineWeb::mpsId).toList());
        }
        return result.stream().distinct().sorted().toList();
    }

    private static final String IDEO_FORMATION_KEY_PATTERN = "FOR\\.\\d+";
    public static boolean isIdeoFormationKey(@NotNull String it) {
        return it.matches(IDEO_FORMATION_KEY_PATTERN);
    }


    private static final String IDEO_METIER_KEY_PATTERN = "MET\\.\\d+";
    public static boolean isIdeoMetierKey(@NotNull String it) {
        return it.matches(IDEO_METIER_KEY_PATTERN);
    }
}

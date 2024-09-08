package fr.gouv.monprojetsup.data.domain.model.metiers;

import fr.gouv.monprojetsup.data.domain.model.onisep.SousDomaineWeb;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.FicheFormationIdeo;
import fr.gouv.monprojetsup.data.domain.model.onisep.metiers.FicheMetierIdeo;
import fr.gouv.monprojetsup.data.domain.model.onisep.metiers.MetierIdeoSimple;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static fr.gouv.monprojetsup.data.domain.Constants.cleanup;
import static fr.gouv.monprojetsup.data.domain.Helpers.removeHtml;

public record MetierIdeoDuSup(
        @NotNull String ideo,
        @NotNull String lib,
        @NotNull List<FicheMetierIdeo.SourceNumerique> urls,
        @Nullable String libRome,
        @Nullable String urlRome,
        @Nullable String codeRome,
        @Nullable String descriptif,
        @NotNull HashSet<String> domaines,//from DomainesPro
        @NotNull HashSet<String> interets,
        @NotNull HashSet<String> motsCles,

        @NotNull HashSet<FicheMetierIdeo.MetierAssocie> metiersAssocies

        ) {

    public void inheritFrom(MetierIdeoDuSup rich) {
        domaines.addAll(rich.domaines);
        interets.addAll(rich.interets);
        motsCles.addAll(rich.motsCles);
        metiersAssocies.addAll(rich.metiersAssocies);
    }

    public MetierIdeoDuSup(MetiersScrapped.MetierScrap m) {
        this(m.key(),
                removeHtml(m.nom()),
                List.of(new FicheMetierIdeo.SourceNumerique(m.url(), removeHtml(m.nom()))),
                null,
                null,
                null,
                removeHtml(m.getDescriptif()),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>()
        );
    }

    public static @NotNull MetierIdeoDuSup merge(MetierIdeoSimple m, Map<String, SousDomaineWeb> sousdomainesWeb, MetierIdeoDuSup o) {

        Set<String> motsCles = new HashSet<>();
        Set<String> domaines = new HashSet<>();
        Set<FicheMetierIdeo.MetierAssocie> metiersAssocies = new HashSet<>();
        if (o != null) {
            motsCles.addAll(o.motsCles());
            domaines.addAll(o.domaines());
            metiersAssocies.addAll(o.metiersAssocies());
        }

        domaines.addAll(m.getDomaines(sousdomainesWeb).stream().map(SousDomaineWeb::mpsId).toList());
        motsCles.addAll(m.getMotsCles());

        return new MetierIdeoDuSup(
                m.idIdeo(),
                m.libelle_metier(),
                m.lien_site_onisepfr() != null
                        ? List.of(new FicheMetierIdeo.SourceNumerique(m.lien_site_onisepfr(), m.libelle_metier()))
                        : List.of(),
                m.libelle_rome(),
                m.getRomeUrl(),
                m.code_rome(),
                o == null ? "" : o.descriptif(),
                new HashSet<>(domaines),
                o == null ? new HashSet<>() : o.interets(),
                new HashSet<>(motsCles),
                new HashSet<>(metiersAssocies)
        );
    }

    public static MetierIdeoDuSup merge(@NotNull FicheMetierIdeo m, @Nullable MetierIdeoDuSup o) {

        Set<String> interets = new HashSet<>();
        Set<String> motsCles = new HashSet<>();
        Set<FicheMetierIdeo.MetierAssocie> metiersAssocies = new HashSet<>();
        if (o != null) {
            interets.addAll(o.interets());
            motsCles.addAll(o.motsCles());
            metiersAssocies.addAll(o.metiersAssocies());
        }

        if (m.synonymes() != null) {
            motsCles.addAll(m.synonymes().stream().map(FicheFormationIdeo.Synonyme::nom_metier).toList());
        }
        if (m.centresInteret() != null) {
            val centesInteretsList = m.centresInteret().stream().toList();
            interets.addAll(centesInteretsList.stream().map(FicheMetierIdeo.CentreInteret::id).toList());
            motsCles.addAll(centesInteretsList.stream().map(FicheMetierIdeo.CentreInteret::libelle).toList());
        }
        if (m.metiersAssocies() != null) {
            val metiersAssociesList = m.metiersAssocies();
            metiersAssocies.addAll(metiersAssociesList);
            motsCles.addAll(metiersAssociesList.stream().map(FicheMetierIdeo.MetierAssocie::libelle).toList());
        }

        String descriptif = m.getDescriptif();

        return new MetierIdeoDuSup(
                m.identifiant(),
                m.nom_metier(),
                Objects.requireNonNullElse(m.sourcesNumeriques(), List.of()),
                o == null ? null : o.libRome,
                o == null ? null : o.urlRome,
                o == null ? null : o.codeRome,
                descriptif.isBlank() && o != null ? o.descriptif : descriptif,
                o == null ? new HashSet<>() : o.domaines(),
                new HashSet<>(interets.stream().toList()),
                new HashSet<>(motsCles),
                new HashSet<>(metiersAssocies)
        );
    }


    public @NotNull String idMps() {
        return cleanup(Objects.requireNonNull(ideo()));
    }


}

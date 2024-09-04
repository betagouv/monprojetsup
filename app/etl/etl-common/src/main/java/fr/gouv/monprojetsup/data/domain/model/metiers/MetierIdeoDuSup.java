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

public record MetierIdeoDuSup(
        @NotNull String ideo,
        @NotNull String lib,
        @NotNull List<FicheMetierIdeo.SourceNumerique> urls,
        @Nullable String libRome,
        @Nullable String urlRome,
        @Nullable String codeRome,
        @Nullable String descriptif,

        @NotNull List<String> domaines,//from DomainesPro
        @NotNull ArrayList<String> interets,
        @NotNull List<String> motsCles,

        @NotNull List<FicheMetierIdeo.MetierAssocie> metiersAssocies

        ) {

    public MetierIdeoDuSup(MetiersScrapped.MetierScrap m) {
        this(m.key(),
                m.nom(),
                List.of(new FicheMetierIdeo.SourceNumerique(m.url(), m.nom())),
                null,
                null,
                null,
                m.getDescriptif(),
                List.of(),
                new ArrayList<>(),
                List.of(),
                List.of()
                );
    }

    public static @NotNull MetierIdeoDuSup merge(MetierIdeoSimple m, List<SousDomaineWeb> domainesPro, MetierIdeoDuSup o) {

        Set<String> motsCles = new HashSet<>();
        Set<String> domaines = new HashSet<>();
        Set<FicheMetierIdeo.MetierAssocie> metiersAssocies = new HashSet<>();
        if(o != null) {
            motsCles.addAll(o.motsCles());
            domaines.addAll(o.domaines());
            metiersAssocies.addAll(o.metiersAssocies());
        }

        domaines.addAll(m.getDomaines(domainesPro).stream().map(SousDomaineWeb::mpsId).toList());
        motsCles.addAll(m.getMotsCles());

        return new MetierIdeoDuSup(
                m.idMps(),
                m.libelle_metier(),
                m.lien_site_onisepfr() != null
                        ? List.of(new FicheMetierIdeo.SourceNumerique(m.lien_site_onisepfr(), m.libelle_metier()))
                        : List.of(),
                m.libelle_rome(),
                m.lien_rome(),
                m.code_rome(),
                o == null ? "" : o.descriptif(),
                domaines.stream().toList(),
                o == null ? new ArrayList<>() : o.interets(),
                motsCles.stream().toList(),
                metiersAssocies.stream().toList()
        );
    }
    public static MetierIdeoDuSup merge(@NotNull FicheMetierIdeo m, @Nullable MetierIdeoDuSup o) {

        Set<String> interets = new HashSet<>();
        Set<String> motsCles = new HashSet<>();
        Set<FicheMetierIdeo.MetierAssocie> metiersAssocies = new HashSet<>();
        if(o != null) {
            interets.addAll(o.interets());
            motsCles.addAll(o.motsCles());
            metiersAssocies.addAll(o.metiersAssocies());
        }

        if(m.synonymes() != null) {
            motsCles.addAll(m.synonymes().stream().map(FicheFormationIdeo.Synonyme::nom_metier).toList());
        }
        if(m.centresInteret() != null) {
            val centesInteretsList = m.centresInteret().stream().toList();
            interets.addAll(centesInteretsList.stream().map(z -> cleanup(z.id())).toList());
            motsCles.addAll(centesInteretsList.stream().map(FicheMetierIdeo.CentreInteret::libelle).toList());
        }
        if(m.metiersAssocies() != null) {
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
                descriptif.isBlank() ? o.descriptif : descriptif,
                o == null ? List.of() : o.domaines(),
                new ArrayList<>(interets.stream().toList()),
                motsCles.stream().toList(),
                metiersAssocies.stream().toList()
        );
    }



    public @NotNull String idMps() {
        return cleanup(Objects.requireNonNull(ideo()));
    }


}

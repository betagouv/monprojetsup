package fr.gouv.monprojetsup.data.domain.model.metiers;

import fr.gouv.monprojetsup.data.domain.model.onisep.DomainePro;
import fr.gouv.monprojetsup.data.domain.model.onisep.metiers.FicheMetierIdeo;
import fr.gouv.monprojetsup.data.domain.model.onisep.metiers.MetierIdeoSimple;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static fr.gouv.monprojetsup.data.domain.Constants.cleanup;

public record MetierIdeoDuSup(
        @NotNull String ideo,
        @NotNull String lib,
        @NotNull List<String> urls,
        @Nullable String libRome,
        @Nullable String urlRome,
        @Nullable String codeRome,
        @Nullable String descriptif,

        @NotNull List<String> domaines,//from DomainesPro
        @NotNull List<String> interets,
        @NotNull List<String> motsCles,

        @NotNull List<FicheMetierIdeo.MetiersAssocies.MetierAssocie> metiersAssocies

        ) {

    public MetierIdeoDuSup(MetiersScrapped.MetierScrap m) {
        this(m.key(),
                m.nom(),
                List.of(m.url()),
                null,
                null,
                null,
                m.getDescriptif(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
                );
    }

    public static @NotNull MetierIdeoDuSup merge(MetierIdeoSimple m, List<DomainePro> domainesPro, MetierIdeoDuSup o) {

        Set<String> interets = new HashSet<>();
        Set<String> motsCles = new HashSet<>();
        Set<String> domaines = new HashSet<>();
        Set<FicheMetierIdeo.MetiersAssocies.MetierAssocie> metiersAssocies = new HashSet<>();
        if(o != null) {
            interets.addAll(o.interets());
            motsCles.addAll(o.motsCles());
            domaines.addAll(o.domaines());
            metiersAssocies.addAll(o.metiersAssocies());
        }

        domaines.addAll(m.getDomaines(domainesPro));
        motsCles.addAll(m.getMotsCles());

        return new MetierIdeoDuSup(
                m.idMps(),
                m.libelle_metier(),
                m.lien_site_onisepfr() != null ? List.of(m.lien_site_onisepfr()) : List.of(),
                m.libelle_rome(),
                m.lien_rome(),
                m.code_rome(),
                o == null ? "" : o.descriptif(),
                domaines.stream().toList(),
                interets.stream().toList(),
                motsCles.stream().toList(),
                metiersAssocies.stream().toList()
        );
    }
    public static MetierIdeoDuSup merge(@NotNull FicheMetierIdeo m, @Nullable MetierIdeoDuSup o) {

        Set<String> interets = new HashSet<>();
        Set<String> motsCles = new HashSet<>();
        Set<FicheMetierIdeo.MetiersAssocies.MetierAssocie> metiersAssocies = new HashSet<>();
        if(o != null) {
            interets.addAll(o.interets());
            motsCles.addAll(o.motsCles());
            metiersAssocies.addAll(o.metiersAssocies());
        }

        if(m.centresInteret() != null) {
            interets.addAll(m.centresInteret().stream().map(z -> cleanup(z.id())).toList());
            motsCles.addAll(m.centresInteret().stream().map(FicheMetierIdeo.CentreInteret::libelle).toList());
        }
        if(m.metiersAssocies() != null) {
            val metiersAssociesList = m.metiersAssocies().metier_associe();
            metiersAssocies.addAll(metiersAssociesList);
            motsCles.addAll(metiersAssociesList.stream().map(FicheMetierIdeo.MetiersAssocies.MetierAssocie::libelle).toList());
        }

        String descriptif = m.getDescriptif();

        return new MetierIdeoDuSup(
                m.identifiant(),
                m.nom_metier(),
                Objects.requireNonNullElse(m.sources_numeriques().sources_numeriques(), List.of()),
                o == null ? null : o.libRome,
                o == null ? null : o.urlRome,
                o == null ? null : o.codeRome,
                descriptif.isBlank() ? o.descriptif : descriptif,
                o == null ? List.of() : o.domaines(),
                interets.stream().toList(),
                motsCles.stream().toList(),
                metiersAssocies.stream().toList()
        );
    }



    public @NotNull String idMps() {
        return cleanup(Objects.requireNonNull(ideo()));
    }


}

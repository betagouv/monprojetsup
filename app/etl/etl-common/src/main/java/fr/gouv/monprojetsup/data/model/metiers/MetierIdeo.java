package fr.gouv.monprojetsup.data.model.metiers;

import fr.gouv.monprojetsup.data.model.onisep.SousDomaineWeb;
import fr.gouv.monprojetsup.data.model.onisep.formations.FicheFormationIdeo;
import fr.gouv.monprojetsup.data.model.onisep.metiers.FicheMetierIdeo;
import fr.gouv.monprojetsup.data.model.onisep.metiers.MetierIdeoSimple;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static fr.gouv.monprojetsup.data.Helpers.removeHtml;

public record MetierIdeo(
        @NotNull String ideo,
        @NotNull String lib,
        @NotNull List<FicheMetierIdeo.SourceNumerique> urls,
        @Nullable String libRome,
        @Nullable String urlRome,
        @Nullable String codeRome,
        @Nullable String descriptif,
        @NotNull HashSet<String> domainesWeb,
        @NotNull HashSet<String> secteursActivite,
        @NotNull HashSet<String> interets,
        @NotNull HashSet<String> motsCles,
        @NotNull HashSet<FicheMetierIdeo.MetierAssocie> metiersAssocies
        ) {

    public void inheritFrom(MetierIdeo rich) {
        secteursActivite.addAll(rich.secteursActivite);
        interets.addAll(rich.interets);
        motsCles.addAll(rich.motsCles);
        metiersAssocies.addAll(rich.metiersAssocies);
        domainesWeb.addAll(rich.domainesWeb);
    }

    public MetierIdeo(MetiersScrapped.MetierScrap m) {
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
                new HashSet<>(),
                new HashSet<>()
        );
    }

    public static @NotNull MetierIdeo merge(MetierIdeoSimple m, Map<String, SousDomaineWeb> sousdomainesWeb, MetierIdeo o) {

        HashSet<String> motsCles = new HashSet<>();
        HashSet<String> domainesWeb = new HashSet<>();
        if (o != null) {
            motsCles.addAll(o.motsCles());
            domainesWeb.addAll(o.domainesWeb());
        }

        motsCles.addAll(m.getMotsCles());
        domainesWeb.addAll(m.getDomaines(sousdomainesWeb).stream().map(SousDomaineWeb::ideo).toList());

        return new MetierIdeo(
                m.idIdeo(),
                m.libelle_metier(),
                m.lien_site_onisepfr() != null
                        ? List.of(new FicheMetierIdeo.SourceNumerique(m.lien_site_onisepfr(), m.libelle_metier()))
                        : List.of(),
                m.libelle_rome(),
                m.getRomeUrl(),
                m.code_rome(),
                o == null ? "" : o.descriptif(),
                domainesWeb,
                new HashSet<>(o == null ? Set.of() : o.secteursActivite()),//les secteurs activité sont dans ls fiches
                new HashSet<>(o == null ? Set.of() : o.interets()),//les intérêts sont dans ls fiches
                motsCles,
                new HashSet<>(o == null ? Set.of() : o.metiersAssocies())//les métiers associés sont dans ls fiches
        );
    }

    public static MetierIdeo merge(@NotNull FicheMetierIdeo m, @Nullable MetierIdeo o) {

        HashSet<String> interets = new HashSet<>();
        HashSet<String> motsCles = new HashSet<>();
        HashSet<String> secteursActivite = new HashSet<>();
        HashSet<FicheMetierIdeo.MetierAssocie> metiersAssocies = new HashSet<>();

        if (o != null) {
            interets.addAll(o.interets());
            motsCles.addAll(o.motsCles());
            secteursActivite.addAll(o.secteursActivite());
            metiersAssocies.addAll(o.metiersAssocies());
        }

        if (m.synonymes() != null) {
            motsCles.addAll(m.synonymes().stream().map(FicheFormationIdeo.Synonyme::nom_metier).toList());
        }

        secteursActivite.addAll(m.getSecteursActivite().stream().map(FicheMetierIdeo.SecteurActivite::id).toList());

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

        return new MetierIdeo(
                m.identifiant(),
                m.nom_metier(),
                Objects.requireNonNullElse(m.sourcesNumeriques(), List.of()),
                o == null ? null : o.libRome,
                o == null ? null : o.urlRome,
                o == null ? null : o.codeRome,
                descriptif.isBlank() && o != null ? o.descriptif : descriptif,
                new HashSet<>(o == null ? Set.of() : o.domainesWeb()),//pas de domaines dans les fiches
                secteursActivite,
                interets,
                motsCles,
                metiersAssocies
        );
    }

}

package fr.gouv.monprojetsup.data.model.onisep;

import fr.gouv.monprojetsup.data.model.formations.FilierePsupVersIdeoData;
import fr.gouv.monprojetsup.data.model.formations.FormationIdeoDuSup;
import fr.gouv.monprojetsup.data.model.metiers.MetierIdeo;
import fr.gouv.monprojetsup.data.model.onisep.metiers.FicheMetierIdeo;
import fr.gouv.monprojetsup.data.model.taxonomie.Taxonomie;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.gouv.monprojetsup.data.Constants.cleanup;
import static fr.gouv.monprojetsup.data.Constants.gFlCodToMpsId;
import static fr.gouv.monprojetsup.data.Constants.includeKey;

public record OnisepData(
        Taxonomie domaines,

        Taxonomie interets,

        List<@NotNull Pair<@NotNull String,@NotNull String>> edgesFormationsDomaines,

        List<@NotNull Pair<@NotNull String,@NotNull String>> edgesMetiersFormations,

        List<@NotNull FilierePsupVersIdeoData> filieresToFormationsOnisep,

        List<@NotNull MetierIdeo> metiersIdeo,

        List<@NotNull FormationIdeoDuSup> formationsIdeo,

        Set<@NotNull String> metiersIdeoDuSupKeys
        ) {



    public @NotNull List<Pair<String,String>> getEdgesSecteursMetiers() {
        return metiersIdeoDuSup().flatMap(metier -> metier.secteursActivite().stream().map(secKey -> Pair.of(secKey, metier.ideo()))).toList();
    }
    public @NotNull List<Pair<String,String>> getEdgesDomainesMetiers() {
        val idDomaines = domaines.getAtomesIds();
        return metiersIdeoDuSup().flatMap(metier -> metier.domainesWeb().stream()
                .filter(idDomaines::contains)
                .map(domKey -> Pair.of(domKey, metier.ideo()))).toList();
    }
    public @NotNull List<Pair<String,String>> getEdgesInteretsMetiers() {
        val idInterets = interets.getAtomesIds();
        return metiersIdeoDuSup().flatMap(metier -> metier.interets().stream()
                .filter(idInterets::contains)
                .map(intKey -> Pair.of(intKey, metier.ideo()))).toList();
    }

    public @NotNull List<Pair<String,String>> getEdgesAtomeToElement() {
        return Stream.concat(
                interets.getAtomesversElements(),
                domaines.getAtomesversElements()
        ).toList();
    }


    public @NotNull List<Pair<String,String>> getEdgesMetiersAssocies() {

        return metiersIdeoDuSup()
                .flatMap(metier -> metier.metiersAssocies().stream()
                        .map(m -> Pair.of(metier.ideo(), m.id())))
                .toList();
    }

    private @NotNull Stream<@NotNull MetierIdeo> metiersIdeoDuSup() {
        return metiersIdeo.stream().filter(m -> metiersIdeoDuSupKeys.contains(m.ideo()));
    }

    @NotNull
    public Map<String, String> getMetiersLabels(boolean includeKeys) {
        val result = new HashMap<String, String>();
        this.metiersIdeo().forEach(metier -> {
                    String libelle = metier.lib();
                    if (includeKeys) libelle = includeKey(metier.ideo(), libelle);
                    result.put(
                            metier.ideo(),
                            libelle);
                    metier.metiersAssocies().forEach(metierAssocie
                            -> {
                        String libelleMetierAssocie = metierAssocie.libelle();
                        if (includeKeys) libelleMetierAssocie = includeKey(metierAssocie.id(), libelleMetierAssocie);
                        result.put(
                                cleanup(metierAssocie.id()),
                                libelleMetierAssocie
                        );
                    });
                }
        );
        return result;
    }

    public @NotNull Map<String, @NotNull List<@NotNull String>> getMetiersAssociesLabels() {
        Map<String, @NotNull List<@NotNull String>> result = new HashMap<>();
        //ajout des secteurs d'activité
        metiersIdeo.forEach(fiche -> {
            String keyMetier = fiche.ideo();
            result.computeIfAbsent(keyMetier, z -> new ArrayList<>())
                    .addAll(
                            fiche.metiersAssocies().stream()
                                    .map(FicheMetierIdeo.MetierAssocie::libelle).toList()
                    );
        });
        return result;
    }

    public @NotNull Map<String,@NotNull String> getLiensCarteParcoursup() {
        return filieresToFormationsOnisep.stream()
                .map(f -> Pair.of(gFlCodToMpsId(f.gFlCod()), f.liensVersSiteAvenir()))
                .filter( p -> p.getRight() != null)
                .collect(Collectors.toMap(
                        Pair::getLeft,
                        Pair::getRight)
                );
    }


    public Map<String, String> getDomainesLabels(boolean includeKeys) {
        return domaines.getLabels(includeKeys);
    }
}

package fr.gouv.monprojetsup.data.domain.model.onisep;

import fr.gouv.monprojetsup.data.domain.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record DomainesMps(
        Map<String, Regroupement> regroupements,
        List<SousDomaineWeb> sousDomainesWeb
) {
    public DomainesMps() {
        this(
                new HashMap<>(), new ArrayList<>()
        );
    }

    public void setSousDomainesWeb(List<SousDomaineWeb> domaines) {
        this.sousDomainesWeb.clear();
        this.sousDomainesWeb.addAll(domaines);

        //to remove when data fixed
        this.regroupements.clear();
        domaines.forEach(domainePro -> {
            regroupements.put(
                    domainePro.mpsId(),
                    new Regroupement(
                            domainePro.sousDomaineOnisep(),
                            domainePro.domaineOnisep(),
                            "\uD83C\uDF31",
                            "\uD83E\uDD55")
            );
        });
    }

    public Map<String, String> getLabels() {
        Map<String, String> result = new HashMap<>();
        sousDomainesWeb.forEach(domainePro -> result.put(domainePro.mpsId(), domainePro.sousDomaineOnisep()));
        regroupements.forEach((key, value) -> result.put(key, value.label));
        return result;
    }

    public record Regroupement(
            String label,
            String groupe,

            String emojiGroupe,
            String emoji
    ) {
    }

    public void add(ThematiqueOnisep item) {
        if(item.nom != null && !item.nom.isEmpty()) {
            String kid = Constants.cleanup(item.id);
        }
    }

    public void add(String id, String label, String groupe, String emojiGroupe, String emoji) {
        regroupements.put(id, new Regroupement(label, groupe, emojiGroupe, emoji));
    }

    public record ThematiqueOnisep(
            @NotNull String id,
            String nom,
            String parent,

            String redirection
    ) {

    }
}

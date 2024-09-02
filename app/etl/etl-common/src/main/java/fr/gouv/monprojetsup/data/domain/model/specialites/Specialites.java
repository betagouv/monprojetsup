package fr.gouv.monprojetsup.data.domain.model.specialites;

import fr.gouv.monprojetsup.data.domain.model.Matiere;
import fr.gouv.monprojetsup.data.domain.model.stats.AdmisMatiereBacAnnee;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques.*;

public record Specialites(
        String source,
        Map<@NotNull Integer,@NotNull String> specialites,
        Map<@NotNull String, @NotNull Set<@NotNull String>> specialitesParBac
)
{
    public static final int SPEC_ANGLAIS_CODE_PSUP = 1076;
    public static final String SPEC_LLCER_MPS_KEY = Matiere.idPsupToIdMps(10001076);
    public static final String SPEC_LLCER_MPS_LABEL = "Langues, littératures et cultures étrangères et régionales (LLCE)";
    public static final String SPEC_AMC_MPS_KEY = Matiere.idPsupToIdMps(20001076);
    public static final String SPEC_AMC_MPS_LABEL = "Anglais Monde Contemporain (AMC)";

    public Specialites() {
        this("", new HashMap<>(), new HashMap<>());
    }

    public static Specialites build(Specialites specsWithoutBacInfo, List<AdmisMatiereBacAnnee> stats) {
        val result = new Specialites();
        result.specialites().putAll(specsWithoutBacInfo.specialites());
        result.injectPairesSpecialiteBac(stats);
        return result;
    }

    public boolean isSpecialite(int key) {
        return specialites.containsKey(key);
    }

    @NotNull
    public List<@NotNull String> getBacs(String mpsKey) {
        return specialitesParBac.entrySet().stream()
                .filter(e -> e.getValue().contains(mpsKey))
                .map(Map.Entry::getKey).toList();
    }

    private void injectPairesSpecialiteBac(@NotNull List<AdmisMatiereBacAnnee> paires) {
        specialitesParBac.clear();
        paires.forEach(pair -> {
            val specPsupId = pair.iMtCod();
            if (
                    specialites.containsKey(specPsupId)
                    && pair.annLycee() == ANNEE_LYCEE_TERMINALE || pair.annLycee() == ANNEE_LYCEE_PREMIERE
                    && pair.nb() > 50
            ) {
                val set = specialitesParBac.computeIfAbsent(pair.bac(), k -> new HashSet<>());
                if(specPsupId == SPEC_ANGLAIS_CODE_PSUP) {
                    set.add(SPEC_LLCER_MPS_KEY);
                    set.add(SPEC_AMC_MPS_KEY);
                } else {
                    val specMpsId = Matiere.idPsupToIdMps(specPsupId);
                    set.add(specMpsId);
                }
            }
        });

        specialitesParBac()
                .computeIfAbsent(TOUS_BACS_CODE_MPS, k -> new HashSet<>())
                .addAll(specialitesParBac.values().stream().flatMap(Set::stream).toList());

    }

    @NotNull
    public List<Matiere> toMatieres() {
        val result = new ArrayList<Matiere>();
        specialites().forEach((key, value) -> {
            if(key != SPEC_ANGLAIS_CODE_PSUP) {
                val keyMps = Matiere.idPsupToIdMps(key);
                result.add(new Matiere(keyMps, key, value, true, getBacs(keyMps)));
            } else {
                result.add(new Matiere(SPEC_LLCER_MPS_KEY, key, SPEC_LLCER_MPS_LABEL, true, getBacs(SPEC_LLCER_MPS_KEY)));
                result.add(new Matiere(SPEC_AMC_MPS_KEY, key, SPEC_AMC_MPS_LABEL, true, getBacs(SPEC_AMC_MPS_KEY)));
            }
        });
        return result;
    }
}

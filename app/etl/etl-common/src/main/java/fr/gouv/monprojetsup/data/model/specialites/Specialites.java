package fr.gouv.monprojetsup.data.model.specialites;

import fr.gouv.monprojetsup.data.model.Matiere;
import fr.gouv.monprojetsup.data.model.psup.SpeBac;
import fr.gouv.monprojetsup.data.model.stats.AdmisMatiereBacAnnee;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static fr.gouv.monprojetsup.data.model.Matiere.idPsupToIdMps;
import static fr.gouv.monprojetsup.data.model.Matiere.idSpeBacPsupToIdMps;
import static fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.*;

public record Specialites(
        String source,
        Map<@NotNull Integer,@NotNull String> eds,
        Map<@NotNull String,@NotNull Set<@NotNull Integer>> edsParBac,
        Map<@NotNull Integer,@NotNull String> spesBacs,
        Map<@NotNull String, @NotNull Set<@NotNull String>> specialitesParBac
)
{
    public static final int SPEC_ANGLAIS_CODE_PSUP = 1076;
    public static final String SPEC_LLCER_MPS_KEY = idPsupToIdMps(10001076);
    public static final String SPEC_LLCER_MPS_LABEL = "Langues, littératures et cultures étrangères et régionales (LLCER)";
    public static final String SPEC_AMC_MPS_KEY = idPsupToIdMps(20001076);
    public static final String SPEC_AMC_MPS_LABEL = "Anglais Monde Contemporain (AMC)";

    public Specialites() {
        this("", new HashMap<>(), new HashMap<>(), new HashMap<>(),new HashMap<>());
    }

    public static Specialites build(Specialites specsFromFile, List<AdmisMatiereBacAnnee> stats, @NotNull Collection<@NotNull SpeBac> spesBacs) {
        val result = new Specialites();
        result.eds().putAll(specsFromFile.eds());
        specsFromFile.edsParBac().forEach((bac, idPsups) ->
                result.specialitesParBac
                .computeIfAbsent(bac , z -> new HashSet<>())
                .addAll(idPsups.stream().map(Matiere::idPsupToIdMps).toList())
        );
        result.injectPairesSpecialiteBac(stats);

        spesBacs.forEach(sb -> {
            if(sb.iClCod().equals("P") || sb.iClCod().equals("PA") || sb.iClCod().startsWith("S")) {
                val specPsupId = Integer.parseInt(sb.iSpCod());
                result.spesBacs().put(specPsupId, sb.iSpLib());
                result.specialitesParBac
                        .computeIfAbsent(sb.iClCod(), z -> new HashSet<>())
                        .add(idSpeBacPsupToIdMps(specPsupId));
            }
        });

        return result;
    }

    public boolean isSpecialite(int key) {
        return eds.containsKey(key);
    }

    @NotNull
    public List<@NotNull String> getBacs(String mpsKey) {
        return specialitesParBac.entrySet().stream()
                .filter(e -> e.getValue().contains(mpsKey))
                .map(Map.Entry::getKey).toList();
    }

    private void injectPairesSpecialiteBac(@NotNull List<AdmisMatiereBacAnnee> paires) {
        if(specialitesParBac.isEmpty()) {
            paires.forEach(pair -> {
                val specPsupId = pair.iMtCod();
                if (
                        eds.containsKey(specPsupId)
                                && pair.annLycee() == ANNEE_LYCEE_TERMINALE || pair.annLycee() == ANNEE_LYCEE_PREMIERE
                                && pair.nb() > 50
                ) {
                    val set = specialitesParBac.computeIfAbsent(pair.bac(), k -> new HashSet<>());
                    if (specPsupId == SPEC_ANGLAIS_CODE_PSUP) {
                        set.add(SPEC_LLCER_MPS_KEY);
                        set.add(SPEC_AMC_MPS_KEY);
                    } else {
                        val specMpsId = idPsupToIdMps(specPsupId);
                        set.add(specMpsId);
                    }
                }
            });
        } else {
            specialitesParBac.forEach((s, set) -> {
                val stringsPs = new HashSet<>(set);
                set.clear();
                stringsPs.forEach(specPsupIdStr -> {
                    if (specPsupIdStr.equals(idPsupToIdMps(SPEC_ANGLAIS_CODE_PSUP))) {
                        set.add(SPEC_LLCER_MPS_KEY);
                        set.add(SPEC_AMC_MPS_KEY);
                    } else {
                        set.add(specPsupIdStr);
                    }
                });
            });
        }

        specialitesParBac()
                .computeIfAbsent(TOUS_BACS_CODE_MPS, k -> new HashSet<>())
                .addAll(specialitesParBac.values().stream().flatMap(Set::stream).toList());

    }

    @NotNull
    public List<Matiere> toSpecialites() {
        val result = new ArrayList<>(toEds());
        spesBacs().forEach((key, label) -> {
            String mpsKey = idSpeBacPsupToIdMps(key);
            result.add(new Matiere(mpsKey, key, label, true, getBacs(mpsKey)));
        });
        return result;
    }

    @NotNull
    public List<Matiere> toEds() {
        val result = new ArrayList<Matiere>();
        eds().forEach((key, value) -> {
            if(key != SPEC_ANGLAIS_CODE_PSUP) {
                val keyMps = idPsupToIdMps(key);
                result.add(new Matiere(keyMps, key, value, true, getBacs(keyMps)));
            } else {
                result.add(new Matiere(SPEC_LLCER_MPS_KEY, key, SPEC_LLCER_MPS_LABEL, true, getBacs(SPEC_LLCER_MPS_KEY)));
                result.add(new Matiere(SPEC_AMC_MPS_KEY, key, SPEC_AMC_MPS_LABEL, true, getBacs(SPEC_AMC_MPS_KEY)));
            }
        });
        return result;
    }
}

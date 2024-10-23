package fr.gouv.monprojetsup.data.model.specialites;

import fr.gouv.monprojetsup.data.model.Specialite;
import fr.gouv.monprojetsup.data.model.psup.SpeBac;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static fr.gouv.monprojetsup.data.model.Specialite.idPsupMatToIdMps;
import static fr.gouv.monprojetsup.data.model.Specialite.idSpeBacPsupToIdMps;
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
    public static final String SPEC_LLCER_MPS_KEY = idPsupMatToIdMps(10001076);
    public static final String SPEC_LLCER_MPS_LABEL = "Langues, littératures et cultures étrangères et régionales (LLCER)";
    public static final String SPEC_AMC_MPS_KEY = idPsupMatToIdMps(20001076);
    public static final String SPEC_AMC_MPS_LABEL = "Anglais Monde Contemporain (AMC)";

    public Specialites() {
        this("", new HashMap<>(), new HashMap<>(), new HashMap<>(),new HashMap<>());
    }

    public static Specialites build(Specialites specsFromFile, @NotNull Collection<@NotNull SpeBac> spesBacs) {
        val result = new Specialites();
        result.eds().putAll(specsFromFile.eds());
        specsFromFile.edsParBac().forEach((bac, idPsups) ->
                result.specialitesParBac
                .computeIfAbsent(bac , z -> new HashSet<>())
                .addAll(idPsups.stream().map(Specialite::idPsupMatToIdMps).toList())
        );
        result.extendSpecialiteBac();

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

    public boolean isEds(String key) {
        return eds.keySet().stream().anyMatch(e -> Specialite.idPsupMatToIdMps(e).equals(key));
    }

    @NotNull
    public List<@NotNull String> getBacs(String mpsKey) {
        return specialitesParBac.entrySet().stream()
                .filter(e -> e.getValue().contains(mpsKey))
                .map(Map.Entry::getKey).toList();
    }

    private void extendSpecialiteBac() {
            specialitesParBac.forEach((s, set) -> {
                val stringsPs = new HashSet<>(set);
                set.clear();
                stringsPs.forEach(specPsupIdStr -> {
                    if (specPsupIdStr.equals(idPsupMatToIdMps(SPEC_ANGLAIS_CODE_PSUP))) {
                        set.add(SPEC_LLCER_MPS_KEY);
                        set.add(SPEC_AMC_MPS_KEY);
                    } else {
                        set.add(specPsupIdStr);
                    }
                });
            });

        specialitesParBac()
                .computeIfAbsent(TOUS_BACS_CODE_MPS, k -> new HashSet<>())
                .addAll(specialitesParBac.values().stream().flatMap(Set::stream).toList());

    }

    @NotNull
    public List<Specialite> toSpecialites() {
        val result = new ArrayList<Specialite>();

        eds().forEach((key, value) -> {
            if(key != SPEC_ANGLAIS_CODE_PSUP) {
                val keyMps = idPsupMatToIdMps(key);
                result.add(new Specialite(keyMps, key, value, true, getBacs(keyMps)));
            } else {
                result.add(new Specialite(SPEC_LLCER_MPS_KEY, key, SPEC_LLCER_MPS_LABEL, true, getBacs(SPEC_LLCER_MPS_KEY)));
                result.add(new Specialite(SPEC_AMC_MPS_KEY, key, SPEC_AMC_MPS_LABEL, true, getBacs(SPEC_AMC_MPS_KEY)));
            }
        });

        spesBacs().forEach((key, label) -> {
            String mpsKey = idSpeBacPsupToIdMps(key);
            result.add(new Specialite(mpsKey, key, label, true, getBacs(mpsKey)));
        });

        return result;
    }

}

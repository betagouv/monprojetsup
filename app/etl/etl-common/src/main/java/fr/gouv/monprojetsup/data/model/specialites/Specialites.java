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

    public static final int SPEC_2I2D_CODE_PSUP = 1096;
    //SIN, AC, ITEC, EE
    public static final String SPEC_2I2D_SIN_MPS_KEY = "sp340";
    public static final String SPEC_2I2D_SIN_MPS_LABEL = "2I2D - SIN - Systèmes d'information et numérique - en terminale";
    public static final String SPEC_2I2D_AC_MPS_KEY = "sp337";
    public static final String SPEC_2I2D_AC_MPS_LABEL = "2I2D - AC - Architecture et construction - en terminale";
    public static final String SPEC_2I2D_ITEC_MPS_KEY = "sp339";
    public static final String SPEC_2I2D_ITEC_MPS_LABEL = "2I2D - ITEC - Innovation, Technologique et Eco-Conception - en terminale";
    public static final String SPEC_2I2D_EE_MPS_KEY = "sp338";
    public static final String SPEC_2I2D_EE_MPS_LABEL = "2I2D - EE - Energie et environnement- en terminale";

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
                    } else if (specPsupIdStr.equals(idPsupMatToIdMps(SPEC_2I2D_CODE_PSUP))) {
                        set.add(SPEC_2I2D_AC_MPS_KEY);
                        set.add(SPEC_2I2D_SIN_MPS_KEY);
                        set.add(SPEC_2I2D_ITEC_MPS_KEY);
                        set.add(SPEC_2I2D_EE_MPS_KEY);
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

        //speBacs en premier, car écrasées par certaines eds
        spesBacs().forEach((key, label) -> {
            String mpsKey = idSpeBacPsupToIdMps(key);
            result.add(new Specialite(mpsKey, key, label, true, getBacs(mpsKey)));
        });

        eds().forEach((key, value) -> {
            if(key == SPEC_ANGLAIS_CODE_PSUP) {
                result.add(new Specialite(SPEC_LLCER_MPS_KEY, key, SPEC_LLCER_MPS_LABEL, true, getBacs(SPEC_LLCER_MPS_KEY)));
                result.add(new Specialite(SPEC_AMC_MPS_KEY, key, SPEC_AMC_MPS_LABEL, true, getBacs(SPEC_AMC_MPS_KEY)));
            } else if(key == SPEC_2I2D_CODE_PSUP) {
                result.add(new Specialite(SPEC_2I2D_AC_MPS_KEY, key, SPEC_2I2D_AC_MPS_LABEL, true, getBacs(SPEC_2I2D_AC_MPS_KEY)));
                result.add(new Specialite(SPEC_2I2D_SIN_MPS_KEY, key, SPEC_2I2D_SIN_MPS_LABEL, true, getBacs(SPEC_2I2D_SIN_MPS_KEY)));
                result.add(new Specialite(SPEC_2I2D_ITEC_MPS_KEY, key, SPEC_2I2D_ITEC_MPS_LABEL, true, getBacs(SPEC_2I2D_ITEC_MPS_KEY)));
                result.add(new Specialite(SPEC_2I2D_EE_MPS_KEY, key, SPEC_2I2D_EE_MPS_LABEL, true, getBacs(SPEC_2I2D_EE_MPS_KEY)));
            } else {
                val keyMps = idPsupMatToIdMps(key);
                result.add(new Specialite(keyMps, key, value, true, getBacs(keyMps)));
            }
        });


        return result;
    }

}

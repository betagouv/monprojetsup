package fr.gouv.monprojetsup.data.etl.labels;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.model.formations.FormationIdeoDuSup;
import fr.gouv.monprojetsup.data.model.onisep.OnisepData;
import fr.gouv.monprojetsup.data.model.psup.PsupData;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.*;

public class Labels {

    @NotNull
    public static Map<String, String> getFormationsLabels(@NotNull PsupData psupData, boolean includeKeys) {

        val result = new HashMap<String, String>();
        /* les noms affichés sur la carte */
        val nomsFilieres = psupData.nomsFilieres();
        val mpsKeyToPsupKeys = psupData.getMpsKeyToPsupKeys();

        nomsFilieres.forEach((key, libelle) -> {
            libelle = getLibelleFront(key, libelle);
            if(includeKeys) {
                val psupKeys = mpsKeyToPsupKeys.getOrDefault(key, Set.of());
                if (psupKeys.size() >= 2) {
                    libelle = libelle + " " + psupKeys;
                }
                libelle = includeKey(key, libelle);
            }
            result.put(key, libelle);
        });

        /* used for LAS */
        psupData.formations().filieres.forEach((gFlCod, filiere) -> {
            String key = Constants.gFlCodToMpsId(gFlCod);
            if (!result.containsKey(key)) {
                String frLib = psupData.formations().typesMacros.get(filiere.gFrCod());
                String libelle = filiere.libelle();
                if (frLib != null && frLib.startsWith("Licence ")) {
                    libelle = libelle.replace(frLib, "Licence ");
                }
                libelle = getLibelleFront(key, libelle);
                if(includeKeys) libelle = includeKey(key, libelle);
                result.put(key, libelle);
            }
            //fallback pour les formations qui n'apparaissent qu'en apprentisssage dans psupData.formations().filieres
            if(filiere.gFlCodeFi() > 0) {
                String keyFi = Constants.gFlCodToMpsId(filiere.gFlCodeFi());
                if(!result.containsKey(keyFi)) {
                    String libelle;
                    var formationSansApprentissage = psupData.formations().formations.get(filiere.gFlCodeFi());
                    if (formationSansApprentissage != null) {
                        libelle = formationSansApprentissage.libelle;
                    } else {
                        libelle = filiere.libelle().replace((" en apprentissage"), "");
                    }
                    libelle = getLibelleFront(keyFi, libelle);
                    if (includeKeys) libelle = includeKey(keyFi, libelle);
                    result.put(keyFi, libelle);
                }
            }
        });

        psupData.formations().typesMacros.forEach((frCod, frLib) -> {
                    String key = gFrCodToMpsId(frCod);
                    String libelle = getLibelleFront(key, frLib);
                    if(includeKeys) libelle = includeKey(key, libelle);
                    result.put(key, libelle);
        });

        psupData.formations().formations.forEach((gTaCod, form) -> {
            String key = gTaCodToMpsId(gTaCod);
            String libelle = getLibelleFront(key, form.toString());
            //if(includeKeys) libelle = includeKey(key, libelle);
            result.put(key, libelle);
        });

        Map<String, String> lasToGeneric = psupData.getLasToGeneric();
        lasToGeneric.forEach((lasKey, genericKey) -> {
            if(result.containsKey(genericKey)) {
                String libelle = result.get(genericKey) + " -  Accès Santé (LAS)";
                if(includeKeys) libelle = includeKey(lasKey, libelle);
                result.put(lasKey, libelle);
            }
        });
        return result;
    }


    private static @NotNull Map<String, @NotNull String> getFormationsLabels(OnisepData oniData, boolean includeKeys) {
        return oniData.formationsIdeo().stream()
                .collect(Collectors.toMap(
                        FormationIdeoDuSup::ideo,
                        formation -> {
                            String libelle = formation.label();
                            if(includeKeys) libelle = includeKey(formation.ideo(), libelle);
                            return libelle;
                        }
                ));
    }

    @NotNull
    public static Map<String, String> getMetiersLabels(@NotNull OnisepData oniData, boolean includeKeys) {
        val result = new HashMap<String, String>();
        oniData.metiersIdeo().forEach(metier -> {
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

    public static Map<String,@NotNull String> getLabels(
            PsupData psupData,
            OnisepData oniData) {
        val result = new HashMap<String,@NotNull String>();
        result.putAll(getFormationsLabels(psupData, false));
        result.putAll(getFormationsLabels(oniData, false));
        result.putAll(getMetiersLabels(oniData, false));
        result.putAll(oniData.interets().getLabels(false));
        result.putAll(oniData.getDomainesLabels(false));
        return result;
    }

    @NotNull
    public static Map<String, String> getDebugLabels(@NotNull PsupData psupData, @NotNull OnisepData oniData) {
        val result = new HashMap<String,@NotNull String>();
        result.putAll(getFormationsLabels(psupData, true));
        result.putAll(getFormationsLabels(oniData, true));
        result.putAll(getMetiersLabels(oniData, true));
        result.putAll(oniData.interets().getLabels(true));
        result.putAll(oniData.getDomainesLabels(true));
        return result;

    }



    private static String getLibelleFront(String key, String libelle) {
        //should be somewhere else
        String newLibelle =
                libelle
                        .replace(" - Sciences, technologie, santé - ", " - ")
                        .replace("L1", "Licence")
                        .replace("CPGE", "Classes prépa (CPGE)")
                        .replace("CUPGE", "Classes prépa universitaires (CUPGE)")
                ;
        if(newLibelle.contains("EA-BAC3")) {
            newLibelle = libelle.replace("EA-BAC3", "Ecole ") + " (Bac +3)";
        }
        if(newLibelle.contains("EA-BAC5")) {
            newLibelle = libelle.replace("EA-BAC3", "Ecole ") + " (Bac +5)";
        }
        if(key.equals("fr90")) {
            newLibelle = "Sciences Po / Instituts d'études politiques (IEP)";
        }
        if(key.equals("fl230")) {
            newLibelle = "Bachelors des écoles d'ingénieurs (Bac+3)";
        }
        if(key.equals("fl210")) {
            newLibelle = "Ecoles d'ingénieurs (Bac+5)";
        }
        if(key.equals("fl241")) {
            newLibelle = "Bachelors des écoles de commerce (Bac+3)";
        }
        if(key.equals("fl240")) {
            newLibelle = "Ecoles de commerce (Bac+5)";
        }
        if(key.equals("fl242")) {
            newLibelle = "Ecoles de commerce (Bac+4)";
        }
        if(key.equals("fl270")) {
            newLibelle = "Ecoles supérieures d'art (Bac +5)";
        }
        if(key.equals("fl271")) {
            newLibelle = "Formations des écoles supérieures d'art (Bac +3)";
        }
        return newLibelle;
    }

    private Labels() {

    }

}

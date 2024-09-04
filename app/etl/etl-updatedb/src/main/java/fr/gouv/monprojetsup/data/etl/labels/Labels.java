package fr.gouv.monprojetsup.data.etl.labels;

import fr.gouv.monprojetsup.data.domain.model.onisep.OnisepData;
import fr.gouv.monprojetsup.data.domain.model.psup.PsupData;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static fr.gouv.monprojetsup.data.domain.Constants.*;

public class Labels {

    @NotNull
    public static Map<String, String> getFormationsLabels(@NotNull PsupData psupData) {

        val result = new HashMap<String, String>();
        /* les noms affichés sur la carte */
        val nomsFilieres = psupData.nomsFilieres();
        val psupKeysToMpsKeys = psupData.getPsupKeyToMpsKey();
        nomsFilieres.forEach((key, libelle) -> result.put(key, getLibelleFront(key, libelle)));

        /* used for LAS */
        psupData.formations().filieres.forEach((gFlCod, filiere) -> {
            String key = gFlCodToFrontId(gFlCod);
            if (!result.containsKey(key)) {
                String frLib = psupData.formations().typesMacros.get(filiere.gFrCod);
                String libelle = filiere.libelle;
                if (frLib != null && frLib.startsWith("Licence ")) {
                    libelle = libelle.replace(frLib, "Licence ");
                }
                result.put(key, getLibelleFront(key, libelle));
            }
        });

        //on prend seulement les types macros intéressants
        psupKeysToMpsKeys.values().forEach(key -> {
            if(key.startsWith(TYPE_FORMATION_PREFIX)) {
                Integer frCod = Integer.parseInt(key.substring(TYPE_FORMATION_PREFIX.length()));
                String frLib = psupData.formations().typesMacros.get(frCod);
                if(frLib == null) {
                    throw new RuntimeException("Failed to retrieve label of " + key);
                }
                result.put(key, getLibelleFront(key, frLib));
            }
        });

        psupData.formations().formations.forEach((gTaCod, form) -> {
            String key = gTaCodToFrontId(gTaCod);
            result.put(key, getLibelleFront(key, form.toString()));
        });

        Map<String, String> lasToGeneric = psupData.getLasToGeneric();
        lasToGeneric.forEach((lasKey, genericKey) -> {
            if(result.containsKey(genericKey)) {
                result.put(lasKey, result.get(genericKey) + " -  Accès Santé (LAS)");
            }
        });
        return result;
    }

    @NotNull
    public static Map<String, String> getMetiersLabels(@NotNull OnisepData oniData) {
        val result = new HashMap<String, String>();
        oniData.metiersIdeo().forEach(metier
                        -> {
                    result.put(
                            metier.idMps(),
                            getLibelleFront(metier.idMps(), metier.lib()));
                    metier.metiersAssocies().forEach(metierAssocie
                            -> result.put(
                            cleanup(metierAssocie.id()),
                            metierAssocie.libelle()
                    ));
                }
        );
        return result;
    }

    public static Map<String,@NotNull String> getLabels(
            PsupData psupData,
            OnisepData oniData) {
        val result = new HashMap<String,@NotNull String>();
        result.putAll(getFormationsLabels(psupData));
        result.putAll(getMetiersLabels(oniData));
        result.putAll(oniData.interets().getLabels());
        result.putAll(oniData.getDomainesLabels());
        return result;
    }

    private static String getLibelleFront(String key, String libelle) {
        //should be somewhere else
        String newLibelle = libelle.replace(" - Sciences, technologie, santé - ", " - ");
        if(key.equals("fr90")) {
            newLibelle = "Sciences Po / Instituts d'études politiques";
        }
        if(key.equals("fl230")) {
            newLibelle = "Bachelors des écoles d'ingénieurs (Bac+3)";
        }
        return newLibelle;
    }

    private Labels() {

    }

}

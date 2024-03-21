package fr.gouv.monprojetsup.data.model.eds;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public record EDSAnalysis(
        Integer gFlCod,
        String gFlLib,
        @Nullable String attendus,
        @Nullable String recoPremGeneriques,
        @Nullable String recoTermGeneriques,
        Map<Integer, String> nbAdmisEDS,

        Map<Integer, String> nbCandidatsEDS,

        Map<String, List<String>> recosScoPremSpecifiques,
        Map<String, List<String>> recosScoTermSpecifiques
) {
    public EDSAnalysis(
            int gFlCod,
            String gFlLib,
            String attendus,
            String recoPremGeneriques,
            String recoTermGeneriques) {
        this(gFlCod,
                gFlLib,
                attendus,
                recoPremGeneriques,
                recoTermGeneriques,
                new TreeMap<>(Comparator.comparing(integer -> -integer)),
                new TreeMap<>(Comparator.comparing(integer -> -integer)),
                new TreeMap<>(),
                new TreeMap<>()
        );
    }

    private @Nullable String compress(@Nullable String attendus) {
        if(attendus == null) return null;
        int i = attendus.toLowerCase().lastIndexOf("cadrage national");
        if(i > 0) {
            attendus = attendus.substring(i + 16);
            i = attendus.indexOf("<br/><br/>");
            if(i > 0) {
                attendus = attendus.substring(i + 10);
            }
        }
        return attendus;
    }
    public Attendus frontReco() {
        boolean takeRecoPrem = (recoPremGeneriques != null
                && (recoTermGeneriques == null || !recoPremGeneriques.equals(recoTermGeneriques))
                && !recoPremGeneriques.contains("Pas de données pour cette fili")
        );

        boolean takeRecoGen = !takeRecoPrem && recoTermGeneriques != null;


        boolean takeRecoTerm = !takeRecoGen &&
                recoTermGeneriques != null
                //&& recoTermGeneriques.contains("spécialité")
                //&& !recoPremGeneriques.contains("des profils vari")
                && !recoTermGeneriques.contains("Pas de données pour cette fili");

        if(attendus == null && !takeRecoGen && !takeRecoTerm)
            return null;

        return new Attendus(
                compress(attendus),
                takeRecoGen ? compress(recoTermGeneriques) : null,
                takeRecoPrem ? compress(recoPremGeneriques) : null,
                takeRecoTerm ? compress(recoTermGeneriques) : null
        );
    }
}

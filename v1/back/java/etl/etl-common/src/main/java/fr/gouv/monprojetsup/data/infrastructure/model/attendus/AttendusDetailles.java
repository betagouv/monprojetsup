package fr.gouv.monprojetsup.data.infrastructure.model.attendus;

import fr.gouv.monprojetsup.data.infrastructure.model.specialites.Specialites;
import fr.gouv.monprojetsup.data.infrastructure.psup.PsupData;
import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.infrastructure.model.stats.PsupStatistiques;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public record AttendusDetailles(
        Map<String, AttenduDetaille> analyses
) {
    public AttendusDetailles() {
        this(new TreeMap<>());
    }

    public static AttendusDetailles getAttendusDetailles(
            PsupData backPsupData,
            PsupStatistiques statistiques,
            Specialites specialites,
            boolean specifiques,
            boolean prettyPrint
    ) {

        AttendusDetailles analyses = new AttendusDetailles();

        val filActives = new HashSet<>(backPsupData.filActives());
        filActives.addAll(backPsupData.las());

        val ftf = backPsupData.getFormationToVoeux();

        filActives.forEach(gFlCod -> {
            String key = Constants.gFlCodToFrontId(gFlCod);
            String gFlLib = statistiques.nomsFilieres.get(key);
            if(gFlLib != null) {
                String ppkey = prettyPrint ? statistiques.nomsFilieres.getOrDefault(key, key) : key;
                //les nbAdmisEDS
                AttenduDetaille analysis = analyses.analyses().computeIfAbsent(ppkey, z -> new AttenduDetaille(
                        gFlCod,
                        gFlLib,
                        backPsupData.getAttendus(gFlCod),
                        backPsupData.getRecoPremGeneriques(gFlCod),
                        backPsupData.getRecoTermGeneriques(gFlCod)
                ));
                Map<Integer, Integer> statsEds = statistiques.getNbAdmisParSpec(key);
                if(statsEds != null) {
                    statsEds.forEach((iMtCod, pct) -> {
                        String name = specialites.specialites().get(iMtCod);
                        if(name != null) {
                            analysis.nbAdmisEDS().put(pct, name);
                        }
                    });
                }
                Map<Integer, Integer> statsEds2 = statistiques.getStatsSpecCandidats(key);
                if(statsEds2 != null) {
                    statsEds2.forEach((iMtCod, pct) -> {
                        String name = specialites.specialites().get(iMtCod);
                        if(name != null) {
                            analysis.nbCandidatsEDS().put(pct, name);
                        }
                    });
                }

                if(specifiques) {
                    //les messages
                    //on aggrege tous les codes jurys de la filiere
                    List<Integer> gTaCods = ftf.getOrDefault(key,List.of()).stream().map(f -> f.gTaCod).toList();
                    Set<String> juryCodes = backPsupData.getJuryCodesFromGTaCods(gTaCods);
                    analysis.recosScoPremSpecifiques().putAll(backPsupData.getRecoScoPremiere(juryCodes));
                    analysis.recosScoTermSpecifiques().putAll(backPsupData.getRecoScoTerminale(juryCodes));
                }
            }
        });
        return analyses;
    }

    public Map<String, Attendus> getSimplifiedFrontVersion() {
        return analyses.entrySet()
                .stream()
                .map(e -> Pair.of(e.getKey(),e.getValue().simplifyForFront()))
                .filter(p -> p.getRight() != null)
                .collect(Collectors.toMap(
                        Pair::getLeft,
                        Pair::getRight
                ));
    }

    public record AttenduDetaille(
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
        public AttenduDetaille(
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

        public static @Nullable String compress(@Nullable String attendus) {
            if(attendus == null) return null;
            if(attendus.contains("ne comprend pas")) return null;
            int i = attendus.toLowerCase().lastIndexOf("cadrage national");
            if(i > 0) {
                attendus = attendus.substring(i + 16);
                i = attendus.indexOf("<br/><br/>");
                if(i > 0) {
                    attendus = attendus.substring(i + 10);
                }
            }
            return attendus.replaceAll("null", " ");
        }
        public Attendus simplifyForFront() {
            boolean takeRecoPrem = (recoPremGeneriques != null
                    && (!recoPremGeneriques.equals(recoTermGeneriques))
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
}

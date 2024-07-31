package fr.gouv.monprojetsup.suggestions.infrastructure.psup;

import fr.gouv.monprojetsup.suggestions.domain.Constants;
import fr.gouv.monprojetsup.suggestions.domain.model.Voeu;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.attendus.GrilleAnalyse;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.formations.Formation;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.formations.Formations;
import fr.parcoursup.carte.algos.tools.Paire;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.suggestions.domain.Constants.*;
import static fr.parcoursup.carte.algos.Filiere.LAS_CONSTANT;


record FormationsSimilaires(
        //indexé par "fl1234""
        Map<String, FormationsSimilairesParBac> parFiliereOrigine
) {
    public FormationsSimilaires() {
        this(new HashMap<>());
    }

    public Map<String, Integer> get(String fl, int bacIndex) {
        FormationsSimilairesParBac t = parFiliereOrigine().get(fl);
        //TODO remove and use new format for sim
        if (t == null) return Collections.emptyMap();
        Map<String, Integer> sim = t.parBac().get(bacIndex);
        if (sim == null) sim = t.parBac().get(0);
        return sim;
    }

    public void add(String gFlCodOri, String gFlCodSim, int gFsSco, int iTcCod) {
        parFiliereOrigine.computeIfAbsent(gFlCodOri, z -> new FormationsSimilairesParBac())
                .parBac().computeIfAbsent(iTcCod, z -> new HashMap<>())
                .put(gFlCodSim, gFsSco);
    }

    public void normalize() {
        parFiliereOrigine.values().forEach(FormationsSimilairesParBac::normalize);
        //some cleanup, may improve performances?
        parFiliereOrigine.entrySet().removeIf(e -> e.getValue().parBac().isEmpty());
    }

    public @NotNull Map<Integer, @NotNull Map<String, @NotNull Integer>> getStats(Set<String> psupKeys) {
        Map<Integer, @NotNull Map<String, @NotNull Integer>> result = new HashMap<>();
        psupKeys.forEach(psupKey -> {
            val stats = parFiliereOrigine.get(psupKey);
            if (stats != null) {
                stats.parBac().forEach((bac, sim) -> {
                    sim.forEach((key, value) -> {
                        result.computeIfAbsent(bac, z -> new HashMap<>()).merge(key, value, Integer::sum);
                    });
                });
            }
        });
        return result;
    }
}

record DureesEtudes(
        //durees des etudes dans chaque filieres, indexées par "fl1234"
        Map<String, Integer> durees
) {
    public DureesEtudes() {
        this(new HashMap<>());
    }

    public void add(String fl, int gFrCod, String gFlLib, String gFrLib, String gFrSig) {
        int duree;
        if (gFlLib.contains("4") || gFrLib.contains("4")) {
            duree = 4;
        } else if (gFlLib.contains("3") || gFrLib.contains("3")) {
            duree = 3;
        } else if (gFlLib.contains("5") || gFrLib.contains("5")) {
            duree = 5;
        } else if (gFrCod <= 27) {
            duree = 5;//les prepas / ecoles / CMI
        } else if (gFrSig.contains("BTS")
                || gFrSig.contains("BUT")
                || gFrSig.contains("DCG")
                || gFrSig.contains("DE")
                || gFrSig.contains("3")
                || gFrSig.contains("LP")
                || gFrSig.contains("FVGL")
        ) {
            duree = 3;
        } else if (gFrSig.contains("L1")
                || gFrSig.contains("CUPGE")
                || gFrSig.contains("CPES")
                || gFrSig.contains("5")
                || gFrLib.contains("Sciences politiques")
                || gFrSig.contains("Véto")
        ) {
            duree = 5;
        } else if (gFrSig.contains("FP")
                || gFrSig.contains("DEUST")
                || gFrSig.contains("DMA")
                || gFrSig.contains("DNA")
                || gFrSig.contains("BPJEPS")
        ) {
            duree = 2;
        } else if (gFrSig.contains("FCIL") || gFrSig.contains("MC") || gFrSig.contains("CSA")) {
            duree = 1;
        } else if (gFrSig.contains("D-Etab")
                || gFrSig.contains("DU")
                || gFrSig.contains("MAN")
                || gFrSig.contains("Ann. Prép")
        ) {
            //Delicat: passerelles vers le SUP donc au moins un an mais ensuite? On va dire 1+3 car pas professionnalisant
            duree = 4;
        } else if (gFrSig.contains("DNSP")) {
            duree = 3;
        } else if (gFrSig.equals("DSP")) {
            duree = 1;
        } else if (gFlLib.contains("Bac +1")) {
            duree = 3;
        } else if (gFrCod == 69) {//Brevet de maitrise compagnons du tour de France
            duree = 2;
        } else if (gFrCod == 49) {//Certificat ede spécialisation
            duree = 1;
        } else {
            throw new RuntimeException("Unknown formation duree for g_fr_cod=" + gFrCod);
        }
        //DU FP DEUST
        durees.put(fl, duree);
    }
}




record CorrelationPaireFiliere(int fl1, int fl2, double corr) {

    public CorrelationPaireFiliere(Paire<Integer, Integer> integerIntegerPaire, Double aDouble) {
        this(integerIntegerPaire.cg1, integerIntegerPaire.cg2, aDouble);
    }
}


public record PsupData(
        /* les gflcod qui ont recruté à n-1 */
        Set<@NotNull Integer> filActives,

        @NotNull FormationsSimilaires filsim,

        @NotNull DureesEtudes duree,

        /* les formations et filières */
        @NotNull Formations formations,

        /* indexed by name, mapped to a list of object represented as a String -> String */
        @NotNull Map<String, @NotNull List<Map<String, @NotNull String>>> diversPsup,

        /* correlations between wishes, by bac type and by filieres */
        @NotNull CorrelationsParBac correlations,

        @NotNull Set<Integer> las,

        @NotNull List<@NotNull Set<@NotNull Integer>> voeuxParCandidat,

        @NotNull DescriptifsFormations descriptifsFormations
        ) {
    public static final String C_JA_COD = "C_JA_COD";
    public static final String G_TA_COD = "G_TA_COD";
    public static final String C_JUR_ADM = "C_JUR_ADM";
    public static final String A_REC_GRP = "A_REC_GRP";

    public PsupData() {
        this(
                new HashSet<>(),
                new FormationsSimilaires(),
                new DureesEtudes(),
                new Formations(),
                new HashMap<>(),
                new CorrelationsParBac(),
                new HashSet<>(),
                new ArrayList<>(),
                new DescriptifsFormations()
        );
    }

    public static Map<String, String> getGtaToLasMapping(PsupData backPsupData) {
        val grpToFormations = backPsupData.getFormationToVoeux();
        Map<String, String> result = new HashMap<>();

        Set<String> lasCodes = backPsupData.getLasMpsKeys();
        lasCodes.forEach(las -> {
            val formations = grpToFormations.getOrDefault(las, List.of());
            formations.forEach(formation -> result.put(gTaCodToFrontId(formation.gTaCod), las));
        });
        return result;

    }

    public Set<String> getJuryCodesFromGTaCods(List<Integer> gTaCods) {
        Set<String> gTaCodsSet = gTaCods.stream().map(Object::toString).collect(Collectors.toSet());
        List<Map<String, String>> data = diversPsup().getOrDefault(A_REC_GRP, new ArrayList<>());
        return data.stream()
                .filter(m -> m.containsKey(G_TA_COD) && m.containsKey(C_JA_COD))
                .filter(m -> gTaCodsSet.contains(m.get(G_TA_COD)))
                .map(m -> m.get(C_JA_COD))
                .collect(Collectors.toSet());
    }

    public Map<String, List<String>> getRecoScoPremiere(Set<String> juryCodes) {
        return getRecoScoSpecifiques(juryCodes, "PREM");
    }

    public Map<String, List<String>> getRecoScoTerminale(Set<String> juryCodes) {
        return getRecoScoSpecifiques(juryCodes, "TERM");
    }

    public @Nullable String getRecoPremGeneriques(Integer gFlCod) {
        return getRecoScoGeneriques(gFlCod, "PREM");
    }

    public @Nullable String getRecoTermGeneriques(Integer gFlCod) {
        return getRecoScoGeneriques(gFlCod, "PREM");
    }

    public @Nullable String getRecoScoGeneriques(Integer gFlCod, String key) {
        List<Map<String, String>> dataFl = diversPsup().getOrDefault("g_fil_att_con", new ArrayList<>());
        Optional<Map<String, String>> entry = dataFl.stream().filter(m -> m.getOrDefault("G_FL_COD", "").equals(gFlCod.toString())).findAny();
        return entry.map(stringStringMap -> stringStringMap.get("G_FL_CON_LYC_" + key)).orElse(null);
    }

    public @Nullable String getAttendus(Integer gFlCod) {
        List<Map<String, String>> dataFl = diversPsup().getOrDefault("g_fil_att_con", new ArrayList<>());
        Optional<Map<String, String>> entry = dataFl.stream()
                .filter(m -> m.getOrDefault("G_FL_COD", "").equals(gFlCod.toString()))
                .findAny();
        return entry.map(stringStringMap -> stringStringMap.get("G_FL_DES_ATT")).orElse(null);
    }

    private Map<String, List<String>> getRecoScoSpecifiques(Set<String> juryCodes, String key) {


        List<Map<String, String>> data = diversPsup().getOrDefault(C_JUR_ADM, new ArrayList<>());
        return data.stream()
                .filter(m -> m.containsKey(C_JA_COD) && juryCodes.contains(m.get(C_JA_COD)) && m.containsKey("C_JA_CON_LYC_" + key))
                .collect(Collectors.groupingBy(m -> m.get("C_JA_CON_LYC_" + key)))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().map(
                                mm -> C_JA_COD + "=" + mm.getOrDefault(C_JA_COD, "?")
                                        + " G_TI_COD=" + mm.getOrDefault("G_TI_COD", "?")
                                        + " C_JA_LIB=" + mm.getOrDefault("C_JA_LIB", "?")
                        ).toList()
                ));

    }


    public Map<String, Integer> getFilieresSimilaires(String fil, int bacIndex) {
        return filsim.get(fil, bacIndex);
    }

    public void addFiliereSimilaire(int gFlCodOri, int gFlCodSim, int gFsSco, int iTcCod) {
        if (!estFiliereActive(gFlCodOri) || !estFiliereActive(gFlCodSim)) return;
        filsim.add(Constants.gFlCodToFrontId(gFlCodOri), Constants.gFlCodToFrontId(gFlCodSim), gFsSco, iTcCod);
    }

    public void addDuree(int gFlCod, int gFrCod, String gFlLib, String gFrLib, String gFrSig) {
        if (!estFiliereActive(gFlCod)) return;
        duree.add(Constants.gFlCodToFrontId(gFlCod), gFrCod, gFlLib, gFrLib, gFrSig);
    }

    public int getDuree(@NotNull String mpsKey, @NotNull Map<String, String> psupKeyToMpsKey) {
        if(psupKeyToMpsKey.containsKey(mpsKey)) mpsKey = psupKeyToMpsKey.get(mpsKey);
        val result = duree.durees().get(mpsKey);
        if(result == null) throw new RuntimeException("Durée inconnue pour " + mpsKey);
        return result;
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean estFiliereActive(int fl) {
        return filActives.contains(fl);
    }

    public void cleanup() {
        filsim().normalize();
        filActives.retainAll(formations().filieres.keySet());
        //do not restrict to fil actives because we want to keep apprentissage
        formations().cleanup();


    }

    /**
     * Maps a fl** to an fl** or a fr**
     *
     * @return the correspondance
     */
    public Map<String, String> getPsupKeyToMpsKey() {
        Map<Integer, Integer> flToFl = new HashMap<>();

        //si un libellé de flAAA est un préfixe strict du libellé de flBBB alors flBBB est dans le groupe de flAAA
        addFormationsPrefixFomAnother(flToFl);


        /* regroupement explicite des filières en apprentissage */
        formations.filieres.values().stream()
                .filter(f -> f.apprentissage
                        && f.gFlCodeFi != f.gFlCod
                        && filActives.contains(f.gFlCodeFi)
                        && filActives.contains(f.gFlCod)
                )
                .forEach(f -> flToFl.put(f.gFlCod, f.gFlCodeFi));

        Map<Integer, Integer> typeFormationToCapa
                = formations().formations.values().stream()
                .collect(Collectors.groupingBy(
                                f -> formations().filieres.get(f.gFlCod).gFrCod
                        )
                )
                .entrySet().stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().stream().mapToInt(f -> f.capacite).sum()
                        )

                );
        Map<Integer, Long> typeFormationToNb
                = formations().formations.values().stream()
                .collect(Collectors.groupingBy(
                                f -> formations().filieres.get(f.gFlCod).gFrCod
                        )
                )
                .entrySet().stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().stream().mapToInt(f -> f.capacite).count()
                        )

                );

        Map<String, String> flToGrp = flToFl.entrySet().stream().collect(Collectors.toMap(
                e -> FILIERE_PREFIX + e.getKey(),
                e -> FILIERE_PREFIX + e.getValue())
        );


        //abstrcations automatiques de certains fl (filières) en fr (type de formation parcoursup) sur différents critères.
        typeFormationToCapa.forEach((fr, capa) -> {
            String name = formations.typesMacros.get(fr);
            long nb = typeFormationToNb.getOrDefault(fr, 2L);
            if (nb <= 1
                    || capa < 500
                    || name.contains("CUPGE")
                    || name.contains("FCIL")
                    || fr == 64 // FCIL
                    || name.contains("C.M.I")
                    || fr == 22 // CMI
                    || name.contains("BPJEPS")
                    || fr == 63 //année préparatoire
                    || name.contains("Année préparatoire")//année préparatoire
                    || fr == 75000 // Diplôme d'Etablissement
                    || fr == 90 //sciences po

            ) {
                String grp = Constants.TYPE_FORMATION_PREFIX + fr;
                formations.filieres.values().stream()
                        .filter(fil -> fil.gFrCod == fr)
                        .forEach(fil -> flToGrp.put(FILIERE_PREFIX + fil.gFlCod, grp));
            }
        });

        /* ajouts à la main
         "DEUST - Animation et gestion des activités physiques, sportives ou culturelles (fl828)": [
      "DEUST - Animation et gestion des activités sportives, physiques ou culturelles, parcours activités de pleine nature (fl851)(100 places)",
      "DEUST - Animation et gestion des activités sportives, physiques ou culturelles, parcours activités aquatiques (fl898)(62 places)",
      "DEUST - Animation et gestion des activités sportives, physiques ou culturelles, parcours animation (fl850)(33 places)",
      "DEUST - Animation et gestion des activités sportives, physiques ou culturelles, parcours agent de développement de club sportif (fl849)(28 places)"
    ],
         */
        String deustSport = "fl828";
        flToGrp.remove(deustSport);
        flToGrp.put("fl851", deustSport);
        flToGrp.put("fl898", deustSport);
        flToGrp.put("fl850", deustSport);
        flToGrp.put("fl849", deustSport);

        /*    "CPES - Cycle pluridisciplinaire d'Études Supérieures - Economie, société et droit (fl680003)": [
      "CPES - Cycle pluridisciplinaire d'Études Supérieures - Economie (fl680011)(10 places)",
      "CPES - Cycle pluridisciplinaire d'Études Supérieures - Sciences - Environnement - Société (fl680016)(44 places)",
      "CPES - Cycle pluridisciplinaire d'Études Supérieures - Sciences et société (fl680004)(228 places)",
      "CPES - Cycle pluridisciplinaire d'Études Supérieures - Humanités, Lettres et Sociétés (fl680006)(45 places)",
      "CPES - Cycle pluridisciplinaire d'Études Supérieures - Sciences pour l'ingénieur - Economie (fl680007)(48 places)"
    ],
    */
        String cpesEcoSocDroit = "fl680003";
        flToGrp.remove(cpesEcoSocDroit);
        flToGrp.put("fl680011", cpesEcoSocDroit);
        flToGrp.put("fl680016", cpesEcoSocDroit);
        flToGrp.put("fl680004", cpesEcoSocDroit);
        flToGrp.put("fl680006", cpesEcoSocDroit);
        flToGrp.put("fl680007", cpesEcoSocDroit);
        //fl680015 à intégrer dans fl680002

        //"CPES - Cycle pluridisciplinaire d'Études Supérieures - Mobilités douces, développement durable et ouverture internationale (fl680015)(24 places)",
        //groupées dans
        //"CPES - Cycle pluridisciplinaire d'Études Supérieures - Sciences (fl680002)"
        flToGrp.put("fl680015", "fl680002");

        flToGrp.keySet().remove("fl250001");//on laisse louvre tel quel

        /* ajout des filières dans leurs propres regroupements */
        Set<String> flAsGroups = flToGrp.values().stream().filter(key -> key.startsWith(FILIERE_PREFIX)).collect(Collectors.toSet());
        flAsGroups.forEach(s -> flToGrp.put(s, s));


        //ajout de la correspondance LAS
        //TODO: idf real licences are grouped, so should be the LAS as well
        getLasMpsKeys().forEach(mpsKey -> flToGrp.put(mpsKey,mpsKey));

        return flToGrp;
    }

    @NotNull
    public Set<@NotNull String> getLasMpsKeys() {
        val psupKeytoMpsKey = getPsupKeyToMpsKey();
        return formations.formations.values().stream()
                .filter(f -> las.contains(f.gTaCod))
                .map(f -> gFlCodToFrontId(f.gFlCod))
                .map(mpsKey -> psupKeytoMpsKey.getOrDefault(mpsKey,mpsKey))
                .collect(Collectors.toSet());
    }

    @NotNull
    public  Map<String, String> getGenericToLas() {
        return formations.formations.values().stream()
                .filter(f -> las.contains(f.gTaCod))
                .map(f -> f.gFlCod)
                .distinct()
                .collect(Collectors.toMap(
                        Constants::gFlCodToFrontId,
                        gFlCod -> gFlCodToFrontId(LAS_CONSTANT + gFlCod)
                        )
                );
    }

    @NotNull
    public  Map<String, String> getLasToGeneric() {
        return formations.formations.values().stream()
                .filter(f -> las.contains(f.gTaCod))
                .map(f -> f.gFlCod)
                .distinct()
                .collect(Collectors.toMap(
                        gFlCod -> gFlCodToFrontId(LAS_CONSTANT + gFlCod),
                                Constants::gFlCodToFrontId
                        )
                );
    }
    @NotNull
    public  Map<String, String> getLasToPass() {
        return formations.formations.values().stream()
                .filter(f -> las.contains(f.gTaCod))
                .map(f -> f.gFlCod)
                .distinct()
                .collect(Collectors.toMap(
                                gFlCod -> gFlCodToFrontId(LAS_CONSTANT + gFlCod),
                                gFlCod -> gFlCodToFrontId(PASS_FL_COD)
                        )
                );
    }



    private void addFormationsPrefixFomAnother(Map<Integer, Integer> result) {
        Map<String, Integer> inverse =
                formations.filieres.values().stream()
                        .collect(
                                Collectors.toMap(
                                        f -> f.libelle,
                                        f -> f.gFlCod
                                )
                        );
        //on constitue l a liste des libellés qui ne sont pas encore en correespondance
        LinkedList<String> names = new LinkedList<>(inverse.keySet().stream()
                .filter(name -> !result.containsKey(inverse.get(name)))
                .sorted().toList());
        //on considère les noms qui n'ont pas encore été matchés
        int i = 0;
        while (i < names.size() - 1) {
            String labeli = names.get(i);
            String labelii = names.get(i + 1);
            if (labelii.startsWith(labeli)) {
                Integer keyi = inverse.get(labeli);
                Integer keyii = inverse.get(labelii);
                result.put(keyii, keyi);
                names.remove(i + 1);
            } else {
                i++;
            }
        }
    }

    public Set<String> displayedFilieres() {
        Set<String> result = new HashSet<>();
        Map<String, String> corr = getPsupKeyToMpsKey();
        result.addAll(
                filActives.stream()
                        .filter(formations.filieres::containsKey)
                        .map(Constants::gFlCodToFrontId)
                        .filter(key -> !corr.containsKey(key) || corr.get(key).equals(key))
                        .collect(Collectors.toSet())
        );
        result.addAll(corr.values());
        return result;
    }


    public Map<String, GrilleAnalyse> getGrillesAnalyseCandidatures() {

        if (diversPsup.containsKey(A_REC_GRP) && diversPsup.containsKey(C_JUR_ADM)) {
            val arec = diversPsup.get(A_REC_GRP);
            Map<Integer, Set<Integer>> juryToFils = new HashMap<>();
            arec.forEach(m -> {
                if (m.containsKey(C_JA_COD) && m.containsKey(G_TA_COD)) {
                    int cja = Integer.parseInt(m.get(C_JA_COD));
                    int gta = Integer.parseInt(m.get(G_TA_COD));
                    val form = formations.formations.get(gta);
                    if (form != null) {
                        int fl = form.gFlCod;
                        if (form.isLAS() && fl < LAS_CONSTANT) {
                            fl += LAS_CONSTANT;
                        }
                        juryToFils.computeIfAbsent(cja, z -> new HashSet<>()).add(fl);
                    }
                }
            });

            val corr = getPsupKeyToMpsKey();

            val jurys = diversPsup.get(C_JUR_ADM);
            Map<String, Map<String, List<Integer>>> filToPctsListe = new HashMap<>();
            jurys.forEach(m -> {
                if (m.containsKey(C_JA_COD)) {
                    int cja = Integer.parseInt(m.get(C_JA_COD));
                    if (juryToFils.containsKey(cja)) {
                        for (val key : GrilleAnalyse.getLabelsMap().keySet()) {
                            String fullKey = "C_JA_CGV_" + key + "_PRC";
                            String pctStr = m.getOrDefault(fullKey, "0");
                            int pct = Integer.parseInt(pctStr);
                            juryToFils.get(cja).forEach(fl -> {
                                String flStr = Constants.gFlCodToFrontId(fl);
                                filToPctsListe.computeIfAbsent(
                                        flStr,
                                        z -> new HashMap<>()
                                ).computeIfAbsent(
                                        key,
                                        z -> new ArrayList<>()
                                ).add(pct);
                                if (corr.containsKey(flStr)) {
                                    val flgr = corr.get(flStr);
                                    filToPctsListe.computeIfAbsent(
                                            flgr,
                                            z -> new HashMap<>()
                                    ).computeIfAbsent(
                                            key,
                                            z -> new ArrayList<>()
                                    ).add(pct);
                                }

                            });
                        }
                    }
                }
            });

            Map<String, GrilleAnalyse> result = new HashMap<>();
            filToPctsListe.forEach((fl, m) -> {
                Map<String, Integer> pcts = new HashMap<>();
                m.forEach((key, list) -> {
                    if (!list.isEmpty()) {
                        int sum = list.stream().mapToInt(z -> z).sum();
                        pcts.put(key, sum / list.size());
                    }
                });
                GrilleAnalyse grille = new GrilleAnalyse(pcts);
                result.put(fl, grille);
            });
            return result;
        }
        return Map.of();
    }


    public Map<String, List<Formation>> getFormationToVoeux() {

        val groupes = getPsupKeyToMpsKey();
        Map<String, List<Formation>> result = new HashMap<>();
        formations().formations.values()
                .forEach(f -> {
                    int gFlCod = (f.isLAS() && f.gFlCod < LAS_CONSTANT) ? f.gFlCod + LAS_CONSTANT : f.gFlCod;
                    String filKey = Constants.gFlCodToFrontId(gFlCod);
                    val grKey = groupes.getOrDefault(filKey, filKey);
                    result
                            .computeIfAbsent(grKey, z -> new ArrayList<>())
                            .add(f);
                });
        return result;
    }

    @NotNull
    public Set<@NotNull String> getApprentissage() {
        val result = new HashSet<String>();
        val psupKeyToMpsKey = getPsupKeyToMpsKey();
        //apprentissage
        formations().filieres.values().forEach(filiere -> {
            if (filiere.apprentissage) {
                val key1 = gFlCodToFrontId(filiere.gFlCod);
                val key2 = gFlCodToFrontId(filiere.gFlCodeFi);
                result.addAll(List.of(
                        key1,
                        key2,
                        psupKeyToMpsKey.getOrDefault(key1,key1),
                        psupKeyToMpsKey.getOrDefault(key2,key2)
                ));
            }
        });
        return result;
    }

    @NotNull
    public  List<Voeu> getVoeux() {
        val indexedDescriptifs = descriptifsFormations.indexed();
        val psupIndextoMpsIndex = getPsupKeyToMpsKey();
        return  formations.formations.values().stream()
                .map(v -> toVoeu(v, indexedDescriptifs, psupIndextoMpsIndex))
                .toList();
    }

    private Voeu toVoeu(
            Formation f,
            Map<Integer, DescriptifVoeu> indexedDescriptifs,
            Map<String, String> psupIndextoMpsIndex
    ) {
        var mpsKey = gFlCodToFrontId(f.isLAS() ? (LAS_CONSTANT + f.gFlCod) : f.gFlCod);
        return new Voeu(
                    Constants.gTaCodToFrontId(f.gTaCod),
                    psupIndextoMpsIndex.getOrDefault(mpsKey,mpsKey),
                    f.lat,
                    f.lng,
                    Objects.requireNonNullElse(f.libelle,"Voeu " + Constants.gTaCodToFrontId(f.gTaCod)),
                    f.capacite,
                    indexedDescriptifs.get(f.gTaCod)
        );
    }

    @Nullable
    public @NotNull Map<Integer, @NotNull Map<String, @NotNull Integer>> getStatsFilSim(@NotNull Set<@NotNull String> psupKeys) {
        return  filsim.getStats(psupKeys);
    }
}

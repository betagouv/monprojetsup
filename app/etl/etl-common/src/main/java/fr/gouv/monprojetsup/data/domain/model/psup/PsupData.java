package fr.gouv.monprojetsup.data.domain.model.psup;

import fr.gouv.monprojetsup.data.carte.algos.AlgoCarteEntree;
import fr.gouv.monprojetsup.data.carte.algos.Filiere;
import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.Candidat;
import fr.gouv.monprojetsup.data.domain.model.Voeu;
import fr.gouv.monprojetsup.data.domain.model.attendus.GrilleAnalyse;
import fr.gouv.monprojetsup.data.domain.model.bacs.Bac;
import fr.gouv.monprojetsup.data.domain.model.formations.Formation;
import fr.gouv.monprojetsup.data.domain.model.formations.Formations;
import fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.domain.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.carte.algos.Filiere.LAS_CONSTANT;
import static fr.gouv.monprojetsup.data.domain.Constants.*;


public record PsupData(
        /* les gflcod qui ont recruté à n-1 */
        Set<@NotNull Integer> filActives,

        @NotNull FormationsSimilaires filsim,

        @NotNull DureesEtudes duree,

        /* les formations et filières */
        @NotNull Formations formations,

        /* indexed by name, mapped to a list of object represented as a String -> String */
        @NotNull Map<String, @NotNull List<Map<String, @NotNull String>>> diversPsup,

        @NotNull Set<Integer> las,

        @NotNull List<Candidat> voeuxParCandidat,

        @NotNull DescriptifsFormations descriptifsFormations,

        @NotNull Map<@NotNull Integer, @NotNull Filiere> filieres,//from carte, including data on LAS

//nom des filieres, par code, tels qu'affichés sur la carte
        //auxquels on rajoute les noms spécifiques LAS
        @NotNull Map<String, @NotNull String> nomsFilieres,

        @NotNull TagsSources motsCles,

        //liens onisep, par filière
        @NotNull Map<String, @NotNull String> liensOnisep,

        PsupStatistiques stats,

        @NotNull List<@NotNull Bac> bacs

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
                new HashSet<>(),
                new ArrayList<>(),
                new DescriptifsFormations(),
                new HashMap<>(),
                new TreeMap<>(),
                new TagsSources(),
                new TreeMap<>(),
                new PsupStatistiques(),
                new ArrayList<>()
        );
    }

    public List<Filiere> getFilieres() {
        return new ArrayList<>(filieres.values());
    }
    public Collection<Integer> getLasFlCodes() {
        return filieres.values().stream().filter(f -> f.isLas).map(f -> f.cle).toList();
    }

    public void ajouterLienFiliereOnisep(Integer gFlCod, String lien) {
        liensOnisep.put(FILIERE_PREFIX + gFlCod, lien);
    }

    public  Map<String, String> getGtaToLasMapping() {
        Map<String, List<Formation>> grpToFormations = getFormationToVoeux();
        Map<String, String> result = new HashMap<>();

        Set<String> lasCodes = getLasMpsKeys();
        lasCodes.forEach(las -> {
            List<Formation> formations = grpToFormations.getOrDefault(las, List.of());
            formations.forEach(formation -> result.put(gTaCodToFrontId(formation.gTaCod), las));
        });
        return result;

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

    public @NotNull Map<Integer, @NotNull Map<String, @NotNull Integer>> getStatsFilSim(@NotNull Set<@NotNull String> psupKeys) {
        return  filsim.getStats(psupKeys);
    }

    public void addFiliereSimilaire(int gFlCodOri, int gFlCodSim, int gFsSco, int iTcCod) {
        if (!estFiliereActive(gFlCodOri) || !estFiliereActive(gFlCodSim)) return;
        filsim.add(Constants.gFlCodToFrontId(gFlCodOri), Constants.gFlCodToFrontId(gFlCodSim), gFsSco, iTcCod);
    }

    public void addDuree(int gFlCod, int gFrCod, String gFlLib, String gFrLib, String gFrSig) {
        if (!estFiliereActive(gFlCod)) return;
        duree.add(Constants.gFlCodToFrontId(gFlCod), gFrCod, gFlLib, gFrLib, gFrSig);
    }

    public @Nullable Integer getDuree(
            @NotNull String mpsKey,
            @NotNull Map<String, Set<String>> mpsKeyToPsupKeys,
            @NotNull Set<String> las) {
        val psupKeys = mpsKeyToPsupKeys.getOrDefault(mpsKey, Set.of(mpsKey));
        if(las.contains(mpsKey)) return Constants.DUREE_LAS;
        val result = psupKeys.stream()
                .map(k -> duree.durees().get(k)).filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .min()
                .orElse(0);
        return result > 0 ? result : null;
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean estFiliereActive(int fl) {
        return filActives.contains(fl);
    }

    public void cleanup() {
        filsim().normalize();
        filActives.addAll(getLasFlCodes());
        filActives.retainAll(formations().filieres.keySet());
        //do not restrict to fil actives because we want to keep apprentissage
        formations().cleanup();


        Set<String> bacsActifs = new HashSet<>(stats.getBacsWithAtLeastNdAdmis(MIN_NB_ADMIS_FOR_BAC_ACTIF));
        bacsActifs.add(PsupStatistiques.TOUS_BACS_CODE);
        this.bacs.removeIf(b -> !bacsActifs.contains(b.key()));
        try {
            Serialisation.toJsonFile("bacsActifs.json", this.bacs, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.stats.restrictToBacs(bacsActifs);

        //stats.removeSmallPopulations();//not the moment to do that
        stats.cleanup();
        stats.rebuildMiddle50();
        stats.createGroupAdmisStatistique(getPsupKeyToMpsKey());
        stats.createGroupAdmisStatistique(getGtaToLasMapping());
    }

    public void injecterNomsFilieresManquants(AlgoCarteEntree carte, Set<Integer> filActives) {
        this.filieres.clear();
        this.filieres.putAll(carte.filieres);
        //liste de mots-clés filtrée (pas les villes et les chaines établissement et onisep en entier)
        carte.filieres.values().forEach(filiere -> {
            if (filActives.contains(filiere.cle)) {
                //nomsFilieres est initialisé avec les noms de filières de v_car
                // il n'y a pas tout
                // typiquement il manque les LAS qui sont récupérés via la carte
                String idfiliere = gFlCodToFrontId(filiere.cle);
                if(!this.nomsFilieres.containsKey(idfiliere)) {
                    this.nomsFilieres.put(idfiliere, filiere.libelle);
                }
            }
        });
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
                        && f.gFlCodeFi > 0//equivalent of null
                        && filActives.contains(f.gFlCod)
                        && filActives.contains(f.gFlCodeFi)
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
                    || fr == 75001 // DSP
                    || fr == 75 // DU

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
        val filieresWhichAreGroupsAsWell = flToGrp.values()
                .stream()
                .filter(key -> key.startsWith(FILIERE_PREFIX))
                .distinct()
                .toList();
        filieresWhichAreGroupsAsWell.forEach(s -> flToGrp.put(s, s));


        //ajout de la correspondance LAS
        getGenericToLas().values().forEach(mpsLasKey -> flToGrp.put(mpsLasKey,mpsLasKey));

        return flToGrp;
    }

    @NotNull
    public Map<String,Set<String>> getMpsKeyToPsupKeys() {
        val mpsKeyToPsupKeys = new HashMap<String,Set<String>>();
        getPsupKeyToMpsKey().forEach((s, s2) -> mpsKeyToPsupKeys.computeIfAbsent(s2, z -> new HashSet<String>() ).add(s));
        return mpsKeyToPsupKeys;
    }

    public void setMotsCles(TagsSources motsCles) {
        this.motsCles.set(motsCles);
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
    public  Map<String, @NotNull String> getLasToGeneric() {
        return formations.formations.values().stream()
                .filter(f -> las.contains(f.gTaCod) || f.isLAS())
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

        val aRecGrpKey = A_REC_GRP.toLowerCase();
        val cJurAdmKey = C_JUR_ADM.toLowerCase();
        if (diversPsup.containsKey(aRecGrpKey) && diversPsup.containsKey(cJurAdmKey)) {
            val arec = diversPsup.get(aRecGrpKey);
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

            val jurys = diversPsup.get(cJurAdmKey);
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
    public  List<Voeu> getVoeux(List<String> formationsMps) {
        val indexedDescriptifs = descriptifsFormations.indexed();
        val psupIndextoMpsIndex = getPsupKeyToMpsKey();
        return  formations.formations.values().stream()
                .map(v -> toVoeu(v, indexedDescriptifs, psupIndextoMpsIndex, formationsMps))
                .filter(Objects::nonNull)
                .toList();
    }

    private @Nullable Voeu toVoeu(
            Formation f,
            Map<Integer, DescriptifVoeu> indexedDescriptifs,
            Map<String, String> psupIndextoMpsIndex,
            List<String> formationsMps) {
        val candidateMpsKey = gFlCodToFrontId(f.isLAS() ? (LAS_CONSTANT + f.gFlCod) : f.gFlCod);
        var mpsKey = psupIndextoMpsIndex.get(candidateMpsKey);
        if(mpsKey == null) {
            if(formationsMps.contains(candidateMpsKey)) {
                mpsKey = candidateMpsKey;
            } else {
                return null;
                //throw new RuntimeException("No mps key for psup key " + candidateMpsKey + " gFlCod " + f.gFlCod + " gTaCod " + f.gTaCod);
            }
        }
        if(f.libelle == null) {
            throw new RuntimeException("No libelle for psup key " + candidateMpsKey + " gFlCod " + f.gFlCod + " gTaCod " + f.gTaCod);
        }
        return new Voeu(
                    Constants.gTaCodToFrontId(f.gTaCod),
                    mpsKey,
                    f.lat,
                    f.lng,
                    f.libelle,
                    f.capacite,
                    indexedDescriptifs.get(f.gTaCod),
                    f.commune,
                    f.codeCommune
        );
    }


    public void minimizeForFront() {
        voeuxParCandidat.clear();
        stats.minimize();
    }

    public void setBacs(List<Bac> bacs) {
        this.bacs.clear();
        this.bacs.addAll(bacs);
    }

}

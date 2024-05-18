package fr.gouv.monprojetsup.data.model.stats;

import fr.gouv.monprojetsup.data.update.psup.PsupData;
import fr.gouv.monprojetsup.data.update.onisep.OnisepData;
import fr.parcoursup.carte.algos.AlgoCarteEntree;
import fr.parcoursup.carte.algos.Filiere;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.*;
import static fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.*;
import static fr.gouv.monprojetsup.data.model.stats.StatistiquesAdmisParMatiere.getStatAgregee;
import static fr.parcoursup.carte.algos.Filiere.LAS_CONSTANT;


record StatistiquesAdmisParMatiere(
        //indexé par I_TC_COD
        Map<Integer, Statistique> parMatiere
) {
    public StatistiquesAdmisParMatiere() {
        this(new HashMap<>());
    }

    public static StatistiquesAdmisParMatiere getStatAgregee(List<StatistiquesAdmisParMatiere> toList) {

        return new StatistiquesAdmisParMatiere(
          toList.stream()
                  .flatMap(e -> e.parMatiere.entrySet().stream())
                  .collect(Collectors.groupingBy(Entry::getKey))
                  .entrySet().stream()
                  .collect(Collectors.toMap(
                          Entry::getKey,
                          e -> Statistique.getStatAgregee(e.getValue().stream().map(Entry::getValue).toList())
                  ))
        );
    }

    public void setStatistique(Map<Integer, int[]> integerMapMap) {
        parMatiere.clear();
        integerMapMap.forEach(
                (matiere, integerIntegerMap)
                        -> parMatiere.put(matiere, Statistique.getStatistiqueFromCompteurs(integerIntegerMap))
        );
    }

    private static final List<Integer> matieresPrincipales
            = Arrays.asList(PsupStatistiques.MOYENNE_GENERALE_CODE, MOYENNE_BAC_CODE, 1, 2, 3, 4, 5, 6);

    public void minimize() {
        parMatiere.keySet().retainAll(matieresPrincipales);
    }

    public void removeSmallPopulations() {
        this.parMatiere.values().removeIf(statistique -> statistique.nb() < MIN_POPULATION_SIZE_FOR_STATS);
    }
}

record TauxAccesParBac (
        Map<Integer,Integer> parBac
) {
    public TauxAccesParBac() { this(new TreeMap<>()); }
}

record TauxSpecialites (
    Map<Integer, Integer> parSpecialite
) {
    public TauxSpecialites() {
        this(new HashMap<>());
    }
}

record TauxSpecialitesParGroupe(
        Map<String, TauxSpecialites> parGroupe
) {
    public TauxSpecialitesParGroupe() { this(new HashMap<>()); }
}

public class PsupStatistiques implements Serializable {

    public static final int MIN_POPULATION_SIZE_FOR_STATS = 10;
    public static final int SIM_FIL_MAX_WEIGHT = 100000;

    //liens onisep, par filière
    public final Map<String, String> liensOnisep = new TreeMap<>();

    //nom des filieres, par code, tels qu'affichés sur la carte
    //auxquels on rajoute les noms spécifiques LAS
    public final Map<String, String> nomsFilieres = new TreeMap<>();

    public Map<String, String> labels = new TreeMap<>();
    public static final String TOUS_GROUPES_CODE = "";
    public static final int MOYENNE_BAC_CODE = -2;
    public static final String TOUS_BACS_CODE = "";

    public static final String BACS_GENERAL_CODE = "Générale";
    public static final int PRECISION_PERCENTILES = 40;
    public static final int MOYENNE_GENERALE_CODE = -1;

    /* taux access indexés par 1) gTaCod 2) typeBac */
    public final TauxAccesParGroupe tauxAcces = new TauxAccesParGroupe();

    /* stats sur les spécialités des admis */
    private final TauxSpecialitesParGroupe statsSpecialites = new TauxSpecialitesParGroupe();

    /* stats sur les spécialités des candidats */
    private final TauxSpecialitesParGroupe statsSpecialitesCandidats = new TauxSpecialitesParGroupe();

    private final StatistiquesAdmisParGroupe statsAdmis = new StatistiquesAdmisParGroupe();

    //par groupe puis par bac
    private final Map<String, Map<String,Integer>> admisParGroupes = new TreeMap<>();

    //par groupe puis par bac
    private final Map<String, Map<String,Integer>> candidatsParGroupes = new TreeMap<>();

    public final Map<Integer, String> matieres = new TreeMap<>();

    public final AdmisMatiereBacAnneeStats admisMatiereBacAnneeStats = new AdmisMatiereBacAnneeStats();
    private @NotNull Map<Integer, Filiere> filieres = new HashMap<>();//from carte, including data on LAS

    public static Map<String,String> getLabels(
            Map<String, String> nomsFilieres,
            PsupData psupData,
            OnisepData oniData,
            Map<String,String> flGroups,
            Map<String, String> lasCorrespondance) {
        Map<String,String> result = new HashMap<>();
        oniData.metiers().metiers().forEach((key, metier) -> result.put(cleanup(key), getLibelleFront(cleanup(key), metier.lib())));
        oniData.interets().interets().forEach((key, interet) -> result.put(cleanup(key), getLibelleFront(cleanup(key), interet)));
        oniData.thematiques().thematiques().forEach((key, thematiques) -> result.put(cleanup(key), getLibelleFront(cleanup(key), thematiques)));
        oniData.domainesWeb().domaines().forEach(domaine -> result.put(cleanup(domaine.id()), getLibelleFront(cleanup(domaine.id()), domaine.nom())));


        /* les noms affichés sur la carte */
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


        if(flGroups == null) {
            //on prend tous les types macros
            psupData.formations().typesMacros.forEach((gFrCod, libelle) -> {
                String key = TYPE_FORMATION_PREFIX + gFrCod;
                result.put(key, getLibelleFront(key, libelle));
            });
        } else {
            //on prend seulement les types macros intéressants
            flGroups.values().forEach(key -> {
                if(key.startsWith(TYPE_FORMATION_PREFIX)) {
                    Integer frCod = Integer.parseInt(key.substring(TYPE_FORMATION_PREFIX.length()));
                    String frLib = psupData.formations().typesMacros.get(frCod);
                    if(frLib == null) {
                        throw new RuntimeException("Failed to retrieve label of " + key);
                    }
                    result.put(key, getLibelleFront(key, frLib));
                }
            });
        }

        psupData.formations().formations.forEach((gTaCod, form) -> {
            String key = gTaCodToFrontId(gTaCod);
            result.put(key, getLibelleFront(key, form.toString()));
        });

        lasCorrespondance.forEach((s, s2) -> {
            if(result.containsKey(s2)) {
                result.put(s, result.get(s2) + " -  Accès Santé (LAS)");
            }
        });

        /* on intègre les associations récupérées dans les métiers associés */
        oniData.fichesMetiers().metiers().metier().stream()
                .filter(metier -> metier.metiers_associes() != null)
                .filter(metier -> metier.metiers_associes().metier_associe() != null)
                .forEach(
                metier -> metier.metiers_associes().metier_associe().forEach(metierAssocie ->
                    result.put(
                            cleanup(metierAssocie.id()),
                            metierAssocie.libelle()
                    )
                )
        );
        return result;
    }

    private static String getLibelleFront(String key, String libelle) {
        //should be somewhere else
        String newLibelle = libelle.replace(" - Sciences, technologie, santé - ", " - ");
        if(key.equals("fr90")) {
            newLibelle = "Sciences Po / Instituts d'études politiques";
        }
        if(key.equals("fl230")) {
            newLibelle = "Bachelors des écoles d'ingénieurs Ingénierie (Bac+3)";
        }
        if(DEBUG_MODE) newLibelle += " (" + key + ")";
        return newLibelle;
    }

    public void injecterNomsFilieresManquantsEtTauxAcces(AlgoCarteEntree carte, Set<Integer> filActives) {
        this.filieres = carte.filieres;
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
        //taux d'accès
        tauxAcces.add(carte.tauxAcces);
    }

    /**
     * Retourne le taux d'accès en fonction du groupe et du type de bac.
     * Le type de bac n'est que partiellement pris en compte (Gen / techno / Pro)
     * @param g groupe
     * @param bac type de bac
     * @return le taux accés ou null si l'info n'est pas disponible
     */
    @SuppressWarnings("unused")
    public @Nullable Integer getTauxAcces(String g, @Nullable String bac) {
        TauxAccesParBac ta = (tauxAcces.parGroupe() == null) ? null : tauxAcces.parGroupe().get(g);
        if (ta == null) return null;
        int result = -1;
        if (bac == null || bac.isEmpty()) result = ta.parBac().get(0);
        else if (bac.equals(BACS_GENERAL_CODE)) result = ta.parBac().get(1);
        else if (bac.startsWith("S")) result = ta.parBac().get(1);
        else if (bac.equals("P")) result = ta.parBac().get(2);
        if (result == -1) return null;
        return result;
    }

    public Integer getNbAdmis(String g, String bac) {
        Map<String, Integer> parBac = admisParGroupes.get(g);
        if(parBac == null) return null;
        return parBac.get(bac);
    }


    public @Nullable Map<Integer, Integer> getStatsSpec(String g) {
        TauxSpecialites s = statsSpecialites.parGroupe().get(g);
        if(s == null) return null;
        return s.parSpecialite();
    }
    public @Nullable Map<Integer, Integer> getStatsSpecCandidats(String g) {
        TauxSpecialites s = statsSpecialitesCandidats.parGroupe().get(g);
        if(s == null) return null;
        return s.parSpecialite();
    }


    public Double getStatsSpecialite(String grp, int specialite) {
        Map<String, Integer> parBac = admisParGroupes.get(grp);
        if(parBac == null) return null;
        int total = parBac.getOrDefault(TOUS_BACS_CODE, 0);
        if (total < 5) return null;

        Map<Integer, Integer> sts = getStatsSpec(grp);
        if(sts == null || sts.isEmpty()) return null;
        int nbWithSpec = sts.getOrDefault(specialite, 0);
        return Math.max(0.0, Math.min(1.0, 1.0 * nbWithSpec / total));
    }

    @SuppressWarnings("unused")
    public Pair<String, Statistique> getStatsMoyGen(String grp, String bac) {
        return getStatScol(grp, MOYENNE_GENERALE_CODE, bac);
    }

    /**
     * utilisé en interne server pour le calcul d'affinité
     * @param grp groupe
     * @param bac bac
     * @return pair bac, stat
     */
    public Pair<String, Statistique> getStatsBac(String grp, String bac) {
        return getStatScol(grp, MOYENNE_BAC_CODE, bac);
    }

    /**
     * utilisé en interne server pour le calcul d'affinité
     * @param groupe groupe
     * @param matiere matiere
     * @param bac bac
     * @return pair bac, stat
     */
    public Pair<String, Statistique> getStatScol(String groupe, int matiere, String bac) {
        StatistiquesAdmisParBac toto = statsAdmis.parGroupe().get(groupe);
        if (toto == null) return null;
        StatistiquesAdmisParMatiere toto2 = toto.parBac().get(bac);
        if (toto2 == null) {
            if (bac != null && bac.equals(TOUS_BACS_CODE)) return null;
            else return getStatScol(groupe, matiere, TOUS_BACS_CODE);
        }
        Statistique toto3 = toto2.parMatiere().get(matiere);
        if (toto3 == null) {
            if (bac != null && bac.equals(TOUS_BACS_CODE)) return null;
            else return getStatScol(groupe, matiere, TOUS_BACS_CODE);
        }
        return Pair.of(bac, toto3);
    }


    public void ajouterMatiere(int iMtCod, String iMtLib) {
        matieres.put(iMtCod, iMtLib);
    }

    public void ajouterLienFiliereOnisep(Integer gFlCod, String lien) {
        liensOnisep.put(FILIERE_PREFIX + gFlCod, lien);
    }
    public void setStatistiquesAdmisFromPercentileCounters(
            Map<String, Map<String, Map<Integer, int[]>>> compteurs) {
        statsAdmis.clear();
        compteurs.forEach((gr, stringMapMap) ->
                statsAdmis.parGroupe()
                        .computeIfAbsent(gr, z -> new StatistiquesAdmisParBac())
                        .set(stringMapMap)
        );

    }

    public void ajouterStatistiqueAdmis(String grp, int specialite, int nb) {
        statsSpecialites.parGroupe()
                .computeIfAbsent(grp, z -> new TauxSpecialites())
                .parSpecialite().put(specialite, nb);
    }

    public void ajouterStatistiqueCandidats(String grp, int specialite, int nb) {
        statsSpecialitesCandidats.parGroupe()
                .computeIfAbsent(grp, z -> new TauxSpecialites())
                .parSpecialite().put(specialite, nb);
    }

    public void incrementeAdmisParFiliere(String g, String bac) {
        Map<String, Integer> m = admisParGroupes.computeIfAbsent(g, z -> new HashMap<>());
        m.put(bac , 1 + m.getOrDefault(bac, 0));
    }

    public void incrementeCandidatsParFiliere(String g, String bac) {
        Map<String, Integer> m = candidatsParGroupes.computeIfAbsent(g, z -> new HashMap<>());
        m.put(bac , 1 + m.getOrDefault(bac, 0));
    }


    public void setAdmisParFiliereMatiere(String fl, int mtCod, int nb) {
        ajouterStatistiqueAdmis(fl, mtCod, nb);
    }

    public void setCandidatsParFiliereMatiere(String fl, int mtCod, int nb) {
        ajouterStatistiqueCandidats(fl, mtCod, nb);
    }

    public void minimize() {
        tauxAcces.parGroupe().clear();
        statsSpecialites.parGroupe().clear();
        statsSpecialitesCandidats.parGroupe().clear();
        admisParGroupes.clear();
        statsAdmis.minimize();
        candidatsParGroupes.clear();
    }

    private static boolean isFormationKey(String key) {
        return key.startsWith(FORMATION_PREFIX);
    }
    public void removeFormations() {
        statsAdmis.parGroupe().keySet().removeIf(PsupStatistiques::isFormationKey);
        nomsFilieres.keySet().removeIf(PsupStatistiques::isFormationKey);
        tauxAcces.parGroupe().keySet().removeIf(PsupStatistiques::isFormationKey);
        statsSpecialites.parGroupe().keySet().removeIf(PsupStatistiques::isFormationKey);
        statsSpecialitesCandidats.parGroupe().keySet().removeIf(PsupStatistiques::isFormationKey);
        admisParGroupes.keySet().removeIf(PsupStatistiques::isFormationKey);
        candidatsParGroupes.keySet().removeIf(PsupStatistiques::isFormationKey);
    }

    public static final int ANNEE_LYCEE_TERMINALE = 3;
    public static final int ANNEE_LYCEE_PREMIERE = 2;
    public void ajouterStatMatiereBac(int annLycee, int iMtCod, String iClCod, int nb) {
        if(nb > 100) {
            admisMatiereBacAnneeStats.stats().add(new AdmisMatiereBacAnnee(annLycee, iMtCod, iClCod, nb));
        }
    }

    public void removeSmallPopulations() {
        statsAdmis.parGroupe().values().forEach(
                StatistiquesAdmisParBac::removeSmallPopulations
        );
        statsAdmis.parGroupe().values().removeIf(statistiquesAdmisParBac -> statistiquesAdmisParBac.parBac().isEmpty());

    }

    public void updateLabels(OnisepData onisepData, PsupData psupData, Map<String, String> lasCorrespondance) {
        labels.putAll(
                getLabels(
                        this.nomsFilieres,
                        psupData,
                        onisepData,
                        psupData.getCorrespondances(),
                        lasCorrespondance
                )
        );
    }

    /**
     * maps las to their mother filiere
     * @return maps from las to their mother filiere
     */
    public LASCorrespondance getLASCorrespondance() {
        //new school
        return new LASCorrespondance(
                filieres.values().stream()
                        .filter(f -> f.isLas)
                        .collect(Collectors.toMap(
                                f -> gFlCodToFrontId(f.cle),
                                f -> gFlCodToFrontId(f.cleFiliere)
                        ))
        );
    }

    public void createGroupAdmisStatistique(Map<String, Set<String>> flGroups) {
        flGroups.forEach(this::createGroupAdmisStatistique);
    }

    private void createGroupAdmisStatistique(
            String newGroup,
            Set<String> groups,
            Map<String, Map<String,Integer>> parGroupeParBac
    ) {
        Map<String, Integer> statAgregee = parGroupeParBac.entrySet().stream()
                .filter(e -> groups.contains(e.getKey()))
                .map(Entry::getValue)
                .flatMap(e -> e.entrySet().stream())
                .collect(Collectors.groupingBy(Entry::getKey))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        e -> e.getValue().stream().mapToInt(Entry::getValue).sum())
                );
        parGroupeParBac.put(newGroup, statAgregee);
    }

    private void createGroupAdmisStatistique(String newGroup, Set<String> groups, TauxSpecialitesParGroupe stats) {
        Map<Integer, Integer> statAgregee = stats.parGroupe().entrySet().stream()
                .filter(e -> groups.contains(e.getKey()))
                .map(Entry::getValue)
                .flatMap(e -> e.parSpecialite().entrySet().stream())
                .collect(Collectors.groupingBy(Entry::getKey))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        e -> e.getValue().stream().mapToInt(Entry::getValue).sum())
                );
        stats.parGroupe().put(
                newGroup,
                new TauxSpecialites(statAgregee)
        );
    }
    private void createGroupAdmisStatistique(
            String newGroup,
            Set<String> groups,
            StatistiquesAdmisParGroupe statsAdmis
    ) {
        Map<String, StatistiquesAdmisParMatiere> statAgregee
                = statsAdmis.parGroupe().entrySet().stream()
                .filter(e -> groups.contains(e.getKey()))
                .flatMap(e -> e.getValue().parBac().entrySet().stream())
                .collect(Collectors.groupingBy(Entry::getKey))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        e -> getStatAgregee(e.getValue().stream().map(Entry::getValue).toList())
                ));

        statsAdmis.parGroupe().put(newGroup, new StatistiquesAdmisParBac(statAgregee));
    }


    public void createGroupAdmisStatistique(String newGroup, Set<String> groups) {

        createGroupAdmisStatistique(newGroup, groups, admisParGroupes);
        createGroupAdmisStatistique(newGroup, groups, candidatsParGroupes);
        createGroupAdmisStatistique(newGroup, groups, statsSpecialites);
        createGroupAdmisStatistique(newGroup, groups, statsSpecialitesCandidats);
        createGroupAdmisStatistique(newGroup, groups, statsAdmis);


    }

    public void rebuildMiddle50() {
        /* reconstruction des middle 50 */
        statsAdmis.parGroupe().values().stream()
                .flatMap(e -> e.parBac().values().stream())
                .flatMap(s -> s.parMatiere().entrySet().stream())
                .forEach(e -> e.setValue(e.getValue().updateMiddle50FromFrequencesCumulees()));
    }


    public record LASCorrespondance(
            Map<String,String> lasToGeneric
    ) {
        public LASCorrespondance() {
            this(new HashMap<>());
        }

        public  boolean isLas(String fl) {
            if(lasToGeneric.containsKey(fl)) return true;
            if(!fl.startsWith(FILIERE_PREFIX)) return false;
            try {
                int code = Integer.parseInt(fl.substring(2));
                return code >= LAS_CONSTANT;
            }catch (NumberFormatException e) {
                return false;
            }
        }

        public Map<String,Set<String>> getGenericToLas() {
            return lasToGeneric.entrySet().stream()
                    .collect(Collectors.groupingBy(Entry::getValue))
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            Entry::getKey,
                            e -> e.getValue().stream().map(Entry::getKey).collect(Collectors.toSet())
                    ));
        }
    }

    public List<Filiere> getFilieres() {
        return new ArrayList<>(filieres.values());
    }


    /**
     * utilisé pour l'envoi des stats aux profs
     *
     * @param g groupe
     * @param bac bac
     * @param onlyMoyGen true if only moy gen is requested
     * @return stats scolaires
     */
    public @NotNull Map<String, StatsContainers.SimpleStatGroup> getGroupStats(String g, @NotNull String bac, boolean onlyMoyGen) {
        StatistiquesAdmisParBac stats = statsAdmis.parGroupe().get(g);
        if(stats == null) return Collections.emptyMap();
        return stats.parBac().entrySet().stream().collect(Collectors.toMap(
                Entry::getKey,
                e -> new StatsContainers.SimpleStatGroup(
                        g,
                        getNbAdmis(g,e.getKey()),
                        /* todo: map taux bac int to bac string */ null,
                        (!onlyMoyGen && (e.getKey().equals(TOUS_BACS_CODE) || e.getKey().equals(bac))) ? getStatsSpec(g) : Collections.emptyMap(),
                        e.getKey().equals(TOUS_BACS_CODE) || e.getKey().equals(bac) ? getStatsScol(g, e.getKey(), onlyMoyGen) : Collections.emptyMap()
                        )
        ));
    }

    /**
     * utilisé
     * pour l'envoi des stats aux profs
     *
     * @param groupe groupe
     * @param bac bac
     * @param onlyMoyGen true if only moy gen is requested
     * @return stats scolaires
     */
    private Map<Integer, StatFront> getStatsScol(String groupe, String bac, boolean onlyMoyGen) {
        return statsCache.computeIfAbsent(Triple.of(groupe, bac, onlyMoyGen), z -> computeStatsScol(groupe, bac, onlyMoyGen));
    }

    //trading cpu for memory
    private final transient ConcurrentHashMap<Triple<String,String, Boolean>, Map<Integer, StatFront>> statsCache = new ConcurrentHashMap<>();

    private Map<Integer, StatFront> computeStatsScol(String groupe, String bac, boolean minimalForStudent) {
        StatistiquesAdmisParBac sapb = statsAdmis.parGroupe().get(groupe);
        if(sapb == null) return Collections.emptyMap();
        StatistiquesAdmisParMatiere st = sapb.parBac().get(bac);
        if(st == null) return Collections.emptyMap();
        Map<Integer, Statistique> result = st.parMatiere();
        result.entrySet().removeIf(e -> (minimalForStudent && e.getKey() != MOYENNE_GENERALE_CODE) || e.getValue().nb() < 5);
        return result.entrySet().stream().collect(Collectors.toMap(
                Entry::getKey,
                e -> StatFront.getStatistique(e.getValue().frequencesCumulees(), minimalForStudent)
        ));
    }

}

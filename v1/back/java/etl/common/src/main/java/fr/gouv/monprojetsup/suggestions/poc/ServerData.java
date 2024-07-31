package fr.gouv.monprojetsup.suggestions.poc;

import fr.gouv.monprojetsup.suggestions.domain.Constants;
import fr.gouv.monprojetsup.suggestions.infrastructure.DataSources;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.attendus.Attendus;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.attendus.AttendusDetailles;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.cities.CitiesBack;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs.DescriptifsFormations;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs.DescriptifsLoader;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.formations.FilieresToFormationsOnisep;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.formations.Formation;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.interets.Interets;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.metiers.Metiers;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.specialites.Specialites;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.specialites.SpecialitesLoader;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.Statistique;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.tags.TagsSources;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.DomainePro;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.OnisepData;
import fr.gouv.monprojetsup.suggestions.infrastructure.psup.PsupData;
import fr.gouv.monprojetsup.suggestions.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.tools.Stats;
import fr.parcoursup.carte.algos.Filiere;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.suggestions.domain.Constants.*;


@Slf4j
@Component
public class ServerData {


    private final DataSources sources;

    @Autowired
    public ServerData(DataSources sources) {
        this.sources = sources;
    }

    public static final String GROUPE_INFIX = " groupe ";
    /***************************************************************************
     ******************* DATAS ***********************************
     ****************************************************************************/

    private PsupData backPsupData;
    private PsupStatistiques statistiques;
    private OnisepData onisepData;

    /* la liste des filières groupées visibles dans le front.
    * Définit le périmètre des résultats de recherche et des recommandations remontées au front.
    * Contient toutes les filières actives + les las - les filières groupées + les groupements de filières
    * */
    public static final Set<String> filieresFront = new HashSet<>();

    public static TagsSources tagsSources;

    public static final Map<String, Set<String>> reverseFlGroups = new HashMap<>();

    public static Specialites specialites;

    //regroupement des filieres
    public static Map<String, String> flGroups = null;

    protected static final Map<String, List<Formation>> grpToFormations = new HashMap<>();
    protected static final Map<String, String> formationsToGrp = new HashMap<>();

    public static CitiesBack cities = null;
    /*
    ************************************************************************
    **************************** LOADERS ***********************************
    ************************************************************************
     */

    private static boolean dataLoaded = false;
    private static final Map<String, Integer> nbFormations = new HashMap<>();
    private static final Map<String, Integer> capacity = new HashMap<>();
    public static int p90NbFormations;
    public static int p50Capacity;
    public static int p75Capacity;
    public static int p90Capacity;

    public Map<Integer, fr.gouv.monprojetsup.suggestions.infrastructure.model.formations.Filiere> formations() {
        return backPsupData.formations().filieres;
    }

    public Collection<String> idMetiersOnisep() {
        return onisepData.metiers().metiers().keySet();
    }

    public Map<String , String > liensOnisep() {
        return statistiques.liensOnisep;
    }


    /**
     * Load data into server
     * @throws IOException unlucky
     */
    public synchronized void load() throws IOException {

        if (dataLoaded) return;

        //log.info("Loading server data...");

        BackEndData backendData = Serialisation.fromZippedJson(sources.getBackDataFilePath(), BackEndData.class);
        onisepData = backendData.onisepData();
        backPsupData = backendData.psupData();
        backPsupData.cleanup();//should be useless but it does not harm...
        statistiques = Serialisation.fromZippedJson(sources.getSourceDataFilePath(DataSources.STATS_BACK_SRC_FILENAME), PsupStatistiques.class);

        val ftf = backPsupData.getFormationToVoeux();
        grpToFormations.clear();
        grpToFormations.putAll(ftf);
        formationsToGrp.clear();
        ftf.forEach((key, value) -> value.forEach(f -> formationsToGrp.put(gTaCodToFrontId(f.gTaCod), key)));

        ServerData.cities = new CitiesBack(backendData.cities().cities());

        flGroups = new HashMap<>(backPsupData.getPsupKeyToMpsKey());
        flGroups.forEach((s, s2) -> reverseFlGroups.computeIfAbsent(s2, z -> new HashSet<>()).add(s));

        specialites = SpecialitesLoader.load(statistiques, sources);

        /* can be deleted afte rnext data update */
        statistiques.removeSmallPopulations();
        statistiques.rebuildMiddle50();
        statistiques.createGroupAdmisStatistique(flGroups);
        statistiques.createGroupAdmisStatistique(getGtaToLasMapping());

        updateLabelsForDebug();

        computeFilieresFront();

        for (String s : filieresFront) {
            if (grpToFormations.getOrDefault(s, List.of()).isEmpty())
                throw new RuntimeException("No formations for " + s);
        }

        grpToFormations.forEach((key, value) -> {
            nbFormations.put(key, value.size());
            capacity.put(key, value.stream().mapToInt(f -> f.capacite).sum());
        });
        p90NbFormations
                = Stats.p50(nbFormations.entrySet().stream().filter(e -> filieresFront.contains(e.getKey())).map(Map.Entry::getValue).toList());
        p50Capacity = Stats.p50(capacity.entrySet().stream().filter(e -> filieresFront.contains(e.getKey())).map(Map.Entry::getValue).toList());
        p75Capacity = Stats.p75(capacity.entrySet().stream().filter(e -> filieresFront.contains(e.getKey())).map(Map.Entry::getValue).toList());
        p90Capacity = Stats.p90(capacity.entrySet().stream().filter(e -> filieresFront.contains(e.getKey())).map(Map.Entry::getValue).toList());

        initTagSources();

        dataLoaded = true;
    }

    private void computeFilieresFront() {
        ServerData.filieresFront.clear();
        ServerData.filieresFront.addAll(
                computeFilieresFront(backPsupData, statistiques.getLasFlCodes())
        );
    }
    @NotNull
    public static List<@NotNull String> computeFilieresFront(
            @NotNull PsupData backendData,
            @NotNull Collection<Integer> lasFlCodes
            ) {

        val filActives  = backendData.filActives();

        filActives.addAll(lasFlCodes);

        return computeFilieresFront(
                filActives,
                backendData.getPsupKeyToMpsKey(),
                backendData.getFormationToVoeux().keySet()
        );
    }


    private static List<@NotNull String> computeFilieresFront(
            Collection<Integer> filActives,
            Map<String,String> flGroups,
            Set<String> groupesWithAtLeastOneFormation
    ) {
        val result = new ArrayList<>(filActives.stream().map(Constants::gFlCodToFrontId).toList());
        result.removeAll(flGroups.keySet());
        result.addAll(flGroups.values());
        Collections.sort(result);
        result.retainAll(groupesWithAtLeastOneFormation);
        return result;
    }

    private void initTagSources() throws IOException {
        tagsSources = TagsSources.loadTagsSources(backPsupData.getPsupKeyToMpsKey(), sources);
        val metiersVersFormations = getMetiersVersFormations();
        filieresFront.forEach(filiere -> {
            String label = getLabel(filiere);
            if(label !=  null) {
                tagsSources.add(label, filiere);
                if (label.contains("L1")) {
                    tagsSources.add("licence", filiere);
                }
                if (label.toLowerCase().contains("infirmier")) {
                    tagsSources.add("IFSI", filiere);
                }
            } else {
                //LOGGER.warning("Excluding label in search for  " + filiere + " since it has no label");
            }
            getMetiersAssocies(filiere, metiersVersFormations).forEach(
                    metier -> tagsSources.add(getLabel(metier), filiere));
        });

        tagsSources.normalize();

        Serialisation.toJsonFile("tagsSources.json", tagsSources, true);

    }

    /**
     * maps las to their mother filiere
     * @return maps from las to their mother filiere
     */
    private PsupStatistiques.LASCorrespondance getLASCorrespondance() {
        //new school
        return new PsupStatistiques.LASCorrespondance(
                statistiques.getFilieres().stream()
                        .filter(f -> f.isLas)
                        .collect(Collectors.toMap(
                                f -> gFlCodToFrontId(f.cle),
                                f -> gFlCodToFrontId(f.cleFiliere)
                        ))
        );
    }


    private Collection<String> getMetiersAssocies(String filiere, Map<String, Set<String>> metiersVersFormations) {
        Set<String> result = new HashSet<>(
                metiersVersFormations.entrySet().stream()
                        .filter(e -> e.getValue().contains(filiere))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet())
        );
        if(getLASCorrespondance().isLas(filiere)) {
            result.addAll(getMetiersAssocies(gFlCodToFrontId(PASS_FL_COD), metiersVersFormations));
        }
        if(reverseFlGroups.containsKey(filiere)) {
            result.addAll(reverseFlGroups.get(filiere)
                    .stream()
                    .filter(f -> !f.equals(filiere))
                    .flatMap(f -> getMetiersAssocies(f, metiersVersFormations).stream()).collect(Collectors.toSet()));
        }
        return result;
    }

    public @NotNull Map<Pair<String,String>,List<String>> getAttendusParGroupes() {
        Map<Pair<String,String>, List<String>> result = new HashMap<>();
        backPsupData.filActives().forEach(gFlCod -> {
            String key = Constants.gFlCodToFrontId(gFlCod);
            String label = getDebugLabel(key);
            val attendus = backPsupData.getAttendus(gFlCod);
            val recoPrem = backPsupData.getRecoPremGeneriques(gFlCod);
            val recoTerm = backPsupData.getRecoTermGeneriques(gFlCod);
            if(attendus != null) {
                val p = Pair.of("attendus", attendus);
                result.computeIfAbsent(p, z -> new ArrayList<>()).add(label);
            }
            if(recoPrem != null) {
                val p = Pair.of("recoPrem", recoPrem);
                result.computeIfAbsent(p, z -> new ArrayList<>()).add(label);
            }
            if(recoTerm != null) {
                val p = Pair.of("recoTerm", recoTerm);
                result.computeIfAbsent(p, z -> new ArrayList<>()).add(label);
            }
        });
        return result;
    }
    private void loadBackEndData() throws IOException {


    }


    private Map<String, String> getGtaToLasMapping() {
        return PsupData.getGtaToLasMapping(backPsupData);
    }

    private void updateLabelsForDebug() {
        statistiques.updateLabels(onisepData, backPsupData);
        try {
            Serialisation.toJsonFile("labelsNoDebug.json", statistiques.labels, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> suffixes =
                reverseFlGroups.entrySet().stream()
                        .filter(e -> !e.getValue().isEmpty())
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        s -> s.getValue().toString())
                                );
        suffixes.forEach((key, suffix) -> statistiques.labels.put(key, statistiques.labels.get(key) + GROUPE_INFIX + suffix) );
        try {
            Serialisation.toJsonFile("labelsDebug.json", statistiques.labels, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /* ************************************************************************
    ************************* HELPERS to get labels associated with a key ***********************
     */
    public String getLabel(String key) {
        return getLabel(key, statistiques.nomsFilieres.get(key));
    }

    public String getDebugLabel(String key) {
        return getLabel(key) + " (" + key  + ")";
    }

    public String getLabel(String key, String defaultValue) {
        return statistiques.labels.getOrDefault(key, defaultValue);
    }


    /*
    ***********************************************
    ************* STATS HELPERS *******************
    ************************************************/

    /**
     * utilisé pour l'envoi des stats aux profs
     * @param bac le bac
     * @param groups les groupes
     * @return les stats

    public static Map<String, StatsContainers.DetailFiliere> getGroupStats(@Nullable String bac, @Nullable Collection<String> groups) {
        if(groups == null) return Collections.emptyMap();
        if(bac == null) bac = PsupStatistiques.TOUS_BACS_CODE;
        @Nullable String finalBac = bac;
        return groups.stream().collect(Collectors.toMap(
                g -> g,
                g -> getDetailedGroupStats(finalBac, g)
        ));
    }*/


    /**
     * Get the list of formations for a filiere. Used by the suggestion service to compute foi (formations of interest).
     * @param fl the filiere
     * @return the list of formations
     */
    public static List<Formation> getFormationsFromFil(String fl) {
        return grpToFormations
                .getOrDefault(fl, Collections.emptyList());
    }


    public  Map<String, Set<String>> getMetiersVersFormations() throws IOException {
        return onisepData.getMetiersVersFormationsExtendedWithGroupsAndLASAndDescriptifs(
                backPsupData.getPsupKeyToMpsKey(),
                backPsupData.getGenericToLas(),
                getDescriptifs()
        );

    }

    public static Set<String> getMetiersPass(Map<String, Set<String>> metiersVersFormation) {
        String passKey =  Constants.gFlCodToFrontId(PASS_FL_COD);

        return metiersVersFormation.entrySet().stream()
                .filter(e -> e.getValue().contains( passKey))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }


    public static int getNbFormations(String fl) {
        return nbFormations.getOrDefault(fl, 0);
    }

    public static int getCapacity(String fl) {
        return capacity.getOrDefault(fl, 0);
    }

    public static Map<String, String> getFormationTofilieres() {
        return new HashMap<>(formationsToGrp);
    }

    public Map<Integer, String> getTypesMacros() {
        return backPsupData.formations().typesMacros;
    }

    public DescriptifsFormations getDescriptifs() throws IOException {
        return DescriptifsLoader.loadDescriptifs(
                onisepData,
                backPsupData.getPsupKeyToMpsKey(),
                backPsupData.getLasMpsKeys(),
                sources
        );
    }


    public Map<String, String> nomsFilieres() {
        return statistiques.nomsFilieres;
    }

    public Map<String, String> getLabels() {
        return statistiques.labels;
    }

    public List<Filiere> getFilieres() {
        return statistiques.getFilieres();
    }

    public Map<String, String> getCorrespondances() {
        return backPsupData.getPsupKeyToMpsKey();
    }

    public Set<Integer> las() {
        return backPsupData.las();
    }

    public Set<Integer> filActives() {
        return backPsupData.filActives();
    }

    public Set<String> displayedFilieres() {
        return  backPsupData.displayedFilieres();
    }

    public AttendusDetailles getAttendusDetaills() throws IOException {
        return AttendusDetailles.getAttendusDetailles(
                backPsupData,
                statistiques,
                SpecialitesLoader.load(statistiques, sources),
                true,
                true);

    }

    public Thematiques thematiques() {
        return onisepData.thematiques();
    }

    public FilieresToFormationsOnisep filieresToFormationsOnisep() {
        return onisepData.filieresToFormationsOnisep();
    }

    public Interets interets() {
        return onisepData.interets();
    }

    public Map<String, Attendus> getAttendus() throws IOException {
        val specialites = SpecialitesLoader.load(statistiques, sources);
        return Attendus.getAttendus(
                backPsupData,
                statistiques,
                specialites,
                false
        );

    }

    public Pair<String, Statistique> getStatsMoyGen(String key, String bac) {
        return  statistiques.getStatsMoyGen(key, bac);
    }

    public Pair<String, Statistique> getStatsBac(String key, String bac) {
        return statistiques.getStatsBac(key, bac);
    }

    public Map<Integer, String> typesMacros() {
        return backPsupData.formations().typesMacros;
    }

    public Metiers metiers() {
        return onisepData.metiers();
    }

    public Map<DomainePro, Set<String>> getLiensDomainesMetiers() {
        return onisepData.getDomainesVersMetiers();
    }
}


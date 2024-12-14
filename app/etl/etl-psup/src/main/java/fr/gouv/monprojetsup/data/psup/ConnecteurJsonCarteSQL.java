package fr.gouv.monprojetsup.data.psup;

import fr.gouv.monprojetsup.data.model.psup.Filiere;
import fr.gouv.monprojetsup.data.psup.exceptions.AccesDonneesException;
import fr.gouv.monprojetsup.data.psup.exceptions.AccesDonneesExceptionMessage;
import fr.gouv.monprojetsup.data.psup.exceptions.VerificationException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.psup.AlgoCarteConfig.FILIERE;
import static fr.gouv.monprojetsup.data.psup.AlgoCarteConfig.TYPE_FORMATION;
import static fr.gouv.monprojetsup.data.Constants.LAS_CONSTANT;
import static fr.gouv.monprojetsup.data.psup.Scores.cleanAndSplit;
import static fr.gouv.monprojetsup.data.psup.exceptions.AccesDonneesExceptionMessage.CONNECTEUR_ORACLE_CONNEXION_NULL;


public class ConnecteurJsonCarteSQL {

    public static final String SELECT = "SELECT ";
    public static final String FROM = " FROM ";
    private final Connection connection;

    public ConnecteurJsonCarteSQL(Connection connection) throws AccesDonneesException {
        if(connection == null) {
            throw new AccesDonneesException(CONNECTEUR_ORACLE_CONNEXION_NULL);
        }
        this.connection = connection;
    }


    /**
     * @return récupération des données de la carte
     */
    public AlgoCarteEntree recupererDonneesJSONCarte(AlgoCarteConfig config) throws AccesDonneesException {

        /* récupération de la liste des filières de Parcoursup */
        AlgoCarteEntree entree = new AlgoCarteEntree();

        try {
            recuperationDesFilieres(entree);
            recuperationDomainesOnisep(entree);
            recuperationFormationsTagguees(entree, config);
        } catch (SQLException | VerificationException ex) {
            throw new AccesDonneesException(AccesDonneesExceptionMessage.MESSAGE, ex,  String.format(ex.getMessage()), ex);
        }
        return entree;
    }

    public static final String G_TA_COD = "g_ta_cod";
    public static final String G_TF_MOT_CLE_MDR = "g_tf_mot_cle_mdr";

    public void recuperationDomainesOnisep(AlgoCarteEntree entree) throws VerificationException, SQLException {

        try (Statement stmt = connection.createStatement()) {
            LOGGER.info("Récupération des secteursActivite onisep v2");
            stmt.setFetchSize(1_000_000);
            String sql = SELECT +
                    "g_fl_cod,g_io_met,g_io_dis,g_io_sdm " +
                    FROM + " g_fil_mot_cle_oni2 oni ";

            LOGGER.info(sql);
            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int gFlCod = result.getInt(1);
                    String metiers = result.getString(2);
                    String disciplines = result.getString(3);
                    String sousdomaines = result.getString(4);
                    Filiere filiere = entree.filieres.get(gFlCod);
                    if (filiere != null) {
                        if(metiers != null) Arrays.stream(metiers.split("/")).map(String::trim).forEach(filiere.motsClesOnisepv2::add);
                        if(disciplines != null) Arrays.stream(disciplines.split(";")).map(String::trim).forEach(filiere.motsClesOnisepv2::add);
                        if(sousdomaines != null) Arrays.stream(sousdomaines.split(";")).map(String::trim).forEach(filiere.motsClesOnisepv2::add);
                    }
                }
            }
        }
    }

    public void recuperationFormationsTagguees(AlgoCarteEntree entree, AlgoCarteConfig config) throws AccesDonneesException {

        /* L'ensemble des correspondances g_ta_cod/g_tf_cod
         * (Cas particulier car une formation peut correspondre à plusieurs type
         * 	on charge donc en memoire l'ensemble des correspondance pour limiter les accés bases )*/
        Map<Integer, Set<Integer>> correspondancesTypesFormation = new HashMap<>();
        LOGGER.info("Chargement des correspondances des types de formations");
        try (Statement stmt = connection.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select g_ta_cod,g_tf_cod from mps_sp_g_tri_aff_typ_for";
            LOGGER.info(sql);
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    int gTaCod = rs.getInt("g_ta_cod");
                    int gTfCod = rs.getInt("g_tf_cod");
                    correspondancesTypesFormation
                            .computeIfAbsent(gTaCod, k -> new HashSet<>())
                            .add(gTfCod);
                }
            }
        } catch (SQLException ex) {
            throw new AccesDonneesException(AccesDonneesExceptionMessage.CONNECTEUR_DONNEES_APPEL_ORACLE_ERREUR_SQL_RECUPERATION, ex);
        }

        //Les mots clés par type de formation
        Map<Integer, String> motsClestypeFormation = new HashMap<>();
        LOGGER.info("Chargement des type de formations");
        try (Statement stmt = connection.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select g_tf_cod, g_tf_mot_cle_mdr from mps_v_typ_for";
            LOGGER.info(sql);
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    motsClestypeFormation.put(rs.getInt("g_tf_cod"), rs.getString("g_tf_mot_cle_mdr"));
                }
            }
        } catch (SQLException ex) {
            throw new AccesDonneesException(AccesDonneesExceptionMessage.CONNECTEUR_DONNEES_APPEL_ORACLE_ERREUR_SQL_RECUPERATION, ex);
        }

        try (Statement stmt = connection.createStatement()) {
            LOGGER.info("Récupération des mots-clés");
            stmt.setFetchSize(1_000_000);
            String sql = "SELECT \n" +
                    "aff.g_Ta_Cod " + G_TA_COD + ",\n" +
                    "aff.g_ta_lib_mdr " + "g_ta_lib_mdr" + ",\n" +
                    "aff.g_ea_lib_mdr " + "g_ea_lib_mdr" + ",\n" +
                    "aff.g_ta_des_deb " + "g_ta_des_deb" + ",\n" +
                    "aff.g_ta_des_ens " + "g_ta_des_ens" + ",\n" +
                    "aff.g_ta_cpl_ens_dis " + "g_ta_des_ens_dis" + ",\n" +
                    "aff.g_ta_dis_reu " + "g_ta_dis_reu" + ",\n" +
                    "aff.g_ta_flg_for_las " + "g_ta_flg_for_las" + ",\n" +
                    "fl.g_fl_Cod " + "g_fl_Cod" + ",\n" +
                    "fr.g_fr_lib " + "g_fr_lib" + ",\n" +
                    "aff.g_fr_lib_aff " + "g_fr_lib_aff" + ",\n" +
                    "aff.g_ea_lib_aff " + "g_ea_lib_aff" + ",\n" +
                    "fr.g_fr_sig_mot_rec " + "g_fr_sig_mot_rec" + ",\n" +
                    "fl.g_fl_lib " + "g_fl_lib" + ",\n" +
                    "fl.g_fl_sig " + "g_fl_sig" + ",\n" +
                    "fl.g_fl_sig_mot_rec " + "g_fl_sig_mot_rec" + " ,\n" +
                    (config.noOnisep ? "" :
                    "f_getMotsCleFiliereONISEPMDR(fl.g_fl_cod ) " + "f_get_mots_cles_filiere_onisep_mdr" + ",\n" )
                    +
                    "aff.g_fl_lib_aff " + "g_fl_lib_aff" + ",\n" +
                    "ti.g_ti_mot_cle " + "g_ti_mot_cle" + ",\n" +
                    "ta.g_ta_mot_cle " + "g_ta_mot_cle" + ",\n" +
                    "f_getLanguesL1FormationMDR(ti.g_ti_cod) " + "langues_l1_formation_mdr" + ",\n" +
                    "f_getRegimeHebergement(arec.g_ti_cod,aff.g_ta_cod) " + "f_get_regime_hebergement" + ",\n" +
                    "f_getMotsCleFormationIngMDR(arec.g_ti_cod,aff.g_ta_cod) " + "f_mots_cle_formation_ing_mdr" + ",\n" +
                    "f_getMotsClesMentions(arec.g_ti_cod,aff.g_ta_cod, null, null, null) " + "f_get_mots_cles_mentions" + ",\n" +
                    "f_getMotsCleFormationMDR(ti.g_ti_cod,ta.g_ta_cod) " + "f_get_mots_cle_formation_mdr" + ",\n" +
                    "f_getMotsCleTypeFormation(aff.g_ta_cod) " + "f_get_mots_cle_type_formation" + ",\n" +
                    "fc_g_lib_eta_cal_ref( ti.g_ti_cod, ta.g_ta_cod, 1, ei.g_ea_cod, ei.g_ea_lib, ti.g_ti_flg_lib_eta, ti.g_ti_lib_eta) " + "fc_g_lib_eta_cal_ref" + ",\n" +
                    "f_etains_different(\n" +
                    "                                fc_g_lib_eta_cal_ref( ti.g_ti_cod, ta.g_ta_cod, 1, ei.g_ea_cod, ei.g_ea_lib, ti.g_ti_flg_lib_eta, ti.g_ti_lib_eta),\n" +
                    "                                fc_g_lib_eta_cal_ref( ti.g_ti_cod, ta.g_ta_cod, 2, e.g_ea_cod, e.g_ea_lib, ta.g_ta_flg_lib_eta, ta.g_ta_lib_eta)\n" +
                    "                                ) " + "fc_g_lib_eta_cal_ref2" + ",\n" +
                    "e.g_ea_sig_mot_rec " + "g_ea_sig_mot_rec" + ",\n" +
                    "e.g_ea_com " + "g_ea_com" + ",\n" +
                    "e.g_ea_vil_pri " + "g_ea_vil_pri" + ",\n" +
                    "e.g_dp_lib " + "g_dp_lib" + ",\n" +
                    "e.g_aa_lib " + "g_aa_lib" + ",\n" +
                    "f_getInfoRegionMDR(e.g_rg_cod, e.g_rg_lib) " + "f_get_info_region_mdr" + "\n" +
                    "\n" +
                    "\n" +
                    "from\n" +
                    "mps_G_FOR fr,\n" +
                    "mps_G_FIL fl,\n" +
                    "mps_aff aff,\n" +
                    "mps_g_tri_ins ti,\n" +
                    "mps_aff ta,\n" +
                    "mps_a_rec arec,\n" +
                    "mps_v_eta ei,\n" +
                    "mps_v_eta e\n" +
                    "WHERE\n" +
                    "fr.g_fr_cod=aff.g_fr_cod_aff\n" +
                    "AND fl.g_fl_cod=aff.g_fl_cod_aff\n" +
                    "AND  ti.g_ti_cod=arec.g_ti_cod\n" +
                    "AND  arec.g_ta_cod=aff.g_ta_cod\n" +
                    "AND  ta.g_ta_cod=aff.g_ta_cod\n" +
                    "AND ei.g_ea_cod=ti.g_ea_cod_ins\n" +
                    "AND e.g_ea_cod=aff.g_ea_cod_aff"
                    ;
            LOGGER.info(sql);
            Set<String> ignoredWords = new HashSet<>(config.ignoredWords);

            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int gTaCod = result.getInt(G_TA_COD);
                    int gFlCod = result.getInt("g_fl_cod");
                    boolean isLAS = result.getBoolean("g_ta_flg_for_las");

                    FormationCarteAlgoTags f = new FormationCarteAlgoTags(gTaCod, gFlCod, config.specificTreatmentforLAS && isLAS);
                    entree.formations.put(f.gTaCod, f);

                    Filiere fil = entree.getFiliere(gFlCod, config.specificTreatmentforLAS && isLAS);

                    for (Map.Entry<String, String> entry : config.fieldToSourceType.entrySet()) {
                        String fieldName = entry.getKey();
                        String sourceType = entry.getValue();
                        if(fieldName.equals("f_get_mots_cles_filiere_onisep_mdr") && config.noOnisep) {
                            continue;
                        }
                        /* le champ G_TF_MOT_CLE_MDR est traité plus bas,
                            après la boucle
                            car plusieurs matches possibles
                             */
                        try {
                            if (!fieldName.equalsIgnoreCase(G_TF_MOT_CLE_MDR)) {
                                String chain = result.getString(fieldName);
                                if (chain != null) {
                                    f.donnees.put(fieldName, chain);
                                    if (fil != null
                                            && (
                                                    sourceType.equals(FILIERE)
                                                            || sourceType.equals(TYPE_FORMATION)
                                                            || fieldName.equalsIgnoreCase("langues_l1_formation_mdr"))
                                    ) {
                                        //au niveau filiere on ne recupere les mots cles de mentions que si
                                        //c'est un LAS
                                        if(!fieldName.equals("f_get_mots_cles_mentions") || isLAS) {
                                            boolean split =  !config.noSplitMode || fieldName.startsWith("f_") || fieldName.equals("langues_l1_formation_mdr");
                                            fil.addMotCles(cleanAndSplit(chain, ignoredWords, split));
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.severe(fieldName);//debug
                            throw e;
                        }
                    }

                    /* ajout des mots clés types formations, plusieurs hits possibles */
                    Set<String> tags =
                            correspondancesTypesFormation
                                    .getOrDefault(gTaCod, Set.of())
                                    .stream().map(motsClestypeFormation::get)
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toSet())
                            ;
                    StringBuilder mtfs = new StringBuilder();
                    for (String tag : tags) {
                            mtfs.append(tag).append(" ");
                    }
                    if (!mtfs.isEmpty()) {
                        f.donnees.put(G_TF_MOT_CLE_MDR, mtfs.toString());
                        if (fil != null) {
                            fil.addMotCles(cleanAndSplit(mtfs.toString(), ignoredWords, config.noSplitMode));
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            throw new AccesDonneesException(AccesDonneesExceptionMessage.CONNECTEUR_DONNEES_APPEL_ORACLE_ERREUR_SQL_RECUPERATION, ex);
        }
    }

    public void recuperationDesFilieres(AlgoCarteEntree entree) throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            /* récupère la liste des candidats depuis la base de données */
            LOGGER.info("Récupération des filières non las");
            stmt.setFetchSize(1_000_000);
            String sql = SELECT
                    //id du groupe de classement
                    + "fil.gFlLib, "
                    + "fil.G_FL_SIG, "
                    + "fil.gFlCod, "
                    + "NVL(fil.G_FL_COD_FI,fil.gFlCod), "
                    + "fil.G_FL_FLG_APP "
                    + " FROM mps_G_FIL fil";

            LOGGER.info(sql);

            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    String gFlLib = result.getString(1);
                    String gFlSig = result.getString(2);
                    int gFlCod = result.getInt(3);
                    int gFlCodFi = result.getInt(4);
                    boolean gFlFlgApp = result.getBoolean(5);
                    Filiere filiere = new Filiere(gFlLib, gFlSig, gFlCod, gFlCodFi, gFlFlgApp, false);
                    entree.filieres.put(gFlCod, filiere);
                }
            }
        }


        //traitement spécifique LAS
        try (Statement stmt = connection.createStatement()) {
            LOGGER.info("Récupération des filières las");
            stmt.setFetchSize(1_000_000);
            String sql = SELECT
                    //id du groupe de classement
                    + "distinct G_FL_LIB_aff, "
                    + "G_FL_COD_aff "
                    + " FROM mps_aff where NVL(G_TA_FLG_FOR_LAS,0)=1";

            LOGGER.info(sql);

            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    String gFlLib = result.getString(1);
                    int gFlCod = result.getInt(2);
                    Filiere f = entree.filieres.get(gFlCod);
                    if (f != null) {
                        int newGFlCod = LAS_CONSTANT + gFlCod;
                        Filiere filiere = new Filiere(gFlLib, f.sigle, newGFlCod, gFlCod, false, true);
                        entree.filieres.put(newGFlCod, filiere);
                    }
                }
            }
        }
    }

    private static final Logger LOGGER = Logger.getLogger(ConnecteurJsonCarteSQL.class.getSimpleName());


}

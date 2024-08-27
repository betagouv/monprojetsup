
/* Copyright 2019 © Ministère de l'Enseignement Supérieur, de la Recherche et de
l'Innovation,
    Hugo Gimbert (hugo.gimbert@enseignementsup.gouv.fr)

    This file is part of Algorithmes-de-parcoursup.

    Algorithmes-de-parcoursup is free software: you can redistribute it and/or modify
    it under the terms of the Affero GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Algorithmes-de-parcoursup is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Affero GNU General Public License for more details.

    You should have received a copy of the Affero GNU General Public License
    along with Algorithmes-de-parcoursup.  If not, see <http://www.gnu.org/licenses/>.

 */
package fr.gouv.monprojetsup.data.psup;

import com.google.gson.GsonBuilder;
import fr.gouv.monprojetsup.data.carte.algos.AlgoCarteConfig;
import fr.gouv.monprojetsup.data.carte.algos.AlgoCarteEntree;
import fr.gouv.monprojetsup.data.carte.algos.explicabilite.AlgoCarteDetails;
import fr.gouv.monprojetsup.data.carte.algos.explicabilite.reco.AlgoFiliereSimilairesDetailsCalculFormationScore;
import fr.gouv.monprojetsup.data.carte.algos.explicabilite.reco.AlgoFilieresSimilairesDetailsCalcul;
import fr.gouv.monprojetsup.data.carte.algos.explicabilite.reco.AlgoFilieresSimilairesDetailsCalculFiliere;
import fr.gouv.monprojetsup.data.carte.algos.filsim.AlgoFilieresSimilairesSortie;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.psup.SQLStringsConstants.*;
import static fr.gouv.monprojetsup.data.psup.ExportDonneesCarte.zipper;

public class ConnecteurDonneesAnnuellesCarteSQL {

    private static final String TAUX_ACCES_DETAILS_FILENAME = "taux_acces_details.json";
    private static final String RECO_DETAILS_FILENAME = "filsim_details.json";
    private final Connection connection;

    public ConnecteurDonneesAnnuellesCarteSQL(Connection connection) throws AccesDonneesException {
        if(connection == null) {
            throw new AccesDonneesException(AccesDonneesExceptionMessage.CONNECTEUR_ORACLE_CONNEXION_NULL);
        }
        this.connection = connection;
    }


    public AlgoCarteEntree recupererDonneesAnnuellesCarte() throws AccesDonneesException {

        /* récupération de la liste des filières de Parcoursup */
        AlgoCarteEntree entree = new AlgoCarteEntree();


        try {
            ConnecteurJsonCarteSQL connMotsCles = new ConnecteurJsonCarteSQL(connection);

            connMotsCles.recuperationDesFilieres(entree);
            connMotsCles.recuperationFormationsTagguees(entree,new AlgoCarteConfig());
            recuperationTypesBacs(entree);
            recuperationFilieresParCandidat(entree);
            connMotsCles.recuperationDomainesOnisep(entree);

        } catch (SQLException | VerificationException ex) {
            throw new AccesDonneesException(AccesDonneesExceptionMessage.MESSAGE, ex, String.format(ex.getMessage()), ex);
        }
        return entree;
    }


    public static final String TABLE_FILIERES_SIMILAIRE = "G_FIL_SIM";
    static final String TABLE_MOTS_CLES = "G_FIL_KEYS";
    public static final String TABLE_STATS_TAUX_ACCES = "G_REC_TAU_ACC";
    private static final int MIN_NB_CANDIDATS_POUR_TAUX_ACCES = 5;

    /** deprecated
    public void exporterStatistiquesBacheliers() throws SQLException {
        LOGGER.info("Export des statistiques des admissions bacheliers l'an dernier");
        try (Statement ps = connection.createStatement()) {
            ps.execute(TRUNCATE_TABLE + TABLE_STATS_BAC);
        }

        try (Statement ps = connection.createStatement()) {
            String sql =
                    INSERT_INTO + TABLE_STATS_BAC
                            + " (g_ta_cod,G_RB_GEN,G_RB_TEC,G_RB_PRO,G_RB_TOT) "
                            + "(SELECT adm.g_ta_cod g_ta_cod,"
                            + "count(case when (tbac.I_TC_COD = '1') then 1 end) as G_RB_GEN,"
                            + "count(case when (tbac.I_TC_COD = '2') then 1 end) as G_RB_TEC,"
                            + "count(case when (tbac.I_TC_COD = '3') then 1 end) as G_RB_PRO,"
                            + "count(*) as G_RB_TOT "
                            + "FROM A_ADM_ARCH adm,SP_G_TRI_AFF_ARCH ta,G_CAN_ARCH can,I_CLA_ARCH tbac,A_SIT_VOE_ARCH asv, G_TRI_AFF aff"
                            + " WHERE  "
                            + "adm.G_CN_COD=can.G_CN_COD "
                            + "AND can.I_CL_COD_BAC=tbac.I_CL_COD "
                            + "AND adm.A_SV_COD=asv.A_SV_COD "
                            + "AND asv.A_SV_FLG_AFF=1  "
                            + "AND ta.g_ta_cod=adm.g_ta_cod "
                            + "AND adm.g_ta_cod=aff.g_ta_cod "
                            + "GROUP BY adm.g_ta_cod,adm.G_TI_COD, ta.G_FL_COD_AFF)";
            LOGGER.info(sql);
            ps.execute(sql);
        }
    }
    */

    public boolean checkTableHasColumn(String tableName, String columnName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String sql = "select * from all_tab_cols  where table_name = '" + tableName.toUpperCase() + "' "
                    + " AND column_name = '" + columnName.toUpperCase() + "'";
            try (ResultSet result = stmt.executeQuery(sql)) {
                return result.next();
            }
        }
    }

    public void stopExecutionIfTableHasNotColumn(String tableName, String columnName, String format) throws SQLException {
        if(!checkTableHasColumn(tableName, columnName)) {
            try {
                ajouterColonne(tableName, columnName, format);
            } catch (Exception ignored) {
                String message = "L'export nest pas possible car la table " + tableName + " ne contient pas la colomne " + columnName + " au format " + format
                        + " et la création de cette colonne n'est pas possible";
                LOGGER.severe(message);
                throw new RuntimeException(message);
            }
        }
    }

    public void stopExecutionIfTableHasColumn(String tableName, String columnName) throws SQLException {
        if(checkTableHasColumn(tableName, columnName)) {
            try {
                supprimerColonne(tableName, columnName);
            } catch (Exception ignored) {
                String message = "L'export nest pas possible car la table '" + tableName + "' contient la colonne désormais inutile '" + columnName
                        + "' et la suppression de cette colonne n'est pas possible";
                LOGGER.severe(message);
                throw new RuntimeException(message);
            }
        }
    }

    public void ajouterColonne(String tableName, String columnName, String format) throws SQLException {
        try (Statement ps = connection.createStatement()) {
            ps.execute("ALTER TABLE " + tableName + " ADD " + columnName + " " + format);
            LOGGER.info("Une modification de la structure de la table " + tableName + " a été nécessaire: ajout de la colonne  " + columnName + " au format " + format);
        }
    }

    public void supprimerColonne(String tableName, String columnName) throws SQLException {
        try (Statement ps = connection.createStatement()) {
            ps.execute("ALTER TABLE " + tableName + " DELETE " + columnName);
            LOGGER.info("Une modification de la structure de la table " + tableName + " a été nécessaire: suppression de la colonne  " + columnName);
        }
    }

    public void verifierPossibiliteExport() throws SQLException {


        stopExecutionIfTableHasNotColumn(TABLE_STATS_TAUX_ACCES, "G_RT_TAU_ACC", "NUMBER(3,0)");
        stopExecutionIfTableHasNotColumn(TABLE_STATS_TAUX_ACCES, "G_RT_TAU_ACC_GEN", "NUMBER(3,0)");
        stopExecutionIfTableHasNotColumn(TABLE_STATS_TAUX_ACCES, "G_RT_TAU_ACC_PRO", "NUMBER(3,0)");
        stopExecutionIfTableHasNotColumn(TABLE_STATS_TAUX_ACCES, "G_RT_TAU_ACC_TEC", "NUMBER(3,0)");
        stopExecutionIfTableHasColumn(TABLE_STATS_TAUX_ACCES, "TAUX_ACCES");

        stopExecutionIfTableHasNotColumn(TABLE_FILIERES_SIMILAIRE, "G_FS_COR", "NUMBER(10,0)");
        stopExecutionIfTableHasNotColumn(TABLE_FILIERES_SIMILAIRE, "I_TC_COD", "NUMBER(3,0)");
        stopExecutionIfTableHasNotColumn(TABLE_FILIERES_SIMILAIRE, "G_FS_SCO", "NUMBER(10,0)");
        stopExecutionIfTableHasNotColumn(TABLE_FILIERES_SIMILAIRE, "G_FS_PRO_SEM", "NUMBER(10,0)");
        stopExecutionIfTableHasColumn(TABLE_FILIERES_SIMILAIRE, "G_FS_PCT_VOE_COM");

    }

    public void exporterTauxAcces() throws SQLException, IOException, AccesDonneesException {

        LOGGER.info("Export des taux d'accès de l'an dernier");

        AlgoCarteDetails details = new AlgoCarteDetails();
        ConnecteurDetailsJsonCarteSQL conn = new ConnecteurDetailsJsonCarteSQL(connection);
        conn.recupererDetailsTauxAcces(details, MIN_NB_CANDIDATS_POUR_TAUX_ACCES);

        LOGGER.info("Export des résultats sous forme de fichier Json " + TAUX_ACCES_DETAILS_FILENAME);
        try(FileWriter fw = new FileWriter(TAUX_ACCES_DETAILS_FILENAME)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(details, fw);
        }
        zipper(TAUX_ACCES_DETAILS_FILENAME);

        LOGGER.info("Export en BDD");
        LOGGER.info("Effacement de la table " + TABLE_STATS_TAUX_ACCES);
        try (Statement ps = connection.createStatement()) {
            ps.execute(TRUNCATE_TABLE + TABLE_STATS_TAUX_ACCES);
        }


        LOGGER.info("Insertion dans la table " + TABLE_STATS_TAUX_ACCES);
        Map<Integer, Map<Integer,Integer>> tauxAcces = details.parBac.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getTauxAcces()
                ));
        for(int tbac = 0; tbac <= 3; tbac++) {
            tauxAcces.computeIfAbsent(tbac, z -> new HashMap<>());
        }


        String sql = INSERT_INTO + TABLE_STATS_TAUX_ACCES
                + " (g_ta_cod,g_rt_tau_acc, g_rt_tau_acc_gen, g_rt_tau_acc_tec, g_rt_tau_acc_pro) VALUES (?,?,?,?,?) ";

        LOGGER.info(sql);
        Set<Integer> allGtas = new HashSet<>();
        for(int tBac = 0; tBac <= 3; tBac ++) {
            allGtas.addAll(tauxAcces.computeIfAbsent(tBac, z -> new HashMap<>()).keySet());
        }
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int gTaCod : allGtas) {
                ps.setInt(1, gTaCod);
                ps.setInt(2, tauxAcces.get(0).getOrDefault(gTaCod, -1));
                ps.setInt(3, tauxAcces.get(1).getOrDefault(gTaCod, -1));
                ps.setInt(4, tauxAcces.get(2).getOrDefault(gTaCod, -1));
                ps.setInt(5, tauxAcces.get(3).getOrDefault(gTaCod, -1));
                ps.addBatch();
            }
            ps.executeBatch();
        }

    }

    public static String getSQLDernierAppeleEnPP() {
        return
                "SELECT adm.g_ta_cod g_ta_cod," +
                        "  cla.C_GP_COD c_gp_cod," +
                        "  MAX(cla.C_CG_ORD_APP) rang  "
                        + "FROM                "
                        + "A_ADM_ARCH adm,         "
                        + "C_CAN_GRP_ARCH cla    "
                        + "WHERE               "
                        + "adm.G_CN_COD=cla.G_CN_COD    "
                        + "AND adm.A_TA_COD = 1           "
                        + "AND adm.C_GP_COD = cla.C_GP_COD    "
                        + "AND cla.C_CG_ORD_APP is not null GROUP BY          "
                        + "adm.g_ta_cod,   cla.c_gp_cod";
    }

    public static String getSQLNbCanAuDessousBarre(int typeBac) {

        return
                "SELECT      " +
                        "voe.g_ta_cod,     "
                        + "cla.c_gp_cod,     "
                        + "COuNT(DISTINCT voe.G_CN_COD) nb,"
                        + "COuNT(DISTINCT (case when cla.C_CG_ORD_APP is not null and cla.C_CG_ORD_APP <= rang_der_appele.rang then voe.G_CN_COD end)) nb_admissibles," +
                        "  rang_der_appele.rang    "
                        + "FROM        "
                        + "G_CAN_ARCH can, "
                        + "A_VOE_ARCH voe, "
                        + "G_PAY_ARCH pays,"
                        + "C_CAN_GRP_ARCH cla,   "
                        + "rang_der_appele,  "
                        + (typeBac > 0 ? "I_CLA_ARCH tbac, " : "")
                        + "I_INS_ARCH ins, "
                        + "A_REC_GRP_ARCH arg "
                        + WHERE
                        + "can.g_cn_cod=voe.g_cn_cod   "
                        + (typeBac > 0 ? "AND can.I_CL_COD_BAC=tbac.I_CL_COD " : "")
                        + (typeBac > 0 ? "AND tbac.I_TC_COD = ? " : "")
                        + "AND voe.a_sv_cod > -90   "
                        + " AND voe.G_CN_COD=ins.G_CN_COD  "
                        + " AND voe.G_TA_COD=arg.G_TA_COD  "
                        + " AND ins.G_TI_COD=arg.G_TI_COD  "
                        + " AND NVL(ins.I_IS_VAL,0)=1  "
                        + "AND voe.G_CN_COD=cla.G_CN_COD  "
                        + "AND cla.C_GP_COD=rang_der_appele.C_GP_COD   "
                        + "AND voe.g_ta_cod = rang_der_appele.g_ta_cod  "
                        + "AND can.g_py_cod_nat = pays.g_py_cod  "
                        + "AND (can.g_po_cod in (1,11) or NVL(pays.g_py_flg_cee,0)=1) " //scolarisé en france ou nationalité européenne
                        + "GROUP BY          "
                        + "voe.g_ta_cod, cla.c_gp_cod, rang_der_appele.rang";

    }

    public void exporterFilieresSimilaires(AlgoFilieresSimilairesSortie sortie) throws SQLException, IOException {

        LOGGER.info("Export des résultats sous forme de fichier Json " + RECO_DETAILS_FILENAME);
        try (FileWriter fw = new FileWriter(RECO_DETAILS_FILENAME)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(sortie.recommandations, fw);
        }
        zipper(RECO_DETAILS_FILENAME);


        /*
            g_fil_sim  :  table des filères similaires
             */
        LOGGER.info("Vidage de la table " + TABLE_FILIERES_SIMILAIRE + " avant export");
        try (Statement ps = connection.createStatement()) {
            ps.execute(TRUNCATE_TABLE + TABLE_FILIERES_SIMILAIRE);
        }

        for (int typeBac = 0; typeBac <= 3; typeBac++) {
            AlgoFilieresSimilairesDetailsCalcul recos = sortie.recommandations.getOrDefault(typeBac, new AlgoFilieresSimilairesDetailsCalcul());

            LOGGER.log(Level.INFO, "Type Bac " + typeBac + ",export de {0} recommandations dans la table " + TABLE_FILIERES_SIMILAIRE,
                    recos.resultats.size());
            try (PreparedStatement ps = connection.prepareStatement(
                    INSERT_INTO + TABLE_FILIERES_SIMILAIRE
                            + " (G_FL_COD_ORI,G_FL_COD_SIM,G_FS_SCO,G_FS_PRO_SEM, G_FS_COR, I_TC_COD)"
                            + " VALUES (?,?,?,?,?,?)")) {

                int maxPRoSem = Integer.MIN_VALUE;
                int maxScore = Integer.MIN_VALUE;
                int maxCorr = Integer.MIN_VALUE;
                for (AlgoFilieresSimilairesDetailsCalculFiliere entry : recos.resultats.values()) {
                    Set<Integer> cc = entry.recoOnisepCorrelation.stream().map(r -> r.filIdx).collect(Collectors.toSet());
                    if (cc.size() == entry.recoOnisepCorrelation.size()) {
                        entry.recoOnisepCorrelation.sort(Comparator.comparingInt(o -> -o.score));
                        for (AlgoFiliereSimilairesDetailsCalculFormationScore r : entry.recoOnisepCorrelation) {
                            int proSem = entry.getProximiteSemantique(r.filIdx);
                            int score = r.score;
                            int corr = entry.getCorrelation(r.filIdx);
                            maxPRoSem = Math.max(maxPRoSem, proSem);
                            maxScore = Math.max(maxScore, score);
                            maxCorr = Math.max(maxCorr, corr);
                            ps.setInt(1, entry.codeFiliere);
                            ps.setInt(2, r.filIdx);
                            ps.setInt(3, score);
                            ps.setInt(4, proSem);
                            ps.setInt(5, corr);
                            ps.setInt(6, typeBac);
                            ps.addBatch();
                        }
                    } else {
                        throw new RuntimeException("Consistency problem");
                    }
                }
                LOGGER.info("MaxProSem " + maxPRoSem);
                LOGGER.info("maxScore " + maxScore);
                LOGGER.info("maxcorr " + maxCorr);
                ps.executeBatch();
            }
        }


    }

    public void viderTableMotsCles() throws SQLException {
        /*
            g_fil_keys: table liant une filière à une liste de mots clés rentrée sous la forme d'une chaine de caractères séparés par des espaces.
             */
        LOGGER.info("Vidage de la table " + TABLE_MOTS_CLES );
        try (Statement ps = connection.createStatement()) {
            ps.execute(TRUNCATE_TABLE + TABLE_MOTS_CLES);
        }
    }


    /* export des données */
    public void exporterDonneesAnnuellesCarte(AlgoFilieresSimilairesSortie sortie) throws AccesDonneesException, IOException {
        exporterDonneesAnnuellesCarte(sortie, false);
    }

    public void exporterDonneesAnnuellesCarte(AlgoFilieresSimilairesSortie sortie, boolean skipTauxAcces) throws IOException, AccesDonneesException {
        try {

            if(!skipTauxAcces) exporterTauxAcces();

            exporterFilieresSimilaires(sortie);

                viderTableMotsCles();//deprecated now extracted dynamically from JSON

        } catch (SQLException ex) {
            throw new AccesDonneesException(AccesDonneesExceptionMessage.MESSAGE, ex,  "Echec de l'exportation des donnees de la carte", ex);
        }

    }

    public void recuperationTypesBacs(AlgoCarteEntree entree) throws SQLException {

        try (Statement stmt = connection.createStatement()) {

            /* récupère la liste des candidats ayant des voeux confirmés à l'année n-1 */
            LOGGER.info("Récupération des types de bac par candidat");
            stmt.setFetchSize(1_000_000);
            String sql = ""
                    + "SELECT can.g_cn_cod, ic.i_tc_cod from g_can_arch can, i_cla_arch ic " +
                    "where  can.I_CL_COD_BAC=ic.I_CL_COD and ic.i_tc_cod is not null";
            LOGGER.info(sql);

            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int gCnCod = result.getInt(1);
                    int typeBac = result.getInt(2);
                    entree.typesBacCandiats.put(gCnCod,typeBac);
                }
            }

        }
    }

    public void recuperationFilieresParCandidat(AlgoCarteEntree entree) throws SQLException {
        /* récupération des voeux communs à l'année n-1 */
        try (Statement stmt = connection.createStatement()) {

            /* récupère la liste des candidats ayant des voeux confirmés à l'année n-1 */
            LOGGER.info("Récupération des filieres par candidat");
            stmt.setFetchSize(1_000_000);
            String sql = ""
                    + "SELECT  DISTINCT voeu.g_cn_cod, aff.G_FL_COD_AFF g_fl_cod"
                    //id du groupe de classement
                    + " FROM A_VOE_ARCH voeu, "
                    + "I_INS_ARCH ins, "
                    + "A_REC_GRP_ARCH  arecgrp, "
                    + "SP_G_TRI_AFF_ARCH  aff"
                    + WHERE
                    + " voeu.g_ta_cod=aff.g_ta_cod"
                    + " AND voeu.g_ta_cod = arecgrp.g_ta_cod"
                    + " AND arecgrp.g_ti_cod = ins.g_ti_cod"
                    + " AND voeu.g_cn_cod = ins.g_cn_cod"
                    + " AND voeu.a_sv_cod > -90" //élimine les voeux non sélectionnés
                    + " AND NVL(ins.i_is_val,0) = 1" //élimine les voeux non validés
            ;

            LOGGER.info(sql);

            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int gCnCod = result.getInt(1);
                    int gFlCod = result.getInt(2);
                    entree.candidatsParFiliere.computeIfAbsent(gFlCod, z -> new HashSet<>()).add(gCnCod);
                }
            }

        }
    }



    private static final Logger LOGGER = Logger.getLogger(ConnecteurDonneesAnnuellesCarteSQL.class.getSimpleName());

}

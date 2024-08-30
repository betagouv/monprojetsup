
/* Copyright 2020 © Ministère de l'Enseignement Supérieur, de la Recherche et de
l'Innovation,
    Hugo Gimbert (hugo.gimbert@enseignementsup.gouv.fr)

    This file is part of orientation-parcoursup.

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

import fr.gouv.monprojetsup.data.carte.algos.AlgoCarteConfig;
import fr.gouv.monprojetsup.data.carte.algos.AlgoCarteEntree;
import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.Candidat;
import fr.gouv.monprojetsup.data.domain.model.bacs.Bac;
import fr.gouv.monprojetsup.data.domain.model.formations.Filiere;
import fr.gouv.monprojetsup.data.domain.model.formations.Formation;
import fr.gouv.monprojetsup.data.domain.model.formations.Formations;
import fr.gouv.monprojetsup.data.domain.model.psup.DescriptifVoeu;
import fr.gouv.monprojetsup.data.domain.model.psup.PsupData;
import fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.domain.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.psup.exceptions.AccesDonneesException;
import fr.gouv.monprojetsup.data.psup.tags.MergeDuplicateTags;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

import static fr.gouv.monprojetsup.data.carte.algos.Filiere.LAS_CONSTANT;
import static fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques.TOUS_BACS_CODE;
import static fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques.TOUS_GROUPES_CODE;

public class ConnecteurBackendSQL {

    public static final String LYCEENS_ADMIS_FILIERE = " mps_admis_filiere ";
    public static final String LYCEENS_CANDIDATS_FILIERE = " mps_candidats_filieres ";
    public static final String LYCEENS_ADMIS_FORMATION = " mps_admis_formations ";
    public static final String FOR_TYPE_TABLE = " mps_G_FOR " ;
    public static final String FIL_TYPE_TABLE = " mps_G_FIL " ;
    public static final String WHERE = " WHERE ";
    private static final Logger LOGGER = Logger.getLogger(ConnecteurBackendSQL.class.getSimpleName());
    private static final String FROM = " FROM ";
    private static final String MPS_V_FIL_CAR = " mps_v_fil_car ";
    private static final String MPS_MATIERES = "mps_matieres";
    private static final String MPS_BACS_CANDIDATS = "mps_bacs_candidats";

    @NotNull
    private final Connection conn;

    public ConnecteurBackendSQL(@NotNull Connection conn) {
        this.conn = conn;
    }

    public static Map<String,List<Map<String, String>>> exportSelectToObject(Connection conn, Map<String, String> sqls) throws SQLException {
        Map<String,List<Map<String, String>>> m = new HashMap<>();
        for (Map.Entry<String, String> entry : sqls.entrySet()) {
            String prefix = entry.getKey();
            String sql = entry.getValue();
            List<Map<String, String>> result = new ArrayList<>();
            try (Statement stmt = conn.createStatement()) {
                /* récupère la liste des candidats ayant des voeux confirmés à l'année n-1 */
                stmt.setFetchSize(1_000_000);
                LOGGER.info(sql);

                try (ResultSet resultSet = stmt.executeQuery(sql)) {

                    ResultSetMetaData rsmd = resultSet.getMetaData();
                    int nb = rsmd.getColumnCount();
                    int cnt = 0;
                    while (resultSet.next()) {
                        Map<String, String> o = new HashMap<>();
                        for (int i = 1; i <= nb; i++) {
                            String st = resultSet.getString(i);
                            String columnName = rsmd.getColumnName(i);
                            o.put(columnName, st);
                        }
                        if (++cnt % 100000 == 0) {
                            LOGGER.info("Got " + cnt + " entries");
                        }
                        result.add(o);
                    }
                }
            }
            m.put(prefix, result);
        }
        return m;
    }


    private void recupererNomsfilieres(PsupData data, Set<Integer> filActives) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            LOGGER.info("Récupération des noms des filieres sur la carte");
            data.nomsFilieres().clear();
            stmt.setFetchSize(1_000_000);
            String sql = "select g_fl_cod, g_fl_lib_mdr, g_fl_sig from " + MPS_V_FIL_CAR;
            LOGGER.info(sql);
            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int gFlCod = result.getInt(1);
                    if(!filActives.contains(gFlCod)) continue;
                    data.nomsFilieres().put(Constants.gFlCodToFrontId(gFlCod), result.getString(2));
                }
            }
        }
    }

    private void recupererStatsAdmisParFiliereEtSpecialites(PsupStatistiques stats, Map<Integer, String> bacs, Set<Integer> filActives) throws SQLException {

        //TODO récupérer année et la setter dans stats

        ConnecteurBackendSQL.LOGGER.info("Récupération du nombre de lycéens admis à n-1 dans chaque filière");
        String sqlAdmisParFilieres =
                "select g_cn_cod,g_fl_cod from " + ConnecteurBackendSQL.LYCEENS_ADMIS_FILIERE;
        ConnecteurBackendSQL.LOGGER.info(sqlAdmisParFilieres);
        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(500000);
            try (ResultSet result = stmt.executeQuery(sqlAdmisParFilieres)) {
                while (result.next()) {
                    int gCnCod = result.getInt(1);
                    int gFlCod = result.getInt(2);
                    if(!filActives.contains(gFlCod)) continue;
                    String bac = bacs.get(gCnCod);
                    if(bac != null) {
                        String g = Constants.gFlCodToFrontId(gFlCod);
                        stats.incrementeAdmisParFiliere(TOUS_GROUPES_CODE, TOUS_BACS_CODE);
                        stats.incrementeAdmisParFiliere(g, TOUS_BACS_CODE);
                        if (!bac.equals(TOUS_BACS_CODE)) {
                            stats.incrementeAdmisParFiliere(TOUS_GROUPES_CODE, bac);
                            stats.incrementeAdmisParFiliere(g, bac);
                        }
                    }
                }
            }
        }

        ConnecteurBackendSQL.LOGGER.info("Récupération du nombre de lycéens candidats à n-1 dans chaque filière");
        String sqlCanddidatsParFilieres =
                """
                        select g_cn_cod,g_fl_cod\040
                        from\040""" + ConnecteurBackendSQL.LYCEENS_CANDIDATS_FILIERE;
        ConnecteurBackendSQL.LOGGER.info(sqlCanddidatsParFilieres);
        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(500000);
            try (ResultSet result = stmt.executeQuery(sqlCanddidatsParFilieres)) {
                while (result.next()) {
                    int gCnCod = result.getInt(1);
                    int gFlCod = result.getInt(2);
                    if(!filActives.contains(gFlCod)) continue;
                    String bac = bacs.get(gCnCod);
                    if(bac != null) {
                        String g = Constants.gFlCodToFrontId(gFlCod);
                        stats.incrementeCandidatsParFiliere(g, TOUS_BACS_CODE);
                        if (!bac.equals(TOUS_BACS_CODE)) {
                            stats.incrementeCandidatsParFiliere(g, bac);
                        }
                    }
                }
            }
        }

        ConnecteurBackendSQL.LOGGER.info("Récupération du nombre de lycéens admis à n-1 de chaque spécialité/matière dans chaque filière");
        String sqlAdmisParFilieresMatieres =
                "select count(*) cnt, i_mt_cod, g_fl_cod from "
                        + LYCEENS_ADMIS_FILIERE + " admis_filiere, "
                        + MPS_MATIERES + " matieres "
                        +
                        """
                        where admis_filiere.g_cn_cod=matieres.g_cn_cod
                        group by i_mt_cod,g_fl_cod
                        order by g_fl_cod, cnt desc
                        """;
        ConnecteurBackendSQL.LOGGER.info(sqlAdmisParFilieresMatieres);
        try (
                Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            try (ResultSet result = stmt.executeQuery(sqlAdmisParFilieresMatieres)) {
                while (result.next()) {
                    int nb = result.getInt(1);
                    int mtCod = result.getInt(2);
                    int gFlCod = result.getInt(3);
                    stats.setAdmisParFiliereMatiere(Constants.gFlCodToFrontId(gFlCod),mtCod,nb);
                }
            }
        }

        ConnecteurBackendSQL.LOGGER.info("Récupération du nombre de lycéens candidats à n-1 de chaque spécialité/matière dans chaque filière");
        String sqlCandidatsParFilieresMatieres =
                """
         select count(*) cnt, i_mt_cod, g_fl_cod
        from""" + LYCEENS_CANDIDATS_FILIERE + " candidats, "
                        + MPS_MATIERES + " matieres " +
                        """
                        where
                        candidats.g_cn_cod=matieres.g_cn_cod
                        group by i_mt_cod,g_fl_cod
                        order by g_fl_cod, cnt desc
                        """;
        ConnecteurBackendSQL.LOGGER.info(sqlCandidatsParFilieresMatieres);
        try (
                Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            try (ResultSet result = stmt.executeQuery(sqlCandidatsParFilieresMatieres)) {
                while (result.next()) {
                    int nb = result.getInt(1);
                    int mtCod = result.getInt(2);
                    int gFlCod = result.getInt(3);
                    stats.setCandidatsParFiliereMatiere(Constants.gFlCodToFrontId(gFlCod),mtCod,nb);
                }
            }
        }

    }



    public PsupData recupererData() throws Exception {
        PsupData data = new PsupData();

        recupererAnnee(data);

        recupererLas(data);

        Set<Integer> filActives = recupererFilieresActives(conn);
        data.filActives().addAll(filActives);


        recupererNomsfilieres(data, filActives);
        recupererNomsFilieresManquantsEtMostCles(data, filActives);
        recupererLiensOnisep(data, filActives);
        recupererFilieresSimilaires(data);
        recupererDureesEtudes(data);
        recupererFormations(data);
        recupererDiversPsup(data);
        recupererTypesBacs(data);

        Map<Integer, String> bacs = recupereBacsCandidats();
        recupererVoeuxParCandidat(data, bacs);
        recupererProfilsScolaires(data.stats(), bacs);
        recupererStatsAdmisParFiliereEtSpecialites(data.stats(), bacs, filActives);

        data.cleanup();

        return data;
    }

    private Map<Integer, String> recupereBacsCandidats() throws SQLException {
        LOGGER.info("Récupération des bacs de chaque candidat");
        Map<Integer, String> bacs = new HashMap<>();
        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select g_cn_cod,i_cl_cod from " + MPS_BACS_CANDIDATS;
            LOGGER.info(sql);
            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int gCnCod = result.getInt(1);
                    String bac = result.getString(2);
                    bacs.put(gCnCod, bac);
                }
            }
        }
        return bacs;
    }

    private void recupererAnnee(PsupData data) throws SQLException {
        //mps_annee
        try (Statement stmt = this.conn.createStatement()) {

            /* récupère la liste des candidats ayant des voeux confirmés à l'année n-1 */
            LOGGER.info("Récupération de l'année");
            String sql = "SELECT a_am_dat_max FROM mps_annee";
            LOGGER.info(sql);
            try (ResultSet result = stmt.executeQuery(sql)) {
                if (result.next()) {
                    Date date = result.getDate(1);
                    data.stats().setAnnee(date.toLocalDate().getYear());
                } else {
                    throw new RuntimeException("Echec de la récupération de l'année");
                }
            }
        }

    }

    private void recupererVoeuxParCandidat(PsupData data, Map<Integer, String> bacs) throws SQLException {


        Map<Integer, Set<Integer>> voeuxParCandidat = new HashMap<>();

        try (Statement stmt = this.conn.createStatement()) {

            /* récupère la liste des candidats ayant des voeux confirmés à l'année n-1 */
            LOGGER.info("Récupération des filieres par candidat");
            stmt.setFetchSize(1_000_000);
            String sql = "SELECT g_cn_cod, g_ta_cod FROM mps_voeux";

            LOGGER.info(sql);

            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int gCnCod = result.getInt(1);
                    int gTaCod = result.getInt(2);
                    voeuxParCandidat.computeIfAbsent(gCnCod, z -> new HashSet<>()).add(gTaCod);
                }
            }

        }

        data.voeuxParCandidat().clear();
        data.voeuxParCandidat().addAll(
                voeuxParCandidat.entrySet().stream()
                        .map(e -> new Candidat(bacs.getOrDefault(e.getKey(),TOUS_BACS_CODE), e.getValue()))
                        .toList()
        );
    }

    private void recupererLas(PsupData data) throws SQLException {

        try (Statement stmt = this.conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select distinct g_ta_cod from mps_las";
            LOGGER.info(sql);
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    int gTaCod = rs.getInt("g_ta_cod");
                    data.las().add(gTaCod);
                }
            }
        }

    }

    private void recupererDiversPsup(PsupData data) throws SQLException {
        Map<String, List<Map<String, String>>> o = exportSelectToObject(this.conn,
                Map.of("c_jur_adm", "select * from mps_c_jur_adm",
                        "c_jur_adm_comments", "select * from user_col_comments where table_name like '%C_JUR_ADM%' and comments is not null",
                        "g_for_tau_reu", "select * from g_for_tau_reu",
                        "g_for_tau_reu_comments", "select * from user_col_comments where table_name like '%G_FOR_TAU_REU%' and comments is not null",
                        "a_rec_grp", "select c_gp_cod,g_ti_cod,g_ta_cod,c_ja_cod,a_rg_pla,a_rg_nbr_sou from mps_a_rec_grp",
                        "g_fil_att_con", "select g_fl_cod,g_fl_lib,g_fl_des_att, g_fl_sig_mot_rec,g_fl_con_lyc_prem,g_fl_con_lyc_term, g_fl_typ_con_lyc from mps_g_fil",
                        "descriptions_formations", "select g_ta_cod,g_fr_cod_aff,g_fl_cod_aff,g_fl_lib_aff, g_ta_lib_voe, g_ta_des_deb, g_ta_des_ens from mps_descriptions_formations"
                )
        );
        data.diversPsup().putAll(o);
        recupererDescriptions(data);
    }


    public void recupererDescriptions(PsupData data) throws SQLException {

        try (Statement stmt = this.conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select g_ta_cod,g_fr_cod_aff,g_fl_cod_aff,g_fl_lib_aff, g_ta_lib_voe, g_ta_des_deb, g_ta_des_ens " +
                    "from mps_descriptions_formations";
            LOGGER.info(sql);
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    data.descriptifsFormations().descriptions().add(new DescriptifVoeu(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7)
                    ));
                }
            }
        }
    }

    private void recupererTypesBacs(PsupData data) throws SQLException {
        try (Statement stmt = this.conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select i_cl_cod, i_cl_bac_lib from mps_bacs";
            LOGGER.info(sql);
            List<Bac> bacs = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    bacs.add(new Bac(rs.getString(1), rs.getString(2)));
                }
            data.setBacs(bacs);            }
        }
    }

    private void recupererFormations(PsupData data) throws Exception {


        Formations formations = new Formations();

        LOGGER.info("Récupération des filieres et groupes par filiere");

        String sql = "SELECT G_FR_COD,G_FR_LIB   " + FROM
                + FOR_TYPE_TABLE;
        try (
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(sql)) {
            while (result.next()) {
                int gFrCod = result.getInt("G_FR_COD");
                String gFrLib = result.getString("G_FR_LIB");
                formations.typesMacros.put(gFrCod, gFrLib);
            }
        }


        sql = "SELECT G_FL_COD,fr.G_FR_COD,G_FR_LIB,G_FL_LIB,G_FL_FLG_APP,G_FL_COD_FI FROM  "
                + FIL_TYPE_TABLE + " fil,"
                + FOR_TYPE_TABLE + " fr "
                + WHERE + " fil.g_fr_cod=fr.g_fr_cod "
                + "ORDER BY fr.g_fr_cod,G_FL_COD";
        try (
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(sql)) {
            while (result.next()) {
                int gFlCod = result.getInt("G_FL_COD");
                int gFrCod = result.getInt("G_FR_COD");
                String gFrLib = result.getString("G_FR_LIB");
                String gFlLib = result.getString("G_FL_LIB");
                boolean isApp = result.getBoolean("G_FL_FLG_APP");
                int gFlCodFi = result.getInt("G_FL_COD_FI");
                String libFiliere = gFlLib.startsWith(gFrLib) ? gFlLib : gFrLib + " - " + gFlLib;
                formations.creerFiliere(gFlCod, gFrCod, libFiliere, isApp, gFlCodFi);
            }
        }
        sql = "SELECT " +
                "G_TA_LIB_VOE," +
                "G_FL_COD_AFF," +
                "G_EA_COD_AFF," +
                "C_GP_COD," +
                "G_TA_COD, " +
                "G_TI_COD, " +
                "G_AA_LIB," +
                "G_AA_COD, " +
                "capa, " +
                "lng, " +
                "lat," +
                "commune," +
                "code_commune FROM mps_formations "
                + " ORDER BY G_FL_COD_AFF,C_GP_COD";
        LOGGER.info(sql);
        int filieresManquantes = 0;
        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    String gTaLibVoe = result.getString("G_TA_LIB_VOE");
                    int gFlCod = result.getInt("G_FL_COD_AFF");
                    int cGpCod = result.getInt("C_GP_COD");
                    int gTaCod = result.getInt("G_TA_COD");
                    int gTiCod = result.getInt("G_TI_COD");
                    String gEaCodAff = result.getString("G_EA_COD_AFF");
                    String gAaLib = result.getString("G_AA_LIB");
                    int gAaCod = result.getInt("G_AA_COD");
                    int capaciteFormation = result.getInt("capa");
                    Double lat = result.getDouble("lat");
                    Double lng = result.getDouble("lng");
                    if (result.wasNull()) {
                        lat = null;
                        lng = null;
                    }

                    Double finalLat = lat;
                    Double finalLng = lng;
                    Formation f = formations.formations.get(gTaCod);
                    if (f != null) {
                        if (f.gTiCod != gTiCod) {
                            throw new RuntimeException("Deux formations avec le même gta mais pas le même gti");
                        }
                        if (f.gFlCod != gFlCod) {
                            throw new RuntimeException("Deux formations avec le même gta mais pas le même gfl");
                        }
                    } else {
                        f = new Formation(
                                gTaCod,
                                gTiCod,
                                gTaLibVoe,
                                gFlCod,
                                gAaLib,
                                gEaCodAff,
                                gAaCod,
                                capaciteFormation,
                                finalLat,
                                finalLng,
                                result.getString("commune"),
                                result.getString("code_commune")
                        );
                        //communes et codes communes!!
                        formations.formations.put(gTaCod, f);
                    }
                    f.groupes.add(cGpCod);

                    if (formations.hasFiliere(gFlCod)) {
                        Filiere filiere = formations.getFiliere(gFlCod);
                        String actuel = filiere.libellesGroupes.getOrDefault(cGpCod, "");
                        filiere.libellesGroupes.put(cGpCod,
                                (actuel.isEmpty() ? "" : (actuel + " | "))
                                        + f.libelle + " (gTaCod=" + f.gTaCod + ")"
                        );
                    } else {
                        filieresManquantes++;
                    }
                }
            }
        }
        if (filieresManquantes > 0) {
            LOGGER.info("Filieres manquantes pour " + filieresManquantes + " groupes");
        }

        data.formations().ajouterSiInconnu(formations);

    }


    public static Set<Integer> recupererFilieresActives(Connection connection) throws SQLException {
        LOGGER.info("Récupération des filières actives");
        Set<Integer> res = new HashSet<>();

        try (Statement stmt = connection.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select distinct g_fl_cod_aff, g_ta_flg_for_las from mps_filieres_actives";
            LOGGER.info(sql);
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    int gFlCod = rs.getInt("g_fl_cod_aff");
                    boolean flagLAS = rs.getBoolean("g_ta_flg_for_las");
                    res.add(gFlCod + (flagLAS ? LAS_CONSTANT : 0));
                }
            }
        }
        return res;

    }

    private void recupererDureesEtudes(PsupData data) throws SQLException {

        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select g_fl_cod,g_fr_cod,g_fl_lib, g_fr_lib,g_fr_sig from mps_filieres2";
            LOGGER.info(sql);

            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    data.addDuree(
                            rs.getInt("g_fl_cod"),
                            rs.getInt("g_fr_cod"),
                            rs.getString("g_fl_lib"),
                            rs.getString("g_fr_lib"),
                            rs.getString("g_fr_sig")
                    );
                }
            }
        }

    }

    private void recupererFilieresSimilaires(PsupData data) throws SQLException {

        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select g_fl_cod_ori,g_fl_cod_sim, g_fs_sco, i_tc_cod " +
                    "from mps_filieres_sim order by g_fl_cod_ori,g_fs_sco";
            LOGGER.info(sql);

            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    data.addFiliereSimilaire(
                            rs.getInt("g_fl_cod_ori"),
                            rs.getInt("g_fl_cod_sim"),
                            rs.getInt("g_fs_sco"),
                            rs.getInt("i_tc_cod")
                    );
                }
            }
        }

    }

    /**
     * Injecte les motsc=c-éls nécessaires à la carte, en préservant les chaines entières.
     * Effet de bord: injecte les données dsur les formations dans data.
     *
     * @param data les containueur de données à renseigner
     * @param filActives les filières qui nous intéressent
     */
    private void recupererNomsFilieresManquantsEtMostCles(PsupData data, Set<Integer> filActives) throws AccesDonneesException {
        //on recupere tous les mots clés en s'appuyant sur le code de la carte
        ConnecteurJsonCarteSQL conn = new ConnecteurJsonCarteSQL(this.conn);
        AlgoCarteConfig config = new AlgoCarteConfig();//on part de la config par défaut
        AlgoCarteEntree donneesCarte = conn.recupererDonneesJSONCarte(config);

        data.injecterNomsFilieresManquants(
                donneesCarte,
                filActives
        );

        // Stem and merge tags.
        TagsSources motsCles = recupererSources(donneesCarte, filActives);
        motsCles = MergeDuplicateTags.stemMergeTags(motsCles);
        motsCles.normalize();
        data.setMotsCles(motsCles);

    }

    public static TagsSources recupererSources(AlgoCarteEntree carte, Set<Integer> filActives) {

        final Map<String, Set<String>> sources = new HashMap<>();
        carte.filieres.values().forEach(filiere -> {
            if (filActives.contains(filiere.cle)) {
                //une fois avec et une fois sans accents
                String idfiliere = Constants.gFlCodToFrontId(filiere.cle);
                String[] chunks = filiere.libelle.split("\\P{L}+");
                for (String s : chunks) {
                    String chunk = s.toLowerCase();
                    if (chunk.length() > 2 && !chunk.matches(".*\\d.*")) {
                        sources.computeIfAbsent(chunk.trim(), z -> new HashSet<>()).add(idfiliere);
                    }
                }

                filiere.motsClesParcoursup.forEach(m -> {
                    //List<String> mTrimmed = Arrays.stream(m.split("[;\\-]")).map(String::trim).toList();
                    sources.computeIfAbsent(m.trim(), z -> new HashSet<>()).add(idfiliere);
                });
            }
        });
        //carte.suggestions.values().forEach(f -> motsCles.addAll(f.donnees.values()));

        //We remove numeric data
        //on supprime les mots de moins de une lettre
        /* cleanup */
        //on garde les sources synchronisées
        sources.keySet().removeIf(m -> m.matches(".*\\d.*"));//We remove numeric data
        sources.keySet().removeIf(m -> m.length() <= 2);//We remove small words

        //mise a jour des tagsSources
        TagsSources sources2 = new TagsSources();
        sources.forEach(sources2::add);

        return sources2;
    }

    /**
     * Recupère les liens onisep pour chaque filière
     *
     * @param data les données
     * @param filActives les filières intéressantes
     */
    private void recupererLiensOnisep(PsupData data, Set<Integer> filActives) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            /* récupère la liste des candidats ayant des voeux confirmés à l'année n-1 */
            ConnecteurBackendSQL.LOGGER.info("Récupération des liens Onisep ");
            stmt.setFetchSize(1_000_000);
            String sql = "select g_fl_cod, g_fl_lie_inf from mps_liens_onisep";
            ConnecteurBackendSQL.LOGGER.info(sql);
            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int gFlCod = result.getInt(1);
                    if(filActives.contains(gFlCod)) {
                        String lien = result.getString(2);
                        data.ajouterLienFiliereOnisep(gFlCod, lien);
                    }
                }
            }

        }

    }


    //group bac mat
    Map<String, Map<String, Map<Integer, int[] >>> percCounters = new HashMap<>();
    void incremente(String bac, String group, int mat, double note) {
        int[] c = percCounters
                .computeIfAbsent(group, z -> new HashMap<>())
                .computeIfAbsent(bac, z -> new HashMap<>())
                .computeIfAbsent(mat, z -> new int[PsupStatistiques.PRECISION_PERCENTILES]);
        int percentile = Math.max(0,
                Math.min(PsupStatistiques.PRECISION_PERCENTILES-1,
                        (int) Math.floor( note / 20 * PsupStatistiques.PRECISION_PERCENTILES ))
        );
        c[percentile]++;
    }

    private void incremente(Map<Integer, String> bacs,
                            String group,
                            Set<Integer> gcns,
                            Map<Integer, Map<Integer, Double>> datasCandidatsMoyennes) {
        gcns.forEach(gcn -> {
            Map<Integer, Double> notes = datasCandidatsMoyennes.get(gcn);
            if(notes != null) {
                String bac = bacs.getOrDefault(gcn, "?");
                notes.forEach((matiere, note) -> {
                    incremente(bac, group, matiere,note);
                    incremente(TOUS_BACS_CODE, group, matiere,note);
                });
            }
        });
    }


    private void recupererProfilsScolaires(PsupStatistiques data, Map<Integer, String> bacs) throws SQLException {
        //recuperer matieres
        data.ajouterMatiere(PsupStatistiques.MOYENNE_GENERALE_CODE, "Moyenne générale");
        LOGGER.info("Récupération des matières");
        try (Statement stmt = conn.createStatement()) {
            /* récupère la liste des candidats ayant des voeux confirmés à l'année n-1 */
            ConnecteurBackendSQL.LOGGER.info("Récupération des matières");
            stmt.setFetchSize(1_000_000);
            String sql = "select i_mt_cod,i_mt_lib from mps_matieres2";
            ConnecteurBackendSQL.LOGGER.info(sql);
            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int iMtCod = result.getInt(1);
                    String iMtLib = result.getString(2);
                    data.ajouterMatiere(iMtCod, iMtLib);
                }
            }
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select i_cs_ann," +
                    "i_mt_cod, " +
                    "i_cl_cod, " +
                        "nb from mps_stats_mat_bac " +
                    "order by i_mt_cod ";
            ConnecteurBackendSQL.LOGGER.info(sql);
            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int iCsAnn = result.getInt(1);
                    int iMtCod = result.getInt(2);
                    String iClCod = result.getString(3);
                    int nb = result.getInt(4);
                    data.ajouterStatMatiereBac(iCsAnn == 0 ?  PsupStatistiques.ANNEE_LYCEE_TERMINALE : PsupStatistiques.ANNEE_LYCEE_PREMIERE, iMtCod, iClCod, nb);
                }
            }
        }

        //bac / groupe / compteur
        percCounters.clear();

        Map<Integer, Map<Integer, Double>> datasCandidatsMoyennes = new HashMap<>();


        ConnecteurBackendSQL.LOGGER.info("Récupération des  moyennes générales de chaque lycéen");
        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql ="select g_cn_cod,moyenne from mps_moy_gen_candidats";
            ConnecteurBackendSQL.LOGGER.info(sql);
            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int gCnCod = result.getInt(1);
                    double note = result.getDouble(2);
                    datasCandidatsMoyennes
                            .computeIfAbsent(gCnCod, z -> new HashMap<>())
                            .put(PsupStatistiques.MOYENNE_GENERALE_CODE, note);
                }
            }
        }

        ConnecteurBackendSQL.LOGGER.info("Récupération des  moyennes générales par matière de chaque lycéen");

        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select g_cn_cod,i_mt_cod,moyenne from mps_moy_sco_candidats";
            ConnecteurBackendSQL.LOGGER.info(sql);
            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int gCnCod = result.getInt(1);
                    int iMtCod = result.getInt(2);
                    double note = result.getDouble(3);
                    datasCandidatsMoyennes
                            .computeIfAbsent(gCnCod, z -> new HashMap<>())
                            .put(iMtCod, note);
                }
            }
        }


        ConnecteurBackendSQL.LOGGER.info("Récupération des lycéens admis dans chaque filière ");
        Map<String, Set<Integer>> flToGcn = new HashMap<>();
        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select g_cn_cod, g_fl_cod from " + LYCEENS_ADMIS_FILIERE;
            ConnecteurBackendSQL.LOGGER.info(sql);
            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int gCnCod = result.getInt(1);
                    int gFlCod = result.getInt(2);
                    flToGcn.computeIfAbsent(Constants.gFlCodToFrontId(gFlCod), z -> new HashSet<>()).add(gCnCod);
                }
            }
        }

        ConnecteurBackendSQL.LOGGER.info("Récupération des lycéens admis dans chaque formation ");
        Map<String, Set<Integer>> gtaToGcn = new HashMap<>();
        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1_000_000);
            String sql = "select g_cn_cod, g_ta_cod from " + LYCEENS_ADMIS_FORMATION;
            ConnecteurBackendSQL.LOGGER.info(sql);
            try (ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    int gCnCod = result.getInt(1);
                    int gTaCod = result.getInt(2);
                    gtaToGcn.computeIfAbsent(Constants.FORMATION_PREFIX + gTaCod, z -> new HashSet<>()).add(gCnCod);
                }
            }
        }

        percCounters.clear();
        incremente(bacs, TOUS_GROUPES_CODE, datasCandidatsMoyennes.keySet(), datasCandidatsMoyennes);
        flToGcn.forEach((fl, gcns) -> incremente(bacs, fl, gcns, datasCandidatsMoyennes));
        gtaToGcn.forEach((gta, gcns) -> incremente(bacs, gta, gcns, datasCandidatsMoyennes));
        data.setStatistiquesAdmisFromPercentileCounters(percCounters);
    }


}

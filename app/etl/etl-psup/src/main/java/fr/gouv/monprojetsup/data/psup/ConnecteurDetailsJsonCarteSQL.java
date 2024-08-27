package fr.gouv.monprojetsup.data.psup;


import fr.gouv.monprojetsup.data.carte.algos.explicabilite.AlgoCarteDetails;
import fr.gouv.monprojetsup.data.carte.algos.explicabilite.tauxacces.TauxAccesDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import static fr.gouv.monprojetsup.data.psup.AccesDonneesExceptionMessage.CONNECTEUR_ORACLE_CONNEXION_NULL;
import static fr.gouv.monprojetsup.data.psup.ConnecteurDonneesAnnuellesCarteSQL.getSQLDernierAppeleEnPP;
import static fr.gouv.monprojetsup.data.psup.ConnecteurDonneesAnnuellesCarteSQL.getSQLNbCanAuDessousBarre;


public class ConnecteurDetailsJsonCarteSQL {

    private final Connection connection;

    public ConnecteurDetailsJsonCarteSQL(Connection connection) throws AccesDonneesException {
        if (connection == null) {
            throw new AccesDonneesException(CONNECTEUR_ORACLE_CONNEXION_NULL);
        }
        this.connection = connection;
    }


    public void recupererDetailsTauxAcces(AlgoCarteDetails details, int minNbCandidatsPourTauxAcces) throws SQLException {
        details.parBac.clear();
        for (int typeBac = 0; typeBac <= 3; typeBac++) {
            recupererDetailsTauxAccesBac(
                    details.parBac.computeIfAbsent(typeBac, z -> new TauxAccesDetails()),
                    typeBac,
                    minNbCandidatsPourTauxAcces);
        }
    }

    protected void recupererDetailsTauxAccesBac(TauxAccesDetails details, int typeBac, int minNbCandidatsPourTauxAcces) throws SQLException {
        LOGGER.info("***********************************************************\n" +
                "**********************************************************\n" +
                "**** Taux accès, type bac " + typeBac + " ********\n" +
                "**********************************************************\n" +
                "************************************************************");

        /* abandonné car crée une erreur d'allocation
       ORA-01652
       0RA-02063
        String sqlNbCanEnDessousBarre =
                """
                        SELECT   \s
                        arg.g_ta_cod,  \s
                        arg.c_gp_cod,    \s
                        COuNT(DISTINCT voe.G_CN_COD) nb,
                        COuNT(DISTINCT (case when cla.C_CG_ORD_APP is not null and cla.C_CG_ORD_APP <= rang_der_appele.rang then voe.G_CN_COD end)) nb_admissibles,\s
                        rang_der_appele.rang  \s
                        FROM       \s
                        G_CAN_ARCH can,\s
                        A_VOE_ARCH voe,\s
                        G_PAY_ARCH pays,
                        C_CAN_GRP_ARCH cla,\s
                            (SELECT adm.g_ta_cod g_ta_cod, \s
                            cla.C_GP_COD c_gp_cod, \s
                            MAX(cla.C_CG_ORD_APP) rang \s
                            FROM   A_ADM_ARCH adm,         C_CAN_GRP_ARCH cla\s
                            WHERE               adm.G_CN_COD=cla.G_CN_COD  \s
                            AND adm.A_TA_COD = 1         \s
                            AND adm.C_GP_COD = cla.C_GP_COD  \s
                            AND cla.C_CG_ORD_APP is not null\s
                            GROUP BY          adm.g_ta_cod,   cla.c_gp_cod)\s
                        rang_der_appele,\s"""
                        + (typeBac > 0 ? "I_CLA_ARCH tbac, " : "")
                        + """
                        I_INS_ARCH ins,
                        A_REC_GRP_ARCH arg\s
                        WHERE can.g_cn_cod=voe.g_cn_cod \s
                        """
                        + (typeBac > 0 ? "AND can.I_CL_COD_BAC=tbac.I_CL_COD " : "")
                        + (typeBac > 0 ? "AND tbac.I_TC_COD = ? " : "")
                        + """
                                AND voe.a_sv_cod > -90   \s
                                AND voe.G_CN_COD=ins.G_CN_COD \s
                                AND voe.G_TA_COD=arg.G_TA_COD  \s
                                AND ins.G_TI_COD=arg.G_TI_COD \s
                                AND NVL(ins.I_IS_VAL,0)=1 \s
                                AND voe.G_CN_COD=cla.G_CN_COD\s
                                AND cla.C_GP_COD=rang_der_appele.C_GP_COD \s
                                AND voe.g_ta_cod = rang_der_appele.g_ta_cod \s
                                AND can.g_py_cod_nat = pays.g_py_cod\s
                                AND (can.g_po_cod in (1,11) or NVL(pays.g_py_flg_cee,0)=1)\s
                                GROUP BY  arg.g_ta_cod, arg.c_gp_cod, rang_der_appele.rang        
                        """;
         */
        String sqlNbCanEnDessousBarre ="WITH rang_der_appele AS  (" + getSQLDernierAppeleEnPP() + ") " +
                getSQLNbCanAuDessousBarre(typeBac);

        LOGGER.info(sqlNbCanEnDessousBarre);
        try (PreparedStatement ps = connection.prepareStatement(sqlNbCanEnDessousBarre)) {
            ps.setFetchSize(1_000_000);
            if (typeBac > 0) ps.setInt(1, typeBac);
            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    int gTaCod = result.getInt(1);
                    int cGpCod = result.getInt(2);
                    int nbTotal = result.getInt(3);
                    int nbSousBarre = result.getInt(4);
                    int rangDerApp = result.getInt(5);
                    if (nbTotal >= minNbCandidatsPourTauxAcces) {
                        details.addNbCandidats(gTaCod, cGpCod, nbTotal);
                        details.addNbSousBarre(gTaCod, cGpCod, nbSousBarre);
                        details.addDernierAppele(gTaCod, cGpCod, rangDerApp);
                    }
                }
            }
        }

    }


    private static final Logger LOGGER = Logger.getLogger(ConnecteurDetailsJsonCarteSQL.class.getSimpleName());


}

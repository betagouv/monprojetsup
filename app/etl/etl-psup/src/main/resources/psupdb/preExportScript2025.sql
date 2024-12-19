
define anneeSeconde = 2022;
define MOYENNE_BAC_I_EB_COD = 20;

--il y a une bascule entre la version archiv�e de janvier � septembre 
--et la version courante de septembre � mi-d�cembre

define a_adm_stats = a_adm_arch;
define g_can_stats = g_can_arch;
define sp_g_tri_aff_stats = sp_g_tri_aff_arch;
define g_pro_new_stats = g_pro_new_arch;
define a_sit_voe_stats = a_sit_voe_arch;
define a_voe_stats = a_voe_arch;
define i_bul_sco_stats = i_bul_sco_arch;
define a_rec_grp_stats = a_rec_grp_arch;
define i_ins_stats = i_ins_arch;
define i_can_sco_stats = i_can_sco_arch;


-------------------------------------------------
----------------- stats -------------------------
-------------------------------------------------

drop view mps_annee;
CREATE VIEW mps_annee as select max(a_am_dat) A_AM_DAT_MAX from &a_adm_stats;


DROP TABLE mps_specs;
CREATE TABLE mps_specs as 
(
    select distinct i_cl_cod, i_ne_cod, i_ne_lib, i_sp_cod, i_sp_lib, i_mt_cod, i_mt_lib
    from sp_bul_sco
    where
--i_cl_cod in ('STAV','ST2S','S2TMD','STMG','STL','STHR','STD2A','STI2D', 'G�n�rale') and 
    sp_bs_flg_eds = 1
    and sp_flg_ouv = 1
    and i_cs_ann = 0
    and i_ne_cod in (1,10)
 --   order by i_cl_cod,i_mt_lib
);

--select * from mps_specs order by i_cl_cod,i_mt_lib;

DROP TABLE mps_admis;
CREATE TABLE mps_admis as (select distinct can.g_cn_cod
                                           from
                                           &g_can_stats can,
                                           &a_adm_stats adm,
                                           &g_pro_new_stats  g_pro_new,
                                           &sp_g_tri_aff_stats  aff,
                                           &a_sit_voe_stats  asv
                                           where can.g_cn_cod=adm.g_cn_cod
                                           and adm.g_ta_cod=aff.g_ta_cod
                                           and adm.a_ta_cod in (1,2)
                                           and adm.a_sv_cod=asv.a_sv_cod
                                           and asv.a_sv_flg_aff=1
                                           and can.g_pn_cod=g_pro_new.g_pn_cod
                                           and g_pro_new.g_pm_cod=1);
ALTER TABLE mps_admis ADD  CONSTRAINT constr PRIMARY KEY (g_cn_cod);

drop table mps_bacs_candidats;
create table mps_bacs_candidats as
    (select admis.g_cn_cod g_cn_cod,can.i_cl_cod i_cl_cod
    from mps_admis admis,&g_can_stats can
    where admis.g_cn_cod=can.g_cn_cod);
ALTER TABLE mps_bacs_candidats ADD  CONSTRAINT constr_bacs_candidats PRIMARY KEY (g_cn_cod);

drop table mps_bacs;
create table mps_bacs as
    (select distinct i_cla.i_cl_cod,i_cl_bac_lib
    from i_cla,mps_bacs_candidats
    where i_cla.i_cl_cod=mps_bacs_candidats.i_cl_cod and i_tc_cod in (1,2,3) and nvl(i_cl_clo,0)=0 );

DROP TABLE mps_candidats_filieres;
CREATE TABLE mps_candidats_filieres as (select distinct can.g_cn_cod,aff.g_fl_cod_aff g_fl_cod
                                       from
                                       &g_can_stats  can,
                                       &a_voe_stats voe,
                                       &g_pro_new_stats  g_pro_new,
                                       &sp_g_tri_aff_stats  aff
                                       where can.g_cn_cod=voe.g_cn_cod
                                       and voe.g_ta_cod=aff.g_ta_cod
                                       and voe.a_sv_cod > -90
                                       and can.g_pn_cod=g_pro_new.g_pn_cod
                                       and g_pro_new.g_pm_cod=1);
ALTER TABLE mps_candidats_filieres ADD  CONSTRAINT constr2 PRIMARY KEY (g_cn_cod, g_fl_cod);

DROP TABLE mps_admis_formations;                                       
CREATE TABLE mps_admis_formations as (select distinct can.g_cn_cod,aff.g_ta_cod g_ta_cod
                                       from
                                       &g_can_stats  can,
                                       &a_adm_stats  adm,
                                       &g_pro_new_stats  g_pro_new,
                                       &sp_g_tri_aff_stats  aff,
                                       &a_sit_voe_stats  asv
                                       where can.g_cn_cod=adm.g_cn_cod
                                       and adm.g_ta_cod=aff.g_ta_cod
                                       and adm.a_ta_cod in (1,2)
                                       and adm.a_sv_cod=asv.a_sv_cod
                                       and asv.a_sv_flg_aff=1
                                       and can.g_pn_cod=g_pro_new.g_pn_cod
                                       and g_pro_new.g_pm_cod=1);   
ALTER TABLE mps_admis_formations ADD  CONSTRAINT constr_adm_forr PRIMARY KEY (g_cn_cod,g_ta_cod);


drop table mps_matieres;
create table mps_matieres as 
     (select distinct g_cn_cod,i_mt_cod from &i_bul_sco_stats 
     where NVL(I_BS_MOY_CDT,-1)>= 0 );
ALTER TABLE mps_matieres ADD  CONSTRAINT constr4 PRIMARY KEY (g_cn_cod,i_mt_cod);
     
drop table mps_moy_gen_candidats;
create table mps_moy_gen_candidats as (select admis.g_cn_cod,AVG(i_bs_moy_cdt) moyenne
             from &i_bul_sco_stats  bul, mps_admis admis
            where
            bul.g_cn_cod=admis.g_cn_cod
            and i_bs_moy_cdt is not null
            and i_bs_moy_cdt >= 0
            and i_cs_ann = 0
            group by admis.g_cn_cod);
            ALTER TABLE mps_moy_gen_candidats ADD  CONSTRAINT constr5 PRIMARY KEY (g_cn_cod);

drop table mps_moy_sco_candidats;
create table mps_moy_sco_candidats as 
            (select admis.g_cn_cod,i_mt_cod,AVG(i_bs_moy_cdt) moyenne
             from &i_bul_sco_stats  bul, mps_admis admis
            where
            bul.g_cn_cod=admis.g_cn_cod
            and i_bs_moy_cdt is not null
            and i_bs_moy_cdt >= 0
            and i_cs_ann = 0
            group by admis.g_cn_cod, i_mt_cod);
ALTER TABLE mps_moy_sco_candidats ADD  CONSTRAINT constr6 PRIMARY KEY (g_cn_cod,i_mt_cod);

--deprecated
drop table mps_las;
create table mps_las as
select g_ta_cod from sp_g_tri_aff -- 
where g_ta_lib_voe like '%LAS%' and g_ta_des_ens like '%LAS%';
ALTER TABLE mps_las ADD  CONSTRAINT constrlas PRIMARY KEY (g_ta_cod);

                            
drop table mps_descriptions_formations;
create table mps_descriptions_formations as
select g_ta_cod,g_fr_cod_aff,g_fl_cod_aff,g_fl_lib_aff, g_ta_lib_voe, g_ta_des_deb, g_ta_des_ens 
from sp_g_tri_aff --
;
ALTER TABLE mps_descriptions_formations ADD  CONSTRAINT constr_de_for PRIMARY KEY (g_ta_cod);

drop table mps_filieres_actives;
create table mps_filieres_actives as
--les recrutements � n-1
(
select distinct g_fl_cod_aff, g_ta_flg_for_las 
from &sp_g_tri_aff_stats aff,&a_rec_grp_stats arg 
where aff.g_ta_cod=arg.g_ta_cod and NVL(arg.a_rg_pla,0) > 0
union 
--les admissions � n
select distinct g_fl_cod_aff, g_ta_flg_for_las 
from sp_g_tri_aff aff,a_adm adm  
where aff.g_ta_cod=adm.g_ta_cod
union 
--les admissions � n
select distinct g_fl_cod_aff, g_ta_flg_for_las 
from sp_g_tri_aff aff,a_rec_grp arg  
where aff.g_ta_cod=arg.g_ta_cod
and NVL(a_rg_pla,0) > 0
);


drop table mps_voeux;
create table mps_voeux as 
(SELECT  DISTINCT voeu.g_cn_cod, aff.g_ta_cod g_ta_cod
                    FROM &A_VOE_stats voeu,
                    &I_INS_stats ins,
                    &A_REC_GRP_stats  arecgrp,
                    &SP_G_TRI_AFF_stats  aff
                    WHERE
                    voeu.g_ta_cod=aff.g_ta_cod
                    AND voeu.g_ta_cod = arecgrp.g_ta_cod
                    AND arecgrp.g_ti_cod = ins.g_ti_cod
                    AND voeu.g_cn_cod = ins.g_cn_cod
                    AND voeu.a_sv_cod > -90
                    AND NVL(ins.i_is_val,0) = 1
                    );

drop table mps_admis_bacs_spe;
create table mps_admis_bacs_spe as
(select admis.g_cn_cod, cs.i_sp_cod
from &i_can_sco_stats cs , mps_admis admis
where 
cs.g_cn_cod=admis.g_cn_cod
and cs.i_sp_cod is not null
and cs.i_cs_ann = 0 --ann� scolaire actuelle
)
;

-------------------------------------------------
------------------------- ACTUEL ---------------
-------------------------------------------------

drop table mps_filieres_actuelles;
create table mps_filieres_actuelles as
select g_fl_cod,g_for.g_fr_cod,g_fl_lib, g_fr_lib,g_fr_sig 
from g_fil,g_for where g_fil.g_fr_cod=g_for.g_fr_cod;
     
drop table mps_filieres_sim;
create table mps_filieres_sim as
select g_fl_cod_ori,g_fl_cod_sim, g_fs_sco, i_tc_cod 
from g_fil_sim--
order by g_fl_cod_ori,g_fs_sco;

CREATE OR REPLACE view mps_a_rec_grp as 
(select "C_GP_COD","G_TI_COD","G_TA_COD","C_JA_COD","A_RG_PLA","A_RG_RAN_MAX_CON_ALT","A_RG_NBR_SOU","A_RG_RAN_LIM","A_RG_JUS_SUR","A_RG_RAN_DER","A_RG_FIN_LIS","A_RG_ETA_CPL","A_RG_DAT_RET_CPL","A_RG_FLG_INS_PC_OTO","A_RG_MOD_RET_PC","A_RG_RAN_MAX_CON_ALT_AFF","A_RG_RAN_LIM_MAX","A_RG_RAN_LIM_OLD","A_RG_RAN_LIM_REF","A_RG_NBR_SOU_OLD","A_RG_NBR_SOU_DER_ALG","A_RG_NBR_ATT","A_RG_NBR_SOU_CAES","A_RG_FLG_NON_PC","A_RG_PCT_OUI","A_RG_NBR_VOE_CONF","A_RG_RAN_LIM_UTI_MAX","A_RG_RAN_DER_ANN_PRE","A_RG_RAN_DER_ADM","A_RG_DAT_INS_CPL","A_RG_MOD_ADM_PC","A_RG_RAN_DER_SIM","A_RG_VAL_DON_APP","A_RG_DAT_DER_VAL_DON_APP","A_RG_FLG_ADM_STOP","G_UP_COD_VAL_DON_APP","A_RG_RAN_LIM_MAX_SCN","A_RG_RAN_LIM_MAX_PAS_CLA","A_RG_RAN_LIM_MAX_LABRI","A_RG_RAN_LIM_MAX_DYN","A_RG_RAN_LIM_MAX_SCN_BRUT","A_RG_RAN_LIM_MAX_TYP_OPE","A_RG_FLG_RES_CAES","A_RG_RAN_LIM_MAX_LABRI_NEW","A_RG_FLG_RET_CPL","A_RG_FLG_RAN_LIM_POS_SCN","A_RG_RAN_DER_CLA","A_RG_RAN_DER_ANN_PRE_GDD","A_RG_FIN_LIS_ANN_PRE","A_RG_FLG_PC_ANN_PRE","A_RG_RAN_LIM_J1_ANN_PRE","A_RG_RAN_LIM_DEC_SCN","A_RG_RAN_LIM_DEC_JT_FLO","A_RG_NBR_SOU_PC_APR_ALGO_VEI","A_RG_RAN_DER_PP" from a_rec_grp);

create OR REPLACE view mps_g_for as 
(select "G_FR_COD","G_FR_LIB","G_FR_SIG","G_FR_ORD_TRI","G_FR_REG_FOR","G_TF_COD","I_TA_COD","G_FR_FLG_SEL","G_FR_TAU_SUR_AUT","G_FR_CAP_PAR_CLA","G_FR_FLG_DOM_L1_DER","I_RG_COD","G_FR_SIG_MOT_REC","G_FR_MOD_PAR" from g_for);

create OR REPLACE view mps_g_fil as 
(select 
g_fl_cod,g_fl_lib,G_FR_COD,g_fl_sig,G_FL_COD_FI,g_fl_des_att, g_fl_sig_mot_rec,g_fl_con_lyc_prem,g_fl_con_lyc_term, g_fl_typ_con_lyc, G_FL_FLG_APP from g_fil);

CREATE OR REPLACE VIEW "MPS_G_TRI_INS" ("G_TI_COD", "G_EA_COD_INS", "G_TI_LIB_ETA", "G_TI_FLG_LIB_ETA", "G_TI_MOT_CLE") AS 
  (select g_ti_cod,g_ea_cod_ins,G_TI_LIB_ETA,g_ti_flg_lib_eta,G_TI_MOT_CLE from g_tri_ins);

create or replace view mps_g_rap_pub  ("G_TI_COD", "C_JA_COD", "G_RB_DON_PRO_CMP", "G_RB_MOD_EXA_VOE", "G_RB_ALG_EXA_VOE", "G_RB_ALG_EXA_VOE_CMP", "G_RB_ENS_SES_CON_CDT", "G_RB_TAB_SYN", "G_RB_TAB_SYN_PAR_EFF", "G_RB_PAR_EFF", "G_RB_SIG_EFF", "G_UP_COD_SIG", "G_RB_DAT_SIG", "G_RB_MD5", "G_RB_PUB_EFF", "G_UP_COD_PUB", "G_RB_DAT_PUB", "G_RB_SES", "G_RB_DET_DEP", "G_US_COD_DEP", "G_RB_DAT_DEP", "G_RB_EDS_EXA_VOE", "G_RB_EDS_RES_ACA", "G_RB_EDS_EXA_CDT") AS 
  (select "G_TI_COD","C_JA_COD","G_RB_DON_PRO_CMP","G_RB_MOD_EXA_VOE","G_RB_ALG_EXA_VOE","G_RB_ALG_EXA_VOE_CMP","G_RB_ENS_SES_CON_CDT","G_RB_TAB_SYN","G_RB_TAB_SYN_PAR_EFF","G_RB_PAR_EFF","G_RB_SIG_EFF","G_UP_COD_SIG","G_RB_DAT_SIG","G_RB_MD5","G_RB_PUB_EFF","G_UP_COD_PUB","G_RB_DAT_PUB","G_RB_SES","G_RB_DET_DEP","G_US_COD_DEP","G_RB_DAT_DEP","G_RB_EDS_EXA_VOE","G_RB_EDS_RES_ACA","G_RB_EDS_EXA_CDT" from g_rap_pub);

create or replace view mps_c_jur_adm as 
(select "C_JA_COD","C_JA_LIB","G_TI_COD","C_TJ_COD","C_JA_DES_ATT","C_JA_CGV_RES_ACA","C_JA_CGV_COM_ACA","C_JA_CGV_SAV_ETR","C_JA_CGV_MOT_CON","C_JA_CGV_ACT_CEN_INT","C_JA_FLG_CGV_RES_ACA","C_JA_FLG_CGV_COM_ACA","C_JA_FLG_CGV_SAV_ETR","C_JA_FLG_CGV_MOT_CON","C_JA_FLG_CGV_ACT_CEN_INT","C_JA_CON_LYC_PREM","C_JA_CON_LYC_TERM","C_JA_TYP_CON_LYC","C_JA_FLG_PAS_CLA_POS","C_JA_FLG_PAS_CLA_REP","C_JA_FLG_VAL_COR","C_JA_ENS_SES_CON_CDT","C_JA_CGV_RES_ACA_PRC","C_JA_CGV_COM_ACA_PRC","C_JA_CGV_SAV_ETR_PRC","C_JA_CGV_MOT_CON_PRC","C_JA_CGV_ACT_CEN_INT_PRC" from c_jur_adm);

create or replace view mps_sp_g_tri_aff_typ_for as 
(select g_ta_cod,g_tf_cod from sp_g_tri_aff_typ_for);

create or replace view mps_v_typ_for as 
  (select g_tf_cod, g_tf_mot_cle_mdr from v_typ_for);

create or replace view mps_aff as 
(select 
g_ta_cod,
g_fr_cod_aff,G_FR_LIB_AFF,
g_fl_cod_aff,g_fl_lib_aff,
g_ea_cod_aff,G_EA_LIB_AFF,
g_ta_mot_cle,
g_ta_flg_lib_eta,
g_ta_lib_eta,
g_ta_lib_mdr,
g_ea_lib_mdr,
g_ta_des_deb,
g_ta_des_ens,
g_ta_cpl_ens_dis,
g_ta_dis_reu,
g_ta_flg_for_las,
g_ta_lat,
g_ta_lng
from sp_g_tri_aff
);

drop table mps_formations_actuelles;
create table mps_formations_actuelles as
SELECT G_TA_LIB_VOE,G_FL_COD_AFF,G_EA_COD_AFF,C_GP_COD,formations.g_ta_cod G_TA_COD, rec.g_ti_cod G_Ti_COD, 
G_AA_LIB,G_AA_COD, arec.A_RC_CAP capa, formations.g_ta_lng lng, formations.g_ta_lat lat, eta.b_cm_cod code_commune, NVL(b_cm_lib,eta.g_ea_com) commune
FROM  A_REC_GRP rec, A_REC arec,G_ETA eta, SP_G_TRI_AFF  formations, B_COM com  
WHERE rec.g_ta_cod=formations.g_ta_cod AND rec.g_ta_cod=arec.g_ta_Cod AND eta.g_ea_cod=formations.g_ea_cod_aff 
and eta.b_cm_cod=com.b_cm_cod
ORDER BY G_FL_COD_AFF,C_GP_COD;

create or replace view mps_a_rec as 
(select g_ta_cod,g_ti_cod from a_rec);

create or replace view mps_v_eta as
(select g_ea_cod,g_ea_lib,g_ea_sig_mot_rec,g_ea_com,g_ea_vil_pri,g_dp_lib,g_aa_lib,g_rg_cod,g_rg_lib from v_eta);



drop table mps_v_fil_car;
create table mps_v_fil_car as select * from v_fil_car;

drop table mps_bacs_spe;
create table mps_bacs_spe as
select mn.b_mf_cod_nat, mn.b_mf_lib_lon, mn.i_sp_cod , sp.i_sp_lib, i_cl_cod
from b_mef_nat mn
inner join i_spe sp on sp.i_sp_cod = mn.i_sp_cod
where 
--i_cl_cod in ('P','PA') and 
i_ne_cod = 10 -- terminale
and b_mf_dat_fer is null
;


drop table mps_prepas_bacs_pro;
create table mps_prepas_bacs_pro as
select distinct g_ta_cod
from i_cla_spe_fil_cnd_ins ci, a_rec_grp arg
where
--autorisation d'inscription
i_ci_flg_int = 0
--pour des séries de classes pro
AND i_cl_cod in ('P','PA')
and ci.g_ti_cod=arg.g_ti_cod
--les cnd inscription sont sur une CPGE
AND exists (
           select 1
           from sp_g_tri_ins
           where sp_g_tri_ins.g_ti_cod = ci.g_ti_cod
           and g_tf_cod = 1
           )
;

drop table mps_prepas_slt_bacs_pro;
create table mps_prepas_slt_bacs_pro as
select distinct g_ta_cod
from i_cla_spe_fil_cnd_ins ci, a_rec_grp arg
where
--autorisation d'inscription
i_ci_flg_int = 0
--pour des séries de classes pro
AND i_cl_cod in ('P','PA')
and ci.g_ti_cod=arg.g_ti_cod
--les cnd inscription sont sur une CPGE
AND exists (
           select 1
           from sp_g_tri_ins
           where sp_g_tri_ins.g_ti_cod = ci.g_ti_cod
           and g_tf_cod = 1
           )
--pas de cnd insc sur d'autres classes que les séries pro pour cette formation si tu veux des prépas exclusivement réservées aux pros
--si ça peut être ouvert à d'autres séries comme des bacs technos tu enlèves cette condition.
AND NOT EXISTS (
               select 1
               from i_cla_spe_fil_cnd_ins
               where i_cla_spe_fil_cnd_ins.g_ti_cod = ci.g_ti_cod
               and i_cl_cod not in ('P','PA')
               )
order by arg.g_ta_cod
;

--select i_cl_cod, i_sp_cod, g_ta_cod,  nb from mps_admis_bacs_spe;
--select b_mf_cod_nat, b_mf_lib_lon, i_sp_cod , i_sp_lib, i_cl_cod from mps_bacs_spe;
--select g_ta_cod from mps_prepas_bacs_pro;
--select g_ta_cod from mps_prepas_slt_bacs_pro;
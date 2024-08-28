select count(*) from a_adm;

define anneeSeconde = 2022;
define MOYENNE_BAC_I_EB_COD = 20;

CREATE VIEW mps_annee as select max(a_am_dat) from a_adm;

DROP TABLE mps_admis;
CREATE TABLE mps_admis as (select distinct can.g_cn_cod
                                           from
                                           g_can@dbl_arch can,
                                           a_adm@dbl_arch adm,
                                           g_pro_new@dbl_arch  g_pro_new,
                                           sp_g_tri_aff@dbl_arch  aff,
                                           a_sit_voe@dbl_arch  asv
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
select admis.g_cn_cod g_cn_cod,can.i_cl_cod i_cl_cod
from admis,g_can@dbl_arch can 
where admis.g_cn_cod=can.g_cn_cod;
ALTER TABLE mps_bacs_candidats ADD  CONSTRAINT constr_bacs_candidats PRIMARY KEY (g_cn_cod);
                              
DROP TABLE mps_admis_filiere;
CREATE TABLE mps_admis_filiere as (select distinct can.g_cn_cod,aff.g_fl_cod_aff g_fl_cod
                                       from
                                       g_can@dbl_arch  can,
                                       a_adm@dbl_arch  adm,
                                       g_pro_new@dbl_arch  g_pro_new,
                                       sp_g_tri_aff@dbl_arch  aff,
                                       a_sit_voe@dbl_arch  asv
                                       where can.g_cn_cod=adm.g_cn_cod
                                       and adm.g_ta_cod=aff.g_ta_cod
                                       and adm.a_ta_cod in (1,2)
                                       and adm.a_sv_cod=asv.a_sv_cod
                                       and asv.a_sv_flg_aff=1
                                       and can.g_pn_cod=g_pro_new.g_pn_cod
                                       and g_pro_new.g_pm_cod=1);
ALTER TABLE mps_admis_filiere ADD  CONSTRAINT constr2 PRIMARY KEY (g_cn_cod);

DROP TABLE mps_candidats_filieres;
CREATE TABLE mps_candidats_filieres as (select distinct can.g_cn_cod,aff.g_fl_cod_aff g_fl_cod
                                       from
                                       g_can@dbl_arch  can,
                                       a_voe@dbl_arch voe,
                                       g_pro_new@dbl_arch  g_pro_new,
                                       sp_g_tri_aff@dbl_arch  aff
                                       where can.g_cn_cod=voe.g_cn_cod
                                       and voe.g_ta_cod=aff.g_ta_cod
                                       and voe.a_sv_cod > -90
                                       and can.g_pn_cod=g_pro_new.g_pn_cod
                                       and g_pro_new.g_pm_cod=1);
ALTER TABLE mps_candidats_filieres ADD  CONSTRAINT constr2 PRIMARY KEY (g_cn_cod, g_fl_cod);

DROP TABLE mps_admis_formations;                                       
CREATE TABLE mps_admis_formations as (select distinct can.g_cn_cod,aff.g_ta_cod g_ta_cod
                                       from
                                       g_can@dbl_arch  can,
                                       a_adm@dbl_arch  adm,
                                       g_pro_new@dbl_arch  g_pro_new,
                                       sp_g_tri_aff@dbl_arch  aff,
                                       a_sit_voe@dbl_arch  asv
                                       where can.g_cn_cod=adm.g_cn_cod
                                       and adm.g_ta_cod=aff.g_ta_cod
                                       and adm.a_ta_cod in (1,2)
                                       and adm.a_sv_cod=asv.a_sv_cod
                                       and asv.a_sv_flg_aff=1
                                       and can.g_pn_cod=g_pro_new.g_pn_cod
                                       and g_pro_new.g_pm_cod=1);   
ALTER TABLE mps_admis_formations ADD  CONSTRAINT constr_adm_for PRIMARY KEY (g_cn_cod,g_ta_cod);


drop table mps_matieres;
create table mps_matieres as 
     (select distinct g_cn_cod,i_mt_cod from i_bul_sco@dbl_arch 
     where NVL(I_BS_MOY_CDT,-1)>= 0 );
ALTER TABLE mps_matieres ADD  CONSTRAINT constr4 PRIMARY KEY (g_cn_cod,i_mt_cod);
     
drop table mps_moy_gen_candidats;
create table mps_moy_gen_candidats as (select mps_admis.g_cn_cod,AVG(i_bs_moy_cdt) moyenne
             from i_bul_sco@dbl_arch  bul, admis
            where
            bul.g_cn_cod=admis.g_cn_cod
            and i_bs_moy_cdt is not null
            and i_bs_moy_cdt >= 0
            and i_cs_ann = 0
            group by admis.g_cn_cod);
            ALTER TABLE moy_gen_candidats ADD  CONSTRAINT constr5 PRIMARY KEY (g_cn_cod);

drop table mps_moy_sco_candidats;
create table mps_moy_sco_candidats as 
            (select mps_admis.g_cn_cod,i_mt_cod,AVG(i_bs_moy_cdt) moyenne
             from i_bul_sco@dbl_arch  bul, admis
            where
            bul.g_cn_cod=admis.g_cn_cod
            and i_bs_moy_cdt is not null
            and i_bs_moy_cdt >= 0
            and i_cs_ann = 0
            group by admis.g_cn_cod, i_mt_cod);

ALTER TABLE mps_moy_sco_candidats ADD  CONSTRAINT constr6 PRIMARY KEY (g_cn_cod,i_mt_cod);

drop table mps_las;
create table mps_las as
select g_ta_cod from sp_g_tri_aff --@dbl_arch 
where g_ta_lib_voe like '%LAS%' and g_ta_des_ens like '%LAS%';
ALTER TABLE mps_las ADD  CONSTRAINT constrlas PRIMARY KEY (g_ta_cod);

                            
drop table mps_descriptions_formations;
create table mps_descriptions_formations as
select g_ta_cod,g_fr_cod_aff,g_fl_cod_aff,g_fl_lib_aff, g_ta_lib_voe, g_ta_des_deb, g_ta_des_ens 
from sp_g_tri_aff --@dbl_arch
;
ALTER TABLE mps_descriptions_formations ADD  CONSTRAINT constr_de_for PRIMARY KEY (g_ta_cod);

drop table mps_filieres_actives;
create table mps_filieres_actives as
--les recrutements à n-1
(
select distinct g_fl_cod_aff, g_ta_flg_for_las 
from sp_g_tri_aff aff,a_rec_grp arg 
where aff.g_ta_cod=arg.g_ta_cod and NVL(arg.a_rg_pla,0) > 0
union 
--les admissions à n
select distinct g_fl_cod_aff, g_ta_flg_for_las 
from sp_g_tri_aff@dbl_arch aff,a_adm@dbl_arch adm  
where aff.g_ta_cod=adm.g_ta_cod
union 
--les admissions à n-1
select distinct g_fl_cod_aff, g_ta_flg_for_las 
from sp_g_tri_aff aff,a_adm adm  
where aff.g_ta_cod=adm.g_ta_cod);

drop table mps_filieres2;
create table mps_filieres2 as
select g_fl_cod,g_for.g_fr_cod,g_fl_lib, g_fr_lib,g_fr_sig 
from g_fil,g_for where g_fil.g_fr_cod=g_for.g_fr_cod;
                      
drop table mps_filieres_sim;
create table mps_filieres_sim as
select g_fl_cod_ori,g_fl_cod_sim, g_fs_sco, i_tc_cod 
from g_fil_sim --@dbl_arch
order by g_fl_cod_ori,g_fs_sco;

drop table mps_liens_onisep;
create table mps_liens_onisep as 
select g_fl_cod, g_fl_lie_inf 
from 
g_fil --@dbl_arch 
where g_fl_lie_inf is not null;


drop table mps_matieres2;
create table mps_matieres2 as
select i_mt_cod,i_mt_lib 
from i_mat; --@dbl_arch 



create view mps_a_rec_grp as 
(select c_gp_cod,g_ti_cod,g_ta_cod,c_ja_cod,a_rg_pla,a_rg_nbr_sou from a_rec_grp);

create view mps_g_for as 
(select g_fr_cod,g_fr_lib,g_fr_sig_mot_rec from g_for);

create view mps_g_fil as 
(select g_fl_cod,g_fl_lib,g_fl_des_att, g_fl_sig_mot_rec,g_fl_con_lyc_prem,g_fl_con_lyc_term, g_fl_typ_con_ly, G_FL_FLG_APP from g_fil);

create view mps_c_jur_adm as (select * from c_jur_adm);

create view mps_sp_g_tri_aff_typ_for as 
(select g_ta_cod,g_tf_cod from sp_g_tri_aff_typ_for);

create view mps_v_typ_for as 
(select g_tf_cod, g_tf_mot_cle_mdr from v_typ_for);

create view mps_aff as 
(select g_ta_cod,g_fr_cod_aff,g_fl_cod_aff,g_fl_lib_aff,g_ea_cod_aff,g_ta_mot_cle,g_ta_flg_lib_eta,g_ta_lib_eta,G_TA_FLG_FOR_LAS from sp_g_tri_aff);

create view mps_a_rec as 
(select g_ta_cod,g_ti_cod from a_rec);

create view mps_v_eta as
(select g_ea_cod,g_ea_lib,g_ea_sig_mot_rec,g_ea_com,g_ea_vil_pri,g_dp_lib,g_aa_lib,g_rg_cod,g_rg_lib from v_eta);


drop table mps_voeux;
create table mps_voeux as 
(SELECT  DISTINCT voeu.g_cn_cod, aff.g_ta_cod g_ta_cod
                    FROM A_VOE voeu,
                    I_INS ins,
                    A_REC_GRP  arecgrp,
                    SP_G_TRI_AFF  aff
                    WHERE
                    voeu.g_ta_cod=aff.g_ta_cod
                    AND voeu.g_ta_cod = arecgrp.g_ta_cod
                    AND arecgrp.g_ti_cod = ins.g_ti_cod
                    AND voeu.g_cn_cod = ins.g_cn_cod
                    AND voeu.a_sv_cod > -90
                    AND NVL(ins.i_is_val,0) = 1
                    );
                    
drop table mps_stats_mat_bac;
create table mps_stats_mat_bac as
select i_cs_ann,i_mt_cod,i_cl_cod,count(*)  nb
from  i_bul_sco@dbl_arch bul,  mps_admis, 
g_can@dbl_arch can
where i_cs_ann in (0,1) 
and mps_admis.g_cn_cod=can.g_cn_cod 
and bul.g_cn_cod=can.g_cn_cod 
and can.g_cn_ann_sec=&anneeSeconde
group by i_cs_ann,i_mt_cod,i_cl_cod 
order by i_mt_cod;

drop table mps_v_fil_car;
create table mps_v_fil_car as select * from v_fil_car;

--todo: take up to date coordinates and max between old and new capas  
drop table mps_formations;
create table mps_formations as
SELECT G_TA_LIB_VOE,G_FL_COD_AFF,G_EA_COD_AFF,C_GP_COD,formations.g_ta_cod G_TA_COD, rec.g_ti_cod G_Ti_COD, 
G_AA_LIB,G_AA_COD, arec.A_RC_CAP capa, eta.G_EA_LNG lng, eta.G_EA_LAT lat 
FROM  A_REC_GRP@dbl_arch rec, A_REC@dbl_arch arec,G_ETA eta, SP_G_TRI_AFF  formations  
WHERE rec.g_ta_cod=formations.g_ta_cod AND rec.g_ta_cod=arec.g_ta_Cod AND eta.g_ea_cod=formations.g_ea_cod_aff ORDER BY G_FL_COD_AFF,C_GP_COD;


define anneeSeconde = 2020;
define MOYENNE_BAC_I_EB_COD = 20;

DROP TABLE ADMIS;
CREATE TABLE admis as (select distinct can.g_cn_cod
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
ALTER TABLE admis ADD  CONSTRAINT constr PRIMARY KEY (g_cn_cod);
             
drop table bacs_candidats;
create table bacs_candidats as
select admis.g_cn_cod g_cn_cod,can.i_cl_cod i_cl_cod
from admis,g_can@dbl_arch can 
where admis.g_cn_cod=can.g_cn_cod;
ALTER TABLE bacs_candidats ADD  CONSTRAINT constr_bacs_candidats PRIMARY KEY (g_cn_cod);
                              
DROP TABLE admis_filiere;
CREATE TABLE admis_filiere as (select distinct can.g_cn_cod,aff.g_fl_cod_aff g_fl_cod
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
ALTER TABLE admis_filiere ADD  CONSTRAINT constr2 PRIMARY KEY (g_cn_cod);
select count(*) from admis_filiere;

DROP TABLE candidats_filieres;
CREATE TABLE candidats_filieres as (select distinct can.g_cn_cod,aff.g_fl_cod_aff g_fl_cod
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
ALTER TABLE candidats_filieres ADD  CONSTRAINT constr2 PRIMARY KEY (g_cn_cod, g_fl_cod);

DROP TABLE admis_formations;                                       
CREATE TABLE admis_formations as (select distinct can.g_cn_cod,aff.g_ta_cod g_ta_cod
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
ALTER TABLE admis_formations ADD  CONSTRAINT constr_adm_for PRIMARY KEY (g_cn_cod,g_ta_cod);


drop table matieres;
create table matieres as 
     (select distinct g_cn_cod,i_mt_cod from i_bul_sco@dbl_arch 
     where NVL(I_BS_MOY_CDT,-1)>= 0 );
ALTER TABLE matieres ADD  CONSTRAINT constr4 PRIMARY KEY (g_cn_cod,i_mt_cod);
     
     drop table moy_gen_candidats;
     create table moy_gen_candidats as (select admis.g_cn_cod,AVG(i_bs_moy_cdt) moyenne
             from i_bul_sco@dbl_arch  bul, admis
            where
            bul.g_cn_cod=admis.g_cn_cod
            and i_bs_moy_cdt is not null
            and i_bs_moy_cdt >= 0
            and i_cs_ann = 0
            group by admis.g_cn_cod);
            ALTER TABLE moy_gen_candidats ADD  CONSTRAINT constr5 PRIMARY KEY (g_cn_cod);

     drop table moy_sco_candidats;
            create table moy_sco_candidats as 
            (select admis.g_cn_cod,i_mt_cod,AVG(i_bs_moy_cdt) moyenne
             from i_bul_sco@dbl_arch  bul, admis
            where
            bul.g_cn_cod=admis.g_cn_cod
            and i_bs_moy_cdt is not null
            and i_bs_moy_cdt >= 0
            and i_cs_ann = 0
            group by admis.g_cn_cod, i_mt_cod);
            ALTER TABLE moy_sco_candidats ADD  CONSTRAINT constr6 PRIMARY KEY (g_cn_cod,i_mt_cod);

drop table moyenne_bac;
create table moyenne_bac as
select admis.g_cn_cod g_cn_cod,i_ce_not note from i_can_epr_bac@dbl_arch bac,admis
                            where
                            bac.g_cn_cod=admis.g_cn_cod
                            and i_ce_not is not null
                            and i_ce_not >= 0
                            and i_eb_cod=&MOYENNE_BAC_I_EB_COD;
            ALTER TABLE moyenne_bac ADD  CONSTRAINT constr_moy_bac PRIMARY KEY (g_cn_cod);

drop table las;
create table las as
select g_ta_cod from sp_g_tri_aff --@dbl_arch 
where g_ta_lib_voe like '%LAS%' and g_ta_des_ens like '%LAS%';
ALTER TABLE las ADD  CONSTRAINT constrlas PRIMARY KEY (g_ta_cod);

                            
drop table descriptions_formations;
create table descriptions_formations as
select g_ta_cod,g_fr_cod_aff,g_fl_cod_aff,g_fl_lib_aff, g_ta_lib_voe, g_ta_des_deb, g_ta_des_ens 
from sp_g_tri_aff --@dbl_arch
;
ALTER TABLE descriptions_formations ADD  CONSTRAINT constr_de_for PRIMARY KEY (g_ta_cod);

drop table filieres_actives;
create table filieres_actives as
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

drop table filieres2;
create table filieres2 as
select g_fl_cod,g_for.g_fr_cod,g_fl_lib, g_fr_lib,g_fr_sig 
from g_fil,g_for where g_fil.g_fr_cod=g_for.g_fr_cod;
                      
drop table filieres_sim;
create table filieres_sim as
select g_fl_cod_ori,g_fl_cod_sim, g_fs_sco, i_tc_cod 
from g_fil_sim --@dbl_arch
order by g_fl_cod_ori,g_fs_sco;

drop table liens_onisep;
create table liens_onisep as 
select g_fl_cod, g_fl_lie_inf 
from 
g_fil --@dbl_arch 
where g_fl_lie_inf is not null;


drop table matieres2;
create table matieres2 as
select i_mt_cod,i_mt_lib 
from i_mat; --@dbl_arch 


--drop table stats_sec;
--create table stats_sec as 
--select g_cn_ann_sec, count(*) nb from g_can@dbl_arch  group by g_cn_ann_sec;


drop table stats_mat_bac;
create table stats_mat_bac as
select i_cs_ann,i_mt_cod,i_cl_cod,count(*)  nb
from  i_bul_sco@dbl_arch bul,  admis, 
g_can@dbl_arch can
where i_cs_ann in (0,1) 
and admis.g_cn_cod=can.g_cn_cod 
and bul.g_cn_cod=can.g_cn_cod 
and can.g_cn_ann_sec=&anneeSeconde
group by i_cs_ann,i_mt_cod,i_cl_cod 
order by i_mt_cod;

drop table v_fil_car_mps;
create table v_fil_car_mps as select * from v_fil_car;


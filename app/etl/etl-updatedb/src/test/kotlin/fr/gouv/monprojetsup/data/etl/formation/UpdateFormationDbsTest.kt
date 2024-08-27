package fr.gouv.monprojetsup.data.etl.formation

import fr.gouv.monprojetsup.data.etl.sources.MpsDataPort
import fr.gouv.monprojetsup.data.formation.entity.FormationEntity
import fr.gouv.monprojetsup.data.formation.infrastructure.FormationDb
import fr.gouv.monprojetsup.data.etl.BDDRepositoryTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired

class UpdateFormationDbsTest : BDDRepositoryTest() {

    @Autowired
    lateinit var formationsdb : FormationDb

    @Autowired
    lateinit var updateFormationDbs: UpdateFormationDbs

    @Autowired
    lateinit var mpsDataPort: MpsDataPort

    @Nested
    inner class UpdateFormationsDbTest {

        @Test
        fun `Doit réussir à sauver une entity formation avec des descriptifs longs`() {
            // When
            val entity = FormationEntity()
            /*Caused by: java.sql.BatchUpdateException:
            Batch entry 82 insert into formation
            (,,formations_associees,label,label_details,las,liens,metiers,mots_clefs,psup_fl_ids,stats,voeux,id)
            values
            (),?,('BPJEPS'),('),(NULL),('[{"nom":"La liste des spécialités https://www.sports.gouv.fr/bpjeps-21","url":"La liste des spécialités https://www.sports.gouv.fr/bpjeps-21"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Activités de la forme - en apprentissage","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Activités physiques pour tous - en apprentissage","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Activités équestres - en apprentissage","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Activités aquatiques et de la natation - en apprentissage","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Activités du canoë-kayak et disciplines associées en eau vive jusqu''à classe III, en eau calme et en mer jusqu''à IV Beaufort","url":"https://explorer-avenirs.onisep.fr/http/redirection/formation/slug/FOR.5260"},{"nom":"BPJEPS - Spécialité animateur - Mention Animation sociale","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Animateur - Mention Activités du cirque","url":"https://explorer-avenirs.onisep.fr/http/redirection/formation/slug/FOR.5239"},{"nom":"BPJEPS - Spécialité Animateur - Mention Animation culturelle","url":"https://explorer-avenirs.onisep.fr/http/redirection/formation/slug/FOR.5222"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Activités du Canoë-Kayak et disciplines associées en mer","url":"https://explorer-avenirs.onisep.fr/http/redirection/formation/slug/FOR.5227"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Judo-Jujitsu","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Activités de la forme","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Animateur - Mention Loisirs tous publics","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Canoë-Kayak en eau vive","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Golf","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Voile multi-supports jusqu''à 6 miles nautique d''un abri","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Tennis de table","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Activités aquatiques et de la natation","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Activités équestres","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"},{"nom":"BPJEPS - Spécialité Educateur sportif - Mention Activités physiques pour tous","url":"https://sport.onisep.fr/je-choisis-un-diplome-professionnel-du-sport/les-diplomes-d-etat-du-sport"}]'),?,?,?,('{"admissions":{"P":{"frequencesCumulees":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,2,3,6,10,13,15,17,20,23,25,25,26,27,28,28,28,28,28,28,28],"middle50":{"rangEch25":23,"rangEch50":25,"rangEch75":28,"rangEch10":20,"rangEch90":29,"rangMax":39,"triple":{"left":11.5,"middle":12.5,"right":14.0}}},"":{"frequencesCumulees":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,3,3,4,16,29,50,74,101,121,137,145,153,159,163,165,166,167,168,168,168,168,168,168],"middle50":{"rangEch25":22,"rangEch50":24,"rangEch75":26,"rangEch10":20,"rangEch90":28,"rangMax":39,"triple":{"left":11.0,"middle":12.0,"right":13.0}}},"Générale":{"frequencesCumulees":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,9,21,34,49,54,60,62,65,67,69,70,70,70,70,70,70,70,70,70],"middle50":{"rangEch25":22,"rangEch50":24,"rangEch75":25,"rangEch10":21,"rangEch90":28,"rangMax":39,"triple":{"left":11.0,"middle":12.0,"right":12.5}}}},"nbAdmisParBac":{"STAV":2,"P":41,"":201,"PA":10,"STI2D":6,"ST2S":15,"Générale":100,"STL":1,"STD2A":2,"STHR":1,"STMG":23},"pctAdmisParBac":{"STAV":0,"P":10,"":50,"PA":2,"STI2D":1,"ST2S":4,"Générale":25,"STL":0,"STD2A":0,"STHR":0,"STMG":6},"nbAdmisParSpecialite":{"1":101,"2":23,"4":3,"5":10,"6":150,"7":201,"8":178,"9":1,"138":100,"11":160,"1037":41,"1038":23,"1039":15,"1040":7,"1041":1,"1042":2,"1043":2,"1050":1,"282":13,"1051":1,"1052":15,"1180":1,"1181":9,"1053":23,"1054":23,"1055":6,"1056":6,"1057":1,"1058":2,"1059":2,"1062":42,"40":9,"553":3,"1065":8,"1067":15,"1068":2,"1069":1,"1070":9,"1071":1,"1073":1,"50":201,"51":201,"1076":26,"1077":2,"1078":2,"1079":2,"700":50,"701":32,"702":61,"703":50,"320":15,"833":2,"321":15,"706":2,"710":3,"1095":10,"1096":6,"713":41,"714":41,"1098":40,"587":33,"1105":1,"339":2,"1111":2,"1116":2,"740":1,"1002":150,"1003":1,"1006":1,"1008":1,"1009":6,"116":12,"118":30,"887":4,"890":11,"126":51},"pctAdmisParSpecialite":{"1":4,"2":1,"4":0,"5":0,"6":6,"7":9,"8":8,"9":0,"138":4,"11":7,"1037":2,"1038":1,"1039":1,"1040":0,"1041":0,"1042":0,"1043":0,"1050":0,"282":1,"1051":0,"1052":1,"1180":0,"1181":0,"1053":1,"1054":1,"1055":0,"1056":0,"1057":0,"1058":0,"1059":0,"1062":2,"40":0,"553":0,"1065":0,"1067":1,"1068":0,"1069":0,"1070":0,"1071":0,"1073":0,"50":9,"51":9,"1076":1,"1077":0,"1078":0,"1079":0,"700":2,"701":1,"702":3,"703":2,"320":1,"833":0,"321":1,"706":0,"710":0,"1095":0,"1096":0,"713":2,"714":2,"1098":2,"587":1,"1105":0,"339":0,"1111":0,"1116":0,"740":0,"1002":6,"1003":0,"1006":0,"1008":0,"1009":0,"116":1,"118":1,"887":0,"890":0,"126":2},"formationsSimilaires":{"0":{"fl851":126470,"fl850":100000,"fl1828":204410,"fl671":264394,"fl660001":202630,"fl1671":178430,"fl640021":1024194,"fl650007":368764,"fl640012":1024194,"fl640022":352097,"fl2020":30236,"fl650004":2343866,"fl640002":559537,"fl828":307040,"fl849":108823,"fl650002":359537,"fl640016":1024194,"fl640004":2337984,"fl826":309982,"fl640007":268764,"fl650022":352097,"fl825":234982,"fl898":75000},"1":{"fl851":156303,"fl850":103225,"fl1828":214919,"fl671":260693,"fl660001":202630,"fl1671":183063,"fl640021":1026409,"fl650007":375418,"fl640012":1026409,"fl640022":248145,"fl2020":30148,"fl650004":2344434,"fl640002":564156,"fl828":307321,"fl849":106451,"fl650002":443860,"fl640016":1026409,"fl640004":2337984,"fl826":296177,"fl640007":248145,"fl650022":275418,"fl825":231662,"fl898":200000},"2":{"fl851":150102,"fl850":100000,"fl1828":209146,"fl671":264791,"fl660001":202630,"fl1671":177545,"fl640021":999836,"fl650007":363188,"fl640012":1022321,"fl640022":247803,"fl2020":37888,"fl650004":2343388,"fl640002":544708,"fl828":311776,"fl849":100000,"fl650002":344708,"fl640016":1022321,"fl640004":2337984,"fl826":314894,"fl640007":263188,"fl650022":247803,"fl825":243169,"fl898":169646,"fl842":3788},"3":{"fl851":127777,"fl850":100000,"fl1828":196527,"fl671":296379,"fl660001":208184,"fl1671":193749,"fl640021":1018105,"fl650007":364944,"fl640012":1018105,"fl640022":246194,"fl2020":58737,"fl650004":2343538,"fl640002":526679,"fl828":299157,"fl849":100000,"fl650002":326679,"fl640016":1018105,"fl640004":2337984,"fl826":269295,"fl640007":264944,"fl650022":246194,"fl825":208184,"fl898":100000}}}'),?,('fr640'))*/
            entity.apprentissage = true
            entity.capacite = 1251
            entity.criteresAnalyse = listOf(3)
            entity.descriptifAttendus = null
            entity.descriptifConseils = null
            entity.descriptifDiplome = null
            entity.descriptifGeneral =
                """
            Tu aimes le sport, et tu as envie de transmettre cette passion sans avoir besoin de faire des
            années d’études ? Tu es au bon endroit. Grâce à cette formation,
            tu vas apprendre à devenir éducateur ou moniteur sportif
            (tu as le choix entre le diplôme en animation et celui en sport), et tu auras aussi des notions
            de gestion si un jour tu as envie de participer à l’organisation d’une structure.
            En plus, il s’agit d’une formation en alternance, donc tu vas vite commencer à exercer ton métier.
            Cette formation est disponible dans plus de 40 spécialités dont
            [la liste est disponible ici](https://www.sports.gouv.fr/bpjeps-21).
            Pour y accéder, tu devras avoir un niveau de base dans la spécialité que tu souhaites enseigner,
                mais il ne s''agit pas d''une formation pour devenir sportif de haut niveau !
                Où se former ? Dans des centres spécifiques, comme des fédérations sportives, des organismes privés,
            des établissements nationaux. Tu trouveras les différents centres sur ParcourSup.
            """
            entity.duree = 3
            entity.formationsAssociees = listOf()

            entity.label = "BPJEPS"
            entity.labelDetails =
                """BPJEPS groupe [fl640052, fl640032, fl640010, fl640034, fl640012, fl650036, 
                    fl640035, fl640016, fl640015, fl650032, fl640039, fl650052, fl640021, fl650007, 
                    fl650028, fl650005, fl640022, fl650004, fl650025, fl640002, fl650024, fl650002, 
                    fl640004, fl650022, fl640007, fl640028]
                    """

            // Then
            assertDoesNotThrow { formationsdb.saveAll(listOf(entity)) }
        }

        @Test
        fun `Plus de 95% des formations en base doivent réussir le test d'intégrité`() {
            updateFormationDbs.updateFormationDbs()
            val formations = formationsdb.findAll()
            val nbFormations = formations.size
            val nbFormationsIntegres = formations.count { it.integrityCheck() }
            assert( nbFormationsIntegres > 95 * nbFormations / 100)

        }

    }

}

package fr.gouv.monprojetsup.recherche.usecase

import fr.gouv.monprojetsup.commun.Constantes.NOTE_MAXIMAL
import fr.gouv.monprojetsup.commun.Constantes.TAILLE_ECHELLON_NOTES
import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation.FicheFormationPourProfil.MoyenneGeneraleDesAdmis
import fr.gouv.monprojetsup.recherche.domain.port.MoyenneGeneraleAdmisRepository
import org.springframework.stereotype.Service

@Service
class MoyenneGeneraleDesAdmisService(
    val moyenneGeneraleAdmisRepository: MoyenneGeneraleAdmisRepository,
) {
    fun recupererMoyenneGeneraleDesAdmisDUneFormation(
        baccalaureat: Baccalaureat?,
        idFormation: String,
        classe: ChoixNiveau,
    ): MoyenneGeneraleDesAdmis? {
        return when (classe) {
            ChoixNiveau.SECONDE, ChoixNiveau.SECONDE_STHR, ChoixNiveau.SECONDE_TMD, ChoixNiveau.NON_RENSEIGNE -> null
            ChoixNiveau.PREMIERE, ChoixNiveau.TERMINALE -> recupererMoyenneGeneraleDesAdmisDUneFormation(baccalaureat, idFormation)
        }
    }

    private fun recupererMoyenneGeneraleDesAdmisDUneFormation(
        baccalaureat: Baccalaureat?,
        idFormation: String,
    ): MoyenneGeneraleDesAdmis? {
        val frequencesCumulees = moyenneGeneraleAdmisRepository.recupererFrequencesCumuleesDeToutLesBacs(idFormation)
        return when {
            lesDonnesSontVides(frequencesCumulees) -> null
            leBaccalaureatEstTrouve(
                baccalaureat,
                frequencesCumulees,
            ) && assezDAdmisPourLeBaccalaureat(frequencesCumulees, baccalaureat!!) -> {
                MoyenneGeneraleDesAdmis(
                    idBaccalaureat = baccalaureat.id,
                    nomBaccalaureat = baccalaureat.nom,
                    centiles = transformerFrequencesCumuleesEnCentiles(frequencesCumulees[baccalaureat.id]!!),
                )
            }
            else -> {
                MoyenneGeneraleDesAdmis(
                    idBaccalaureat = null,
                    nomBaccalaureat = null,
                    centiles =
                        transformerFrequencesCumuleesEnCentiles(
                            frequencesCumulees = calculerLesFrequencesCumuleesDeTousLesBaccalaureatsConfondus(frequencesCumulees),
                        ),
                )
            }
        }
    }

    private fun assezDAdmisPourLeBaccalaureat(
        frequencesCumulees: Map<String, List<Int>>,
        baccalaureat: Baccalaureat,
    ) = frequencesCumulees[baccalaureat.id]!!.last() > MINIMUM_ADMIS

    private fun leBaccalaureatEstTrouve(
        baccalaureat: Baccalaureat?,
        frequencesCumulees: Map<String, List<Int>>,
    ) = baccalaureat?.id != null && frequencesCumulees.containsKey(baccalaureat.id)

    private fun lesDonnesSontVides(frequencesCumulees: Map<String, List<Int>>) =
        frequencesCumulees.isEmpty() || frequencesCumulees.values.flatten().isEmpty()

    private fun calculerLesFrequencesCumuleesDeTousLesBaccalaureatsConfondus(
        listeDesFrequencesCumulees: Map<String, List<Int>>,
    ): List<Int> {
        val tailleTableauNotes = (NOTE_MAXIMAL / TAILLE_ECHELLON_NOTES).toInt()
        var frequencesCumulees = List(tailleTableauNotes) { 0 }
        listeDesFrequencesCumulees.forEach {
            frequencesCumulees =
                frequencesCumulees.zip(it.value) { valeurSommeFrequence, valeurFrequence ->
                    valeurSommeFrequence + valeurFrequence
                }
        }
        return frequencesCumulees
    }

    private fun transformerFrequencesCumuleesEnCentiles(frequencesCumulees: List<Int>): List<MoyenneGeneraleDesAdmis.Centile> {
        val totalEleves = frequencesCumulees.last()
        val centiles = listOf(CENTILLE_5EME, CENTILLE_25EME, CENTILLE_75EME, CENTILLE_95EME)
        val moitieDesCentiles = centiles.size / 2
        return centiles.mapIndexed { index: Int, centile: Int ->
            val indexDeLaFrequenceDuCentile =
                frequencesCumulees.indexOfFirst {
                    it >= (centile * totalEleves / CENTILLE_100EME.toFloat())
                }
            MoyenneGeneraleDesAdmis.Centile(
                centile = centile,
                note = recupererLaNoteSelonLeCoteDeLIntervalle(index, moitieDesCentiles, indexDeLaFrequenceDuCentile),
            )
        }
    }

    private fun recupererLaNoteSelonLeCoteDeLIntervalle(
        indexDuCentileEnCours: Int,
        moitieDesCentiles: Int,
        indexDeLaFrequenceDuCentile: Int,
    ): Float {
        val note =
            if (indexDuCentileEnCours < moitieDesCentiles) {
                indexDeLaFrequenceDuCentile * TAILLE_ECHELLON_NOTES
            } else {
                indexDeLaFrequenceDuCentile * TAILLE_ECHELLON_NOTES + TAILLE_ECHELLON_NOTES
            }
        return note
    }

    companion object {
        private const val CENTILLE_5EME = 5
        private const val CENTILLE_25EME = 25
        private const val CENTILLE_75EME = 75
        private const val CENTILLE_95EME = 95
        private const val CENTILLE_100EME = 100

        private const val MINIMUM_ADMIS = 30
    }
}

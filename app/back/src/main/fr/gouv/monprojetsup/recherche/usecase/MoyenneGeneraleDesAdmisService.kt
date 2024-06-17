package fr.gouv.monprojetsup.recherche.usecase

import fr.gouv.monprojetsup.commun.Constantes.NOTE_MAXIMAL
import fr.gouv.monprojetsup.commun.Constantes.TAILLE_ECHELLON_NOTES
import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat
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
                    centilles = transformerFrequencesCumuleesEnCentilles(frequencesCumulees[baccalaureat.id]!!),
                )
            }
            else -> {
                MoyenneGeneraleDesAdmis(
                    idBaccalaureat = null,
                    nomBaccalaureat = null,
                    centilles =
                        transformerFrequencesCumuleesEnCentilles(
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

    private fun transformerFrequencesCumuleesEnCentilles(frequencesCumulees: List<Int>): List<MoyenneGeneraleDesAdmis.Centille> {
        val totalEleves = frequencesCumulees.last()
        val centilles = listOf(CENTILLE_5EME, CENTILLE_25EME, CENTILLE_75EME, CENTILLE_95EME)
        val moitieDesCentilles = centilles.size / 2
        return centilles.mapIndexed { index: Int, centille: Int ->
            val indexDeLaFrequenceDuCentille =
                frequencesCumulees.indexOfFirst {
                    it >= (centille * totalEleves / CENTILLE_100EME.toFloat())
                }
            MoyenneGeneraleDesAdmis.Centille(
                centille = centille,
                note = recupererLaNoteSelonLeCoteDeLIntervalle(index, moitieDesCentilles, indexDeLaFrequenceDuCentille),
            )
        }
    }

    private fun recupererLaNoteSelonLeCoteDeLIntervalle(
        indexDuCentilleEnCours: Int,
        moitieDesCentilles: Int,
        indexDeLaFrequenceDuCentille: Int,
    ): Float {
        val note =
            if (indexDuCentilleEnCours < moitieDesCentilles) {
                indexDeLaFrequenceDuCentille * TAILLE_ECHELLON_NOTES
            } else {
                indexDeLaFrequenceDuCentille * TAILLE_ECHELLON_NOTES + TAILLE_ECHELLON_NOTES
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

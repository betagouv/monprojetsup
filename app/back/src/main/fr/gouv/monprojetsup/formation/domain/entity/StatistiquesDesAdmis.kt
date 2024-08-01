package fr.gouv.monprojetsup.formation.domain.entity

import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat

data class StatistiquesDesAdmis(
    val repartitionAdmis: RepartitionAdmis,
    val moyenneGeneraleDesAdmis: MoyenneGeneraleDesAdmis?,
) {
    data class RepartitionAdmis(
        val total: Int,
        val parBaccalaureat: List<TotalAdmisPourUnBaccalaureat>,
    ) {
        data class TotalAdmisPourUnBaccalaureat(
            val baccalaureat: Baccalaureat,
            val nombreAdmis: Int,
        )
    }

    data class MoyenneGeneraleDesAdmis(
        val baccalaureat: Baccalaureat?,
        val centiles: List<Centile>,
    ) {
        data class Centile(
            val centile: Int,
            val note: Float,
        )
    }
}

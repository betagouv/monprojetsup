package fr.gouv.monprojetsup.data.app.domain

data class StatistiquesDesAdmis(
    val repartitionAdmis: RepartitionAdmis,
    val moyenneGeneraleDesAdmis: MoyenneGeneraleDesAdmis?,
) {
    data class RepartitionAdmis(
        val total: Int,
        val parBaccalaureat: List<TotalAdmisPourUnBaccalaureat>,
    ) {
        data class TotalAdmisPourUnBaccalaureat(
            val baccalaureat: fr.gouv.monprojetsup.data.app.domain.Baccalaureat,
            val nombreAdmis: Int,
        )
    }

    data class MoyenneGeneraleDesAdmis(
        val baccalaureat: fr.gouv.monprojetsup.data.app.domain.Baccalaureat?,
        val centiles: List<Centile>,
    ) {
        data class Centile(
            val centile: Int,
            val note: Float,
        )
    }
}

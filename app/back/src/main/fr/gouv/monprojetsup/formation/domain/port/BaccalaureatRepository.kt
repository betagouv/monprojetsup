package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.Baccalaureat

interface BaccalaureatRepository {
    fun recupererUnBaccalaureatParIdExterne(idExterneBaccalaureat: String): Baccalaureat?

    fun recupererDesBaccalaureatsParIdsExternes(idsExternesBaccalaureats: List<String>): List<Baccalaureat>
}

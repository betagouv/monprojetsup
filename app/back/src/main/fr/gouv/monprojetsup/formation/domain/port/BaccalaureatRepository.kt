package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.Baccalaureat

interface BaccalaureatRepository {
    fun recupererUnBaccalaureatParIdExterne(idExterneBaccalaureat: String): Baccalaureat?

    fun recupererUnBaccalaureat(id: String): Baccalaureat?

    fun recupererTousLesBaccalaureats(): List<Baccalaureat>
}

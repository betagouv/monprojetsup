package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat

interface BaccalaureatRepository {
    fun recupererUnBaccalaureatParIdExterne(idExterneBaccalaureat: String): Baccalaureat?

    fun recupererUnBaccalaureat(id: String): Baccalaureat?

    fun recupererTousLesBaccalaureats(): List<Baccalaureat>
}

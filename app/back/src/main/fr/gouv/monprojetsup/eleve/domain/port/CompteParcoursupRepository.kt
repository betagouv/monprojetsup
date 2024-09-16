package fr.gouv.monprojetsup.eleve.domain.port

import java.util.UUID

interface CompteParcoursupRepository {
    fun recupererIdCompteParcoursup(idEleve: UUID): Int?
}

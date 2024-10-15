package fr.gouv.monprojetsup.eleve.domain.port

interface CompteParcoursupRepository {
    fun recupererIdCompteParcoursup(idEleve: String): Int?
}

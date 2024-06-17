package fr.gouv.monprojetsup.recherche.domain.port

interface MoyenneGeneraleAdmisRepository {
    fun recupererFrequencesCumuleesDeToutLesBacs(idFormation: String): Map<String, List<Int>>
}

package fr.gouv.monprojetsup.data.etl.parametre

import fr.gouv.monprojetsup.data.Constants.MAJ_SUGGESTIONS_REF_DATA_NECESSAIRE
import fr.gouv.monprojetsup.data.parametre.entity.ParametreEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
interface ParametreDb :
    JpaRepository<ParametreEntity, String>

@Component
class UpdateParametreDb(
    private val parametreDb: ParametreDb
) {

    companion object {
        private const val ETL_EN_COURS = "ETL_EN_COURS"
        private const val FORCE_FORMATIONS_UPDATE = "FORCE_FORMATIONS_UPDATE"
    }

    fun setEtlEnCours(b: Boolean) {
        val etlEnCoursParam = ParametreEntity()
        etlEnCoursParam.id = ETL_EN_COURS
        etlEnCoursParam.statut = b
        parametreDb.save(etlEnCoursParam)

        val majSuggestionsParam = ParametreEntity()
        majSuggestionsParam.id = MAJ_SUGGESTIONS_REF_DATA_NECESSAIRE
        majSuggestionsParam.statut = true
        parametreDb.save(majSuggestionsParam)
    }

}
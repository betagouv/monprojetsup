package fr.gouv.monprojetsup.data.etl.parametre

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
    }

    fun setEtlEnCours(b: Boolean) {
        val newParam = ParametreEntity()
        newParam.id = ETL_EN_COURS
        newParam.statut = b
        parametreDb.save(newParam)
    }

}
package fr.gouv.monprojetsup.data.etl.parametre

import fr.gouv.monprojetsup.data.parametre.entity.ParametreEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
interface ParametreDb :
    JpaRepository<ParametreEntity, String> {
}

@Component
class UpdateParametreDb(
    private val parametreDb: ParametreDb
) {

    companion object {
        private const val ETL_EN_COURS = "ETL_EN_COURS"
        private const val FORCE_FORMATIONS_UPDATE = "FORCE_FORMATIONS_UPDATE"
    }

    fun setEtlEnCours(b: Boolean) {
        val newParam = ParametreEntity()
        newParam.id = ETL_EN_COURS
        newParam.statut = b
        parametreDb.save(newParam)
    }

    fun getFormationUpdateForcedFlag() : Boolean {
        return parametreDb
            .findById(FORCE_FORMATIONS_UPDATE)
            .orElse(ParametreEntity().apply { statut = false })
            .statut
    }

    fun setFormationUpdateForcedFlag(b: Boolean) {
        val newParam = ParametreEntity()
        newParam.id = FORCE_FORMATIONS_UPDATE
        newParam.statut = b
        parametreDb.save(newParam)
    }

}
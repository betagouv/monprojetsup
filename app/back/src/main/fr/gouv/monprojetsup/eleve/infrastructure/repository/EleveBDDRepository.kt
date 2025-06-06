package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.eleve.infrastructure.entity.ProfilEleveEntity
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import org.springframework.stereotype.Repository

@Repository
class EleveBDDRepository(
    private val eleveJPARepository: EleveJPARepository,
    private val logger: MonProjetSupLogger,
) : EleveRepository {
    override fun recupererUnEleve(id: String): ProfilEleve {
        val eleve = recupererEleve(id) ?: ProfilEleve.SansCompte(id)
        return eleve
    }

    override fun creerUnEleve(id: String): ProfilEleve.AvecProfilExistant {
        if (eleveJPARepository.existsById(id)) {
            logger.warn(type = "ID_ELEVE_EXISTE_DEJA", message = "L'élève $id a voulu être crée alors qu'il existe déjà en base")
            return recupererEleve(id)!!
        } else {
            val entity = ProfilEleveEntity()
            entity.id = id
            val entitySauvegarde = eleveJPARepository.save(entity)
            logger.info(type = "ELEVE_CREE", message = "Un élève a été crée en base")
            return entitySauvegarde.toProfilEleve()
        }
    }

    @Throws(MonProjetSupNotFoundException::class)
    override fun mettreAJourUnProfilEleve(profilEleve: ProfilEleve.AvecProfilExistant) {
        try {
            eleveJPARepository.getReferenceById(profilEleve.id)
            eleveJPARepository.save(ProfilEleveEntity(profilEleve))
        } catch (exception: Exception) {
            throw MonProjetSupNotFoundException("ELEVE_NON_CREE", "L'élève ${profilEleve.id} n'a pas été crée en base")
        }
    }

    private fun recupererEleve(id: String): ProfilEleve.AvecProfilExistant? {
        val entity = eleveJPARepository.findByIdWithIdParcoursup(id).firstOrNull()
        val compteParcoursupLie = entity?.getIdParcoursup() != null
        return entity?.getProfilEleve()?.toProfilEleve(compteParcoursupLie)
    }
}

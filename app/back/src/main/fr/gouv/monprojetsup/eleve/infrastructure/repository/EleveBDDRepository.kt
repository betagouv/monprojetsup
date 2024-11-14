package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.eleve.infrastructure.entity.ProfilEleveAvecCompteParcoursupEntity
import fr.gouv.monprojetsup.eleve.infrastructure.entity.ProfilEleveEntity
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class EleveBDDRepository(
    private val eleveJPARepository: EleveJPARepository,
    private val entityManager: EntityManager,
    private val logger: MonProjetSupLogger,
) : EleveRepository {
    @Transactional(readOnly = true)
    override fun recupererUnEleve(id: String): ProfilEleve {
        val eleve = recupererEleveAvecCompteParcoursup(id) ?: ProfilEleve.SansCompte(id)
        return eleve
    }

    @Transactional(readOnly = false)
    override fun creerUnEleve(id: String): ProfilEleve.AvecProfilExistant {
        if (eleveJPARepository.existsById(id)) {
            logger.warn(type = "ID_ELEVE_EXISTE_DEJA", message = "L'élève $id a voulu être crée alors qu'il existe déjà en base")
            return recupererEleveAvecCompteParcoursup(id)!!
        } else {
            val entity = ProfilEleveEntity()
            entity.id = id
            val entitySauvegarde = eleveJPARepository.save(entity)
            logger.info(type = "ELEVE_CREE", message = "Un élève a été crée en base")
            return entitySauvegarde.toProfilEleve()
        }
    }

    @Throws(MonProjetSupNotFoundException::class)
    @Transactional(readOnly = false)
    override fun mettreAJourUnProfilEleve(profilEleve: ProfilEleve.AvecProfilExistant) {
        try {
            eleveJPARepository.getReferenceById(profilEleve.id)
            eleveJPARepository.save(ProfilEleveEntity(profilEleve))
        } catch (exception: Exception) {
            throw MonProjetSupNotFoundException("ELEVE_NON_CREE", "L'élève ${profilEleve.id} n'a pas été crée en base")
        }
    }

    private fun recupererEleveAvecCompteParcoursup(id: String): ProfilEleve.AvecProfilExistant? {
        return entityManager.createNativeQuery(
            """
            SELECT profil_eleve.id as id,
                   profil_eleve.situation as situation,
                   profil_eleve.classe as classe,
                   profil_eleve.duree_etudes_prevue as duree_etudes_prevue,
                   profil_eleve.alternance as alternance,
                   profil_eleve.id_baccalaureat as id_baccalaureat,
                   profil_eleve.specialites as specialites,
                   profil_eleve.domaines as domaines,
                   profil_eleve.centres_interets as centres_interets,
                   profil_eleve.metiers_favoris as metiers_favoris,
                   profil_eleve.communes_favorites as communes_favorites,
                   profil_eleve.formations_favorites as formations_favorites,
                   profil_eleve.moyenne_generale as moyenne_generale,
                   profil_eleve.corbeille_formations as corbeille_formations,
                   eleve_compte_parcoursup.id_parcoursup as id_parcoursup
            FROM profil_eleve
                     LEFT JOIN eleve_compte_parcoursup ON eleve_compte_parcoursup.id_eleve = profil_eleve.id
            WHERE profil_eleve.id = :id;
            """.trimIndent(),
            ProfilEleveAvecCompteParcoursupEntity::class.java,
        )
            .setParameter("id", id)
            .setMaxResults(1)
            .resultList
            .firstOrNull()
            ?.let { it as ProfilEleveAvecCompteParcoursupEntity }?.toProfilEleve()
    }
}

package fr.gouv.monprojetsup.data.etl.formationmetier

import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.formationmetier.entity.FormationMetierEntity
import fr.gouv.monprojetsup.data.formationmetier.entity.FormationMetierEntityId
import org.hibernate.StatelessSession
import org.hibernate.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
interface JoinFormationMetiersDb :
    JpaRepository<FormationMetierEntity, String>

@Component
class UpdateFormationMetierDbs(
    private val joinFormationMetiersDb: JoinFormationMetiersDb,
    private val mpsDataPort : MpsDataPort,
    private val sessionFactory : org.hibernate.SessionFactory
) {

    fun update() {

        val statelessSession: StatelessSession = sessionFactory.openStatelessSession()
        val transaction: Transaction = statelessSession.beginTransaction()
        val hql = "DELETE FROM ${FormationMetierEntity::class.simpleName}"
        val query = statelessSession.createMutationQuery(hql)
        query.executeUpdate()


        val formationsVersMetiers = mpsDataPort.getFormationsVersMetiersEtMetiersAssocies()
        val formationRefIds = mpsDataPort.getFormationsMpsIds()
        val metiersRefIds = mpsDataPort.getMetiersMpsIds()

        //on garantit l'unicité des paires
        formationsVersMetiers.entries
            .flatMap { (formationId, metiersIds) ->
                metiersIds.map { metierId ->
                    Pair(formationId, metierId)
                }
            }.filter { (formationId, metierId) ->
                formationRefIds.contains(formationId) && metiersRefIds.contains(metierId)
            }.distinct()
            .forEach { (formationId, metierId) ->
                val entity = FormationMetierEntity()
                entity.id = FormationMetierEntityId(formationId, metierId)
                entity.idFormation = formationId
                entity.idMetier = metierId
                statelessSession.insert(entity)
            }

        transaction.commit()
        statelessSession.close()
    }

    fun clearAll() {
        joinFormationMetiersDb.deleteAll()
    }

}
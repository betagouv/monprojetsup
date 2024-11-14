package fr.gouv.monprojetsup.data.etl

import org.hibernate.StatelessSession
import org.hibernate.Transaction
import org.springframework.stereotype.Component

@Component
class BatchUpdate(
    private val sessionFactory: org.hibernate.SessionFactory
) {

    fun clearEntities(entityName: String) {
        val statelessSession: StatelessSession = sessionFactory.openStatelessSession()
        val transaction: Transaction = statelessSession.beginTransaction()

        val hql = "DELETE FROM $entityName"
        val query = statelessSession.createMutationQuery(hql)
        query.executeUpdate()

        transaction.commit()
        statelessSession.close()
    }

    fun <T> setEntities(entityName: String, entities: Collection<T> ) {
        val statelessSession: StatelessSession = sessionFactory.openStatelessSession()
        val transaction: Transaction = statelessSession.beginTransaction()

        val hql = "DELETE FROM $entityName"
        val query = statelessSession.createMutationQuery(hql)
        query.executeUpdate()

        entities.forEach{ statelessSession.insert(it)}

        transaction.commit()
        statelessSession.close()
    }

    fun <T> upsertEntities(entities: Collection<T>) {
        val statelessSession: StatelessSession = sessionFactory.openStatelessSession()
        val transaction: Transaction = statelessSession.beginTransaction()
        entities.forEach{ statelessSession.upsert(it)}
        transaction.commit()
        statelessSession.close()
    }

}
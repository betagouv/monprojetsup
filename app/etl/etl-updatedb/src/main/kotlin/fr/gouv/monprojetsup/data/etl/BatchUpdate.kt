package fr.gouv.monprojetsup.data.etl

import org.hibernate.Transaction
import org.springframework.stereotype.Component

@Component
class BatchUpdate(
    private val sessionFactory: org.hibernate.SessionFactory
) {

    fun clearEntities(entityName: String) {
        sessionFactory.openStatelessSession().use { statelessSession ->
            {
                val transaction: Transaction = statelessSession.beginTransaction()

                val hql = "DELETE FROM $entityName"
                val query = statelessSession.createMutationQuery(hql)
                query.executeUpdate()

                transaction.commit()
            }
        }
    }

    fun <T> setEntities(entityName: String, entities: Collection<T>) {
        sessionFactory.openStatelessSession().use { statelessSession ->
            {
                val transaction: Transaction = statelessSession.beginTransaction()
                val hql = "DELETE FROM $entityName"
                val query = statelessSession.createMutationQuery(hql)
                query.executeUpdate()
                entities.forEach { statelessSession.insert(it) }
                transaction.commit()
            }
        }
    }

    fun <T> upsertEntities(entities: Collection<T>) {
        sessionFactory.openStatelessSession().use { statelessSession ->
            {
                val transaction: Transaction = statelessSession.beginTransaction()
                entities.forEach { statelessSession.upsert(it) }
                transaction.commit()
            }
        }
    }

}
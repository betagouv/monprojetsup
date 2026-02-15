package fr.gouv.monprojetsup.data.etl

import org.hibernate.Transaction
import org.springframework.stereotype.Component

@Component
class BatchUpdate(
    private val sessionFactory: org.hibernate.SessionFactory
) {

    fun clearEntities(entityName: String) {
        sessionFactory.openStatelessSession().use { statelessSession ->
            val transaction: Transaction = statelessSession.beginTransaction()

            val hql = "DELETE FROM $entityName"
            val query = statelessSession.createMutationQuery(hql)
            query.executeUpdate()

            transaction.commit()
        }
    }

    fun <T> setEntities(entityName: String, entities: Collection<T>) {
        sessionFactory.openStatelessSession().use { statelessSession ->
            val transaction: Transaction = statelessSession.beginTransaction()
            val hql = "DELETE FROM $entityName"
            val query = statelessSession.createMutationQuery(hql)
            query.executeUpdate()
            entities.forEach { statelessSession.insert(it) }
            transaction.commit()
        }
    }

    fun <T> upsertEntities(entities: Collection<T>) {
        sessionFactory.openStatelessSession().use { statelessSession ->
            val transaction: Transaction = statelessSession.beginTransaction()
            entities.forEach { statelessSession.upsert(it) }
            transaction.commit()
        }
    }

    fun <T> getEntities(entityName: String, classe: Class<T> ): Collection<T> =
        getEntitiesHql("FROM $entityName", classe)

    fun <T> getEntitiesHql(hql : String, classe: Class<T> ): Collection<T> {
        sessionFactory.openStatelessSession().use { statelessSession ->
            val query = statelessSession.createSelectionQuery(hql, classe)
            return query.resultList
        }
    }

    fun countEntities(entityName: String) : Long {
        sessionFactory.openStatelessSession().use { statelessSession ->
            val query = statelessSession.createSelectionQuery("SELECT COUNT(e) FROM $entityName e", Long::class.java)
            return query.uniqueResult()
        }
    }

    fun setTableContent(srcTableName: String, destTableName: String) {
        sessionFactory.openStatelessSession().use { statelessSession ->
            val transaction: Transaction = statelessSession.beginTransaction()

            val sqlDelete = "DELETE FROM $destTableName"
            val queryDelete = statelessSession.createNativeMutationQuery(sqlDelete)
            queryDelete.executeUpdate()

            val sqlInsert = "INSERT INTO $destTableName " +
                    "SELECT * FROM $srcTableName"
            val queryInsert = statelessSession.createNativeMutationQuery(sqlInsert)
            queryInsert.executeUpdate()

            transaction.commit()
        }
    }


}
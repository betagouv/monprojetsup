package fr.gouv.monprojetsup.data.metier.infrastructure

import fr.gouv.monprojetsup.data.metier.entity.MetierEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface MetiersDb :
    JpaRepository<MetierEntity, String>

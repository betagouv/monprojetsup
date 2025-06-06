package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.eleve.infrastructure.entity.ProfilEleveEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface EleveJPARepository : JpaRepository<ProfilEleveEntity, String> {
    @Query(
        """
    SELECT pe AS profilEleve, cp.idParcoursup AS idParcoursup
    FROM ProfilEleve pe
    LEFT JOIN CompteParcoursup cp ON cp.idEleve = pe.id
    WHERE pe.id = :idEleve
    """,
    )
    fun findByIdWithIdParcoursup(idEleve: String): List<ProfilEleveWithCompteParcoursup>
}

interface ProfilEleveWithCompteParcoursup {
    fun getProfilEleve(): ProfilEleveEntity

    fun getIdParcoursup(): Int?
}

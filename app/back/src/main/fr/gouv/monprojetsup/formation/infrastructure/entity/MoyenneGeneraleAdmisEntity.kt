package fr.gouv.monprojetsup.formation.infrastructure.entity

import fr.gouv.monprojetsup.referentiel.infrastructure.entity.BaccalaureatEntity
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.io.Serializable

@Entity
@Table(name = "ref_moyenne_generale_admis")
class MoyenneGeneraleAdmisEntity {
    @EmbeddedId
    lateinit var id: MoyenneGeneraleAdmisId

    @Type(ListArrayType::class)
    @Column(name = "frequences_cumulees")
    lateinit var frequencesCumulees: List<Int>

    @Column(name = "annee", insertable = false, updatable = false)
    lateinit var annee: String

    @Column(name = "id_formation", insertable = false, updatable = false)
    lateinit var idFormation: String

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_bac", insertable = false, updatable = false)
    lateinit var baccalaureat: BaccalaureatEntity
}

@Embeddable
data class MoyenneGeneraleAdmisId(
    @Column(name = "annee")
    val annee: String = "",
    @Column(name = "id_formation")
    val idFormation: String = "",
    @Column(name = "id_bac")
    val idBaccalaureat: String = "",
) : Serializable

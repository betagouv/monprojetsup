package fr.gouv.monprojetsup.data.app.referentiel.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "join_baccalaureat_specialite")
class BaccalaureatSpecialiteEntity {

    @Column(name = "id_specialite")
    lateinit var idSpecialite: String

    @Column(name = "id_baccalaureat")
    lateinit var idBaccalaureat: String
}

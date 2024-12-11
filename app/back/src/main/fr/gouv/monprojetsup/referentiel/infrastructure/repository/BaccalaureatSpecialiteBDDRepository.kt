package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatSpecialiteRepository
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class BaccalaureatSpecialiteBDDRepository(
    val baccalaureatJPARepository: BaccalaureatJPARepository,
    val baccalaureatSpecialiteJPARepository: BaccalaureatSpecialiteJPARepository,
    val specialiteJPARepository: SpecialiteJPARepository,
) : BaccalaureatSpecialiteRepository {
    override fun recupererLesIdsDesSpecialitesDUnBaccalaureat(idBaccalaureat: String): List<String> {
        val specialites = baccalaureatSpecialiteJPARepository.findAllByIdBaccalaureat(idBaccalaureat)
        return specialites.map { it.id.idSpecialite }
    }

    override fun recupererLesBaccalaureatsAvecLeursSpecialites(): Map<Baccalaureat, List<Specialite>> {
        val baccalaureats = baccalaureatJPARepository.findAll()
        val baccalaureatSpecialites = baccalaureatSpecialiteJPARepository.findAllByIdBaccalaureatIn(baccalaureats.map { it.id })
        val specialites = specialiteJPARepository.findAllByIdIn(baccalaureatSpecialites.map { it.id.idSpecialite }.distinct())
        return baccalaureats.associate { baccalaureatEntity ->
            val pairesBaccalaureatSpecialiteDuBaccalaureat = baccalaureatSpecialites.filter { it.idBaccalaureat == baccalaureatEntity.id }
            val specialitesDuBac =
                pairesBaccalaureatSpecialiteDuBaccalaureat.map { paire ->
                    specialites.first { it.id == paire.id.idSpecialite }.toSpecialite()
                }
            baccalaureatEntity.toBaccalaureat() to specialitesDuBac
        }
    }

    override fun recupererUnBaccalaureatEtLesIdsDeSesSpecialites(idBaccalaureat: String): Pair<String, List<String>>? {
        return baccalaureatJPARepository.findById(idBaccalaureat).getOrNull()?.let { baccalaureat ->
            val idsSpecialites = baccalaureatSpecialiteJPARepository.findAllByIdBaccalaureat(idBaccalaureat).map { it.id.idSpecialite }
            Pair(baccalaureat.id, idsSpecialites)
        }
    }
}

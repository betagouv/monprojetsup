package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.domain.entity.Label
import fr.gouv.monprojetsup.referentiel.domain.port.LabelsRepository
import org.springframework.stereotype.Repository

@Repository
class LabelBDDRepository(
    val labelJPARepository: LabelJPARepository,
) : LabelsRepository {
    override fun recupererLesLabels(ids: List<String>): List<Label> {
        return labelJPARepository.findAllByIdIn(ids).map { it.toLabel() }
    }
}

package fr.gouv.monprojetsup.referentiel.domain.port

import fr.gouv.monprojetsup.referentiel.domain.entity.Label

interface LabelsRepository {
    fun recupererLesLabels(ids: List<String>): List<Label>
}

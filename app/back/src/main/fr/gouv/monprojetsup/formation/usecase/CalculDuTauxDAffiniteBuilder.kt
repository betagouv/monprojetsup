package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class CalculDuTauxDAffiniteBuilder {
    fun calculDuTauxDAffinite(
        formationAvecLeurAffinite: List<FormationAvecSonAffinite>,
        idFormation: String,
    ): Int {
        return formationAvecLeurAffinite.firstOrNull { it.idFormation == idFormation }?.tauxAffinite?.let { (it * 100).roundToInt() } ?: 0
    }
}

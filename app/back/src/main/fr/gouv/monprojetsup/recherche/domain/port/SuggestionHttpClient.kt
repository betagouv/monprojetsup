package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.recherche.domain.entity.AffinitesPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.Profile

interface SuggestionHttpClient {
    fun recupererLesAffinitees(profile: Profile): AffinitesPourProfil
}

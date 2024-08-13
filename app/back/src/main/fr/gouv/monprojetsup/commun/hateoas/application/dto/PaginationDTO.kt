package fr.gouv.monprojetsup.commun.hateoas.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.ACTUEL
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.DERNIER
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.HREF
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.PARAMETRE_NUMERO_PAGE
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.PREMIER
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.REL
import fr.gouv.monprojetsup.commun.hateoas.domain.PaginationConstants.SUIVANT
import fr.gouv.monprojetsup.commun.hateoas.domain.entity.Hateoas
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

abstract class PaginationDTO {
    val liens: MutableList<LienHateoasDTO> = mutableListOf()

    fun <T> ajouterHateoas(hateoas: Hateoas<T>) {
        val url: ServletUriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentRequest()
        val premierePage = LienHateoasDTO(rel = PREMIER, href = remplacerNumeroPage(url, hateoas.premierePage))
        liens.add(premierePage)
        val dernierePage = LienHateoasDTO(rel = DERNIER, href = remplacerNumeroPage(url, hateoas.dernierePage))
        liens.add(dernierePage)
        val pageActuelle = LienHateoasDTO(rel = ACTUEL, href = remplacerNumeroPage(url, hateoas.pageActuelle))
        liens.add(pageActuelle)
        hateoas.pageSuivante?.let {
            val pageSuivante = LienHateoasDTO(rel = SUIVANT, href = remplacerNumeroPage(url, it))
            liens.add(pageSuivante)
        }
    }

    private fun remplacerNumeroPage(
        url: ServletUriComponentsBuilder,
        numeroPage: Int,
    ): String {
        return url.replaceQueryParam(PARAMETRE_NUMERO_PAGE, numeroPage).build().toUriString()
    }

    data class LienHateoasDTO(
        @JsonProperty(value = REL)
        val rel: String,
        @JsonProperty(value = HREF)
        val href: String,
    )
}

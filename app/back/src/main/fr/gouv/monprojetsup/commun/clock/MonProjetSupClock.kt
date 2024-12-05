package fr.gouv.monprojetsup.commun.clock

import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class MonProjetSupClock {
    fun dateActuelle(): LocalDate = LocalDate.now()
}

package fr.gouv.monprojetsup.commun.clock

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class MonProjetSupClock {
    fun dateActuelle(): LocalDate = LocalDate.now()
    fun dateTimeActuelle(): LocalDateTime = LocalDateTime.now()
}

package fr.gouv.monprojetsup.suggestions.server.usecase

import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions
import fr.gouv.monprojetsup.suggestions.algo.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AlgoConfigurationService(
    @Value("\${mps.suggestions.dynamic_parameter_service.enabled}")
    val enabled: Boolean,
    private val algo: AlgoSuggestions,
    ) {

    fun setAlgoConfig(config: Config): Config {
        if (enabled) algo.setParameters(config)
        return algo.config
    }

    fun getAlgoConfig(): Config {
        return algo.config
    }
}
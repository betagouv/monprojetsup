package fr.gouv.monprojetsup.suggestions.export

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(
            listOf(
                ConcurrentMapCache("myCache"),
                //Caches of algo. One cache per call site otherwise
                //weird behaviours are appearing (type mismatch, etc.
                ConcurrentMapCache("pathes"),
                ConcurrentMapCache("formations"),
                ConcurrentMapCache("retrieveFormation"),
                ConcurrentMapCache("retrieveSpecialites"),
                ConcurrentMapCache("retrieveLabel"),
                ConcurrentMapCache("retrieveDebugLabel"),
                ConcurrentMapCache("getMetiersOfFormation"),
                ConcurrentMapCache("retrieveEdgesOfType"),
                ConcurrentMapCache("getCoords"),
                ConcurrentMapCache("getVoeuxCoords"),
                ConcurrentMapCache("getCityCoords"),
                ConcurrentMapCache("getGeoExplanations"),
                ConcurrentMapCache("getGeoExplanations2"),
            ),
        )
        return cacheManager
    }
}

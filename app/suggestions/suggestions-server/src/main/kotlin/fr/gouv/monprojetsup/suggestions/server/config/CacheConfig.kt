package fr.gouv.monprojetsup.suggestions.server.config

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
                //Caches of algo
                ConcurrentMapCache("pathes"),
                ConcurrentMapCache("formations"),
                ConcurrentMapCache("repositories")
            ),
        )
        return cacheManager
    }
}

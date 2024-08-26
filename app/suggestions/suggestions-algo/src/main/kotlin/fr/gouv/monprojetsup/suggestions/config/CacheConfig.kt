import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.cache.CacheManager
import javax.cache.Caching


@Configuration
@EnableCaching
class CacheConfig  {
    @Bean
    fun cacheManager(): CacheManager {
        val cachingProvider = Caching.getCachingProvider()
        return cachingProvider.getCacheManager(
            javaClass.getResource("/ehcache.xml").toURI(),
            javaClass.classLoader
        )
    }
}
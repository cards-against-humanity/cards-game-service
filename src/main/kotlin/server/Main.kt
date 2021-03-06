package server

import api.ApiCardFetcher
import api.ApiUserFetcher
import api.CachedUserFetcher
import config.SwaggerConfig
import game.GameManager
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import route.GameController
import java.time.Duration

@SpringBootApplication
@ComponentScan(basePackageClasses = [GameController::class, SwaggerConfig::class])
open class Main : WebMvcConfigurerAdapter() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Main::class.java, *args)
        }
    }

    @Bean
    open fun corsConfigurer(args: Args): WebMvcConfigurer {
        return object : WebMvcConfigurerAdapter() {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE").allowedOrigins(args.allowedCorsOrigin).allowedHeaders("*")
            }
        }
    }

    @Bean
    open fun getArgs(): Args {
        return Args()
    }

    @Bean
    open fun getGameManager(args: Args): GameManager {
        val userFetcher = CachedUserFetcher(ApiUserFetcher(args.apiUrl, args.secureApiConnection), 100000, Duration.ofMinutes(30))
        val cardFetcher = ApiCardFetcher(args.apiUrl, args.secureApiConnection)
        return GameManager(userFetcher, cardFetcher)
    }

}
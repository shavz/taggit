package io.shiveenp.taggit.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.coRouter

@Profile("web", "local", "all")
@RestController
class Router {
    @Bean
    fun routes(taggitHandler: TaggitHandler) = coRouter {
        "/api".nest {
            "/login".nest {
                GET("", taggitHandler::login)
            }
            "/user".nest {
                GET("/", taggitHandler::getUser)
            }

            "/repos".nest {
                GET("", taggitHandler::getRepos)
                GET("/untagged", taggitHandler::searchUntaggedRepos)
                GET("/sync", taggitHandler::syncRepos)
                GET("/tags", taggitHandler::getRepoTags)
                POST("/{repoId}/tag", taggitHandler::addTagToRepo)
                DELETE("/{repoId}/tag", taggitHandler::deleteTagFromRepo)
                GET("/search", taggitHandler::searchRepoByTags)
            }
        }
    }
}

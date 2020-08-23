package com.shiveenp.taggit.api

import com.shiveenp.taggit.config.SessionKeyFilter
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.coRouter

@RestController
class Router {

    @Bean
    fun routes(taggitHandler: TaggitHandler, sessionKeyFilter: SessionKeyFilter) = coRouter {
        "/".nest {
            GET("signin", taggitHandler::loginOrSignup)
        }

        // Todo: delete api needs to be added yet
        "/user/{userId}".nest {
            GET("", taggitHandler::getUser)
            PUT("", taggitHandler::updateUser)

            "/repos".nest {
                GET("", taggitHandler::getRepos)
                GET("/sync", taggitHandler::syncRepos)
                GET("/tags", taggitHandler::getRepoTags)
                POST("/{repoId}/tag", taggitHandler::addTagToRepo)
                DELETE("/{repoId}/tag/{tag}", taggitHandler::deleteTagFromRepo)
                POST("/search", taggitHandler::searchRepoByTags)
            }
        }
    }.filter(sessionKeyFilter)
}

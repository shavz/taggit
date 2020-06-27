package com.shiveenp.taggit

import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.coRouter

@RestController
class Router {

    @Bean
    fun routes(taggitHandler: TaggitHandler) = coRouter {
        "/".nest {
            GET("signin", taggitHandler::loginOrSignup)
        }
    }
}

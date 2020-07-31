package com.shiveenp.taggit.service

import com.shiveenp.taggit.models.GithubStargazingResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.reactive.asFlow
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service

/**
 * This is the main service responsible for all the nitty gritty of repo sync logic that
 * needs to stay outside the scope of the other regular CRUDy services
 */
@Service
class RepoSyncService(val githubService: GithubService,
                      val clientService: ReactiveOAuth2AuthorizedClientService) {

    private val logger = KotlinLogging.logger { }

    suspend fun syncUserStargazingData(): String {
        ReactiveSecurityContextHolder.getContext().doOnNext {
            val authToken = it.authentication as OAuth2AuthenticationToken
            clientService.loadAuthorizedClient<OAuth2AuthorizedClient>(authToken.authorizedClientRegistrationId, authToken.name).doOnSuccess {
                logger.info { "Token is: ${it.accessToken.tokenValue}" }
            }.subscribe()
            val startPage = 1
            val userStarredReposList = mutableListOf<GithubStargazingResponse>()

            var stargazingResponse = githubService.requestGithubStargazingResponseOrNull(startPage)
            if (stargazingResponse != null) {
                logger.info { "syncing page 1" }
                userStarredReposList.addAll(stargazingResponse.body ?: emptyList())
                val lastPage = getLastPageFromStargazingResponseOrNull(stargazingResponse)
                if (lastPage != null) {
                    for (i in 2..lastPage) {
                        logger.info { "syncing page $i" }
                        stargazingResponse = githubService.requestGithubStargazingResponseOrNull(i)
                        if (stargazingResponse != null) {
                            userStarredReposList.addAll(stargazingResponse.body ?: emptyList())
                        }
                    }
                }
            }
            logger.info { "all user repos size is: ${userStarredReposList.size}" }
        }
            .asFlow()
            .first()
        return "Accepted"
    }

    fun getLastPageFromStargazingResponseOrNull(reponse: ResponseEntity<MutableList<GithubStargazingResponse>>): Int? {
        val linksHeader = reponse.headers["Link"]?.get(0)
        return if (linksHeader != null) {
            val lastPageLink = linksHeader.split(",").last()
            GithubService.githubLinkMatchRegex.find(lastPageLink)?.groupValues?.last()?.substringAfter("=")?.toInt()
        } else {
            null
        }
    }
}

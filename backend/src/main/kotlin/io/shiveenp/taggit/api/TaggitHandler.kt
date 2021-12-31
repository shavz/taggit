package io.shiveenp.taggit.api

import io.shiveenp.taggit.config.ExternalProperties
import io.shiveenp.taggit.models.TagInput
import io.shiveenp.taggit.service.TaggitService
import io.shiveenp.taggit.util.toUUID
import io.shiveenp.taggit.util.toUri
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*


@Component
class TaggitHandler(
    private val taggitService: TaggitService,
    private val externalProperties: ExternalProperties,
) {
    suspend fun loginOrSignup(req: ServerRequest): ServerResponse {
        taggitService.loginOrRegister()
        return temporaryRedirect(externalProperties.uiUrl.toUri()).buildAndAwait()
    }

    suspend fun getUser(req: ServerRequest): ServerResponse {
        val user = taggitService.getUser()
        return if (user != null) {
            ok().bodyValueAndAwait(user)
        } else {
            notFound().buildAndAwait()
        }
    }

    suspend fun getRepos(req: ServerRequest): ServerResponse {
        val page = req.queryParamOrNull("pageNm")
        val size = req.queryParamOrNull("pageSize")
        return ok().bodyValueAndAwait(taggitService.getUserStarredRepos(page?.toIntOrNull(), size?.toIntOrNull()))
    }

    suspend fun syncRepos(req: ServerRequest): ServerResponse {
        return accepted().bodyValueAndAwait(taggitService.syncUserRepos())
    }

    suspend fun getRepoTags(req: ServerRequest): ServerResponse {
        return ok().bodyValueAndAwait(taggitService.getDistinctTags())
    }

    suspend fun addTagToRepo(req: ServerRequest): ServerResponse {
        val repoId = req.pathVariable("repoId").toUUID()
        val tagInput = req.awaitBody<TagInput>()
        val updatedRepo = taggitService.addRepoTag(repoId, tagInput)
        return if (updatedRepo != null) {
            ok().bodyValueAndAwait(updatedRepo)
        } else {
            notFound().buildAndAwait()
        }
    }

    suspend fun deleteTagFromRepo(req: ServerRequest): ServerResponse {
        val repoId = req.pathVariable("repoId").toUUID()
        val tagToRemove = req.queryParamOrNull("tag")!!
        val updatedRepo = taggitService.deleteTagFromRepo(repoId, tagToRemove)
        return ok().bodyValueAndAwait(updatedRepo)
    }

    suspend fun searchRepoByTags(req: ServerRequest): ServerResponse {
        val tags = req.queryParams()["tag"] ?: emptyList()
        return ok().bodyValueAndAwait(taggitService.searchUserReposByTags(tags))
    }
}


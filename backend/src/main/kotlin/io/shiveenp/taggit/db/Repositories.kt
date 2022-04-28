package io.shiveenp.taggit.db

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CrudRepository<UserEntity, UUID> {
    fun findByGithubUserId(githubUserId: Long): UserEntity?
}

@Repository
interface RepoRepository : PagingAndSortingRepository<RepoEntity, UUID>

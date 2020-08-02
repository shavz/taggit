package com.shiveenp.taggit.db

import com.shiveenp.taggit.models.*
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "users")
data class TaggitUserEntity(
    @Id
    val id: UUID,
    val userName: String,
    val email: String?,
    val avatarUrl: String?,
    val githubUserName: String,
    val githubUserId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun toDto(): TaggitUser {
        return TaggitUser(
            id = this.id,
            userName = this.userName,
            email = this.email,
            avatarUrl = this.avatarUrl,
            githubUserName = this.githubUserName,
            githubUserId = this.githubUserId,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    companion object {
        fun from(githubUser: GithubUser): TaggitUserEntity {
            return TaggitUserEntity(
                id = UUID.randomUUID(),
                userName = githubUser.name ?: githubUser.login,
                email = githubUser.email,
                avatarUrl = githubUser.avatarUrl,
                githubUserName = githubUser.login,
                githubUserId = githubUser.id,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }
}

@Entity
@Table(name = "repo")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
data class TaggitRepoEntity(
    @Id
    val id: UUID,
    val userId: UUID,
    val repoId: Long,
    val repoName: String,
    val githubLink: String,
    val githubDescription: String?,
    val starCount: Int,
    val ownerAvatarUrl: String,
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    val metadata: Metadata?
) {
    fun toDto(): TaggitRepo {
        return TaggitRepo(
            id = this.id,
            userId = this.userId,
            repoId = this.repoId,
            repoName = this.repoName,
            githubLink = this.githubLink,
            githubDescription = this.githubDescription,
            ownerAvatarUrl = this.ownerAvatarUrl,
            metadata = this.metadata
        )
    }

    fun withUpdated(metadata: Metadata? = null): TaggitRepoEntity {
        return this.copy(
            metadata = metadata ?: this.metadata
        )
    }

    companion object {
        fun from(userId: UUID, response: GithubStargazingResponse): TaggitRepoEntity {
            return TaggitRepoEntity(
                id = UUID.randomUUID(),
                userId = userId,
                repoId = response.id,
                repoName = response.name,
                githubLink = response.url,
                githubDescription = response.description,
                starCount = response.stargazersCount,
                ownerAvatarUrl = response.owner.avatarUrl,
                metadata = null
            )
        }
    }
}

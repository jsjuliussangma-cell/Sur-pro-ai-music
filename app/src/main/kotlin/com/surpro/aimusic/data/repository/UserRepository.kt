package com.surpro.aimusic.data.repository

import com.surpro.aimusic.data.models.User
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    suspend fun getCurrentUser(): User? {
        // TODO: Fetch from Supabase
        return User(
            id = UUID.randomUUID().toString(),
            email = "user@example.com",
            name = "User Name",
            plan = com.surpro.aimusic.data.models.UserPlan.FREE,
            tokensBalance = 150,
            totalLyricsGenerated = 12,
            totalMusicsGenerated = 5,
            totalVideosGenerated = 3,
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    suspend fun updateUserProfile(name: String, bio: String): Result<User> = runCatching {
        // TODO: Update in Supabase
        User(
            id = UUID.randomUUID().toString(),
            email = "user@example.com",
            name = name,
            bio = bio,
            plan = com.surpro.aimusic.data.models.UserPlan.FREE,
            tokensBalance = 150,
            totalLyricsGenerated = 12,
            totalMusicsGenerated = 5,
            totalVideosGenerated = 3,
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    suspend fun getUserStats(): Result<Map<String, Int>> = runCatching {
        mapOf(
            "lyrics" to 12,
            "music" to 5,
            "videos" to 3,
            "followers" to 42
        )
    }
}

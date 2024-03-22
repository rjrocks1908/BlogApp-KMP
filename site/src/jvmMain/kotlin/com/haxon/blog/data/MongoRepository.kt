package com.haxon.blog.data

import com.haxon.blog.models.Post
import com.haxon.blog.models.User

interface MongoRepository {
    suspend fun checkUserExistence(user: User): User?
    suspend fun checkUserId(id: String): Boolean
    suspend fun addPost(post: Post): Boolean
}
package com.haxon.blog.data

import com.haxon.blog.models.Post
import com.haxon.blog.models.User
import com.haxon.blog.util.Constants
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.varabyte.kobweb.api.data.add
import com.varabyte.kobweb.api.init.InitApi
import com.varabyte.kobweb.api.init.InitApiContext
import kotlinx.coroutines.flow.firstOrNull

@InitApi
fun initMongoDB(ctx: InitApiContext) {
    System.setProperty(
        "org.litote.mongo.test.mapping.service",
        "org.litote.kmongo.serialization.SerializationClassMappingTypeService"
    )
    ctx.data.add(MongoDB(ctx))
}

class MongoDB(
    private val ctx: InitApiContext
) : MongoRepository {
    private val client = MongoClient.create()
    private val database = client.getDatabase(Constants.DATABASE_NAME)
    private val userCollection = database.getCollection<User>("user")
    private val postCollection = database.getCollection<Post>("post")

    override suspend fun checkUserExistence(user: User): User? {
        return try {
            userCollection
                .find(
                    and(
                        eq(User::username.name, user.username),
                        eq(User::password.name, user.password),
                    )
                ).firstOrNull()
        } catch (e: Exception) {
            ctx.logger.error(e.message.toString())
            null
        }
    }

    override suspend fun checkUserId(id: String): Boolean {
        return try {
            val documentCount = userCollection.countDocuments(eq(User::_id.name, id))
            documentCount > 0
        } catch (e: Exception) {
            ctx.logger.error(e.message.toString())
            false
        }
    }

    override suspend fun addPost(post: Post): Boolean {
        return postCollection.insertOne(post).wasAcknowledged()
    }
}
package com.haxon.blog.api

import com.haxon.blog.data.MongoDB
import com.haxon.blog.models.Post
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.bson.codecs.ObjectIdGenerator

@Api(routeOverride = "addpost")
suspend fun addPost(context: ApiContext) {
    try {
        val post = context.req.body?.decodeToString()?.let {
            Json.decodeFromString<Post>(it)
        }
        val newPost = post?.copy(id = ObjectIdGenerator().generate().toString())
        context.res.setBodyText(
            newPost?.let {
                context.data.getValue<MongoDB>().addPost(it).toString()
            } ?: false.toString()
        )
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(e.message))
    }
}
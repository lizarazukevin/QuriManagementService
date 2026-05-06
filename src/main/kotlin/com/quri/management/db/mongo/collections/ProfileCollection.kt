package com.quri.management.db.mongo.collections

import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.quri.client.model.CreateProfileInput
import com.quri.client.model.Profile
import com.quri.management.db.mongo.MongoSchema.Collections.PROFILES
import com.quri.management.db.mongo.documents.ProfileDocument
import com.quri.management.db.mongo.paginate
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Data access layer for the profiles collection in MongoDB.
 *
 * Handles all CRUD operations against the [ProfileDocument] collection and maps
 * persistence documents to Smithy-generated [Profile] models at the boundary.
 * No Smithy types leak into the persistence layer — mapping is handled internally
 *  * via [ProfileDocument.toApi].
 *
 *  @param dataStoreDatabase the MongoDB database instance injected by Spring
 */
@Component
class ProfileCollection(dataStoreDatabase: MongoDatabase) {
    private val collection: MongoCollection<ProfileDocument> =
        dataStoreDatabase.getCollection(PROFILES, ProfileDocument::class.java)

    /**
     * Finds a single profile by its MongoDB [ObjectId].
     *
     * @param id the [ObjectId] of the profile to retrieve
     * @return the matching [Profile], or `null` if not found
     */
    suspend fun findById(id: ObjectId): Profile? = collection.find(eq("_id", id)).firstOrNull()?.toApi()

    /**
     * Persists a new profile document and returns it alongside its generated ID.
     *
     * @param input the [CreateProfileInput] containing mutable user information
     * @param ownerId the owning entity
     * @return the persisted [Profile] with [Profile.id] populated, or `null` if
     * the insert did not return a generated ID
     */
    suspend fun create(
        input: CreateProfileInput,
        ownerId: String,
    ): Profile? {
        val doc = ProfileDocument.from(input, ownerId)
        val result = collection.insertOne(doc)
        val generatedId = result.insertedId?.asObjectId()?.value?.toString() ?: return null
        return doc.toApi(generatedId)
    }

    /**
     * Returns a paginated list of all profiles in the collection in ascending order by ID.
     *
     * @param pageSize is the maximum results per page
     * @param nextToken is the bookmarked ID the next paginated list starts from
     *
     * @return pair of all [Profile] documents mapped to Smithy models and nullable pagination token
     */
    suspend fun listAll(
        pageSize: Int,
        nextToken: String?,
    ): Pair<List<Profile>, String?> =
        paginate(collection, pageSize, nextToken) { it.id }
            .let { (docs, token) -> docs.map { it.toApi() } to token }

    /**
     * Deletes a profile by its [ObjectId].
     *
     * @param id the [ObjectId] of the profile to delete
     * @return the deleted [ObjectId] if the document was found and removed, `null` otherwise
     */
    suspend fun deleteById(id: ObjectId): ObjectId? {
        val result = collection.deleteOne(eq("_id", id))
        return id.takeIf { result.deletedCount == 1L }
    }
}

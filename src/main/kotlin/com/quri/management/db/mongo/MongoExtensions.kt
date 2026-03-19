package com.quri.management.db.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.toList
import org.bson.conversions.Bson
import org.bson.types.ObjectId

/**
 * Paginates a MongoDB collection using cursor-based pagination.
 *
 * Fetches [pageSize] + 1 documents to determine if more pages exist without
 * an additional count query. Returns the page of results and an opaque
 * [nextToken] string to be passed by the client on the next request.
 *
 * @param collection the MongoDB collection to paginate
 * @param pageSize maximum number of results to return
 * @param nextToken opaque cursor from the previous response, null for the first page
 * @param filter optional additional filter to apply alongside the cursor
 * @param idExtractor function to extract the ObjectId from a document, used to generate the next cursor
 * @return a pair of the page results and the next cursor token, or null if this is the last page
 */
suspend fun <T : Any> paginate(
    collection: MongoCollection<T>,
    pageSize: Int,
    nextToken: String?,
    filter: Bson = Filters.empty(),
    idExtractor: (T) -> ObjectId
): Pair<List<T>, String?> {
    val cursorFilter = nextToken
        ?.let { Filters.and(filter, Filters.gt("_id", ObjectId(it))) }
        ?: filter

    val results = collection
        .find(cursorFilter)
        .sort(Sorts.ascending("_id"))
        .limit(pageSize + 1)
        .toList()

    val hasMore = results.size > pageSize
    val page = if (hasMore) results.dropLast(1) else results
    val newToken = if (hasMore) idExtractor(page.last()).toHexString() else null

    return page to newToken
}
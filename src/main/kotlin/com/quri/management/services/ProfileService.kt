package com.quri.management.services

import com.quri.client.model.CreateProfileInput
import com.quri.client.model.DeleteProfileInput
import com.quri.client.model.GetProfileInput
import com.quri.client.model.InternalFailureException
import com.quri.client.model.Profile
import com.quri.client.model.ResourceNotFoundException
import com.quri.management.db.mongo.collections.ProfileCollection
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

/**
 * Business logic layer for profile operations.
 */
@Service
class ProfileService(private val profileCollection: ProfileCollection) {

    /**
     * Retrieves a profile by its ID.
     *
     * @param input contains the profile ID to look up
     * @return the matching [Profile]
     * @throws ResourceNotFoundException if no profile exists with the ID provided
     */
    suspend fun getProfileFromId(input: GetProfileInput): Profile =
        profileCollection.findById(ObjectId(input.profileId))
            ?: throw ResourceNotFoundException.builder()
                .message("Profile with ID `${input.profileId}` not found")
                .build()

    /**
     * Creates a new profile.
     *
     * @param input contains the user's personal info
     * @param ownerId the owning entity
     * @return the persisted [Profile] with its db-generated ID
     * @throws InternalFailureException if the insert did not return a generated ID
     */
    suspend fun createProfile(
        input: CreateProfileInput,
        ownerId: String,
    ): Profile =
        profileCollection.create(input, ownerId)
            ?: throw InternalFailureException.builder()
                .message("Failed to create profile")
                .build()

    /**
     * Returns a paginated list of profiles.
     *
     * @param pageSize is the maximum results per page
     * @param nextToken is the bookmarked ID the next paginated list starts from
     * @return list of all [Profile] records and nullable pagination token
     */
    suspend fun listProfiles(
        pageSize: Int,
        nextToken: String?,
    ): Pair<List<Profile>, String?> = profileCollection.listAll(pageSize, nextToken)

    /**
     * Deletes a profile by its ID.
     *
     * @param input contains the profile ID to delete
     * @return the deleted profile ID as a [String]
     * @throws ResourceNotFoundException if no profile exists with the ID provided
     */
    suspend fun deleteProfile(input: DeleteProfileInput): String =
        profileCollection.deleteById(ObjectId(input.profileId))?.toString()
            ?: throw ResourceNotFoundException.builder()
                .message("Profile with ID `${input.profileId}` not found")
                .build()
}

package com.quri.management.services

import com.quri.management.db.mongo.collections.ProfileCollection
import com.quri.server.model.Profile
import com.quri.server.model.CreateProfileInput
import com.quri.server.model.DeleteProfileInput
import com.quri.server.model.GetProfileInput
import com.quri.server.model.InternalError
import com.quri.server.model.ResourceNotFoundException
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

/**
 * Business logic layer for profile operations.
 */
@Service
class ProfileService(
    private val profileCollection: ProfileCollection
) {

    /**
     * Retrieves a profile by its ID.
     *
     * @param input contains the profile ID to look up
     * @return the matching [Profile]
     * @throws ResourceNotFoundException if no profile exists with the given ID
     */
    suspend fun getProfileFromId(input: GetProfileInput): Profile =
        profileCollection.findById(ObjectId(input.profileId))
            ?: throw ResourceNotFoundException.builder()
                .message("Profile with ID '${input.profileId}' not found")
                .build()

    /**
     * Creates a new profile.
     *
     * @param input contains the user's personal info
     * @return the persisted [Profile] with its generated ID
     * @throws InternalError if the insert did not return a generated ID
     */
    suspend fun createProfile(input: CreateProfileInput): Profile =
        profileCollection.create(input)
            ?: throw InternalError.builder()
                .message("Failed to create profile")
                .build()

    /**
     * Returns all profiles.
     *
     * @return list of all [Profile] records
     */
    suspend fun listProfiles(): List<Profile> = profileCollection.listAll()

    /**
     * Deletes a profile by its ID.
     *
     * @param input contains the profile ID to delete
     * @return the deleted profile ID as a [String]
     * @throws ResourceNotFoundException if no profile exists with the given ID
     */
    suspend fun deleteProfile(input: DeleteProfileInput): String =
        profileCollection.deleteById(ObjectId(input.profileId))?.toString()
            ?: throw ResourceNotFoundException.builder()
                .message("Profile with ID '${input.profileId}' not found")
                .build()
}
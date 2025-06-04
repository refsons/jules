package com.example.service;

import com.example.model.UserProfile;
import com.example.repository.UserProfileRepository;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Singleton
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile saveUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    public Optional<UserProfile> findUserProfileById(String id) {
        return userProfileRepository.findById(id);
    }

    public List<UserProfile> getAllUserProfiles() {
        return StreamSupport.stream(userProfileRepository.findAll().spliterator(), false)
                            .collect(Collectors.toList());
    }

    public UserProfile updateUserProfile(UserProfile userProfile) {
        return userProfileRepository.update(userProfile);
    }

    public void deleteUserProfileById(String id) {
        userProfileRepository.deleteById(id);
    }
}

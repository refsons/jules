package com.example.service;

import com.example.model.UserProfile;
import com.example.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private UserProfile userProfileSample;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userProfileSample = new UserProfile("1", "John", "Doe", LocalDate.now(), new ArrayList<>());
    }

    @Test
    void testSaveUserProfile() {
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfileSample);
        UserProfile result = userProfileService.saveUserProfile(userProfileSample);
        assertEquals(userProfileSample, result);
        verify(userProfileRepository).save(userProfileSample);
    }

    @Test
    void testFindUserProfileById() {
        when(userProfileRepository.findById("1")).thenReturn(Optional.of(userProfileSample));
        Optional<UserProfile> result = userProfileService.findUserProfileById("1");
        assertTrue(result.isPresent());
        assertEquals(userProfileSample, result.get());
        verify(userProfileRepository).findById("1");
    }

    @Test
    void testGetAllUserProfiles() {
        List<UserProfile> profiles = new ArrayList<>();
        profiles.add(userProfileSample);
        when(userProfileRepository.findAll()).thenReturn(profiles);
        List<UserProfile> result = userProfileService.getAllUserProfiles();
        assertEquals(1, result.size());
        assertEquals(userProfileSample, result.get(0));
        verify(userProfileRepository).findAll();
    }

    @Test
    void testUpdateUserProfile() {
        when(userProfileRepository.update(any(UserProfile.class))).thenReturn(userProfileSample);
        UserProfile result = userProfileService.updateUserProfile(userProfileSample);
        assertEquals(userProfileSample, result);
        verify(userProfileRepository).update(userProfileSample);
    }

    @Test
    void testDeleteUserProfileById() {
        doNothing().when(userProfileRepository).deleteById("1");
        userProfileService.deleteUserProfileById("1");
        verify(userProfileRepository).deleteById("1");
    }
}

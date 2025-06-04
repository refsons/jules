package com.example.service;

import com.example.model.Address;
import com.example.model.UserProfile;
import com.google.cloud.bigquery.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @Mock
    private BigQuery mockBigQuery;

    private UserProfileService userProfileService;

    private final String testProjectId = "test-project";
    private final String testDatasetName = "test_dataset";
    private final String testTableName = "user_profiles";
    private final Gson gson = new Gson();

    private UserProfile userProfileSample;
    private String userProfileSampleJsonAddresses;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userProfileService = new UserProfileService(mockBigQuery, testProjectId, testDatasetName, testTableName);

        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address("Home", "123 Main St", "Anytown", "CA", "90210", "5"));
        userProfileSample = new UserProfile("user123", "John", "Doe", LocalDate.of(1990, 1, 15), addresses);
        userProfileSampleJsonAddresses = gson.toJson(addresses);
    }

    private String getExpectedFullTableName() {
        return String.format("`%s.%s.%s`", testProjectId, testDatasetName, testTableName);
    }

    @Test
    void testSaveUserProfile() throws InterruptedException, JobException {
        TableResult mockTableResult = mock(TableResult.class); // Not strictly needed for insert if we don't check results
        when(mockBigQuery.query(any(QueryJobConfiguration.class))).thenReturn(mockTableResult);

        UserProfile result = userProfileService.saveUserProfile(userProfileSample);

        assertNotNull(result.getId()); // ID should be generated if null, or kept if provided
        assertEquals(userProfileSample.getFirstName(), result.getFirstName());

        ArgumentCaptor<QueryJobConfiguration> captor = ArgumentCaptor.forClass(QueryJobConfiguration.class);
        verify(mockBigQuery).query(captor.capture());

        QueryJobConfiguration capturedConfig = captor.getValue();
        String expectedQuery = String.format(
                "INSERT INTO %s (id, firstName, lastName, dob, addresses_json) VALUES (@id, @firstName, @lastName, @dob, @addressesJson)",
                getExpectedFullTableName()
        );
        assertEquals(expectedQuery, capturedConfig.getQuery());
        // Further checks on named parameters can be added here if needed
        // For example:
        // assertEquals(userProfileSample.getId(), capturedConfig.getNamedParameters().get("id").getValue());
    }

    @Test
    void testFindUserProfileById_found() throws InterruptedException, JobException {
        TableResult mockTableResult = mock(TableResult.class);
        FieldValueList mockRow = mock(FieldValueList.class);

        when(mockTableResult.getTotalRows()).thenReturn(1L);
        when(mockTableResult.iterateAll()).thenReturn(Collections.singletonList(mockRow));

        // Mock FieldValue for each field
        mockFieldValue(mockRow, "id", userProfileSample.getId());
        mockFieldValue(mockRow, "firstName", userProfileSample.getFirstName());
        mockFieldValue(mockRow, "lastName", userProfileSample.getLastName());
        mockFieldValue(mockRow, "dob", userProfileSample.getDob().format(DateTimeFormatter.ISO_LOCAL_DATE));
        mockFieldValue(mockRow, "addresses_json", userProfileSampleJsonAddresses);

        when(mockBigQuery.query(any(QueryJobConfiguration.class))).thenReturn(mockTableResult);

        Optional<UserProfile> result = userProfileService.findUserProfileById("user123");

        assertTrue(result.isPresent());
        assertEquals(userProfileSample.getId(), result.get().getId());
        assertEquals(userProfileSample.getFirstName(), result.get().getFirstName());
        assertEquals(userProfileSample.getAddresses().size(), result.get().getAddresses().size());

        ArgumentCaptor<QueryJobConfiguration> captor = ArgumentCaptor.forClass(QueryJobConfiguration.class);
        verify(mockBigQuery).query(captor.capture());
        assertTrue(captor.getValue().getQuery().contains("WHERE id = @id"));
    }

    @Test
    void testFindUserProfileById_notFound() throws InterruptedException, JobException {
        TableResult mockTableResult = mock(TableResult.class);
        when(mockTableResult.getTotalRows()).thenReturn(0L);
        when(mockBigQuery.query(any(QueryJobConfiguration.class))).thenReturn(mockTableResult);

        Optional<UserProfile> result = userProfileService.findUserProfileById("unknownId");

        assertFalse(result.isPresent());
        verify(mockBigQuery).query(any(QueryJobConfiguration.class));
    }

    @Test
    void testGetAllUserProfiles() throws InterruptedException, JobException {
        TableResult mockTableResult = mock(TableResult.class);
        FieldValueList mockRow = mock(FieldValueList.class);

        when(mockTableResult.iterateAll()).thenReturn(Collections.singletonList(mockRow));
        mockFieldValue(mockRow, "id", userProfileSample.getId());
        mockFieldValue(mockRow, "firstName", userProfileSample.getFirstName());
        mockFieldValue(mockRow, "lastName", userProfileSample.getLastName());
        mockFieldValue(mockRow, "dob", userProfileSample.getDob().format(DateTimeFormatter.ISO_LOCAL_DATE));
        mockFieldValue(mockRow, "addresses_json", userProfileSampleJsonAddresses);

        when(mockBigQuery.query(any(QueryJobConfiguration.class))).thenReturn(mockTableResult);

        List<UserProfile> results = userProfileService.getAllUserProfiles();

        assertEquals(1, results.size());
        assertEquals(userProfileSample.getId(), results.get(0).getId());

        ArgumentCaptor<QueryJobConfiguration> captor = ArgumentCaptor.forClass(QueryJobConfiguration.class);
        verify(mockBigQuery).query(captor.capture());
        assertTrue(captor.getValue().getQuery().startsWith("SELECT id, firstName, lastName, dob, addresses_json FROM"));
    }

    @Test
    void testUpdateUserProfile() throws InterruptedException, JobException {
        // Mock the delete query part of the update
        TableResult mockDeleteResult = mock(TableResult.class);
        // Mock the insert query part of the update
        TableResult mockInsertResult = mock(TableResult.class);

        // Expect two queries: one DELETE, one INSERT
        when(mockBigQuery.query(any(QueryJobConfiguration.class)))
            .thenReturn(mockDeleteResult) // First call for DELETE
            .thenReturn(mockInsertResult); // Second call for INSERT

        UserProfile updatedProfile = new UserProfile(userProfileSample.getId(), "Johnathan", "Doe", userProfileSample.getDob(), userProfileSample.getAddresses());
        UserProfile result = userProfileService.updateUserProfile(updatedProfile);

        assertEquals(updatedProfile.getFirstName(), result.getFirstName());

        ArgumentCaptor<QueryJobConfiguration> captor = ArgumentCaptor.forClass(QueryJobConfiguration.class);
        verify(mockBigQuery, times(2)).query(captor.capture());

        List<QueryJobConfiguration> capturedConfigs = captor.getAllValues();
        assertTrue(capturedConfigs.get(0).getQuery().startsWith("DELETE FROM"));
        assertTrue(capturedConfigs.get(1).getQuery().startsWith("INSERT INTO"));
    }


    @Test
    void testDeleteUserProfileById() throws InterruptedException, JobException {
        TableResult mockTableResult = mock(TableResult.class);
        when(mockBigQuery.query(any(QueryJobConfiguration.class))).thenReturn(mockTableResult);

        userProfileService.deleteUserProfileById("user123");

        ArgumentCaptor<QueryJobConfiguration> captor = ArgumentCaptor.forClass(QueryJobConfiguration.class);
        verify(mockBigQuery).query(captor.capture());
        assertTrue(captor.getValue().getQuery().startsWith("DELETE FROM"));
        assertTrue(captor.getValue().getQuery().contains("WHERE id = @id"));
    }

    // Helper to mock FieldValue getters
    private void mockFieldValue(FieldValueList row, String fieldName, String value) {
        FieldValue mockFv = mock(FieldValue.class);
        when(mockFv.isNull()).thenReturn(value == null);
        if (value != null) {
            when(mockFv.getStringValue()).thenReturn(value);
        }
        when(row.get(fieldName)).thenReturn(mockFv);
    }
}

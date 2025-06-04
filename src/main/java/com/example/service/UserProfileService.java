package com.example.service;

import com.example.model.Address;
import com.example.model.UserProfile;
import com.google.cloud.bigquery.*;
import com.google.gson.Gson; // Using Gson for JSON serialization
import com.google.gson.reflect.TypeToken;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Singleton
public class UserProfileService {

    private final BigQuery bigquery;
    private final String projectId;
    private final String datasetName;
    private final String tableName;
    private final Gson gson = new Gson();
    private final Type addressListType = new TypeToken<ArrayList<Address>>() {}.getType();


    public UserProfileService(
            BigQuery bigquery,
            @Value("${gcp.project-id}") String projectId,
            @Value("${gcp.dataset-name}") String datasetName,
            @Value("${gcp.table-name.userprofile}") String tableName) {
        this.bigquery = bigquery;
        this.projectId = projectId;
        this.datasetName = datasetName;
        this.tableName = tableName;
    }

    private String getFullTableName() {
        return String.format("`%s.%s.%s`", projectId, datasetName, tableName);
    }

    public UserProfile saveUserProfile(UserProfile userProfile) {
        if (userProfile.getId() == null) {
            userProfile.setId(UUID.randomUUID().toString()); // Generate ID if not present
        }

        String addressesJson = gson.toJson(userProfile.getAddresses());

        String query = String.format(
                "INSERT INTO %s (id, firstName, lastName, dob, addresses_json) VALUES (@id, @firstName, @lastName, @dob, @addressesJson)",
                getFullTableName()
        );

        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                .addNamedParameter("id", QueryParameterValue.string(userProfile.getId()))
                .addNamedParameter("firstName", QueryParameterValue.string(userProfile.getFirstName()))
                .addNamedParameter("lastName", QueryParameterValue.string(userProfile.getLastName()))
                .addNamedParameter("dob", QueryParameterValue.date(userProfile.getDob() != null ? userProfile.getDob().format(DateTimeFormatter.ISO_LOCAL_DATE) : null))
                .addNamedParameter("addressesJson", QueryParameterValue.string(addressesJson))
                .build();

        try {
            bigquery.query(queryConfig);
            return userProfile;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("BigQuery insert interrupted", e);
        } catch (JobException e) {
            throw new RuntimeException("BigQuery job failed during insert", e);
        }
    }

    public Optional<UserProfile> findUserProfileById(String id) {
        String query = String.format("SELECT id, firstName, lastName, dob, addresses_json FROM %s WHERE id = @id", getFullTableName());
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                .addNamedParameter("id", QueryParameterValue.string(id))
                .build();

        try {
            TableResult result = bigquery.query(queryConfig);
            if (result.getTotalRows() == 0) {
                return Optional.empty();
            }
            FieldValueList row = result.iterateAll().iterator().next();
            return Optional.of(mapRowToUserProfile(row));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("BigQuery select by ID interrupted", e);
        } catch (JobException e) {
            throw new RuntimeException("BigQuery job failed during select by ID", e);
        }
    }

    public List<UserProfile> getAllUserProfiles() {
        String query = String.format("SELECT id, firstName, lastName, dob, addresses_json FROM %s", getFullTableName());
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();

        try {
            TableResult result = bigquery.query(queryConfig);
            List<UserProfile> profiles = new ArrayList<>();
            for (FieldValueList row : result.iterateAll()) {
                profiles.add(mapRowToUserProfile(row));
            }
            return profiles;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("BigQuery select all interrupted", e);
        } catch (JobException e) {
            throw new RuntimeException("BigQuery job failed during select all", e);
        }
    }

    // Note: BigQuery Standard SQL does not directly support UPDATE of arbitrary rows easily without unique IDs.
    // This implementation assumes 'id' is the unique key for update.
    // A more robust update might involve deleting and re-inserting or using MERGE.
    public UserProfile updateUserProfile(UserProfile userProfile) {
        if (userProfile.getId() == null) {
            throw new IllegalArgumentException("UserProfile ID cannot be null for update.");
        }
        String addressesJson = gson.toJson(userProfile.getAddresses());

        // Simple approach: delete then insert. For true atomic update, MERGE would be better.
        // This is non-atomic. Consider implications.
        deleteUserProfileByIdInternal(userProfile.getId()); // Internal delete without throwing if not found for this pattern

        // Re-insert the record
        // This is not a true "update" but a replacement.
        // For a real update, one might use a MERGE statement or update specific fields.
        // The following query is an example of how one might update specific fields,
        // but it requires knowing which fields changed.
        // A full MERGE is more complex.
        // For now, we use a simpler (but less robust) delete and insert pattern.

        // We will perform an insert, which is effectively a replacement after delete
        String query = String.format(
                "INSERT INTO %s (id, firstName, lastName, dob, addresses_json) VALUES (@id, @firstName, @lastName, @dob, @addressesJson)",
                getFullTableName()
        );

        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                .addNamedParameter("id", QueryParameterValue.string(userProfile.getId()))
                .addNamedParameter("firstName", QueryParameterValue.string(userProfile.getFirstName()))
                .addNamedParameter("lastName", QueryParameterValue.string(userProfile.getLastName()))
                .addNamedParameter("dob", QueryParameterValue.date(userProfile.getDob() != null ? userProfile.getDob().format(DateTimeFormatter.ISO_LOCAL_DATE) : null))
                .addNamedParameter("addressesJson", QueryParameterValue.string(addressesJson))
                .build();

        try {
            bigquery.query(queryConfig);
            return userProfile;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("BigQuery update (via insert) interrupted", e);
        } catch (JobException e) {
            throw new RuntimeException("BigQuery job failed during update (via insert)", e);
        }
    }

    private void deleteUserProfileByIdInternal(String id) {
        String query = String.format("DELETE FROM %s WHERE id = @id", getFullTableName());
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                .addNamedParameter("id", QueryParameterValue.string(id))
                .build();
        try {
            bigquery.query(queryConfig);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Log and ignore for internal delete during update, or handle as critical
        } catch (JobException e) {
            // Log and ignore for internal delete during update, or handle as critical
        }
    }


    public void deleteUserProfileById(String id) {
        String query = String.format("DELETE FROM %s WHERE id = @id", getFullTableName());
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                .addNamedParameter("id", QueryParameterValue.string(id))
                .build();
        try {
            bigquery.query(queryConfig);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("BigQuery delete interrupted", e);
        } catch (JobException e) {
            throw new RuntimeException("BigQuery job failed during delete", e);
        }
    }

    private UserProfile mapRowToUserProfile(FieldValueList row) {
        String id = row.get("id").getStringValue();
        String firstName = row.get("firstName").isNull() ? null : row.get("firstName").getStringValue();
        String lastName = row.get("lastName").isNull() ? null : row.get("lastName").getStringValue();
        LocalDate dob = row.get("dob").isNull() ? null : LocalDate.parse(row.get("dob").getStringValue());

        List<Address> addresses = new ArrayList<>();
        FieldValue addressesValue = row.get("addresses_json");
        if (addressesValue != null && !addressesValue.isNull()) {
            String addressesJson = addressesValue.getStringValue();
            if (addressesJson != null && !addressesJson.isEmpty()) {
                 addresses = gson.fromJson(addressesJson, addressListType);
            }
        }
        return new UserProfile(id, firstName, lastName, dob, addresses);
    }
}

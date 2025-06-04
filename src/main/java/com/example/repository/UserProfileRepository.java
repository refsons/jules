package com.example.repository;

import com.example.model.UserProfile;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, String> {
}

package com.example.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import java.time.LocalDate;
import java.util.List;

@Introspected
@MappedEntity
public class UserProfile {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private List<Address> addresses;

    public UserProfile() {
    }

    public UserProfile(String id, String firstName, String lastName, LocalDate dob, List<Address> addresses) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.addresses = addresses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}

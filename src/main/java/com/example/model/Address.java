package com.example.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Address {

    private String status;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String numberOfYears;

    public Address() {
    }

    public Address(String status, String address, String city, String state, String zip, String numberOfYears) {
        this.status = status;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.numberOfYears = numberOfYears;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getNumberOfYears() {
        return numberOfYears;
    }

    public void setNumberOfYears(String numberOfYears) {
        this.numberOfYears = numberOfYears;
    }
}

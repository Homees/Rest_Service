package com.example.rest.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Employee {
    private @Id @GeneratedValue Long id;
    private String firstName;
    private String lastName;
    private String role;

    public Employee() {}

    public Employee(String firstName, String lastName, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public void setName(String name) {
        String[] split = name.split(" ");
        this.firstName = split[0];
        this.lastName = split[1];
    }
}

package com.jalandhara.hostelproject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private Integer age;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @JsonBackReference
    private Room room;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "wifi_id")
    private Wifi wifi;

    @ManyToMany
    @JoinTable(
            name = "person_job",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "job_id")
    )
    @JsonManagedReference
    private Set<Job> jobs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id != null && id.equals(person.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}

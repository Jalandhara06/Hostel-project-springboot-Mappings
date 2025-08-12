package com.jalandhara.hostelproject.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "room_no")
    private Integer roomNo;
    @Column(name = "floor_no")
    private Integer floorNo;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tv_id", referencedColumnName = "id")
    @JsonManagedReference
    private Tv tv;

    @OneToMany(mappedBy = "room")
    @JsonManagedReference
    private List<Person> persons;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id != null && id.equals(room.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


}

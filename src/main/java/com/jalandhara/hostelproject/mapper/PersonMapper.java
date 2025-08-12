package com.jalandhara.hostelproject.mapper;

import com.jalandhara.hostelproject.entity.Job;
import com.jalandhara.hostelproject.entity.Person;
import com.jalandhara.hostelproject.entity.Room;
import com.jalandhara.hostelproject.entity.Wifi;
import com.jalandhara.hostelproject.requestBean.PersonRequestBean;
import com.jalandhara.hostelproject.responseBean.PersonResponseBean;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PersonMapper {

    // RequestBean -> Entity
    public Person toEntity(PersonRequestBean bean, Wifi wifi, Room room, Set<Job> jobs) {
        return Person.builder()
                .name(bean.getName())
                .age(bean.getAge())
                .wifi(wifi)
                .room(room)
                .jobs(jobs)
                .build();
    }

    // Entity -> ResponseBean
    public PersonResponseBean toResponse(Person person) {

        return Optional.ofNullable(person)
                .map(p-> PersonResponseBean.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .age(p.getAge())
                        .wifiId(p.getWifi() != null ? p.getWifi().getId() : null)
                        .roomId(p.getRoom() != null ? p.getRoom().getId() : null)
                        .jobIds(p.getJobs() != null ? p.getJobs().stream().map(Job::getId).collect(Collectors.toSet()) : new HashSet<>())
                        .build())
                .orElse(null);

    }


}

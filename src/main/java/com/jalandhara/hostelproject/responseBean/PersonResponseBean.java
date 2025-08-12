package com.jalandhara.hostelproject.responseBean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PersonResponseBean {
    private UUID id;
    private String name;
    private Integer age;
    private UUID roomId;
    private UUID wifiId;
    private Set<UUID> jobIds;

}
